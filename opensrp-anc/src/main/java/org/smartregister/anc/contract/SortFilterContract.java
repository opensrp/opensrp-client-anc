package org.smartregister.anc.contract;

import android.content.Context;

import org.smartregister.configurableviews.model.Field;
import org.smartregister.configurableviews.model.RegisterConfiguration;

import java.util.List;

public interface SortFilterContract {

    public interface View {

        Context getContext();

        void updateSortAndFilter(List<Field> filterList, Field sortField);

        void updateSortLabel(String sortText);
    }

    public interface Presenter {

        void updateSortAndFilter();

        void updateSort();

        RegisterConfiguration getConfig();

        List<Field> getFilterList();

        Field getSortField();

        void setSortField(Field sortField);
    }
}
