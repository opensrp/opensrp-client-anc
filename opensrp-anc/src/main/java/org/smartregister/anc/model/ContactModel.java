package org.smartregister.anc.model;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.anc.contract.ContactContract;
import org.smartregister.anc.util.DBConstants;

import java.util.Map;

public class ContactModel implements ContactContract.Model {

    @Override
    public String extractPatientName(Map<String, String> womanDetails) {
        String firstName = extractValue(womanDetails, DBConstants.KEY.FIRST_NAME);
        String lastName = extractValue(womanDetails, DBConstants.KEY.LAST_NAME);

        if (StringUtils.isBlank(firstName) && StringUtils.isBlank(lastName)) {
            return "";
        } else if (StringUtils.isBlank(firstName)) {
            return lastName.trim();
        } else if (StringUtils.isBlank(lastName)) {
            return firstName.trim();
        } else {
            return firstName.trim() + " " + lastName.trim();
        }

    }


    private String extractValue(Map<String, String> details, String key) {
        if (details == null || details.isEmpty() || StringUtils.isBlank(key)) {
            return "";
        }

        return details.get(key);
    }
}
