package org.smartregister.anc.fragment;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.mvp.MvpBasePresenter;
import com.vijay.jsonwizard.mvp.ViewState;
import com.vijay.jsonwizard.presenters.JsonFormFragmentPresenter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.anc.R;
import org.smartregister.anc.activity.BaseUnitTest;
import org.smartregister.anc.util.DBConstants;
import org.smartregister.anc.viewstate.ContactJsonFormFragmentViewState;

/**
 * Created by ndegwamartin on 04/07/2018.
 */

public class ContactJsonFormFragmentTest extends BaseUnitTest {

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

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetFormFragmentShouldCreateAValidFragmentInstance() {

        ContactJsonFormFragment formFragment = ContactJsonFormFragment.getFormFragment(JsonFormConstants.FIRST_STEP_NAME);
        Assert.assertNotNull(formFragment);

        formFragment.onOptionsItemSelected(menuItem);

        ViewGroup nullViewGroup = null;
        Mockito.doReturn(view).when(layoutInflater).inflate(ArgumentMatchers.eq(R.layout.contact_form_fragment_json_wizard), ArgumentMatchers.eq(nullViewGroup));
        Mockito.doReturn(scrollView).when(view).findViewById(R.id.scroll_view);
        Mockito.doReturn(linearLayout).when(view).findViewById(R.id.main_layout);
        Mockito.doReturn(button).when(view).findViewById(R.id.previous);
        Mockito.doReturn(button).when(view).findViewById(R.id.next);
        Mockito.doReturn(imageView).when(view).findViewById(R.id.previous_icon);
        Mockito.doReturn(imageView).when(view).findViewById(R.id.next_icon);

        ContactJsonFormFragment formFragmentSpy = Mockito.spy(formFragment);

        Mockito.doReturn(actionBar).when(formFragmentSpy).getSupportActionBar();

        formFragmentSpy.onCreateView(layoutInflater, viewGroup, bundle);

        Bundle fragmentArgs = formFragmentSpy.getArguments();
        Assert.assertEquals(JsonFormConstants.FIRST_STEP_NAME, fragmentArgs.get(DBConstants.KEY.STEPNAME));
    }

    @Test
    public void testCreateViewStateShouldCreateAValidViewState() {

        ContactJsonFormFragment formFragment = ContactJsonFormFragment.getFormFragment(JsonFormConstants.FIRST_STEP_NAME);
        Assert.assertNotNull(formFragment);

        ContactJsonFormFragment fragmentSpy = Mockito.spy(formFragment);

        ViewState viewState = fragmentSpy.createViewState();
        Assert.assertNotNull(viewState);
        Assert.assertTrue(viewState instanceof ContactJsonFormFragmentViewState);
    }


    @Test
    public void testCreatePresenterShouldCreateAValidPresenter() {

        ContactJsonFormFragment formFragment = ContactJsonFormFragment.getFormFragment(JsonFormConstants.FIRST_STEP_NAME);
        Assert.assertNotNull(formFragment);

        ContactJsonFormFragment fragmentSpy = Mockito.spy(formFragment);

        MvpBasePresenter presenter = fragmentSpy.createPresenter();
        Assert.assertNotNull(presenter);
        Assert.assertTrue(presenter instanceof JsonFormFragmentPresenter);
    }

}
