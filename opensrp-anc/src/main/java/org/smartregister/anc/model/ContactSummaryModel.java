package org.smartregister.anc.model;

import org.json.JSONObject;

import java.util.Map;

public class ContactSummaryModel extends BaseContactModel {

    private String contactName;
    private String contactDate;

    public ContactSummaryModel() {
    }

    public ContactSummaryModel(String contactName, String contactDate) {
        this.contactName = contactName;
        this.contactDate = contactDate;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactDate() {
        return contactDate;
    }

    public void setContactDate(String contactDate) {
        this.contactDate = contactDate;
    }

    @Override
    public String extractPatientName(Map<String, String> womanDetails) {
        return super.extractPatientName(womanDetails);
    }

    @Override
    public JSONObject getFormAsJson(String formName, String entityId, String currentLocationId) throws Exception {
        return super.getFormAsJson(formName, entityId, currentLocationId);
    }
}
