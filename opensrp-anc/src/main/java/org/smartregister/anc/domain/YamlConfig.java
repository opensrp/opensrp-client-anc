package org.smartregister.anc.domain;

import java.util.List;

/**
 * Created by ndegwamartin on 04/12/2018.
 */
public class YamlConfig {

    private String group;

    private List<YamlConfigItem> fields;

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

    public List<YamlConfigItem> getFields() {
        return fields;
    }

    public void setFields(List<YamlConfigItem> fields) {
        this.fields = fields;
    }
}
