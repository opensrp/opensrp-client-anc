package org.smartregister.anc.library.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.appcompat.app.ActionBar;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.fragments.JsonWizardFormFragment;
import com.vijay.jsonwizard.mvp.MvpBasePresenter;
import com.vijay.jsonwizard.mvp.ViewState;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.anc.library.R;
import org.smartregister.anc.library.activity.BaseUnitTest;
import org.smartregister.anc.library.activity.ContactJsonFormActivity;
import org.smartregister.anc.library.util.DBConstantsUtils;
import org.smartregister.anc.library.viewstate.ContactJsonFormFragmentViewState;

/**
 * Created by ndegwamartin on 04/07/2018.
 */

public class ContactWizardJsonFormFragmentTest extends BaseUnitTest {

    @Mock
    private MenuItem menuItem;

    @Mock
    private LayoutInflater layoutInflater;

    @Mock
    private Bundle bundle;

    @Mock
    private ViewGroup viewGroup;

    @Mock
    private View view;

    @Mock
    private LinearLayout linearLayout;

    @Mock
    private ScrollView scrollView;

    @Mock
    private Button button;

    @Mock
    private ImageView imageView;

    @Mock
    private ActionBar actionBar;

    @Mock
    private LinearLayout navigationLayout;
    private String testJson = "{\n" +
            "  \"encounter_type\": \"Birth Registration\",\n" +
            "  \"show_errors_on_submit\": true,\n" +
            "  \"count\": \"1\",\n" +
            "  \"display_scroll_bars\": true,\n" +
            "  \"mother\": {\n" +
            "    \"encounter_type\": \"New Woman Registration\"\n" +
            "  }\n" +
            "}";

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetFormFragmentShouldCreateAValidFragmentInstance() throws JSONException {

        JsonWizardFormFragment formFragment = ContactWizardJsonFormFragment.getFormFragment(JsonFormConstants.FIRST_STEP_NAME);
        Assert.assertNotNull(formFragment);

        formFragment.onOptionsItemSelected(menuItem);

        ViewGroup nullViewGroup = null;

        Mockito.doReturn(view).when(layoutInflater).inflate(ArgumentMatchers.eq(R.layout.contact_json_form_fragment_wizard), ArgumentMatchers.eq(nullViewGroup));
        Mockito.doReturn(scrollView).when(view).findViewById(R.id.scroll_view);
        Mockito.doReturn(linearLayout).when(view).findViewById(R.id.main_layout);
        Mockito.doReturn(button).when(view).findViewById(R.id.previous);
        Mockito.doReturn(button).when(view).findViewById(R.id.next);
        Mockito.doReturn(imageView).when(view).findViewById(R.id.previous_icon);
        Mockito.doReturn(imageView).when(view).findViewById(R.id.next_icon);
        Mockito.doReturn(navigationLayout).when(view).findViewById(R.id.navigation_layout);
        Mockito.doReturn(button).when(navigationLayout).findViewById(R.id.refer);
        Mockito.doReturn(button).when(navigationLayout).findViewById(R.id.proceed);

        JsonWizardFormFragment formFragmentSpy = Mockito.spy(formFragment);

        Mockito.doReturn(actionBar).when(formFragmentSpy).getSupportActionBar();
        ContactJsonFormActivity activity = new ContactJsonFormActivity();
        ContactJsonFormActivity activitySpy = Mockito.spy(activity);
        activitySpy.setmJSONObject(new JSONObject(testJson));
        formFragmentSpy.setmJsonApi(activitySpy);

        formFragmentSpy.onCreateView(layoutInflater, viewGroup, bundle);

        Bundle fragmentArgs = formFragmentSpy.getArguments();
        Assert.assertEquals(JsonFormConstants.FIRST_STEP_NAME, fragmentArgs.get(DBConstantsUtils.KeyUtils.STEPNAME));
    }

    @Test
    public void testCreateViewStateShouldCreateAValidViewState() {

        JsonWizardFormFragment formFragment = ContactWizardJsonFormFragment.getFormFragment(JsonFormConstants.FIRST_STEP_NAME);
        Assert.assertNotNull(formFragment);

        JsonWizardFormFragment fragmentSpy = Mockito.spy(formFragment);

        ViewState viewState = ((ContactWizardJsonFormFragment) fragmentSpy).createViewState();
        Assert.assertNotNull(viewState);
        Assert.assertTrue(viewState instanceof ContactJsonFormFragmentViewState);
    }


    @Test
    @Ignore
    public void testCreatePresenterShouldCreateAValidPresenter() {

        ContactWizardJsonFormFragment formFragment = (ContactWizardJsonFormFragment) ContactWizardJsonFormFragment.getFormFragment(JsonFormConstants.FIRST_STEP_NAME);
        Assert.assertNotNull(formFragment);
        ContactWizardJsonFormFragment fragmentSpy = Mockito.spy(formFragment);
        MvpBasePresenter presenter = fragmentSpy.createPresenter();
        Assert.assertNotNull(presenter);
    }

}
