package org.smartregister.anc.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.anc.activity.BaseUnitTest;
import org.smartregister.anc.contract.MeContract;
import org.smartregister.anc.util.Utils;
import org.smartregister.repository.AllSharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MeModelTest extends BaseUnitTest {
    private Utils utils;

    private MeContract.Model model;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        model = new MeModel();
        utils = new Utils();
    }

    @Test
    public void testGetBuildDateShouldReturnCorrectValue() {
        String dateFormat = "dd MMM yyyy";
        String todaysDate = new SimpleDateFormat(dateFormat, Locale.getDefault()).format(Calendar.getInstance().getTime());
        Assert.assertEquals(todaysDate, model.getBuildDate());
    }

    @Test
    public void testUpdateInitials() {
        AllSharedPreferences allSharedPreferences = Mockito.mock(AllSharedPreferences.class);
        MeModel meModel = (MeModel) model;

        utils.setAllSharedPreferences(allSharedPreferences);

        String foundInitials = meModel.getInitials();
        Assert.assertEquals(null, foundInitials);
    }

    @Test
    public void testGetName() {
        AllSharedPreferences allSharedPreferences = Mockito.mock(AllSharedPreferences.class);
        MeModel meModel = (MeModel) model;

        utils.setAllSharedPreferences(allSharedPreferences);

        String foundInitials = meModel.getName();
        Assert.assertEquals("", foundInitials);
    }
}
