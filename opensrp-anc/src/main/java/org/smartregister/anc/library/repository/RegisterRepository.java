package org.smartregister.anc.library.repository;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.anc.library.util.DBConstantsUtils;
import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;

public class RegisterRepository {

    public String getObjectIdsQuery(String mainCondition, String filters) {
        String strMainCondition = "";
        String strFilters = "";
        if (!filters.isEmpty()) {
            strFilters = String.format(" AND " + getDemographicTable() + ".phrase MATCH '*%s*'", filters);
        }
        if (StringUtils.isNotBlank(filters) && StringUtils.isBlank(mainCondition)) {
            strFilters = String.format(" where " + getDemographicTable() + ".phrase MATCH '*%s*'", filters);
        }

        if (!StringUtils.isBlank(mainCondition)) {
            strMainCondition = " where " + mainCondition;
        }

        return "select " + getDemographicTable() + "." + "object_id from " + CommonFtsObject.searchTableName(getDemographicTable()) + " " + getDemographicTable() + "  " +
                "join " + getDetailsTable() + " on " + getDemographicTable() + ".object_id =  " + getDetailsTable() + "." + "id " + strMainCondition + strFilters;
    }

    public String getCountExecuteQuery(String mainCondition, String filters) {
        String strMainCondition = "";
        String strFilters = "";
        if (!filters.isEmpty()) {
            strFilters = String.format(" AND " + CommonFtsObject.searchTableName(getDemographicTable()) + ".phrase MATCH '*%s*'", filters);
        }
        if (StringUtils.isNotBlank(filters) && StringUtils.isBlank(mainCondition)) {
            strFilters = String.format(" where " + CommonFtsObject.searchTableName(getDemographicTable()) + ".phrase MATCH '*%s*'", filters);
        }

        if (!StringUtils.isBlank(mainCondition)) {
            strMainCondition = " where " + mainCondition;
        }

        return "select count(" + getDemographicTable() + "." + "object_id) from " + CommonFtsObject.searchTableName(getDemographicTable()) + " " + getDemographicTable() + "  " +
                "join " + getDetailsTable() + " on " + getDemographicTable() + ".object_id =  " + getDetailsTable() + "." + "id " + strMainCondition + strFilters;
    }

    public String[] mainColumns() {
        return new String[]{getDemographicTable() + "." + DBConstantsUtils.KeyUtils.RELATIONAL_ID, getDemographicTable() + "." + DBConstantsUtils.KeyUtils.LAST_INTERACTED_WITH,
                getDemographicTable() + "." + DBConstantsUtils.KeyUtils.BASE_ENTITY_ID, getDemographicTable() + "." + DBConstantsUtils.KeyUtils.FIRST_NAME,
                getDemographicTable() + "." + DBConstantsUtils.KeyUtils.LAST_NAME, getDemographicTable() + "." + DBConstantsUtils.KeyUtils.ANC_ID,
                getDemographicTable() + "." + DBConstantsUtils.KeyUtils.DOB, getDetailsTable() + "." + DBConstantsUtils.KeyUtils.PHONE_NUMBER,
                getDetailsTable() + "." + DBConstantsUtils.KeyUtils.ALT_NAME, getDemographicTable() + "." + DBConstantsUtils.KeyUtils.DATE_REMOVED,
                getDetailsTable() + "." + DBConstantsUtils.KeyUtils.EDD, getDetailsTable() + "." + DBConstantsUtils.KeyUtils.RED_FLAG_COUNT,
                getDetailsTable() + "." + DBConstantsUtils.KeyUtils.YELLOW_FLAG_COUNT, getDetailsTable() + "." + DBConstantsUtils.KeyUtils.CONTACT_STATUS,
                getDetailsTable() + "." + DBConstantsUtils.KeyUtils.NEXT_CONTACT, getDetailsTable() + "." + DBConstantsUtils.KeyUtils.NEXT_CONTACT_DATE,
                getDetailsTable() + "." + DBConstantsUtils.KeyUtils.LAST_CONTACT_RECORD_DATE};
    }

    public String mainRegisterQuery() {
        SmartRegisterQueryBuilder queryBuilder = new SmartRegisterQueryBuilder();
        queryBuilder.SelectInitiateMainTable(getDemographicTable(), mainColumns());
        queryBuilder.customJoin(" join " + getDetailsTable()
                + " on " + getDemographicTable() + "." + DBConstantsUtils.KeyUtils.BASE_ENTITY_ID + "= " + getDetailsTable() + "." + DBConstantsUtils.KeyUtils.BASE_ENTITY_ID + " ");
        return queryBuilder.getSelectquery();
    }

    public String getDetailsTable() {
        return DBConstantsUtils.RegisterTable.DETAILS;
    }

    public String getDemographicTable() {
        return DBConstantsUtils.RegisterTable.DEMOGRAPHIC;
    }
}
