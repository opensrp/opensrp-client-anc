package org.smartregister.anc.domain;

import com.vijay.jsonwizard.domain.Form;

import java.io.Serializable;
import java.util.Map;

public class Contact extends Form implements Serializable {

    private int background;

    private int requiredFields;

    private String formName;

    private int contactNumber;

    private Map<String, String> globals;

    public int getBackground() {
        return background;
    }

    public void setBackground(int background) {
        this.background = background;
    }

    public int getRequiredFields() {
        return requiredFields;
    }

    public void setRequiredFields(int requiredFields) {
        this.requiredFields = requiredFields;
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public Map<String, String> getGlobals() {
        return globals;
    }

    public void setGlobals(Map<String, String> globals) {
        this.globals = globals;
    }

    public int getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(int contactNumber) {
        this.contactNumber = contactNumber;
    }
}
