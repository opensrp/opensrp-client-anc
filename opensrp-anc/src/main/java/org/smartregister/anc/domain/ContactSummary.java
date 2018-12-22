package org.smartregister.anc.domain;

import java.util.List;

/**
 * Created by ndegwamartin on 04/12/2018.
 */
public class ContactSummary {

    private String group;

    private List<ContactSummaryItem> fields;

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public static final class KEY {
        public static final String GROUP = "group";
        public static final String FIELDS = "fields";
    }

    public List<ContactSummaryItem> getFields() {
        return fields;
    }

    public void setFields(List<ContactSummaryItem> fields) {
        this.fields = fields;
    }
}
