package org.smartregister.anc.util;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
        Utils utils = Mockito.mock(Utils.class);

        PowerMockito.mockStatic(AncApplication.class);
        PowerMockito.when(AncApplication.getInstance()).thenReturn(ancApplication);
        PowerMockito.when(ancApplication.getContext()).thenReturn(context);
        PowerMockito.when(context.allSharedPreferences()).thenReturn(allSharedPreferences);
        Assert.assertNotNull(allSharedPreferences);

        utils.setAllSharedPreferences(allSharedPreferences);
        Assert.assertNotNull(utils);

        Mockito.doReturn(name).when(utils).getName();
        utils.getName();
        
    }
}
