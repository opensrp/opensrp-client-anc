package org.smartregister.anc.library.domain;

/**
 * Created by ndegwamartin on 04/12/2018.
 */
public class YamlConfigWrapper {

    private String group;
    private String subGroup;
    private YamlConfigItem yamlConfigItem;
    private String testResults;

    public YamlConfigWrapper(String group, String subGroup, YamlConfigItem yamlConfigItem) {
        this.group = group;
        this.subGroup = subGroup;
        this.yamlConfigItem = yamlConfigItem;
    }

    public YamlConfigWrapper(String group, String subGroup, YamlConfigItem yamlConfigItem,
                             String testResults) {
        this.group = group;
        this.subGroup = subGroup;
        this.yamlConfigItem = yamlConfigItem;
        this.testResults = testResults;
    }

    public YamlConfigItem getYamlConfigItem() {
        return yamlConfigItem;
    }

    public void setYamlConfigItem(YamlConfigItem yamlConfigItem) {
        this.yamlConfigItem = yamlConfigItem;
    }

    public String getSubGroup() {
        return subGroup;
    }

    public void setSubGroup(String subGroup) {
        this.subGroup = subGroup;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getTestResults() {
        return testResults;
    }

    public void setTestResults(String testResults) {
        this.testResults = testResults;
    }
}
