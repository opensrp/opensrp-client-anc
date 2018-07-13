package org.smartregister.anc.presenter;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.smartregister.anc.activity.BaseUnitTest;
import org.smartregister.anc.contract.ProfileContract;


/**
 * Created by ndegwamartin on 27/06/2018.
 */
public class ProfilePresenterTest extends BaseUnitTest {

    @Mock
    private ProfileContract.View view;

    @Mock
    private ProfileContract.Interactor interactor;

    private ProfileContract.Presenter presenter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        presenter = new ProfilePresenter(view);
    }

    @Test
    public void testGetProfileViewShouldReturnNullIfNoViewIsSet() {
        ProfilePresenter presenter = new ProfilePresenter(null);
        Assert.assertNull(presenter.getProfileView());

    }

    @Test
    public void testRefreshProfileViewInvokesInteractorMethodRefreshProfileView() {

        ProfileContract.Presenter spyPresenter = Mockito.spy(presenter);
        Whitebox.setInternalState(spyPresenter, "mProfileInteractor", interactor);
        spyPresenter.refreshProfileView();
        Mockito.verify(interactor).refreshProfileView();

    }

    @Test
    public void testGetProfileViewShouldReturnCorrectInstance() {
        Assert.assertNotNull(presenter.getProfileView());
        Assert.assertEquals(view, presenter.getProfileView());

        ProfilePresenter presenter = new ProfilePresenter(null);
        Assert.assertNull(presenter.getProfileView());

    }

}
