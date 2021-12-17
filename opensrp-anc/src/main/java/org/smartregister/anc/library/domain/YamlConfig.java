package org.smartregister.anc.library.domain;

import java.util.List;

/**
 * Created by ndegwamartin on 04/12/2018.
 */
public class YamlConfig {

    private String group;
    private String subGroup;
    private List<YamlConfigItem> fields;
    private String testResults;
    private String propertiesFileName;

    public YamlConfig() {
    }

    public YamlConfig(String group, String subGroup, List<YamlConfigItem> fields, String testResults) {
        this.group = group;
        this.subGroup = subGroup;
        this.fields = fields;
        this.testResults = testResults;
    }

    public YamlConfig(String group, String subGroup, List<YamlConfigItem> fields, String testResults, String propertiesFileName) {
        this.group = group;
        this.subGroup = subGroup;
        this.fields = fields;
        this.testResults = testResults;
        this.propertiesFileName = propertiesFileName;
    }

    public String getSubGroup() {
        return subGroup;
    }

    public void setSubGroup(String sub_group) {
        this.subGroup = sub_group;
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
        return testResults;
    }

    public void setTestResults(String test_results) {
        this.testResults = test_results;
    }

    public String getPropertiesFileName() {
        return propertiesFileName;
    }

    public void setPropertiesFileName(String properties_file_name) {
        this.propertiesFileName = properties_file_name;
    }

    public static final class KeyUtils {
        public static final String GROUP = "group";
        public static final String FIELDS = "fields";
    }

}
