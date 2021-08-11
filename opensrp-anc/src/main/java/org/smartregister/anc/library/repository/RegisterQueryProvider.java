package org.smartregister.anc.library.repository;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.anc.library.util.DBConstantsUtils;
import org.smartregister.commonregistry.CommonFtsObject;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;

public class RegisterQueryProvider {

    public String getObjectIdsQuery(String mainCondition, String filters) {

        String strMainCondition = getMainCondition(mainCondition);

        String strFilters = getFilter(filters);

        if (StringUtils.isNotBlank(strFilters) && StringUtils.isBlank(strMainCondition)) {
            strFilters = String.format(" where " + getDemographicTable() + "." + CommonFtsObject.phraseColumn + " MATCH '*%s*'", filters);
        }

        return "select " + getDemographicTable() + "." + CommonFtsObject.idColumn + " from " + CommonFtsObject.searchTableName(getDemographicTable()) + " " + getDemographicTable() + "  " +
                "join " + getDetailsTable() + " on " + getDemographicTable() + "." + CommonFtsObject.idColumn + " =  " + getDetailsTable() + "." + "id " + strMainCondition + strFilters;
    }

    private String getMainCondition(String mainCondition) {
        if (StringUtils.isNotBlank(mainCondition)) {
            return " where " + mainCondition;
        }
        return "";
    }

    private String getFilter(String filters) {

        if (StringUtils.isNotBlank(filters)) {
            return String.format(" AND " + getDemographicTable() + "." + CommonFtsObject.phraseColumn + " MATCH '*%s*'", filters);
        }
        return "";
    }

    public String getDemographicTable() {
        return DBConstantsUtils.RegisterTable.DEMOGRAPHIC;
    }

    public String getDetailsTable() {
        return DBConstantsUtils.RegisterTable.DETAILS;
    }


    public String getCountExecuteQuery(String mainCondition, String filters) {

        String strFilters = getFilter(filters);

        if (StringUtils.isNotBlank(filters) && StringUtils.isBlank(mainCondition)) {
            strFilters = String.format(" where " + CommonFtsObject.searchTableName(getDemographicTable()) + "." + CommonFtsObject.phraseColumn + " MATCH '*%s*'", filters);
        }

        String strMainCondition = getMainCondition(mainCondition);

        return "select count(" + getDemographicTable() + "." + CommonFtsObject.idColumn + ") from " + CommonFtsObject.searchTableName(getDemographicTable()) + " " + getDemographicTable() + "  " +
                "join " + getDetailsTable() + " on " + getDemographicTable() + "." + CommonFtsObject.idColumn + " =  " + getDetailsTable() + "." + "id " + strMainCondition + strFilters;
    }

    public String mainRegisterQuery() {
        SmartRegisterQueryBuilder queryBuilder = new SmartRegisterQueryBuilder();
        queryBuilder.selectInitiateMainTable(getDemographicTable(), mainColumns());
        queryBuilder.customJoin(" join " + getDetailsTable()
                + " on " + getDemographicTable() + "." + DBConstantsUtils.KeyUtils.BASE_ENTITY_ID + "= " + getDetailsTable() + "." + DBConstantsUtils.KeyUtils.BASE_ENTITY_ID + " ");
        return queryBuilder.getSelectquery();
    }

    public String[] mainColumns() {
        return new String[]{DBConstantsUtils.KeyUtils.FIRST_NAME, DBConstantsUtils.KeyUtils.LAST_NAME, DBConstantsUtils.KeyUtils.DOB,
                DBConstantsUtils.KeyUtils.DOB_UNKNOWN, getDetailsTable() + "." + DBConstantsUtils.KeyUtils.PHONE_NUMBER, getDetailsTable() + "." + DBConstantsUtils.KeyUtils.ALT_NAME,
                getDetailsTable() + "." + DBConstantsUtils.KeyUtils.ALT_PHONE_NUMBER, getDemographicTable() + "." + DBConstantsUtils.KeyUtils.BASE_ENTITY_ID,
                getDemographicTable() + "." + DBConstantsUtils.KeyUtils.BASE_ENTITY_ID + " as " + DBConstantsUtils.KeyUtils.ID_LOWER_CASE, DBConstantsUtils.KeyUtils.ANC_ID,
                getDetailsTable() + "." + DBConstantsUtils.KeyUtils.REMINDERS, DBConstantsUtils.KeyUtils.HOME_ADDRESS, getDetailsTable() + "." + DBConstantsUtils.KeyUtils.EDD,
                getDetailsTable() + "." + DBConstantsUtils.KeyUtils.CONTACT_STATUS, getDetailsTable() + "." + DBConstantsUtils.KeyUtils.PREVIOUS_CONTACT_STATUS,
                getDetailsTable() + "." + DBConstantsUtils.KeyUtils.NEXT_CONTACT, getDetailsTable() + "." + DBConstantsUtils.KeyUtils.NEXT_CONTACT_DATE,
                getDetailsTable() + "." + DBConstantsUtils.KeyUtils.VISIT_START_DATE, getDetailsTable() + "." + DBConstantsUtils.KeyUtils.RED_FLAG_COUNT,
                getDetailsTable() + "." + DBConstantsUtils.KeyUtils.YELLOW_FLAG_COUNT, getDetailsTable() + "." + DBConstantsUtils.KeyUtils.LAST_CONTACT_RECORD_DATE,
                getDetailsTable() + "." + DBConstantsUtils.KeyUtils.COHABITANTS, getDemographicTable() + "." + DBConstantsUtils.KeyUtils.RELATIONAL_ID};
    }
}