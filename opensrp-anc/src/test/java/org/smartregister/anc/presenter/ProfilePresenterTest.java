package org.smartregister.anc.presenter;

import android.util.ArrayMap;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.smartregister.anc.R;
import org.smartregister.anc.activity.BaseUnitTest;
import org.smartregister.anc.contract.ProfileContract;
import org.smartregister.anc.util.Constants;
import org.smartregister.anc.util.DBConstants;
import org.smartregister.anc.util.Utils;

import java.util.Map;


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
        spyPresenter.refreshProfileView(TEST_STRING);
        Mockito.verify(interactor).refreshProfileView(TEST_STRING);

    }

    @Test
    public void testGetProfileViewShouldReturnCorrectInstance() {
        Assert.assertNotNull(presenter.getProfileView());
        Assert.assertEquals(view, presenter.getProfileView());

        ((ProfilePresenter) presenter).onUniqueIdFetched(null, null);

        ProfilePresenter presenter = new ProfilePresenter(null);
        Assert.assertNull(presenter.getProfileView());

    }

    @Test
    public void testOnNoUniqueIdShouldDisplayCorrectToastMessage() {
        ProfilePresenter presenterSpy = (ProfilePresenter) Mockito.spy(presenter);

        presenterSpy.onNoUniqueId();

        Mockito.verify(view).displayToast(R.string.no_openmrs_id);
    }


    @Test
    public void testOnRegistrationSavedShouldInvokeRefreshProfileWithCorrectParams() {

        ProfilePresenter presenterSpy = (ProfilePresenter) Mockito.spy(presenter);

        Mockito.doReturn(DUMMY_BASE_ENTITY_ID).when(view).getIntentString(Constants.INTENT_KEY.BASE_ENTITY_ID);
        Mockito.doNothing().when(presenterSpy).refreshProfileView(DUMMY_BASE_ENTITY_ID);
        presenterSpy.onRegistrationSaved(true);
        Mockito.verify(presenterSpy).refreshProfileView(DUMMY_BASE_ENTITY_ID);
    }


    @Test
    public void testOnRegistrationSavedShouldHideProgressDialog() {

        ProfilePresenter presenterSpy = (ProfilePresenter) Mockito.spy(presenter);

        Mockito.doNothing().when(presenterSpy).refreshProfileView(null);
        presenterSpy.onRegistrationSaved(true);
        Mockito.verify(view).hideProgressDialog();
    }


    @Test
    public void testOnRegistrationSavedShouldInvokeCorrectMethodsCorrectly() {

        ProfilePresenter presenterSpy = (ProfilePresenter) Mockito.spy(presenter);

        Mockito.doNothing().when(presenterSpy).refreshProfileView(null);
        presenterSpy.onRegistrationSaved(true);
        Mockito.verify(view).displayToast(R.string.registration_info_updated);


        ProfilePresenter presenterSpy2 = (ProfilePresenter) Mockito.spy(presenter);
        Mockito.doNothing().when(presenterSpy2).refreshProfileView(null);
        presenterSpy2.onRegistrationSaved(false);
        Mockito.verify(view).displayToast(R.string.new_registration_saved);
    }

    @Test
    public void testRefreshProfileTopSectionShouldInvokeCorrectViewMethods() {


        ProfileContract.Presenter presenterSpy = Mockito.spy(presenter);

        Map<String, String> client = new ArrayMap<>();
        client.put(DBConstants.KEY.FIRST_NAME, DUMMY_USERNAME);
        client.put(DBConstants.KEY.LAST_NAME, DUMMY_USERNAME);
        client.put(DBConstants.KEY.DOB, "1997-08-09");
        client.put(DBConstants.KEY.BASE_ENTITY_ID, DUMMY_BASE_ENTITY_ID);
        client.put(DBConstants.KEY.ANC_ID, TEST_STRING);
        client.put(DBConstants.KEY.PHONE_NUMBER, TEST_STRING);

        presenterSpy.refreshProfileTopSection(client);


        Mockito.verify(view).setProfileName(client.get(DBConstants.KEY.FIRST_NAME) + " " + client.get(DBConstants.KEY.LAST_NAME));
        Mockito.verify(view).setProfileAge(String.valueOf(Utils.getAgeFromDate(client.get(DBConstants.KEY.DOB))));
        Mockito.verify(view).setProfileID(client.get(DBConstants.KEY.ANC_ID));
        Mockito.verify(view).setProfileImage(client.get(DBConstants.KEY.BASE_ENTITY_ID));
        Mockito.verify(view).setWomanPhoneNumber(client.get(DBConstants.KEY.PHONE_NUMBER));

    }

}
