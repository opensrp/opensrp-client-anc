package org.smartregister.anc.library.sync;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.AllConstants;
import org.smartregister.CoreLibrary;
import org.smartregister.anc.library.constants.CoreP2pConstants;
import org.smartregister.domain.jsonmapping.Location;
import org.smartregister.domain.jsonmapping.util.LocationTree;
import org.smartregister.domain.jsonmapping.util.TreeNode;
import org.smartregister.p2p.authorizer.P2PAuthorizationService;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.util.AssetHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


public class AncCoreAuthorizationService implements P2PAuthorizationService {

    private Map<String, Object> authorizationDetails = new HashMap<>();

    private boolean checkTeamId;

    public AncCoreAuthorizationService(boolean checkTeamId) {
        this.checkTeamId = checkTeamId;
    }

    public AncCoreAuthorizationService() {
        this.checkTeamId = true;
    }

    @Override
    public void authorizeConnection(@NonNull final Map<String, Object> peerDeviceMap, @NonNull final AuthorizationCallback authorizationCallback) {
        getAuthorizationDetails(map -> {
            Object peerDeviceTeamId = peerDeviceMap.get(AllConstants.PeerToPeer.KEY_TEAM_ID);
            if (!checkTeamId || peerDeviceTeamId instanceof String
                    && peerDeviceTeamId.equals(map.get(AllConstants.PeerToPeer.KEY_TEAM_ID))) {
                Object peerDeviceLocationId = peerDeviceMap.get(CoreP2pConstants.PEER_TO_PEER.LOCATION_ID);
                Object myLocationId = authorizationDetails.get(CoreP2pConstants.PEER_TO_PEER.LOCATION_ID);
                Object myPeerStatus = authorizationDetails.get(org.smartregister.p2p.util.Constants.AuthorizationKeys.PEER_STATUS);

                if (peerDeviceLocationId instanceof String && myLocationId instanceof String && myPeerStatus instanceof String) {

                    if (org.smartregister.p2p.util.Constants.PeerStatus.SENDER.equals(myPeerStatus)) {
                        // If this device is a sender
                        // Make sure that
                        if (isLocationEncompassing((String) peerDeviceLocationId, (String) myLocationId)) {
                            authorizationCallback.onConnectionAuthorized();
                        } else {
                            rejectConnection(authorizationCallback);
                        }
                    } else {
                        // If this device is a receiver
                        if (isLocationEncompassing((String) myLocationId, (String) peerDeviceLocationId)) {
                            authorizationCallback.onConnectionAuthorized();
                        } else {
                            rejectConnection(authorizationCallback);
                        }
                    }
                } else {
                    rejectConnection(authorizationCallback);
                }
            } else {
                rejectConnection(authorizationCallback);
            }
        });
    }

    private boolean isLocationEncompassing(@NonNull String highLocationId, @NonNull String lowerLocationId) {
        //TODO: This method produces a false positive somewhere, I just forgot. :joy: happy-hunting
        LinkedHashMap<String, TreeNode<String, Location>> locationHierarchyMap = retrieveLocationHierarchyMap();

        if (locationHierarchyMap != null) {
            for (String locationId : locationHierarchyMap.keySet()) {
                // If the lower location is higher than the expected high location
                boolean foundHighLocation = false;

                if (locationId.equals(lowerLocationId)) {
                    return false;
                } else if (locationId.equals(highLocationId)) {
                    foundHighLocation = true;
                }

                // Search for the child inside here
                TreeNode<String, Location> highLocationNode = locationHierarchyMap.get(locationId);
                if (highLocationNode != null && highLocationNode.getChildren() != null) {
                    ArrayList<TreeNode<String, Location>> locationQueue = new ArrayList<>(highLocationNode.getChildren().values());

                    for (int i = 0; i < locationQueue.size(); i++) {
                        TreeNode<String, Location> currentNode = locationQueue.get(i);
                        if (!foundHighLocation && currentNode.getId().equals(highLocationId)) {
                            foundHighLocation = true;
                        } else if (currentNode.getId().equals(lowerLocationId)) {
                            // If we had already found the high location, then this is TRUE
                            // Else this is FALSE, because the lower location is higher than the expected-high-location
                            return foundHighLocation;
                        }

                        if (currentNode.getChildren() != null) {
                            locationQueue.addAll(currentNode.getChildren().values());
                        }
                    }
                }
            }
        }

        return false;
    }

    private void rejectConnection(@NonNull AuthorizationCallback authorizationCallback) {
        authorizationCallback.onConnectionAuthorizationRejected("Incorrect authorization details provided");
    }

    @Nullable
    public LinkedHashMap<String, TreeNode<String, Location>> retrieveLocationHierarchyMap() {
        String locationData = CoreLibrary.getInstance().context().anmLocationController().get();
        LocationTree locationTree = AssetHandler.jsonStringToJava(locationData, LocationTree.class);
        if (locationTree != null) {
            return locationTree.getLocationsHierarchy();
        }

        return null;
    }

    @Override
    public void getAuthorizationDetails(@NonNull OnAuthorizationDetailsProvidedCallback onAuthorizationDetailsProvidedCallback) {
        // Load the preferences here
        AllSharedPreferences allSharedPreferences = CoreLibrary.getInstance().context().allSharedPreferences();

        authorizationDetails.put(AllConstants.PeerToPeer.KEY_TEAM_ID, allSharedPreferences.fetchDefaultTeamId(allSharedPreferences.fetchRegisteredANM()));
        String locationId = allSharedPreferences.fetchDefaultLocalityId(allSharedPreferences.fetchRegisteredANM());
        if(StringUtils.isBlank(locationId)) locationId = allSharedPreferences.fetchUserLocalityId(allSharedPreferences.fetchRegisteredANM());
        authorizationDetails.put(CoreP2pConstants.PEER_TO_PEER.LOCATION_ID, locationId);

        onAuthorizationDetailsProvidedCallback.onAuthorizationDetailsProvided(authorizationDetails);
    }
}