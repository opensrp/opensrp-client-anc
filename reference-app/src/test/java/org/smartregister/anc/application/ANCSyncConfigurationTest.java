package org.smartregister.anc.application;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.smartregister.Context;
import org.smartregister.SyncFilter;
import org.smartregister.anc.BaseUnitTest;
import org.smartregister.anc.BuildConfig;
import org.smartregister.anc.library.AncLibrary;
import org.smartregister.anc.library.util.Utils;
import org.smartregister.repository.AllSettings;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.service.UserService;


@RunWith(PowerMockRunner.class)
@PrepareForTest({Utils.class, AncLibrary.class})
public class ANCSyncConfigurationTest extends BaseUnitTest {
    @Mock
    AllSharedPreferences allSharedPreferences;
    @Mock
    AncLibrary ancLibrary;
    @Mock
    Context context;
    @Mock
    UserService userService;
    AncSyncConfiguration configuration = new AncSyncConfiguration();

    @Before
    public void setup() throws Exception {
        PowerMockito.mockStatic(AncLibrary.class);
        PowerMockito.when(AncLibrary.getInstance()).thenReturn(ancLibrary);
        PowerMockito.when(ancLibrary.getContext()).thenReturn(context);
        PowerMockito.when(context, "userService").thenReturn(userService);
        PowerMockito.when(userService.getAllSharedPreferences()).thenReturn(allSharedPreferences);

    }

    @Test
    public void testGetFilterParamTest()
    {
        SyncFilter filter = configuration.getEncryptionParam();
        Assert.assertEquals(filter,SyncFilter.TEAM);
        if(BuildConfig.SYNC_TYPE.equals(SyncFilter.LOCATION_ID.value()))
            Assert.assertEquals(filter, SyncFilter.LOCATION);
        else if(BuildConfig.SYNC_TYPE.equals(SyncFilter.TEAM_ID.value()))
            Assert.assertEquals(filter,SyncFilter.TEAM);
        else
            Assert.assertEquals(filter,SyncFilter.PROVIDER);
    }

    @Test
    public void getSyncFilterValueTest()
    {
        String defaultLocID = "defaultLocalityId";
        String testUser = "testUser";
        String defaultTeamId = "defaultTeamId";
        String anm = "ANM";
        PowerMockito.when(allSharedPreferences.getANMPreferredName(ArgumentMatchers.anyString())).thenReturn(testUser);
        PowerMockito.when(allSharedPreferences.fetchDefaultLocalityId(ArgumentMatchers.anyString())).thenReturn(defaultLocID);
        PowerMockito.when(allSharedPreferences.fetchDefaultTeamId(ArgumentMatchers.anyString())).thenReturn("defaultTeamId");
        PowerMockito.when(allSharedPreferences.fetchRegisteredANM()).thenReturn(anm);
        String  syncValue = configuration.getSyncFilterValue();

        if(BuildConfig.SYNC_TYPE.equals(SyncFilter.LOCATION_ID.value()))
           Assert.assertEquals(syncValue, defaultLocID);
        else if(BuildConfig.SYNC_TYPE.equals(SyncFilter.TEAM_ID.value()))
           Assert.assertEquals(syncValue,defaultTeamId);
        else
            Assert.assertEquals(syncValue,anm);


    }

