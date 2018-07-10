package org.smartregister.anc.presenter;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.Context;
import org.smartregister.anc.R;
import org.smartregister.anc.contract.RegisterFragmentContract;
import org.smartregister.anc.util.ConfigHelper;
import org.smartregister.anc.util.DBConstants;
import org.smartregister.configurableviews.ConfigurableViewsLibrary;
import org.smartregister.configurableviews.model.Field;
import org.smartregister.configurableviews.model.RegisterConfiguration;
import org.smartregister.configurableviews.model.ViewConfiguration;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.repository.AllSharedPreferences;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class RegisterFragmentPresenter implements RegisterFragmentContract.Presenter {

    private WeakReference<RegisterFragmentContract.View> viewReference;

    private Context context;

    private String tableName = DBConstants.WOMAN_TABLE_NAME;
    private String countSelect;
    private String mainSelect;

    private RegisterConfiguration config;

    private Set<org.smartregister.configurableviews.model.View> visibleColumns = new TreeSet<>();
    private String viewConfigurationIdentifier;

    public RegisterFragmentPresenter(RegisterFragmentContract.View view, Context context, String viewConfigurationIdentifier) {
        this.viewReference = new WeakReference<>(view);
        this.context = context;
        this.viewConfigurationIdentifier = viewConfigurationIdentifier;
        this.config = ConfigHelper.defaultRegisterConfiguration(getView().getContext());
    }

    @Override
    public void processViewConfigurations() {
        if (StringUtils.isBlank(viewConfigurationIdentifier)) {
            return;
        }

        ViewConfiguration viewConfiguration = ConfigurableViewsLibrary.getInstance().getConfigurableViewsHelper().getViewConfiguration(viewConfigurationIdentifier);
        if (viewConfiguration != null) {
            config = (RegisterConfiguration) viewConfiguration.getMetadata();
            setVisibleColumns(ConfigurableViewsLibrary.getInstance().getConfigurableViewsHelper().getRegisterActiveColumns(viewConfigurationIdentifier));
        }

        if (config.getSearchBarText() != null && getView() != null) {
            getView().updateSearchBarHint(config.getSearchBarText());
        }
    }

    @Override
    public void initializeQueries(String mainCondition) {
        this.countSelect = countSelect(mainCondition);
        this.mainSelect = mainSelect(mainCondition);

        getView().initializeQueryParams(tableName, countSelect, mainSelect);
        getView().initializeAdapter(visibleColumns);

        getView().CountExecute();
        getView().filterandSortInInitializeQueries();

        getView().refresh();
    }

    private String countSelect(String mainCondition) {
        SmartRegisterQueryBuilder countQueryBuilder = new SmartRegisterQueryBuilder();
        countQueryBuilder.SelectInitiateMainTableCounts(tableName);
        return countQueryBuilder.mainCondition(mainCondition);
    }

    private String mainSelect(String mainCondition) {
        SmartRegisterQueryBuilder queryBUilder = new SmartRegisterQueryBuilder();
        String[] columns = new String[]{
                tableName + ".relationalid",
                tableName + "." + DBConstants.KEY.LAST_INTERACTED_WITH,
                tableName + "." + DBConstants.KEY.BASE_ENTITY_ID,
                tableName + "." + DBConstants.KEY.FIRST_NAME,
                tableName + "." + DBConstants.KEY.LAST_NAME,
                tableName + "." + DBConstants.KEY.ANC_ID,
                tableName + "." + DBConstants.KEY.DOB,
                tableName + "." + DBConstants.KEY.DATE_REMOVED};
        queryBUilder.SelectInitiateMainTable(tableName, columns);
        return queryBUilder.mainCondition(mainCondition);
    }

    @Override
    public void updateInitials() {
        String preferredName = getPrefferedName();
        if (StringUtils.isNotBlank(preferredName)) {
            String[] preferredNameArray = preferredName.split(" ");
            String initials = "";
            if (preferredNameArray.length > 1) {
                initials = String.valueOf(preferredNameArray[0].charAt(0)) + String.valueOf(preferredNameArray[1].charAt(0));
            } else if (preferredNameArray.length == 1) {
                initials = String.valueOf(preferredNameArray[0].charAt(0));
            }
            getView().updateInitialsText(initials);
        }
    }

    private String getPrefferedName() {
        if (context == null || context.allSharedPreferences() == null) {
            return null;
        }

        AllSharedPreferences allSharedPreferences = context.allSharedPreferences();
        return allSharedPreferences.getANMPreferredName(allSharedPreferences.fetchRegisteredANM());
    }

    @Override
    public void startSync() {
        //ServiceTools.startSyncService(getActivity());
    }

    @Override
    public void updateSortAndFilter(List<Field> filterList, Field sortField) {
        String filterText = "<font color=#727272>" + getView().getString(R.string.filter) + "</font> <font color=#f0ab41>(" + filterList.size() + ")</font>";
        String sortText = "";
        if (sortField != null) {
            sortText = "(Sort: " + sortField.getDisplayName() + ")";
        }

        getView().updateFilterAndFilterStatus(filterText, sortText);
    }

    private RegisterFragmentContract.View getView() {
        if (viewReference != null)
            return viewReference.get();
        else
            return null;
    }

    private void setVisibleColumns(Set<org.smartregister.configurableviews.model.View> visibleColumns) {
        this.visibleColumns = visibleColumns;
    }

}
