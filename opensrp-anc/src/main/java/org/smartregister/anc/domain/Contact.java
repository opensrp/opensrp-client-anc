package org.smartregister.anc.domain;

public class Contact {

    private String name;
    private int background;
    private int requiredFields;
    private String formName;

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
