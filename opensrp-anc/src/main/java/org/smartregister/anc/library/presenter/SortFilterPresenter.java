package org.smartregister.anc.library.presenter;

import org.smartregister.anc.library.contract.SortFilterContract;
import org.smartregister.anc.library.util.ConfigHelperUtils;
import org.smartregister.configurableviews.model.Field;
import org.smartregister.configurableviews.model.RegisterConfiguration;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class SortFilterPresenter implements SortFilterContract.Presenter {

    private WeakReference<SortFilterContract.View> viewReference;
    private RegisterConfiguration config;
    private List<Field> filterList = new ArrayList<>();
    private Field sortField;

    public SortFilterPresenter(SortFilterContract.View view) {
        this.viewReference = new WeakReference<>(view);
        this.config = ConfigHelperUtils.defaultRegisterConfiguration(view.getContext());
        if (this.sortField == null && this.config != null && this.config.getSortFields() != null) {
            this.sortField = this.config.getSortFields().get(0);
        }
    }

    @Override
    public void updateSortAndFilter() {
        getView().updateSortAndFilter(filterList, sortField);
    }

    @Override
    public void updateSort() {
        if (sortField != null) {
            getView().updateSortLabel("<b>Sort: </b>" + sortField.getDisplayName());
        }
    }

    @Override
    public RegisterConfiguration getConfig() {
        return config;
    }

    public List<Field> getFilterList() {
        return filterList;
    }

    public Field getSortField() {
        return sortField;
    }

    @Override
    public void setSortField(Field sortField) {
        this.sortField = sortField;
    }

    private SortFilterContract.View getView() {
        if (viewReference != null) return viewReference.get();
        else return null;
    }

}
