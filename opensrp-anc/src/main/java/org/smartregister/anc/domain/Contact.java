package org.smartregister.anc.domain;

import com.vijay.jsonwizard.domain.Form;

import java.io.Serializable;

public class Contact extends Form implements Serializable {

    private int background;

    private int requiredFields;

    private String formName;

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
}
