package org.smartregister.anc.library.auth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.smartregister.AllConstants;
import org.smartregister.CoreLibrary;
import org.smartregister.anc.library.constants.CoreP2pConstants;
import org.smartregister.domain.jsonmapping.Location;
import org.smartregister.domain.jsonmapping.util.LocationTree;
import org.smartregister.domain.jsonmapping.util.TreeNode;
import org.smartregister.p2p.authorizer.P2PAuthorizationService;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.util.AssetHandler;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;


public class AncCoreAuthorizationService implements P2PAuthorizationService {

    private final Map<String, Object> authorizationDetails = new HashMap<>();

    @Override
    public void authorizeConnection(@NonNull final Map<String, Object> peerDeviceMap, @NonNull final P2PAuthorizationService.AuthorizationCallback authorizationCallback) {
        getAuthorizationDetails(map -> {
            Object peerDeviceLocationId = peerDeviceMap.get(CoreP2pConstants.PeerToPeerKeyConstants.LOCATION_ID);
            Object myLocationId = authorizationDetails.get(CoreP2pConstants.PeerToPeerKeyConstants.LOCATION_ID);
            Object myPeerStatus = authorizationDetails.get(org.smartregister.p2p.util.Constants.AuthorizationKeys.PEER_STATUS);
            Object myCountryId = authorizationDetails.get(CoreP2pConstants.PeerToPeerUtil.COUNTRY_ID);

            if (peerDeviceLocationId instanceof String && myLocationId instanceof String && myPeerStatus instanceof String && myCountryId instanceof String) {

                if (isKnownLocation((String) myCountryId)) {
                    authorizationCallback.onConnectionAuthorized();
                } else {
                    rejectConnection(authorizationCallback);
                }
            } else {
                rejectConnection(authorizationCallback);
            }
        });
    }

    @Nullable
    public LinkedHashMap<String, TreeNode<String, Location>> getLocationTreeMap() {
        String locationData = CoreLibrary.getInstance().context().anmLocationController().get();
        LocationTree locationTree = AssetHandler.jsonStringToJava(locationData, LocationTree.class);
        if (locationTree != null) {
            return locationTree.getLocationsHierarchy();
        }

        return null;
    }

    @NonNull
    protected String getCountryId() {
        LinkedHashMap<String, TreeNode<String, Location>> locationHierarchyMap = getLocationTreeMap();
        if (locationHierarchyMap == null) {
            throw new IllegalStateException("Missing Location Hierarchy");
        }
        return Objects.requireNonNull(locationHierarchyMap.keySet().toArray())[0].toString();
    }

    private boolean isKnownLocation(@NonNull String countryId) {
        return countryId.equalsIgnoreCase(getCountryId());
    }

    private void rejectConnection(@NonNull AuthorizationCallback authorizationCallback) {
        authorizationCallback.onConnectionAuthorizationRejected("Incorrect authorization details provided");
    }

    @Override
    public void getAuthorizationDetails(@NonNull OnAuthorizationDetailsProvidedCallback onAuthorizationDetailsProvidedCallback) {
        // Load the preferences here
        AllSharedPreferences allSharedPreferences = CoreLibrary.getInstance().context().allSharedPreferences();
        authorizationDetails.put(AllConstants.PeerToPeer.KEY_TEAM_ID, allSharedPreferences.fetchDefaultTeamId(allSharedPreferences.fetchRegisteredANM()));
        authorizationDetails.put(CoreP2pConstants.PeerToPeerKeyConstants.LOCATION_ID, allSharedPreferences.fetchUserLocalityId(allSharedPreferences.fetchRegisteredANM()));
        authorizationDetails.put(CoreP2pConstants.PeerToPeerUtil.COUNTRY_ID, getCountryId());
        onAuthorizationDetailsProvidedCallback.onAuthorizationDetailsProvided(authorizationDetails);
    }
}