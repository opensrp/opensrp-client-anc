package org.smartregister.anc.util;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.smartregister.anc.activity.BaseUnitTest;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.util.Utils;

@RunWith(PowerMockRunner.class)
public class UtilsTest extends BaseUnitTest {

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @PrepareForTest(Utils.class)
    @Test
    public void testGetNameWithNullPreferences() {
        AllSharedPreferences allSharedPreferences = Mockito.mock(AllSharedPreferences.class);

        PowerMockito.mockStatic(Utils.class);
        PowerMockito.when(Utils.getAllSharedPreferences()).thenReturn(allSharedPreferences);

        String name = Utils.getPrefferedName();
        Assert.assertNull(name);

    }

    @PrepareForTest(Utils.class)
    @Test
    public void testGetName() {
        String username = "userName1";
        AllSharedPreferences allSharedPreferences = Mockito.mock(AllSharedPreferences.class);

        PowerMockito.mockStatic(Utils.class);

        PowerMockito.when(Utils.getAllSharedPreferences()).thenReturn(allSharedPreferences);
        PowerMockito.when(allSharedPreferences.fetchRegisteredANM()).thenReturn(username);
        Assert.assertNotNull(allSharedPreferences);


        Utils.getPrefferedName();

        Mockito.verify(allSharedPreferences).getANMPreferredName(username);
        Mockito.verify(allSharedPreferences).fetchRegisteredANM();

    }

    @PrepareForTest({StringUtils.class, Utils.class})
    @Test
    public void testGetUserInitialsWithTwoNames() {
        String username = "userName2";
        String preferredName = "Anc Reference";

        AllSharedPreferences allSharedPreferences = Mockito.mock(AllSharedPreferences.class);

        PowerMockito.mockStatic(StringUtils.class);
        PowerMockito.mockStatic(Utils.class);

        PowerMockito.when(Utils.getAllSharedPreferences()).thenReturn(allSharedPreferences);

        PowerMockito.when(allSharedPreferences.fetchRegisteredANM()).thenReturn(username);
        PowerMockito.when(allSharedPreferences.getANMPreferredName(username)).thenReturn(preferredName);

        PowerMockito.when(StringUtils.isNotBlank(preferredName)).thenReturn(true);

        Assert.assertNotNull(allSharedPreferences);
        Assert.assertNotNull(username);
        Assert.assertNotNull(preferredName);


        String initials = Utils.getUserInitials();
        Assert.assertEquals("AR", initials);

        Mockito.verify(allSharedPreferences).getANMPreferredName(username);
        Mockito.verify(allSharedPreferences).fetchRegisteredANM();
    }

    @PrepareForTest({StringUtils.class, Utils.class})
    @Test
    public void testGetUserInitialsWithOneNames() {

        String username = "UserNAME3";
        String preferredName = "Anc";
        AllSharedPreferences allSharedPreferences = Mockito.mock(AllSharedPreferences.class);

        PowerMockito.mockStatic(StringUtils.class);
        PowerMockito.mockStatic(Utils.class);

        PowerMockito.when(Utils.getAllSharedPreferences()).thenReturn(allSharedPreferences);
        PowerMockito.when(allSharedPreferences.fetchRegisteredANM()).thenReturn(username);
        PowerMockito.when(allSharedPreferences.getANMPreferredName(username)).thenReturn(preferredName);

        PowerMockito.when(StringUtils.isNotBlank(preferredName)).thenReturn(true);

        Assert.assertNotNull(allSharedPreferences);
        Assert.assertNotNull(username);
        Assert.assertNotNull(preferredName);


        String initials = Utils.getUserInitials();
        Assert.assertEquals("A", initials);

        Mockito.verify(allSharedPreferences).getANMPreferredName(username);
        Mockito.verify(allSharedPreferences).fetchRegisteredANM();
    }

    @PrepareForTest(Utils.class)
    @Test
    public void testGerPreferredNameWithNullSharePreferences() {

        AllSharedPreferences allSharedPreferences = Mockito.mock(AllSharedPreferences.class);
        PowerMockito.mockStatic(Utils.class);
        PowerMockito.when(Utils.getAllSharedPreferences()).thenReturn(allSharedPreferences);

        String name = Utils.getPrefferedName();
        Assert.assertNull(name);
    }

    @PrepareForTest(StringUtils.class)
    @Test
    public void testDobStringToDateTime() {
        String dobString = ArgumentMatchers.anyString();

        PowerMockito.mockStatic(StringUtils.class);
        PowerMockito.when(StringUtils.isNotBlank(dobString)).thenReturn(true);

        DateTime dobStringToDateTime = Utils.dobStringToDateTime(dobString);
        Assert.assertEquals(dobStringToDateTime, dobStringToDateTime);
    }

    @PrepareForTest(StringUtils.class)
    @Test
    public void testDobStringToDateTimeWithNullStringDate() {
        PowerMockito.mockStatic(StringUtils.class);
        PowerMockito.when(StringUtils.isBlank(null)).thenReturn(true);

        DateTime dobStringToDateTime = Utils.dobStringToDateTime(null);
        Assert.assertEquals(dobStringToDateTime, dobStringToDateTime);
    }

}
