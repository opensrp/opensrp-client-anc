package org.smartregister.anc.library.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AncMetadata {
    private ArrayList<String> locationLevels;
    private ArrayList<String> healthFacilityLevels;
    private List<String> fieldsWithLocationHierarchy;

    public AncMetadata() {
        locationLevels = AncLocationUtils.getLocationLevels();
        healthFacilityLevels = AncLocationUtils.getHealthFacilityLevels();
    }

    public ArrayList<String> getLocationLevels() {
        return locationLevels;
    }

    public void setLocationLevels(ArrayList<String> locationLevels) {
        this.locationLevels = locationLevels;
    }

    public ArrayList<String> getHealthFacilityLevels() {
        return healthFacilityLevels;
    }

    public void setHealthFacilityLevels(ArrayList<String> healthFacilityLevels) {
        this.healthFacilityLevels = healthFacilityLevels;
    }

    public List<String> getFieldsWithLocationHierarchy() {
        return fieldsWithLocationHierarchy;
    }

    public void setFieldsWithLocationHierarchy(List<String> fieldsWithLocationHierarchy) {
        this.fieldsWithLocationHierarchy = fieldsWithLocationHierarchy;
    }
}
