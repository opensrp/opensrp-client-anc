package org.smartregister.anc.library.contract;

import android.content.Context;

import org.smartregister.configurableviews.model.Field;
import org.smartregister.configurableviews.model.RegisterConfiguration;

import java.util.List;

public interface SortFilterContract {

    interface View {

        Context getContext();

        void updateSortAndFilter(List<Field> filterList, Field sortField);

        void updateSortLabel(String sortText);
    }

    interface Presenter {

        void updateSortAndFilter();

        void updateSort();

        RegisterConfiguration getConfig();

        List<Field> getFilterList();

        Field getSortField();

        void setSortField(Field sortField);
    }
}
