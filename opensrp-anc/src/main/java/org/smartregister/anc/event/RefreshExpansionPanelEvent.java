package org.smartregister.anc.event;

import android.widget.LinearLayout;

import org.json.JSONArray;

public class RefreshExpansionPanelEvent extends BaseEvent {
    private JSONArray values;
    private LinearLayout linearLayout;
    public RefreshExpansionPanelEvent(JSONArray values, LinearLayout linearLayout){
        this.values= values;
        this.linearLayout= linearLayout;
    }

    public JSONArray getValues() {
        return values;
    }

    public LinearLayout getLinearLayout() {
        return linearLayout;
    }
}
