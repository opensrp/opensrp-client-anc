package org.smartregister.anc.library.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.anc.library.activity.BaseUnitTest;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.view.contract.MeContract;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

@PrepareForTest({CoreLibrary.class, Context.class})
@RunWith(PowerMockRunner.class)
public class MeModelTest extends BaseUnitTest {
    private MeContract.Model model;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        model = new MeModel();
    }

    @Test
    public void testGetBuildDateShouldReturnCorrectValue() {
        String dateFormat = "dd MMM yyyy";
        String dateToday = new SimpleDateFormat(dateFormat, Locale.getDefault()).format(Calendar.getInstance().getTime());
        long buildTimestamp = System.currentTimeMillis();
        Assert.assertNotNull(dateToday);

        CoreLibrary coreLibrary = PowerMockito.mock(CoreLibrary.class);
        PowerMockito.mockStatic(CoreLibrary.class);
        PowerMockito.when(CoreLibrary.getInstance()).thenReturn(coreLibrary);
        Assert.assertNotNull(coreLibrary);

        PowerMockito.when(CoreLibrary.getBuildTimeStamp()).thenReturn(buildTimestamp);
        Assert.assertEquals(dateToday, model.getBuildDate());
    }

    @Test
    public void testUpdateInitials() {
        AllSharedPreferences allSharedPreferences = Mockito.mock(AllSharedPreferences.class);
        CoreLibrary coreLibrary = PowerMockito.mock(CoreLibrary.class);
        Context context = PowerMockito.mock(Context.class);

        PowerMockito.mockStatic(CoreLibrary.class);
        PowerMockito.mockStatic(Context.class);

        PowerMockito.when(CoreLibrary.getInstance()).thenReturn(coreLibrary);
        PowerMockito.when(coreLibrary.context()).thenReturn(context);
        PowerMockito.when(context.allSharedPreferences()).thenReturn(allSharedPreferences);

        MeModel meModel = (MeModel) model;

        String foundInitials = meModel.getInitials();
        Assert.assertEquals("Me", foundInitials);
    }

    @Test
    public void testGetName() {
        AllSharedPreferences allSharedPreferences = Mockito.mock(AllSharedPreferences.class);
        CoreLibrary coreLibrary = PowerMockito.mock(CoreLibrary.class);
        Context context = PowerMockito.mock(Context.class);

        PowerMockito.mockStatic(CoreLibrary.class);
        PowerMockito.mockStatic(Context.class);

        PowerMockito.when(CoreLibrary.getInstance()).thenReturn(coreLibrary);
        PowerMockito.when(coreLibrary.context()).thenReturn(context);
        PowerMockito.when(context.allSharedPreferences()).thenReturn(allSharedPreferences);
        PowerMockito.when(allSharedPreferences.getANMPreferredName(Mockito.anyString())).thenReturn("");

        MeModel meModel = (MeModel) model;

        String foundInitials = meModel.getName();
        Assert.assertNull(foundInitials);
    }
}
