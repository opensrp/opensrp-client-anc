package org.smartregister.anc.domain;

import java.util.List;

/**
 * Created by ndegwamartin on 04/12/2018.
 */
public class ContactSummary {
    private String group;

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }

    private List<String> fields;
}
