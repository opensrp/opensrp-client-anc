package org.smartregister.anc.activity;

import android.content.Intent;
import android.widget.TextView;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.smartregister.anc.R;
import org.smartregister.anc.util.Constants;
import org.smartregister.domain.ProfileImage;
import org.smartregister.repository.ImageRepository;

import java.util.UUID;

import static org.robolectric.Shadows.shadowOf;

public class ContactSummaryActivityTest extends BaseUnitTest {

    private ActivityController<ContactSummaryActivity> activityController;
    private ContactSummaryActivity activity;
    private final String baseEntityId = UUID.randomUUID().toString();

    @Before
    public void setup() {
        Intent contactSummaryActivityIntent = new Intent(RuntimeEnvironment.application,
                ContactSummaryActivity.class);
        contactSummaryActivityIntent.putExtra(Constants.INTENT_KEY.BASE_ENTITY_ID, baseEntityId);
        activityController = Robolectric.buildActivity(ContactSummaryActivity.class,
                contactSummaryActivityIntent);
    }


    @Test
    public void testActivityIsNotNull() {
        activity = activityController.create().get();
        Assert.assertNotNull(activity);
    }

    @Test
    public void testActivityStartedWithIntent() {
        activity = activityController.create().get();
        Intent passedIntent = activity.getIntent();
        Assert.assertNotNull(passedIntent);

    }
    @Test
    public void testDisplayWomansName() {
        activity = activityController.create().get();
        TextView womansName = activity.findViewById(R.id.contact_summary_woman_name);
        Assert.assertNotNull(womansName.getText());

    }

    @Test
    public void testGoToClientButtonClicked() {
        activity = activityController.create().get();
        activity.findViewById(R.id.button_go_to_client_profile).performClick();
        Intent expectedIntent = new Intent(activity, ProfileActivity.class);
        Intent actual = shadowOf(RuntimeEnvironment.application).getNextStartedActivity();
        Assert.assertEquals(expectedIntent.getComponent(), actual.getComponent());
    }

    @After
    public void tearDown() {
        // Destroy activity after every test
        activityController
                .pause()
                .stop()
                .destroy();
    }
}
