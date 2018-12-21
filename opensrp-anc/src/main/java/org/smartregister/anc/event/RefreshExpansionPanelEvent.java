package org.smartregister.anc.event;

import org.json.JSONArray;

public class RefreshExpansionPanelEvent extends BaseEvent {
    private JSONArray values;
    public RefreshExpansionPanelEvent(JSONArray values){
        this.values= values;
    }

    public JSONArray getValues() {
        return values;
    }
}
