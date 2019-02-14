package org.smartregister.anc.domain;

/**
 * Created by ndegwamartin on 04/12/2018.
 */
public class YamlConfigWrapper {

    private String group;
    private String subGroup;
    private YamlConfigItem yamlConfigItem;

    public YamlConfigWrapper(String group, String subGroup, YamlConfigItem yamlConfigItem){
        this.group = group;
        this.subGroup =subGroup;
        this.yamlConfigItem = yamlConfigItem;
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

}
