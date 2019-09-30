package org.smartregister.anc.library.model;

import org.json.JSONObject;
import org.smartregister.anc.library.contract.ContactContract;

import java.util.Map;

public class ContactModel extends BaseContactModel implements ContactContract.Model {

    @Override
    public String extractPatientName(Map<String, String> womanDetails) {
        return super.extractPatientName(womanDetails);

    }

    @Override
    public JSONObject getFormAsJson(String formName, String entityId, String currentLocationId) throws Exception {
        return super.getFormAsJson(formName, entityId, currentLocationId);
    }

}
