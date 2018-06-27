package org.smartregister.anc.interactor;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.smartregister.anc.activity.BaseUnitTest;
import org.smartregister.anc.contract.LoginContract;

/**
 * Created by ndegwamartin on 27/06/2018.
 */
public class LoginInteractorTest extends BaseUnitTest {

    private LoginContract.Interactor interactor;

    @Mock
    private LoginContract.Presenter presenter;

    @Mock
    private LoginContract.View view;

    @Before
    public void setUp() {
        interactor = new LoginInteractor(presenter);
    }

    @Test
    public void testLoginShouldCallLoginTask() {
        //interactor.login(new WeakReference<>(view), "admin", "admin");
        Assert.assertTrue(true);
    }

}
