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
}
