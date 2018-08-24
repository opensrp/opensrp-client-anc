package org.smartregister.anc.domain;

public class Contact {

    private String name;
    private int background;
    private int requiredFields;

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
}
