package org.smartregister.anc.contract;

import android.content.Context;

import org.smartregister.configurableviews.model.RegisterConfiguration;

import java.util.Set;

public class RegisterFragmentContract {

    public interface View {

        void initializeAdapter(Set<org.smartregister.configurableviews.model.View> visibleColumns);

        void initializeQueryParams(String tableName, String countSelect, String mainSelect);

        void CountExecute();

        void filterandSortInInitializeQueries();

        void refresh();

        void updateSearchBarHint(String searchBarText);

        void updateInitialsText(String initals);

        String getString(int resId);
    }

    public interface Presenter {
        RegisterFragmentContract.View getView();

        void processViewConfigurations();

        void initializeQueries(String mainCondition);

        void updateInitials();

        void startSync();

        RegisterConfiguration getConfig();

    }


}
