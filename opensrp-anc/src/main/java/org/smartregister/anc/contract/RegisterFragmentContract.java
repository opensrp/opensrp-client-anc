package org.smartregister.anc.contract;

import android.content.Context;

import org.smartregister.configurableviews.model.Field;
import org.smartregister.configurableviews.model.RegisterConfiguration;
import org.smartregister.configurableviews.model.ViewConfiguration;

import java.util.List;
import java.util.Set;

public class RegisterFragmentContract {

    public interface View {

        void initializeAdapter(Set<org.smartregister.configurableviews.model.View> visibleColumns);

        void initializeQueryParams(String tableName, String countSelect, String mainSelect);

        void countExecute();

        void filterandSortInInitializeQueries();

        void refresh();

        void updateSearchBarHint(String searchBarText);

        void updateInitialsText(String initials);

        Context getContext();

        String getString(int resId);

        void updateFilterAndFilterStatus(String filterText, String sortText);
    }

    public interface Presenter {

        void processViewConfigurations();

        void initializeQueries(String mainCondition);

        void updateInitials();

        void startSync();

        void updateSortAndFilter(List<Field> filterList, Field sortField);

    }

    public interface Model {

        RegisterConfiguration defaultRegisterConfiguration();

        ViewConfiguration getViewConfiguration(String viewConfigurationIdentifier);

        Set<org.smartregister.configurableviews.model.View> getRegisterActiveColumns(String viewConfigurationIdentifier);

        String countSelect(String tableName, String mainCondition);

        String mainSelect(String tableName, String mainCondition);

        String getInitials();

        String getFilterText(List<Field> filterList, String filter);

        String getSortText(Field sortField);

    }


}
