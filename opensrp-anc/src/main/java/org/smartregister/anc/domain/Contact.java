package org.smartregister.anc.domain;

import java.io.Serializable;
import java.util.Map;

public class Contact implements Serializable {

    private String name;
    private int background;
    private int actionBarBackground;
    private int navigationBackground;
    private int requiredFields;
    private String formName;
    private int contactNumber;
    private Map<String,String> globals;

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

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getBackground() {
        return background;
    }

    public void setBackground(int background) {
        this.background = background;
    }

    public int getActionBarBackground() {
        return actionBarBackground;
    }

    public void setActionBarBackground(int actionBarBackground) {
        this.actionBarBackground = actionBarBackground;
    }

    public int getNavigationBackground() {
        return navigationBackground;
    }

    public void setNavigationBackground(int navigationBackground) {
        this.navigationBackground = navigationBackground;
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
}
