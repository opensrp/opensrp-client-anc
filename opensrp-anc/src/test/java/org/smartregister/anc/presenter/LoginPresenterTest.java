package org.smartregister.anc.presenter;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.anc.activity.BaseUnitTest;
import org.smartregister.anc.contract.LoginContract;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

/**
 * Created by ndegwamartin on 27/06/2018.
 */
public class LoginPresenterTest extends BaseUnitTest {

    @Mock
    private LoginContract.View view;

    @Mock
    private LoginContract.Interactor interactor;

    @Mock
    private LoginContract.Model model;

    private LoginContract.Presenter presenter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        presenter = new LoginPresenter(view);
    }

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

        LoginPresenter presenter = new LoginPresenter(view);
        presenter.setLoginModel(model);//set mocked model
        presenter.getOpenSRPContext();
        Mockito.verify(model).getOpenSRPContext();
    }

    @Test
    public void testOnDestroyShouldCallInteractorOnDestroyWithCorrectParameter() {

        LoginPresenter presenter = new LoginPresenter(view);
        presenter.setLoginInteractor(interactor);//set mocked interactor

        Mockito.doNothing().when(interactor).onDestroy(anyBoolean());
        presenter.onDestroy(true);
        Mockito.verify(interactor).onDestroy(true);
        presenter.onDestroy(false);
        Mockito.verify(interactor).onDestroy(false);
    }

    @Test
    public void testAttemptLoginShouldValidateCredentialsCorrectly() {

        LoginPresenter presenter = new LoginPresenter(view);
        presenter.setLoginModel(model);//set mocked model
        presenter.setLoginInteractor(interactor); //set mocked interactor

        Mockito.doReturn(false).when(model).isEmptyUsername(anyString());
        Mockito.doReturn(true).when(model).isPasswordValid(anyString());
        presenter.attemptLogin(DUMMY_USERNAME, DUMMY_PASSWORD);
        Mockito.verify(view).resetPaswordError();
        Mockito.verify(view).resetUsernameError();
        Mockito.verify(model).isEmptyUsername(DUMMY_USERNAME);
        Mockito.verify(model).isPasswordValid(DUMMY_PASSWORD);
        Mockito.verify(interactor).login(any(WeakReference.class), eq(DUMMY_USERNAME), eq(DUMMY_PASSWORD));

    }

    @Test
    public void testAttemptLoginShouldCallLoginMethodWithCorrectParametersWhenValidationPasses() {

        LoginPresenter presenter = new LoginPresenter(view);
        presenter.setLoginModel(model);//set mocked model
        presenter.setLoginInteractor(interactor); //set mocked interactor

        Mockito.doReturn(false).when(model).isEmptyUsername(anyString());
        Mockito.doReturn(true).when(model).isPasswordValid(anyString());
        presenter.attemptLogin(DUMMY_USERNAME, DUMMY_PASSWORD);
        Mockito.verify(interactor).login(any(WeakReference.class), eq(DUMMY_USERNAME), eq(DUMMY_PASSWORD));


    }

    @Test
    public void testAttemptLoginShouldNotCallLoginMethodWhenValidationFails() {

        LoginPresenter presenter = new LoginPresenter(view);
        presenter.setLoginModel(model);//set mocked model
        presenter.setLoginInteractor(interactor); //set mocked interactor
        Mockito.doReturn(false).when(model).isPasswordValid(anyString());
        presenter.attemptLogin(DUMMY_USERNAME, DUMMY_PASSWORD);
        Mockito.verify(interactor, Mockito.times(0)).login(any(WeakReference.class), eq(DUMMY_USERNAME), eq(DUMMY_PASSWORD));

    }

    @Test
    public void testGetLoginViewShouldReturnCorrectInstance() {
        Assert.assertNotNull(presenter.getLoginView());
        Assert.assertEquals(view, presenter.getLoginView());

        LoginPresenter presenter = new LoginPresenter(null);
        Assert.assertNull(presenter.getLoginView());

    }

}
