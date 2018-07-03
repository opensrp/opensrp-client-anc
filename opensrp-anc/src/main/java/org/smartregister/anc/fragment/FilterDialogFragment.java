package org.smartregister.anc.fragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import org.smartregister.anc.R;
import org.smartregister.anc.contract.RegisterFragmentContract;
import org.smartregister.configurableviews.model.Field;

import java.util.List;

/**
 * Created by keyman on 29/06/18.
 */
public class FilterDialogFragment extends DialogFragment {

    private FilterDialogClickListener actionHandler = new FilterDialogClickListener();
    private RegisterFragmentContract.Presenter presenter;
    private BaseAdapter baseAdapter;

    public static FilterDialogFragment newInstance(RegisterFragmentContract.Presenter presenter) {
        FilterDialogFragment filterDialogFragment = new FilterDialogFragment();
        filterDialogFragment.setPresenter(presenter);
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

        updateFilterList(view, presenter.getConfig().getFilterFields());

        View apply = view.findViewById(R.id.apply_layout);
        apply.setOnClickListener(actionHandler);

        View buttonApply = view.findViewById(R.id.button_apply);
        buttonApply.setOnClickListener(actionHandler);

        View clear = view.findViewById(R.id.clear_filter);
        clear.setOnClickListener(actionHandler);

        View cancel = view.findViewById(R.id.cancel_filter);
        cancel.setOnClickListener(actionHandler);
        return view;
    }

    public static FilterDialogFragment launchDialog(Activity activity, RegisterFragmentContract.Presenter presenter, String dialogTag) {
        FilterDialogFragment dialogFragment = FilterDialogFragment.newInstance(presenter);
        FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
        Fragment prev = activity.getFragmentManager().findFragmentByTag(dialogTag);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        dialogFragment.show(ft, dialogTag);

        return dialogFragment;
    }

    protected <T> void updateFilterList(final View view, final List<Field> filterList) {

        if (filterList == null) {
            return;
        }

        baseAdapter = new BaseAdapter() {
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
                final View view;
                final LayoutInflater inflater =
                        getActivity().getLayoutInflater();
                if (convertView == null) {
                    view = inflater.inflate(R.layout.register_filter_item, null);
                } else {
                    view = convertView;
                }

                Field field = filterList.get(position);

                View filterItem = view.findViewById(R.id.filter_item_layout);
                filterItem.setOnClickListener(actionHandler);

                TextView filterLabel = view.findViewById(R.id.filter_label);
                filterLabel.setText(field.getDisplayName());

                final List<Field> currentFilters = presenter.getFilterList();

                final CheckBox checkBox = view.findViewById(R.id.filter_check);
                checkBox.setTag(field);
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        Object tag = buttonView.getTag();
                        if (tag != null && tag instanceof Field) {
                            Field currentField = (Field) tag;
                            if (isChecked) {
                                if (!currentFilters.contains(currentField)) {
                                    currentFilters.add(currentField);
                                }
                            } else {
                                if (currentFilters.contains(currentField)) {
                                    currentFilters.remove(currentField);
                                }
                            }
                        }
                    }
                });

                if (currentFilters.contains(field) && !checkBox.isChecked()) {
                    checkBox.setChecked(true);
                } else if (!currentFilters.contains(field) && checkBox.isChecked()) {
                    checkBox.setChecked(false);
                }

                return view;
            }
        };


        ListView listView = view.findViewById(R.id.filter_list);
        listView.setAdapter(baseAdapter);
    }

    public void setPresenter(RegisterFragmentContract.Presenter presenter) {
        this.presenter = presenter;
    }

    ////////////////////////////////////////////////////////////////
    // Inner classes
    ////////////////////////////////////////////////////////////////
    public class FilterDialogClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int id = v.getId();

            switch (v.getId()) {
                case R.id.cancel_filter:
                    dismiss();
                    break;
                case R.id.apply_layout:
                    v.findViewById(R.id.button_apply).performClick();
                    break;
                case R.id.button_apply:
                    presenter.updateSortAndFilter();
                    dismiss();
                    break;
                case R.id.clear_filter:
                    presenter.getFilterList().clear();
                    baseAdapter.notifyDataSetChanged();
                    break;
                case R.id.filter_item_layout:
                    CheckBox checkBox = v.findViewById(R.id.filter_check);
                    checkBox.toggle();
                    break;
                default:
                    break;
            }
        }
    }
}
