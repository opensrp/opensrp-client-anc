package org.smartregister.anc.model;

import com.vijay.jsonwizard.utils.SecondaryValueModel;

import org.json.JSONArray;

public class AccordionValuesModel extends SecondaryValueModel {
    private String label;

    public AccordionValuesModel(String key, String type, String label, JSONArray values) {
        super(key, type, values);
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
