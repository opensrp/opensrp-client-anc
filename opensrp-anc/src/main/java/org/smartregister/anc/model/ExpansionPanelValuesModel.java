package org.smartregister.anc.model;

import com.vijay.jsonwizard.utils.SecondaryValueModel;

import org.json.JSONArray;
import org.json.JSONObject;

public class ExpansionPanelValuesModel extends SecondaryValueModel {
    private String label;

    public ExpansionPanelValuesModel(String key, String type, String label, JSONArray values, JSONObject openmrsAttributes,
                                     JSONArray valuesOpenMRSAttributes) {
        super(key, type, values, openmrsAttributes, valuesOpenMRSAttributes);
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
