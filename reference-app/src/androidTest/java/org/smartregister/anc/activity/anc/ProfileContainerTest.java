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
public class ProfileContainerTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> mActivityScenario = new ActivityScenarioRule<>(LoginActivity.class);

    private Utils utils = new Utils();

    @Test
    public void A_setUp() throws InterruptedException {
        utils.logIn(Constants.ancConstants.ancUsername, Constants.ancConstants.ancPassword);
    }

    @Test
    public void B_StartContactVisitAndNavigateToProfile() throws Throwable {

        onView(withId(R.id.edt_search)).perform(typeText(Configs.TestDataConfigs.clientName), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.patient_name)).check(matches(isDisplayed()));
        onView(withId(R.id.due_button_wrapper)).perform(click());
        Thread.sleep(4000);

        Activity activity = utils.getCurrentActivity();
        onView(withSubstring("Profile")).perform(click());
        Thread.sleep(2000);

        onView(withText("Demographic Info")).check(matches(isDisplayed()));
        onView(withText("Primary")).perform(click());
        onView(withText("Married or living together")).perform(scrollTo(), click());
        onView(withText("Formal employment")).perform(scrollTo(), click());
        onView(withText("NEXT")).perform(scrollTo(), click());
        Thread.sleep(2000);
    }
}
