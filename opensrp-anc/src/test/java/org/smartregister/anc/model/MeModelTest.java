package org.smartregister.anc.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.smartregister.anc.activity.BaseUnitTest;
import org.smartregister.anc.contract.MeContract;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class
MeModelTest extends BaseUnitTest {
    private MeContract.Model model;

    @Before
    public void setUp() {
        model = new MeModel();
    }

    @Test
    public void testGetBuildDateShouldReturnCorrectValue() {
        String dateFormat = "dd MMMM yyyy";
        String todaysDate = new SimpleDateFormat(dateFormat, Locale.getDefault()).format(Calendar.getInstance().getTime());
        Assert.assertEquals(todaysDate, model.getBuildDate());
    }
}
