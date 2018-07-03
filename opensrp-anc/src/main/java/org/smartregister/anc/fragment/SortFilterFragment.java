package org.smartregister.anc.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.anc.R;
import org.smartregister.anc.activity.HomeRegisterActivity;
import org.smartregister.anc.contract.SortFilterContract;
import org.smartregister.anc.presenter.SortFilterPresenter;
import org.smartregister.configurableviews.model.Field;

import java.util.List;

/**
 * Created by keyman on 29/06/18.
 */
public class SortFilterFragment extends Fragment implements SortFilterContract.View {

    private FilterDialogClickListener actionHandler = new FilterDialogClickListener();
    private SortFilterContract.Presenter presenter;
    private BaseAdapter filterAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Black_NoTitleBar);

        initializePresenter();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.dialog_fragment_filter,
                container, false);

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

    private void initializePresenter() {
        presenter = new SortFilterPresenter(this);
    }

    @Override
    public void updateSortAndFilter(List<Field> filterList, Field sortField) {
        if (getActivity() != null) {
            ((HomeRegisterActivity) getActivity()).updateSortAndFilter(filterList, sortField);
        }
    }

    private void switchToRegister() {
        if (getActivity() != null) {
            ((HomeRegisterActivity) getActivity()).switchToBaseFragment();
        }
    }

    public void updateSortLabel(String sortText) {
        if (getView() != null && StringUtils.isNotBlank(sortText)) {
            TextView sortLabel = getView().findViewById(R.id.sort_label);
            sortLabel.setText(sortText);
        }
    }

    protected <T> void updateFilterList(final View view, final List<Field> filterList) {

        if (filterList == null) {
            return;
        }

        filterAdapter = new BaseAdapter() {
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
        listView.setAdapter(filterAdapter);
    }

    private void updateSortList(final List<Field> sortFields) {

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(getContext());

        final BaseAdapter sortAdapter = new BaseAdapter() {

            RadioButton checkedRadio;

            @Override
            public int getCount() {
                return sortFields.size();
            }

            @Override
            public Object getItem(int position) {
                return sortFields.get(position);
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
                    view = inflater.inflate(R.layout.register_sort_item, null);
                } else {
                    view = convertView;
                }

                Field field = sortFields.get(position);

                View filterItem = view.findViewById(R.id.sort_item_layout);
                filterItem.setOnClickListener(actionHandler);

                TextView filterLabel = view.findViewById(R.id.sort_label);
                filterLabel.setText(field.getDisplayName());

                final RadioButton radioButton = view.findViewById(R.id.sort_radio);
                radioButton.setTag(field);
                radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        Object tag = buttonView.getTag();
                        if (tag != null && tag instanceof Field) {
                            Field currentField = (Field) tag;
                            if (isChecked) {
                                presenter.setSortField(currentField);

                                if (checkedRadio == null) {
                                    checkedRadio = (RadioButton) buttonView;
                                    checkedRadio.setChecked(true);
                                    return;
                                }

                                if (checkedRadio == buttonView) {
                                    return;
                                }

                                checkedRadio.setChecked(false);
                                buttonView.setChecked(true);
                                checkedRadio = (RadioButton) buttonView;
                            }
                        }
                    }
                });

                final Field currentSortField = presenter.getSortField();
                if (currentSortField != null) {
                    if (currentSortField.equals(field)) {
                        radioButton.setChecked(true);
                        checkedRadio = radioButton;
                    } else {
                        radioButton.setChecked(false);
                    }
                }
                return view;
            }
        };

        builderSingle.setNegativeButton("DONE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                presenter.updateSort();
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(sortAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        builderSingle.show();
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
                    switchToRegister();
                    break;
                case R.id.apply_layout:
                    v.findViewById(R.id.button_apply).performClick();
                    break;
                case R.id.button_apply:
                    presenter.updateSortAndFilter();
                    break;
                case R.id.clear_filter:
                    presenter.getFilterList().clear();
                    filterAdapter.notifyDataSetChanged();
                    break;
                case R.id.filter_item_layout:
                    CheckBox checkBox = v.findViewById(R.id.filter_check);
                    checkBox.toggle();
                    break;
                case R.id.sort_item_layout:
                    RadioButton radioButton = v.findViewById(R.id.sort_radio);
                    radioButton.toggle();
                    break;
                case R.id.sort_layout:
                    updateSortList(presenter.getConfig().getSortFields());
                    break;
                default:
                    break;
            }
        }
    }
}
