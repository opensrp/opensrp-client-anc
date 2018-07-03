package org.smartregister.anc.contract;

import android.content.Context;

import org.smartregister.configurableviews.model.Field;
import org.smartregister.configurableviews.model.RegisterConfiguration;

import java.util.List;
import java.util.Set;

public class RegisterFragmentContract {

    public interface View {

        void initializeAdapter(Set<org.smartregister.configurableviews.model.View> visibleColumns);

        void initializeQueryParams(String tableName, String countSelect, String mainSelect);

        void CountExecute();

        void filterandSortInInitializeQueries();

        void refresh();

        void updateSearchBarHint(String searchBarText);

        void updateInitialsText(String initials);

        Context getContext();

        String getString(int resId);

        void updateFilterAndFilterStatus(String filterText, String sortText);
    }

    public interface Presenter {
        RegisterFragmentContract.View getView();

        void processViewConfigurations();

        void initializeQueries(String mainCondition);

        void updateInitials();

        void startSync();

        void updateSortAndFilter();

        List<Field> getFilterList();

        Field getSortField();

        RegisterConfiguration getConfig();

    }


}
