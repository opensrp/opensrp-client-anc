package org.smartregister.anc.interactor;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.smartregister.anc.activity.BaseUnitTest;
import org.smartregister.anc.contract.LoginContract;

/**
 * Created by ndegwamartin on 27/06/2018.
 */
public class LoginInteractorTest extends BaseUnitTest {

    private LoginContract.Interactor interactor;

    @Mock
    private LoginContract.Presenter presenter;

    private static final String FIELD_LOGIN_PRESENTER = "mLoginPresenter";

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        interactor = new LoginInteractor(presenter);
    }

    @Test
    public void testOnDestroyShouldNotResetThePresenterIfIsChangingConfigurationChangeIsTrue() {

        LoginContract.Presenter presenter = Whitebox.getInternalState(interactor, FIELD_LOGIN_PRESENTER);
        Assert.assertNotNull(presenter);

        interactor.onDestroy(true);//configuration change

        presenter = Whitebox.getInternalState(interactor, FIELD_LOGIN_PRESENTER);
        Assert.assertNotNull(presenter);
    }

    @Test
    public void testOnDestroyShouldResetThePresenterIfIsChangingConfigurationChangeIsFalse() {

        LoginContract.Presenter presenter = Whitebox.getInternalState(interactor, FIELD_LOGIN_PRESENTER);
        Assert.assertNotNull(presenter);

        interactor.onDestroy(false);//Not configuration change

        presenter = Whitebox.getInternalState(interactor, FIELD_LOGIN_PRESENTER);
        Assert.assertNull(presenter);

    }
}
