package org.smartregister.anc.fragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.smartregister.anc.R;
import org.smartregister.configurableviews.model.Field;
import org.smartregister.configurableviews.model.RegisterConfiguration;

import java.util.List;

/**
 * Created by keyman on 29/06/18.
 */
public class FilterDialogFragment extends DialogFragment {

    private OnFilterChangedListener listener;
    private RegisterConfiguration configuration;

    public static FilterDialogFragment newInstance(RegisterConfiguration registerConfiguration) {
        FilterDialogFragment filterDialogFragment = new FilterDialogFragment();
        filterDialogFragment.setRegisterConfiguration(registerConfiguration);
        return filterDialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Black_NoTitleBar);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.dialog_fragment_filter,
                container, false);

        updateFilterList(view, configuration.getFilterFields());
        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        // without a handler, the window sizes itself correctly
        // but the keyboard does not show up
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                /*Window window = null;
                if (getDialog() != null) {
                    window = getDialog().getWindow();
                }

                if (window == null) {
                    return;
                }

                Point size = new Point();

                Display display = window.getWindowManager().getDefaultDisplay();
                display.getSize(size);

                int width = size.x;

                window.setLayout((int) (width * 0.7), FrameLayout.LayoutParams.WRAP_CONTENT);
                window.setGravity(Gravity.CENTER);*/
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            listener = (OnFilterChangedListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement OnFilterChangedListener");
        }
    }

    public static FilterDialogFragment launchDialog(Activity activity, RegisterConfiguration configuration, String dialogTag) {
        FilterDialogFragment dialogFragment = FilterDialogFragment.newInstance(configuration);
        FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
        Fragment prev = activity.getFragmentManager().findFragmentByTag(dialogTag);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        dialogFragment.show(ft, dialogTag);

        return dialogFragment;
    }

    public void setRegisterConfiguration(RegisterConfiguration registerConfiguration) {
        this.configuration = registerConfiguration;
    }

    protected <T> void updateFilterList(final View view, final List<Field> filterList) {

        if (filterList == null) {
            return;
        }

        BaseAdapter baseAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return filterList.size();
            }

            @Override
            public Object getItem(int position) {
                return filterList.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view;
                final LayoutInflater inflater =
                        getActivity().getLayoutInflater();
                if (convertView == null) {
                    view = inflater.inflate(R.layout.register_filter_item, null);
                } else {
                    view = convertView;
                }

                Field field = filterList.get(position);

                View filterItem = view.findViewById(R.id.filter_item_layout);
                filterItem.setTag(field.getDbAlias());
                filterItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.updateFilter();
                    }
                });

                TextView filterLabel = filterItem.findViewById(R.id.filter_label);
                filterLabel.setText(field.getDisplayName());

                return view;
            }
        };


        ListView listView = view.findViewById(R.id.filter_list);
        listView.setAdapter(baseAdapter);
    }


    ////////////////////////////////////////////////////////////////
    // Inner classes
    ////////////////////////////////////////////////////////////////
    public interface OnFilterChangedListener {
        void updateFilter();
    }
}
