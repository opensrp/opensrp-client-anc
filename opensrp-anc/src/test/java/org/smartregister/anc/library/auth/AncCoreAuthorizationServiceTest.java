package org.smartregister.anc.library.auth;

import androidx.annotation.NonNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.anc.library.activity.BaseUnitTest;
import org.smartregister.p2p.authorizer.P2PAuthorizationService;
import org.smartregister.repository.AllSharedPreferences;

import java.util.HashMap;
import java.util.Map;

@RunWith(PowerMockRunner.class)
@PrepareForTest({CoreLibrary.class})
public class AncCoreAuthorizationServiceTest extends BaseUnitTest  {

    AncCoreAuthorizationService authorizationService;

    @Mock
    private CoreLibrary coreLibrary;

    @Before
    public void setUp() {
        authorizationService = new AncCoreAuthorizationService();
    }

    @Test
    public void tstAuthorizeConnection() {
        AncCoreAuthorizationService spyService = Mockito.spy(authorizationService);
        PowerMockito.mockStatic(CoreLibrary.class);

        Context context = Mockito.mock(Context.class);
        AllSharedPreferences allSharedPreferences = Mockito.mock(AllSharedPreferences.class);
        PowerMockito.when(CoreLibrary.getInstance()).thenReturn(coreLibrary);
        PowerMockito.when(coreLibrary.context()).thenReturn(context);
        PowerMockito.when(context.allSharedPreferences()).thenReturn(allSharedPreferences);
        PowerMockito.when(allSharedPreferences.fetchRegisteredANM()).thenReturn("");
        PowerMockito.when(allSharedPreferences.fetchDefaultTeamId(Mockito.anyString())).thenReturn("ca2d2f3f-573d-4b99-b060-b1ac1bad3104");
        PowerMockito.when(allSharedPreferences.fetchUserLocalityId(Mockito.anyString())).thenReturn("982eb3f3-b7e3-450f-a38e-d067f2345212");
        PowerMockito.doReturn("02ebbc84-5e29-4cd5-9b79-c594058923e9").when(spyService).getCountryId();

        P2PAuthorizationService.AuthorizationCallback callback = new P2PAuthorizationService.AuthorizationCallback() {
            @Override
            public void onConnectionAuthorized() {
                // DO nothing
            }
            @Override
            public void onConnectionAuthorizationRejected(@NonNull String s) {
                // DO nothing
            }
        };

        P2PAuthorizationService.AuthorizationCallback spyCallback = Mockito.spy(callback);

        Map<String, Object> peerDeviceMap = new HashMap<>();
        peerDeviceMap.put("COUNTRY_ID", "02ebbc84-5e29-4cd5-9b79-c594058923e9");
        peerDeviceMap.put("location-id", "982eb3f3-b7e3-450f-a38e-d067f2345212");
        peerDeviceMap.put("team-id", "ca2d2f3f-573d-4b99-b060-b1ac1bad3104");
        peerDeviceMap.put("peer-status", "receiver");

        Map<String, Object> authDetails = new HashMap<>();
        authDetails.put("peer-status", "sender");

        ReflectionHelpers.setField(spyService, "authorizationDetails", authDetails);

        spyService.authorizeConnection(peerDeviceMap, spyCallback);

        Mockito.verify(spyCallback, Mockito.times(1)).onConnectionAuthorized();

        Map<String, Object> authInvalidDetails = new HashMap<>();
        authInvalidDetails.put("peer-status", null);

        ReflectionHelpers.setField(spyService, "authorizationDetails", authInvalidDetails);

        spyService.authorizeConnection(peerDeviceMap, spyCallback);

        Mockito.verify(spyCallback, Mockito.times(1)).onConnectionAuthorizationRejected(Mockito.anyString());
    }



}
