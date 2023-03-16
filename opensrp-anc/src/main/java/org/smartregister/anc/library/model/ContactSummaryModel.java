package org.smartregister.anc.library.model;

import androidx.annotation.Nullable;

import org.json.JSONObject;

import java.util.Date;
import java.util.Map;

public class ContactSummaryModel extends BaseContactModel {

    private String contactName;
    private String contactDate;
    private Date localDate;
    private String contactWeeks;

    public ContactSummaryModel() {
    }

    public ContactSummaryModel(String contactName, String contactDate) {
        this.contactName = contactName;
        this.contactDate = contactDate;
    }

    public ContactSummaryModel(String contactName, String contactDate, Date localDate, String contactWeeks) {
        this.contactName = contactName;
        this.contactDate = contactDate;
        this.localDate = localDate;
        this.contactWeeks = contactWeeks;
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

    public String getContactWeeks() {
        return contactWeeks;
    }

    public void setContactWeeks(String contactWeeks) {
        this.contactWeeks = contactWeeks;
    }

    public Date getLocalDate() {
        return localDate;
    }

    public void setLocalDate(Date localDate) {
        this.localDate = localDate;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null) {
            return false;
        }
        String key = ((ContactSummaryModel) obj).getContactName();
        return this.getContactName().equals(key);
    }
}
