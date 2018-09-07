package org.smartregister.anc.util;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.smartregister.Context;
import org.smartregister.anc.activity.BaseUnitTest;
import org.smartregister.anc.application.AncApplication;
import org.smartregister.repository.AllSharedPreferences;

@RunWith(PowerMockRunner.class)
public class UtilsTest extends BaseUnitTest {

    private Utils utils;

    @Mock
    private AncApplication ancApplication;

    @Mock
    private Context context;

    @Mock
    private AllSharedPreferences allSharedPreferences;

    private String name;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        utils = new Utils();
    }

    @Test
    public void testGetNameWithNullPreferences() {
        AllSharedPreferences allSharedPreferences = Mockito.mock(AllSharedPreferences.class);
        utils.setAllSharedPreferences(allSharedPreferences);

        String name = utils.getName();
        Assert.assertNull(name);

    }

    @PrepareForTest(AncApplication.class)
    @Test
    public void testGetName() {
        String username = ArgumentMatchers.anyString();

        PowerMockito.mockStatic(AncApplication.class);
        PowerMockito.when(AncApplication.getInstance()).thenReturn(ancApplication);
        PowerMockito.when(ancApplication.getContext()).thenReturn(context);
        PowerMockito.when(context.allSharedPreferences()).thenReturn(allSharedPreferences);
        PowerMockito.when(allSharedPreferences.fetchRegisteredANM()).thenReturn(username);
        Assert.assertNotNull(allSharedPreferences);

        utils.setAllSharedPreferences(allSharedPreferences);
        Assert.assertNotNull(utils);

        utils.getName();

        Mockito.verify(allSharedPreferences).getANMPreferredName(username);
        Mockito.verify(allSharedPreferences).fetchRegisteredANM();

    }

    @PrepareForTest({AncApplication.class, StringUtils.class})
    @Test
    public void testGetUserInitialsWithTwoNames() {
        String username = ArgumentMatchers.anyString();
        String preferredName = "Anc Reference";

        PowerMockito.mockStatic(AncApplication.class);
        PowerMockito.mockStatic(StringUtils.class);

        PowerMockito.when(AncApplication.getInstance()).thenReturn(ancApplication);
        PowerMockito.when(ancApplication.getContext()).thenReturn(context);
        PowerMockito.when(context.allSharedPreferences()).thenReturn(allSharedPreferences);
        PowerMockito.when(allSharedPreferences.fetchRegisteredANM()).thenReturn(username);
        PowerMockito.when(allSharedPreferences.getANMPreferredName(username)).thenReturn(preferredName);

        PowerMockito.when(StringUtils.isNotBlank(preferredName)).thenReturn(true);

        Assert.assertNotNull(allSharedPreferences);
        Assert.assertNotNull(username);
        Assert.assertNotNull(preferredName);

        utils.setAllSharedPreferences(allSharedPreferences);
        Assert.assertNotNull(utils);

        String initials = utils.getUserInitials();
        Assert.assertEquals("AR", initials);

        Mockito.verify(allSharedPreferences).getANMPreferredName(username);
        Mockito.verify(allSharedPreferences).fetchRegisteredANM();
    }

    @PrepareForTest({AncApplication.class, StringUtils.class})
    @Test
    public void testGetUserInitialsWithOneNames() {
        String username = ArgumentMatchers.anyString();
        String preferredName = "Anc";

        PowerMockito.mockStatic(AncApplication.class);
        PowerMockito.mockStatic(StringUtils.class);

        PowerMockito.when(AncApplication.getInstance()).thenReturn(ancApplication);
        PowerMockito.when(ancApplication.getContext()).thenReturn(context);
        PowerMockito.when(context.allSharedPreferences()).thenReturn(allSharedPreferences);
        PowerMockito.when(allSharedPreferences.fetchRegisteredANM()).thenReturn(username);
        PowerMockito.when(allSharedPreferences.getANMPreferredName(username)).thenReturn(preferredName);

        PowerMockito.when(StringUtils.isNotBlank(preferredName)).thenReturn(true);

        Assert.assertNotNull(allSharedPreferences);
        Assert.assertNotNull(username);
        Assert.assertNotNull(preferredName);

        utils.setAllSharedPreferences(allSharedPreferences);
        Assert.assertNotNull(utils);

        String initials = utils.getUserInitials();
        Assert.assertEquals("A", initials);

        Mockito.verify(allSharedPreferences).getANMPreferredName(username);
        Mockito.verify(allSharedPreferences).fetchRegisteredANM();
    }

    @Test
    public void testGerPreferredNameWithNullSharePreferences() {
        AllSharedPreferences allSharedPreferences = Mockito.mock(AllSharedPreferences.class);
        utils.setAllSharedPreferences(allSharedPreferences);

        String name = utils.getPrefferedName();
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
