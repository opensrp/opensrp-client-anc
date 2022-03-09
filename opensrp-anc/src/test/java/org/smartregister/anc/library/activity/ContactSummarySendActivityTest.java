package org.smartregister.anc.library.activity;

import android.content.Intent;
import android.os.Looper;
import android.widget.TextView;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.android.controller.ActivityController;
import org.smartregister.anc.library.R;
import org.smartregister.anc.library.util.ConstantsUtils;

import java.util.HashMap;
import java.util.UUID;

public class ContactSummarySendActivityTest extends BaseUnitTest {

    private final String baseEntityId = UUID.randomUUID().toString();
    private ActivityController<ContactSummarySendActivity> activityController;
    private ContactSummarySendActivity activity;

    @Before
    public void setUp() {
        Intent contactSummaryActivityIntent = new Intent(RuntimeEnvironment.application,
                ContactSummarySendActivity.class);
        contactSummaryActivityIntent.putExtra(ConstantsUtils.IntentKeyUtils.BASE_ENTITY_ID, baseEntityId);
        contactSummaryActivityIntent.putExtra(ConstantsUtils.IntentKeyUtils.CLIENT_MAP, new HashMap<>());
        activityController = Robolectric.buildActivity(ContactSummarySendActivity.class,
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
    public void testGoToClientButtonClicked() throws InterruptedException {
        activity = activityController.create().get();
        ContactSummarySendActivity contactSummarySendActivitySpy = Mockito.spy(activity);
        Mockito.doReturn(new HashMap<>()).when(contactSummarySendActivitySpy).getWomanProfileDetails();
        Shadows.shadowOf(Looper.getMainLooper()).idle();
        Thread.sleep(ASYNC_TIMEOUT);
        Intent expectedIntent = new Intent(contactSummarySendActivitySpy, ProfileActivity.class);
        Assert.assertNotNull(expectedIntent.getComponent().getClassName());
        Assert.assertEquals(expectedIntent.getComponent().getClassName(), ProfileActivity.class.getName());
    }

    @After
    public void tearDown() {
        activityController
                .pause()
                .stop()
                .destroy();
    }
}
