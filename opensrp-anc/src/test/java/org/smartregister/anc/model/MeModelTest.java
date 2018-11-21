package org.smartregister.anc.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.smartregister.anc.activity.BaseUnitTest;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.util.Utils;
import org.smartregister.view.contract.MeContract;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

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
        String todaysDate = new SimpleDateFormat(dateFormat, Locale.getDefault()).format(Calendar.getInstance().getTime());
        Assert.assertEquals(todaysDate, model.getBuildDate());
    }

    @PrepareForTest({Utils.class})
    @Test
    public void testUpdateInitials() {
        AllSharedPreferences allSharedPreferences = Mockito.mock(AllSharedPreferences.class);
        PowerMockito.mockStatic(Utils.class);
        PowerMockito.when(Utils.getAllSharedPreferences()).thenReturn(allSharedPreferences);

        MeModel meModel = (MeModel) model;


        String foundInitials = meModel.getInitials();
        Assert.assertNull(foundInitials);
    }

    @PrepareForTest({Utils.class})
    @Test
    public void testGetName() {
        AllSharedPreferences allSharedPreferences = Mockito.mock(AllSharedPreferences.class);
        PowerMockito.mockStatic(Utils.class);
        PowerMockito.when(Utils.getAllSharedPreferences()).thenReturn(allSharedPreferences);
        PowerMockito.when(allSharedPreferences.getANMPreferredName(Mockito.anyString())).thenReturn("");

        MeModel meModel = (MeModel) model;

        String foundInitials = meModel.getName();
        Assert.assertNull(foundInitials);
    }
}
