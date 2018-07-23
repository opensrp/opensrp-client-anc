package org.smartregister.anc.model;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.anc.contract.AdvancedSearchContract;
import org.smartregister.anc.util.DBConstants;

import java.util.HashMap;
import java.util.Map;

public class AdvancedSearchModel extends RegisterFramentModel implements AdvancedSearchContract.Model {

    @Override
    public Map<String, String> createEditMap(String firstName, String lastName, String ancId, String edd, String dob, String phoneNumber, String alternateContact) {
        Map<String, String> editMap = new HashMap<>();
        if (StringUtils.isNotBlank(firstName)) {
            editMap.put(DBConstants.KEY.FIRST_NAME, firstName);
        }
        if (StringUtils.isNotBlank(lastName)) {
            editMap.put(DBConstants.KEY.LAST_NAME, lastName);
        }
        if (StringUtils.isNotBlank(ancId)) {
            editMap.put(DBConstants.KEY.ANC_ID, ancId);
        }
        if (StringUtils.isNotBlank(edd)) {
            editMap.put(DBConstants.KEY.EDD, edd);
        }
        if (StringUtils.isNotBlank(dob)) {
            editMap.put(DBConstants.KEY.DOB, dob);
        }
        if (StringUtils.isNotBlank(phoneNumber)) {
            editMap.put(DBConstants.KEY.PHONE_NUMBER, phoneNumber);
        }
        if (StringUtils.isNotBlank(alternateContact)) {
            editMap.put(DBConstants.KEY.ALT_CONTACT_NAME, alternateContact);
        }
        return editMap;
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
                mainConditionString += " " + key + " Like '%" + value + "%'";
            } else {
                mainConditionString += " AND " + key + " Like '%" + value + "%'";
            }
        }

        return mainConditionString;

    }
}
