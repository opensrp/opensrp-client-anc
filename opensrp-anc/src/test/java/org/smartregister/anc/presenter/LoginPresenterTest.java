package org.smartregister.anc.presenter;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.smartregister.anc.activity.BaseUnitTest;
import org.smartregister.anc.contract.LoginContract;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by ndegwamartin on 27/06/2018.
 */
public class LoginPresenterTest extends BaseUnitTest {

    @Mock
    private LoginContract.View view;

    private LoginContract.Presenter presenter;

    @Before
    public void setUp() {
        presenter = new LoginPresenter(view);
    }

    //testRenderUndoVaccinationButtonShouldNotBeVisibleIfButtonActivateIsFalse
    @Test
    public void testGetBuildDateShouldReturnCorrectValue() {
        String dateFormat = "dd MMM yyyy";
        String todaysDate = new SimpleDateFormat(dateFormat, Locale.getDefault()).format(Calendar.getInstance().getTime());
        Assert.assertEquals(todaysDate, presenter.getBuildDate());
    }

    @Test
    public void testIsUserLoggedOutShouldReturnTrue() {
        Assert.assertTrue(presenter.isUserLoggedOut());
    }

    @Test
    public void testGetOpenSRPContextShouldReturnValidValue() {
        Assert.assertNotNull(presenter.getOpenSRPContext());
    }
}
