package org.smartregister.anc.library.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.anc.library.R;
import org.smartregister.anc.library.activity.BaseHomeRegisterActivity;
import org.smartregister.anc.library.contract.SortFilterContract;
import org.smartregister.anc.library.presenter.SortFilterPresenter;
import org.smartregister.configurableviews.model.Field;
import org.smartregister.view.activity.BaseRegisterActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by keyman on 29/06/18.
 */
public class SortFilterFragment extends Fragment implements SortFilterContract.View {

    private FilterDialogClickListener actionHandler = new FilterDialogClickListener();
    private SortFilterContract.Presenter presenter;

    private FilterAdapter filterAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializePresenter();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_sort_filter, container, false);

        updateFilterList(view, presenter.getConfig().getFilterFields());

        View sortLayout = view.findViewById(R.id.sort_layout);
        sortLayout.setOnClickListener(actionHandler);

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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.updateSort();
    }

    protected void updateFilterList(final View view, final List<Field> filterList) {

        if (filterList == null) {
            return;
        }

        RecyclerView recyclerView = view.findViewById(R.id.filter_recycler_view);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration itemDecor = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecor);

        filterAdapter = new FilterAdapter(filterList);
        recyclerView.setAdapter(filterAdapter);
    }

    private void initializePresenter() {
        presenter = new SortFilterPresenter(this);
    }

    @Override
    public void updateSortAndFilter(List<Field> filterList, Field sortField) {
        if (getActivity() != null) {
            ((BaseHomeRegisterActivity) getActivity()).updateSortAndFilter(filterList, sortField);
        }
    }

    public void updateSortLabel(String sortText) {
        if (getView() != null && StringUtils.isNotBlank(sortText)) {
            TextView sortLabel = getView().findViewById(R.id.sort_label);
            sortLabel.setText(Html.fromHtml(sortText));
        }
    }

    private void switchToRegister() {
        if (getActivity() != null) {
            ((BaseRegisterActivity) getActivity()).switchToBaseFragment();
        }
    }

    private void updateSortList(final List<Field> sortFields) {


        int currentChecked = -1;
        if (presenter.getSortField() != null) {
            currentChecked = sortFields.indexOf(presenter.getSortField());
        }
        final SortArrayAdapter arrayAdapter = new SortArrayAdapter(getActivity(), sortFields);
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(getContext(), R.style.AncAlertDialog);
        builderSingle.setSingleChoiceItems(arrayAdapter, currentChecked, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                presenter.setSortField(sortFields.get(which));
            }
        });

        builderSingle.setNegativeButton(getString(R.string.done), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });


        builderSingle.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                presenter.updateSort();
            }
        });

        builderSingle.show();
    }


    ////////////////////////////////////////////////////////////////
    // Inner classes
    ////////////////////////////////////////////////////////////////
    private class FilterDialogClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            int i = v.getId();
            if (i == R.id.cancel_filter) {
                switchToRegister();
            } else if (i == R.id.apply_layout) {
                v.findViewById(R.id.button_apply).performClick();
            } else if (i == R.id.button_apply) {
                presenter.updateSortAndFilter();
            } else if (i == R.id.clear_filter) {
                filterAdapter.clear();
            } else if (i == R.id.sort_layout) {
                updateSortList(presenter.getConfig().getSortFields());
            }
        }
    }

    private class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.ViewHolder> {
        private List<Field> filterList;

        public FilterAdapter(List<Field> filterList) {
            this.filterList = filterList;
        }

        @Override
        public FilterAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            CheckedTextView v = (CheckedTextView) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.register_filter_item, parent, false);

            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Field field = filterList.get(position);
            holder.checkedTextView.setText(field.getDisplayName());
            holder.checkedTextView.setTag(field);

            holder.checkedTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((CheckedTextView) v).toggle();
                    boolean isChecked = ((CheckedTextView) v).isChecked();
                    Object tag = v.getTag();
                    if (tag != null && tag instanceof Field) {
                        Field currentField = (Field) tag;
                        if (isChecked) {
                            if (!presenter.getFilterList().contains(currentField)) {
                                presenter.getFilterList().add(currentField);
                            }
                        } else {
                            presenter.getFilterList().remove(currentField);
                        }
                    }
                }
            });

            if (presenter.getFilterList().contains(field) && !holder.checkedTextView.isChecked()) {
                holder.checkedTextView.setChecked(true);
            } else if (!presenter.getFilterList().contains(field) && holder.checkedTextView.isChecked()) {
                holder.checkedTextView.setChecked(false);
            }
        }

        @Override
        public int getItemCount() {
            return filterList.size();
        }

        public void clear() {
            presenter.getFilterList().clear();
            notifyDataSetChanged();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public CheckedTextView checkedTextView;

            public ViewHolder(CheckedTextView v) {
                super(v);
                checkedTextView = v;
            }
        }
    }

    private class SortArrayAdapter extends ArrayAdapter<Field> {

        private Context context;
        private List<Field> list = new ArrayList<>();

        private SortArrayAdapter(@NonNull Context context, @NonNull List<Field> list) {
            super(context, 0, list);
            this.context = context;
            this.list = list;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = LayoutInflater.from(context).inflate(R.layout.register_sort_item, parent, false);
            }

            Field field = list.get(position);

            TextView text = (TextView) view;
            text.setTag(field);
            text.setText(field.getDisplayName());

            return view;
        }
    }
}
