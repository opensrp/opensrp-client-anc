package org.smartregister.anc.library.interactor;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.smartregister.anc.library.activity.BaseUnitTest;
import org.smartregister.anc.library.contract.ProfileContract;
import org.smartregister.anc.library.presenter.ProfilePresenter;

/**
 * Created by ndegwamartin on 27/06/2018.
 */
public class ProfileInteractorTest extends BaseUnitTest {

    private static final String FIELD_PROFILE_PRESENTER = "mProfilePresenter";
    private ProfileContract.Interactor interactor;
    @Mock
    private ProfileContract.Presenter presenter;
    @Mock
    private ProfileContract.View view;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        interactor = new ProfileInteractor(presenter);
    }

    @Test
    public void testOnDestroyShouldNotResetThePresenterIfIsChangingConfigurationChangeIsTrue() {

        ProfileContract.Presenter presenter = Whitebox.getInternalState(interactor, FIELD_PROFILE_PRESENTER);
        Assert.assertNotNull(presenter);

        interactor.onDestroy(true);//configuration change

        presenter = Whitebox.getInternalState(interactor, FIELD_PROFILE_PRESENTER);
        Assert.assertNotNull(presenter);
    }

    @Test
    public void testOnDestroyShouldResetThePresenterIfIsChangingConfigurationChangeIsFalse() {

        ProfileContract.Presenter presenter = Whitebox.getInternalState(interactor, FIELD_PROFILE_PRESENTER);
        Assert.assertNotNull(presenter);

        interactor.onDestroy(false);//Not configuration change

        presenter = Whitebox.getInternalState(interactor, FIELD_PROFILE_PRESENTER);
        Assert.assertNull(presenter);

    }


    @Test
    public void testGetProfileViewShouldInvokePresenterGetProfileViewMethod() {
        ProfilePresenter presenter = new ProfilePresenter(view);
        ProfilePresenter presenterSpy = Mockito.spy(presenter);

        ProfileInteractor interactor = new ProfileInteractor(presenterSpy);
        ProfileInteractor spyInteractor = Mockito.spy(interactor);

        spyInteractor.getProfileView();

        Mockito.verify(presenterSpy, Mockito.times(1)).getProfileView();

        Assert.assertTrue(presenterSpy.getProfileView() instanceof ProfileContract.View);

        Assert.assertEquals(view, spyInteractor.getProfileView());

    }

    @Test
    public void testGetProfileViewShouldReturnNullIfPresenterParameterIsNull() {
        ProfilePresenter presenter = new ProfilePresenter(null);
        ProfilePresenter presenterSpy = Mockito.spy(presenter);

        Assert.assertNull(presenterSpy.getProfileView());

    }

    @Test
    public void testRefreshProfileViewInstantiatesFetchDataAsyncTask() {

        ProfileInteractor interactor = new ProfileInteractor(presenter);
        ProfileInteractor spyInteractor = Mockito.spy(interactor);

        spyInteractor.getProfileView();

        Mockito.verify(spyInteractor, Mockito.times(1)).getProfileView();


    }

}
