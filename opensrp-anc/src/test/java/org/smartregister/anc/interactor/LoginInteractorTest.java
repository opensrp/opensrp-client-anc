package org.smartregister.anc.interactor;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.smartregister.Context;
import org.smartregister.anc.activity.BaseUnitTest;
import org.smartregister.anc.contract.BaseLoginContract;
import org.smartregister.anc.presenter.LoginPresenter;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.service.UserService;

import java.lang.ref.WeakReference;

/**
 * Created by ndegwamartin on 27/06/2018.
 */
public class LoginInteractorTest extends BaseUnitTest {

    private BaseLoginContract.Interactor interactor;

    @Mock
    private BaseLoginContract.Presenter presenter;

    @Mock
    private BaseLoginContract.View view;

    @Mock
    private Context opensrpContext;

    @Mock
    private AllSharedPreferences sharedPreferences;

    @Mock
    private UserService userService;

    private static final String FIELD_LOGIN_PRESENTER = "mLoginPresenter";

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        interactor = new LoginInteractor(presenter);
    }

    @Test
    public void testOnDestroyShouldNotResetThePresenterIfIsChangingConfigurationChangeIsTrue() {

        BaseLoginContract.Presenter presenter = Whitebox.getInternalState(interactor, FIELD_LOGIN_PRESENTER);
        Assert.assertNotNull(presenter);

        interactor.onDestroy(true);//configuration change

        presenter = Whitebox.getInternalState(interactor, FIELD_LOGIN_PRESENTER);
        Assert.assertNotNull(presenter);
    }

    @Test
    public void testOnDestroyShouldResetThePresenterIfIsChangingConfigurationChangeIsFalse() {

        BaseLoginContract.Presenter presenter = Whitebox.getInternalState(interactor, FIELD_LOGIN_PRESENTER);
        Assert.assertNotNull(presenter);

        interactor.onDestroy(false);//Not configuration change

        presenter = Whitebox.getInternalState(interactor, FIELD_LOGIN_PRESENTER);
        Assert.assertNull(presenter);

    }

    @Test
    public void testGetApplicationContextShouldReturnValidInstance() {
        LoginInteractor interactor = new LoginInteractor(presenter);
        Assert.assertNotNull(interactor.getApplicationContext());
    }

    @Test
    public void testGetAllSharedPreferencesShouldReturnCorrectSharedPreferences() {
        LoginPresenter presenter = new LoginPresenter(view);
        LoginPresenter presenterSpy = Mockito.spy(presenter);

        LoginInteractor interactor = new LoginInteractor(presenterSpy);
        LoginInteractor spyInteractor = Mockito.spy(interactor);

        Mockito.doReturn(opensrpContext).when(presenterSpy).getOpenSRPContext();
        Mockito.doReturn(sharedPreferences).when(opensrpContext).allSharedPreferences();

        spyInteractor.getSharedPreferences();

        Mockito.verify(presenterSpy, Mockito.times(1)).getOpenSRPContext();
        Mockito.verify(opensrpContext, Mockito.times(1)).allSharedPreferences();

        Assert.assertEquals(sharedPreferences, spyInteractor.getSharedPreferences());

    }

    @Test
    public void testGetLoginViewShouldInvokePresenterGetLoginViewMethod() {
        LoginPresenter presenter = new LoginPresenter(view);
        LoginPresenter presenterSpy = Mockito.spy(presenter);

        LoginInteractor interactor = new LoginInteractor(presenterSpy);
        LoginInteractor spyInteractor = Mockito.spy(interactor);

        spyInteractor.getLoginView();

        Mockito.verify(presenterSpy, Mockito.times(1)).getLoginView();

        Assert.assertTrue(presenterSpy.getLoginView() instanceof BaseLoginContract.View);

    }

    @Test
    public void testGetUserServiceShouldInvokeUserServiceMethodFromOpensrpContext() {
        LoginPresenter presenter = new LoginPresenter(view);
        LoginPresenter presenterSpy = Mockito.spy(presenter);

        LoginInteractor interactor = new LoginInteractor(presenterSpy);
        LoginInteractor spyInteractor = Mockito.spy(interactor);

        Mockito.doReturn(opensrpContext).when(presenterSpy).getOpenSRPContext();
        Mockito.doReturn(userService).when(opensrpContext).userService();

        spyInteractor.getUserService();

        Mockito.verify(presenterSpy, Mockito.times(1)).getOpenSRPContext();
        Mockito.verify(opensrpContext, Mockito.times(1)).userService();

        Assert.assertEquals(userService, spyInteractor.getUserService());

    }

    @Test
    public void testLoginShouldInvokeLoginWithLocalFlagWithCorrectParameters() {
        LoginPresenter presenter = new LoginPresenter(view);
        LoginPresenter presenterSpy = Mockito.spy(presenter);

        LoginInteractor interactor = new LoginInteractor(presenterSpy);
        LoginInteractor spyInteractor = Mockito.spy(interactor);

        spyInteractor.login(new WeakReference<>(view), DUMMY_USERNAME, DUMMY_PASSWORD);

        Mockito.verify(spyInteractor, Mockito.times(1)).loginWithLocalFlag(Mockito.any(WeakReference.class), Mockito.eq(false), Mockito.eq(DUMMY_USERNAME), Mockito.eq(DUMMY_PASSWORD));

    }

    @Test
    public void testLoginWithFetchForceRemoteLoginTrueShouldInvokeLoginWithLocalFlagWithCorrectParameters() {
        LoginPresenter presenter = new LoginPresenter(view);
        LoginPresenter presenterSpy = Mockito.spy(presenter);

        LoginInteractor interactor = new LoginInteractor(presenterSpy);
        LoginInteractor spyInteractor = Mockito.spy(interactor);

        Mockito.doReturn(sharedPreferences).when(spyInteractor).getSharedPreferences();
        Mockito.doReturn(false).when(sharedPreferences).fetchForceRemoteLogin();

        spyInteractor.login(new WeakReference<>(view), DUMMY_USERNAME, DUMMY_PASSWORD);

        Mockito.verify(spyInteractor, Mockito.times(1)).loginWithLocalFlag(Mockito.any(WeakReference.class), Mockito.eq(true), Mockito.eq(DUMMY_USERNAME), Mockito.eq(DUMMY_PASSWORD));

    }
}
