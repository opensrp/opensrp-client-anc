package org.smartregister.anc.library.util;

import android.content.Context;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.anc.library.AncLibrary;
import org.smartregister.anc.library.activity.BaseUnitTest;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.service.UserService;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Utils.class, TemplateUtils.class, AncLibrary.class})
public class SiteCharacteristicsTemplateUtilsTest extends BaseUnitTest {

    @Mock
    private AncLibrary ancLibrary;

    @Test
    public void testStructureFormForRequestWhenTemplateIsNotFound() throws Exception {
        org.smartregister.Context context = Mockito.mock(org.smartregister.Context.class);
        UserService userService = PowerMockito.mock(UserService.class);
        PowerMockito.mockStatic(AncLibrary.class);
        PowerMockito.when(AncLibrary.getInstance()).thenReturn(ancLibrary);
        PowerMockito.when(ancLibrary.getContext()).thenReturn(context);
        PowerMockito.when(context, "userService").thenReturn(userService);
        PowerMockito.when(userService, "getAllSharedPreferences").thenReturn(PowerMockito.mock(AllSharedPreferences.class));
        PowerMockito.mockStatic(TemplateUtils.class);
        Assert.assertNull(SiteCharacteristicsFormUtils.structureFormForRequest(Mockito.mock(Context.class)));
    }

    @Test
    public void testStructureFormForRequestWhenTemplateIsFound() throws Exception {
        org.smartregister.Context context = Mockito.mock(org.smartregister.Context.class);
        Context contextAndroid = PowerMockito.mock(Context.class);
        UserService userService = PowerMockito.mock(UserService.class);
        AllSharedPreferences allSharedPreferences = Mockito.mock(AllSharedPreferences.class);
        PowerMockito.mockStatic(AncLibrary.class);
        PowerMockito.when(AncLibrary.getInstance()).thenReturn(ancLibrary);
        PowerMockito.when(ancLibrary.getContext()).thenReturn(context);
        PowerMockito.when(context, "userService").thenReturn(userService);
        String provider = "Some Provider";
        String team = "team";
        String teamId = "teamId";
        String locationId = "locations";

        PowerMockito.when(allSharedPreferences.fetchRegisteredANM()).thenReturn(provider);
        PowerMockito.when(allSharedPreferences.fetchDefaultTeam(provider)).thenReturn(team);
        PowerMockito.when(allSharedPreferences.fetchDefaultTeamId(provider)).thenReturn(teamId);
        PowerMockito.when(allSharedPreferences.fetchDefaultLocalityId(provider)).thenReturn(locationId);
        PowerMockito.when(userService, "getAllSharedPreferences").thenReturn(allSharedPreferences);
        PowerMockito.mockStatic(TemplateUtils.class);
        PowerMockito.when(TemplateUtils.getTemplateAsJson(contextAndroid, ConstantsUtils.PrefKeyUtils.SITE_CHARACTERISTICS)).thenReturn(new JSONObject());
        String expected = "{\"locationId\":\"locations\",\"providerId\":\"Some Provider\",\"teamId\":\"teamId\",\"team\":\"team\"}";
        Assert.assertEquals(expected, SiteCharacteristicsFormUtils.structureFormForRequest(contextAndroid).toString());
        ReflectionHelpers.setStaticField(AncLibrary.class, "instance", null);

    }

}