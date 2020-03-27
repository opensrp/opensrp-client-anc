package org.smartregister.anc.library.viewstate;

import android.os.Parcel;
import android.os.Parcelable;

import com.vijay.jsonwizard.viewstates.JsonFormFragmentViewState;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.smartregister.anc.library.activity.BaseUnitTest;

/**
 * Created by ndegwamartin on 04/07/2018.
 */
public class ContactWizardJsonFormFragmentViewStateTest extends BaseUnitTest {
    @Mock
    private Parcel parcel;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetFormFragmentShouldCreateAValidFragmentInstance() {
        ContactJsonFormFragmentViewState viewState = new ContactJsonFormFragmentViewState();
        Assert.assertNotNull(viewState);

        viewState.writeToParcel(parcel, 0);
        Assert.assertEquals(0, viewState.describeContents());

    }


    @Test
    public void testStaticCreatorPropertyShouldHaveValidCreatorInstance() {
        Parcelable.Creator<ContactJsonFormFragmentViewState> creator = ContactJsonFormFragmentViewState.CREATOR;
        Assert.assertNotNull(creator);

        JsonFormFragmentViewState viewState = creator.createFromParcel(parcel);
        Assert.assertNotNull(viewState);
        Assert.assertTrue(viewState instanceof ContactJsonFormFragmentViewState);

        ContactJsonFormFragmentViewState[] viewStateArray = creator.newArray(3);
        Assert.assertNotNull(viewStateArray);
        Assert.assertEquals(3, viewStateArray.length);


    }
}
