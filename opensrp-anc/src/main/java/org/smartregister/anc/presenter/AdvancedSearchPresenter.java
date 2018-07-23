package org.smartregister.anc.presenter;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.anc.contract.AdvancedSearchContract;
import org.smartregister.anc.contract.RegisterFragmentContract;
import org.smartregister.anc.model.AdvancedSearchModel;
import org.smartregister.anc.util.DBConstants;
import org.smartregister.configurableviews.model.Field;

import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdvancedSearchPresenter extends RegisterFragmentPresenter implements AdvancedSearchContract.Presenter {

    private WeakReference<AdvancedSearchContract.View> viewReference;

    private AdvancedSearchContract.Model model;

    public AdvancedSearchPresenter(AdvancedSearchContract.View view, String viewConfigurationIdentifier) {
        super(view, viewConfigurationIdentifier);
        this.viewReference = new WeakReference<>(view);
        model = new AdvancedSearchModel();
    }

    public void search(String firstName, String lastName, String ancId, String edd, String dob, String phoneNumber, String alternateContact, boolean isLocal) {
        Map<String, String> editMap = model.createEditMap(firstName, lastName, ancId, edd, dob, phoneNumber, alternateContact);
        if (editMap == null || editMap.isEmpty()) {
            return;
        }

        if(isLocal) {
            String mainCondition = model.getMainConditionString(editMap);
            String tableName = DBConstants.WOMAN_TABLE_NAME;

            String countSelect = model.countSelect(tableName, mainCondition);
            String mainSelect = model.mainSelect(tableName, mainCondition);

            getView().initializeQueryParams(tableName, countSelect, mainSelect);
            getView().initializeAdapter(visibleColumns);

            getView().countExecute();
            getView().filterandSortInInitializeQueries();

            getView().refresh();

            getView().switchViews(true);
        }

    }

    protected AdvancedSearchContract.View getView() {
        if (viewReference != null)
            return viewReference.get();
        else
            return null;
    }
}
