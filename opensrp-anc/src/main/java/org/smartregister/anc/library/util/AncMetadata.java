package org.smartregister.anc.library.util;

import org.smartregister.anc.library.BuildConfig;
import org.smartregister.anc.library.interactor.ClientTransferProcessor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Specifies additional library independent configurations
 */
public class AncMetadata {
    private ArrayList<String> locationLevels;
    private ArrayList<String> healthFacilityLevels;
    private Set<String> fieldsWithLocationHierarchy;
    private HashMap<String, ClientTransferProcessor> transferProcessorHashMap = new HashMap<>();

    public AncMetadata() {
        locationLevels = new ArrayList<>(Arrays.asList(BuildConfig.LOCATION_LEVELS));
        healthFacilityLevels = new ArrayList<>(Arrays.asList(BuildConfig.HEALTH_FACILITY_LEVELS));
    }

    /**
     * Provides the location levels defined in build gradle
     *
     * @return {@link ArrayList}
     */
    public ArrayList<String> getLocationLevels() {
        return locationLevels;
    }

    public void setLocationLevels(ArrayList<String> locationLevels) {
        this.locationLevels = locationLevels;
    }

    /**
     * Provides the health facility levels defined in build gradle
     *
     * @return {@link ArrayList}
     */
    public ArrayList<String> getHealthFacilityLevels() {
        return healthFacilityLevels;
    }

    public void setHealthFacilityLevels(ArrayList<String> healthFacilityLevels) {
        this.healthFacilityLevels = healthFacilityLevels;
    }

    /**
     * Used to get fields with tree attribute i.e those that are used as location picker
     *
     * @return {@link List}
     */
    public Set<String> getFieldsWithLocationHierarchy() {
        return fieldsWithLocationHierarchy;
    }

    public void setFieldsWithLocationHierarchy(Set<String> fieldsWithLocationHierarchy) {
        this.fieldsWithLocationHierarchy = fieldsWithLocationHierarchy;
    }

    public ClientTransferProcessor getTransferProcessorByEventType(String eventType) {
        return transferProcessorHashMap.get(eventType);
    }

    public void addTransferProcessorToHashMap(String evenType, ClientTransferProcessor clientTransferProcessor) {
        this.transferProcessorHashMap.put(evenType, clientTransferProcessor);
    }

    public HashMap<String, ClientTransferProcessor> getTransferProcessorHashMap() {
        return transferProcessorHashMap;
    }
}
