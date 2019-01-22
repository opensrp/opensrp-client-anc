package org.smartregister.anc.model;

import com.vijay.jsonwizard.utils.SecondaryValueModel;

import org.json.JSONArray;

public class ExpansionPanelValuesModel extends SecondaryValueModel {
    private String label;

    public ExpansionPanelValuesModel(String key, String type, String label, JSONArray values) {
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