    @Test
    public void getLocationsStringShouldAddChildLocations() {
        String hierarchyTree = "{\"locationsHierarchy\":{\"map\":{\"ac7ba751-35e8-4b46-9e53-3cbaad193697\":{\"children\":{\"a899256d-2751-4dfb-b3c2-44b15c2081b2\":{\"children\":{\"b5572cee-399b-4329-8980-28b6d1ecd352\":{\"children\":{\"952953bb-efc6-4bde-9ce3-fbefa536b590\":{\"children\":{\"2089367a-2254-4586-b990-580f14d09479\":{\"id\":\"2089367a-2254-4586-b990-580f14d09479\",\"label\":\"Desa Mawar 2\",\"node\":{\"attributes\":{\"geographicLevel\":4.0},\"locationId\":\"2089367a-2254-4586-b990-580f14d09479\",\"name\":\"Desa Mawar 2\",\"parentLocation\":{\"locationId\":\"952953bb-efc6-4bde-9ce3-fbefa536b590\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Desa\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"952953bb-efc6-4bde-9ce3-fbefa536b590\"},\"51baf329-6180-48bf-946a-e2712d1cb45e\":{\"id\":\"51baf329-6180-48bf-946a-e2712d1cb45e\",\"label\":\"Desa Melati 2\",\"node\":{\"attributes\":{\"geographicLevel\":4.0},\"locationId\":\"51baf329-6180-48bf-946a-e2712d1cb45e\",\"name\":\"Desa Melati 2\",\"parentLocation\":{\"locationId\":\"952953bb-efc6-4bde-9ce3-fbefa536b590\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Desa\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"952953bb-efc6-4bde-9ce3-fbefa536b590\"}},\"id\":\"952953bb-efc6-4bde-9ce3-fbefa536b590\",\"label\":\"Puskesmas Bunga 2\",\"node\":{\"attributes\":{\"geographicLevel\":3.0},\"locationId\":\"952953bb-efc6-4bde-9ce3-fbefa536b590\",\"name\":\"Puskesmas Bunga 2\",\"parentLocation\":{\"locationId\":\"b5572cee-399b-4329-8980-28b6d1ecd352\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Health Facility\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"b5572cee-399b-4329-8980-28b6d1ecd352\"}},\"id\":\"b5572cee-399b-4329-8980-28b6d1ecd352\",\"label\":\"Lombok Barat\",\"node\":{\"attributes\":{\"geographicLevel\":2.0},\"locationId\":\"b5572cee-399b-4329-8980-28b6d1ecd352\",\"name\":\"Lombok Barat\",\"parentLocation\":{\"locationId\":\"a899256d-2751-4dfb-b3c2-44b15c2081b2\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Kabupaten\",\"Facility\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"a899256d-2751-4dfb-b3c2-44b15c2081b2\"}},\"id\":\"a899256d-2751-4dfb-b3c2-44b15c2081b2\",\"label\":\"NTB\",\"node\":{\"attributes\":{\"geographicLevel\":1.0},\"locationId\":\"a899256d-2751-4dfb-b3c2-44b15c2081b2\",\"name\":\"NTB\",\"parentLocation\":{\"locationId\":\"ac7ba751-35e8-4b46-9e53-3cbaad193697\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Provinsi\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"ac7ba751-35e8-4b46-9e53-3cbaad193697\"}},\"id\":\"ac7ba751-35e8-4b46-9e53-3cbaad193697\",\"label\":\"Indonesia\",\"node\":{\"attributes\":{\"geographicLevel\":0.0},\"locationId\":\"ac7ba751-35e8-4b46-9e53-3cbaad193697\",\"name\":\"Indonesia\",\"tags\":[\"Negara\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"}}},\"parentChildren\":{\"952953bb-efc6-4bde-9ce3-fbefa536b590\":[\"2089367a-2254-4586-b990-580f14d09479\",\"51baf329-6180-48bf-946a-e2712d1cb45e\"],\"b5572cee-399b-4329-8980-28b6d1ecd352\":[\"952953bb-efc6-4bde-9ce3-fbefa536b590\"],\"a899256d-2751-4dfb-b3c2-44b15c2081b2\":[\"b5572cee-399b-4329-8980-28b6d1ecd352\"],\"ac7ba751-35e8-4b46-9e53-3cbaad193697\":[\"a899256d-2751-4dfb-b3c2-44b15c2081b2\"]}}}";

        AllSettings allSettings = PowerMockito.mock(AllSettings.class);
        PowerMockito.when(allSettings.fetchANMLocation()).thenReturn(hierarchyTree);
        PowerMockito.when(context.allSettings()).thenReturn(allSettings);

        String locations = configuration.getLocationsString("952953bb-efc6-4bde-9ce3-fbefa536b590");

        Assert.assertEquals("952953bb-efc6-4bde-9ce3-fbefa536b590,2089367a-2254-4586-b990-580f14d09479,51baf329-6180-48bf-946a-e2712d1cb45e", locations);
    }
}
