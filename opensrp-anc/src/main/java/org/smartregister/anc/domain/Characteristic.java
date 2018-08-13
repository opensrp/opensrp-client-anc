package org.smartregister.anc.domain;

/**
 * Created by ndegwamartin on 13/08/2018.
 */
public class Characteristic {
    private String key;
    private boolean value;
    private String label;
    private String info;

    public Characteristic(String key, boolean value, String label, String info) {
        this.key = key;
        this.value = value;
        this.label = label;
        this.info = info;
    }

    public boolean getValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
