package org.smartregister.anc.library.domain;

import java.util.List;

/**
 * Created by ndegwamartin on 04/12/2018.
 */
public class YamlConfig {

    private String group;
    private String sub_group;
    private List<YamlConfigItem> fields;
    private String test_results;
    private String properties_file_name;

    public YamlConfig() {
    }

    public YamlConfig(String group, String sub_group, List<YamlConfigItem> fields, String test_results) {
        this.group = group;
        this.sub_group = sub_group;
        this.fields = fields;
        this.test_results = test_results;
    }

    public YamlConfig(String group, String sub_group, List<YamlConfigItem> fields, String test_results, String properties_file_name) {
        this.group = group;
        this.sub_group = sub_group;
        this.fields = fields;
        this.test_results = test_results;
        this.properties_file_name = properties_file_name;
    }

    public String getSubGroup() {
        return sub_group;
    }

    public void setSubGroup(String sub_group) {
        this.sub_group = sub_group;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public List<YamlConfigItem> getFields() {
        return fields;
    }

    public void setFields(List<YamlConfigItem> fields) {
        this.fields = fields;
    }

    public String getTestResults() {
        return test_results;
    }

    public void setTestResults(String test_results) {
        this.test_results = test_results;
    }

    public String getPropertiesFileName() {
        return properties_file_name;
    }

    public void setPropertiesFileName(String properties_file_name) {
        this.properties_file_name = properties_file_name;
    }

    public static final class KeyUtils {
        public static final String GROUP = "group";
        public static final String FIELDS = "fields";
    }

}
