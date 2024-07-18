package org.smartregister.anc.activity.anc;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSubstring;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.app.Activity;

import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.vijay.jsonwizard.activities.JsonFormActivity;

import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.smartregister.anc.R;
import org.smartregister.anc.activity.LoginActivity;
import org.smartregister.anc.activity.utils.Configs;
import org.smartregister.anc.activity.utils.Constants;
import org.smartregister.anc.activity.utils.Utils;

@LargeTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4.class)
public class ContactsActivityTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> mActivityScenario = new ActivityScenarioRule<>(LoginActivity.class);

    private Utils utils = new Utils();

    @Test
    public void A_setUp() throws InterruptedException {
        utils.logIn(Constants.ancConstants.ancUsername, Constants.ancConstants.ancPassword);
    }

    @Test
    public void B_StartContactVisit() throws Throwable {

//        Thread.sleep(2000);

        onView(withId(R.id.edt_search)).perform(typeText(Configs.TestDataConfigs.clientName), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.patient_name)).check(matches(isDisplayed()));

        onView(withId(R.id.due_button_wrapper)).perform(click());
        Thread.sleep(2000);

        try {
            onView(withText("Form Update")).check(matches(isDisplayed()));
            onView(withText("OK")).perform(click());
            Thread.sleep(2000);
        } catch (NoMatchingViewException e) {
        }
        onView(withId(R.id.contact_title)).check(matches(isDisplayed()));
        onView(withContentDescription("First contact"));
        Activity activity = utils.getCurrentActivity();
        onView(withId(Utils.getViewId((JsonFormActivity) activity, "step1:contact_reason"))).perform(click());
        onView(withSubstring("First contact")).perform(click());;
        Thread.sleep(4000);
        onView(withSubstring("None")).perform(click());
        Thread.sleep(2000);

    }

}

