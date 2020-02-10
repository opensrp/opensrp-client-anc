package org.smartregister.anc.library.repository;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.anc.library.AncLibrary;
import org.smartregister.anc.library.util.DBConstantsUtils;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;

public class RegisterRepository {

    public String getObjectIdsQuery(String mainCondition, String filters, String detailsCondition) {
        if (!filters.isEmpty()) {
            filters = String.format(" AND ec_client_search.phrase MATCH '%s'", filters);
        }
        if (!StringUtils.isBlank(mainCondition)) {
            mainCondition = " AND " + mainCondition;
        }

        if (!StringUtils.isBlank(detailsCondition)) {
            detailsCondition = " where " + detailsCondition;
        } else {
            detailsCondition = "";
        }
        return "select ec_client_search.object_id from ec_client_search where ec_client_search.object_id IN (select object_id from ec_mother_details_search " + detailsCondition + ") " + mainCondition + filters;
    }

    public String getCountExecuteQuery(String mainCondition, String filters, String detailsCondition) {
        if (!filters.isEmpty()) {
            filters = String.format(" AND ec_client_search.phrase MATCH '%s'", filters);
        }
        if (!StringUtils.isBlank(mainCondition)) {
            mainCondition = " AND " + mainCondition;
        }

        if (!StringUtils.isBlank(detailsCondition)) {
            detailsCondition = " where " + detailsCondition;
        } else {
            detailsCondition = "";
        }
        return "select count(ec_client_search.object_id) from ec_client_search where ec_client_search.object_id IN (select object_id from ec_mother_details_search " + detailsCondition + ") " + mainCondition + filters;
    }

    public String[] mainColumns() {
        return new String[]{getDemographicTable() + "." + DBConstantsUtils.KeyUtils.RELATIONAL_ID, getDemographicTable() + "." + DBConstantsUtils.KeyUtils.LAST_INTERACTED_WITH,
                getDemographicTable() + "." + DBConstantsUtils.KeyUtils.BASE_ENTITY_ID, getDemographicTable() + "." + DBConstantsUtils.KeyUtils.FIRST_NAME,
                getDemographicTable() + "." + DBConstantsUtils.KeyUtils.LAST_NAME, getDemographicTable() + "." + DBConstantsUtils.KeyUtils.ANC_ID,
                getDemographicTable() + "." + DBConstantsUtils.KeyUtils.DOB, AncLibrary.getInstance().getRegisterRepository().getDetailsTable() + "." + DBConstantsUtils.KeyUtils.PHONE_NUMBER,
                AncLibrary.getInstance().getRegisterRepository().getDetailsTable() + "." + DBConstantsUtils.KeyUtils.ALT_NAME, getDemographicTable() + "." + DBConstantsUtils.KeyUtils.DATE_REMOVED,
                AncLibrary.getInstance().getRegisterRepository().getDetailsTable() + "." + DBConstantsUtils.KeyUtils.EDD, AncLibrary.getInstance().getRegisterRepository().getDetailsTable() + "." + DBConstantsUtils.KeyUtils.RED_FLAG_COUNT,
                AncLibrary.getInstance().getRegisterRepository().getDetailsTable() + "." + DBConstantsUtils.KeyUtils.YELLOW_FLAG_COUNT, AncLibrary.getInstance().getRegisterRepository().getDetailsTable() + "." + DBConstantsUtils.KeyUtils.CONTACT_STATUS,
                AncLibrary.getInstance().getRegisterRepository().getDetailsTable() + "." + DBConstantsUtils.KeyUtils.NEXT_CONTACT, AncLibrary.getInstance().getRegisterRepository().getDetailsTable() + "." + DBConstantsUtils.KeyUtils.NEXT_CONTACT_DATE,
                AncLibrary.getInstance().getRegisterRepository().getDetailsTable() + "." + DBConstantsUtils.KeyUtils.LAST_CONTACT_RECORD_DATE};
    }

    public String mainRegisterQuery() {
        SmartRegisterQueryBuilder queryBuilder = new SmartRegisterQueryBuilder();
        queryBuilder.SelectInitiateMainTable(getDemographicTable(), mainColumns());
        queryBuilder.customJoin(" join " + AncLibrary.getInstance().getRegisterRepository().getDetailsTable()
                + " on " + AncLibrary.getInstance().getRegisterRepository().getDemographicTable() + "." + DBConstantsUtils.KeyUtils.BASE_ENTITY_ID + "= " + AncLibrary.getInstance().getRegisterRepository().getDetailsTable() + "." + DBConstantsUtils.KeyUtils.BASE_ENTITY_ID + " ");
        return queryBuilder.getSelectquery();
    }

    public String getDetailsTable() {
        return DBConstantsUtils.RegisterTable.DETAILS;
    }

    public String getDemographicTable() {
        return DBConstantsUtils.RegisterTable.DEMOGRAPHIC;
    }
}
