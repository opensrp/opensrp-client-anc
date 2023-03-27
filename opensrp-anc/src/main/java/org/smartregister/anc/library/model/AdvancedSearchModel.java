package org.smartregister.anc.library.model;

import android.content.Context;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.anc.library.R;
import org.smartregister.anc.library.contract.AdvancedSearchContract;
import org.smartregister.anc.library.util.DBConstantsUtils;

import java.util.LinkedHashMap;
import java.util.Map;

public class AdvancedSearchModel extends RegisterFragmentModel implements AdvancedSearchContract.Model {


    public static final String GLOBAL_FIRST_NAME = "firstName";
    public static final String GLOBAL_LAST_NAME = "lastName";
    public static final String GLOBAL_BIRTH_DATE = "birthdate";
    public static final String GLOBAL_ATTRIBUTE = "attribute";
    public static final String GLOBAL_IDENTIFIER = "identifier";
    public static final String ANC_ID = "ANC_ID";
    public static final String EDD_ATTR = "edd";
    public static final String PHONE_NUMBER = "phone_number";
    public static final String ALT_CONTACT_NAME = "alt_name";
    public static final String FIRST_NAME = "First name:";
    public static final String LAST_NAME = "Last name:";
    public static final String SEARCH_TERM_ANC_ID = "ANC ID:";
    public static final String EDD = "Edd:";
    public static final String DOB = "Dob:";
    public static final String MOBILE_PHONE_NUMBER = "Mobile phone number:";
    public static final String ALTERNATE_CONTACT_NAME = "Alternate contact name:";
    public static final String LIKE = "Like";
    public static final String AND = "AND";


    @Override
    public Map<String, String> createEditMap(String firstName, String lastName, String ancId, String edd, String dob,
                                             String phoneNumber, String alternateContact, boolean isLocal) {
        Map<String, String> editMap = new LinkedHashMap<>();
        addMapValues(firstName, isLocal, editMap, DBConstantsUtils.KeyUtils.FIRST_NAME, GLOBAL_FIRST_NAME);
        addMapValues(lastName, isLocal, editMap, DBConstantsUtils.KeyUtils.LAST_NAME, GLOBAL_LAST_NAME);
        addMapValuesCheckingLocals(ancId, isLocal, editMap, DBConstantsUtils.KeyUtils.ANC_ID, GLOBAL_IDENTIFIER, ANC_ID);
        addMapValuesCheckingLocals(edd, isLocal, editMap, DBConstantsUtils.KeyUtils.EDD, GLOBAL_ATTRIBUTE, EDD_ATTR);
        addMapValuesCheckingLocals(edd, isLocal, editMap, DBConstantsUtils.KeyUtils.EDD, GLOBAL_ATTRIBUTE, EDD_ATTR);
        addMapValues(dob, isLocal, editMap, DBConstantsUtils.KeyUtils.DOB, GLOBAL_BIRTH_DATE);
        addMapValues(phoneNumber, isLocal, editMap, DBConstantsUtils.KeyUtils.PHONE_NUMBER, PHONE_NUMBER);
        addMapValues(alternateContact, isLocal, editMap, DBConstantsUtils.KeyUtils.ALT_NAME, ALT_CONTACT_NAME);
        return editMap;
    }

    private void addMapValues(String field, boolean isLocal, Map<String, String> editMap, String key, String globalField) {
        if (StringUtils.isNotBlank(field)) {
            editMap.put(isLocal ? key : globalField, field);
        }
    }

    private void addMapValuesCheckingLocals(String field, boolean isLocal, Map<String, String> editMap, String key, String globalField, String keyToMapTo) {
        if (StringUtils.isNotBlank(field)) {
            editMap.put(isLocal ? key : globalField, isLocal ? field : keyToMapTo + ":" + field);
        }
    }

    @Override
    public String createSearchString(Context context, String firstName, String lastName, String ancId, String edd, String dob,
                                     String phoneNumber, String alternateContact) {

        String searchCriteria = "";

        if (StringUtils.isNotBlank(firstName)) {
            searchCriteria += " " + context.getString(R.string.first_name) + ": " + firstName + ";";
        }
        if (StringUtils.isNotBlank(lastName)) {
            searchCriteria += " " + context.getString(R.string.last_name) + ": " + lastName + ";";
        }
        if (StringUtils.isNotBlank(ancId)) {
            searchCriteria += " " + context.getString(R.string.anc_id) + ": " + ancId + ";";
        }
        if (StringUtils.isNotBlank(edd)) {
            searchCriteria += " " + context.getString(R.string.edd) + ": " + edd + ";";
        }
        if (StringUtils.isNotBlank(dob)) {
            searchCriteria += " " + context.getString(R.string.dob) + ": " + dob + ";";
        }
        if (StringUtils.isNotBlank(phoneNumber)) {
            searchCriteria += " " + context.getString(R.string.mobile_phone_number) + ": " + phoneNumber + ";";
        }
        if (StringUtils.isNotBlank(alternateContact)) {
            searchCriteria += " " + context.getString(R.string.alt_contact_name) + ": " + alternateContact + ";";
        }
        return removeLastSemiColon(searchCriteria);
    }

    @Override
    public String getMainConditionString(Map<String, String> editMap) {

        String mainConditionString = "";
        if (editMap == null || editMap.isEmpty()) {
            return mainConditionString;
        }

        for (Map.Entry<String, String> entry : editMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (StringUtils.isBlank(mainConditionString)) {
                mainConditionString += " " + key + " " + LIKE + " '%" + value + "%'";
            } else {
                mainConditionString += " " + AND + " " + key + " " + LIKE + " '%" + value + "%'";
            }
        }

        mainConditionString += " ";

        return mainConditionString;

    }

    private String removeLastSemiColon(String str) {
        if (StringUtils.isBlank(str)) {
            return str;
        }
        String s = str.trim();
        if (s.charAt(s.length() - 1) == ';') {
            return s.substring(0, s.length() - 1);
        }
        return s;
    }

}
