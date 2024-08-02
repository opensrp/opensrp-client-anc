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

import org.junit.AfterClass;
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

//        onView(withText("Current Pregnancy")).check(matches(isDisplayed()));
//        onView(withId(Utils.getViewId((JsonFormActivity) activity, "step1:lmp_known")));
//        onView(withSubstring("Yes")).perform(click());
//        onView(withText("08-03-2024")).perform(click());
//
//        onView(withId(Utils.getViewId((JsonFormActivity) activity, "step2:ultrasound_done")));
//        onView(withSubstring("Yes")).perform(scrollTo(), click());
//        onView(withText("22-04-2024")).perform(click());
//
//        onView(withSubstring("GA from ultrasound-weeks")).perform(scrollTo(), typeText("6"), ViewActions.closeSoftKeyboard());
//        onView(withSubstring("GA from ultrasound-days")).perform(scrollTo(), typeText("3"), ViewActions.closeSoftKeyboard());
//
//        onView(withText("Select preferred gestational age")).check(matches(isDisplayed()));
//        onView(withSubstring("Using LMP")).perform(scrollTo(), click());

        // Skip Current Pregnancy page and navigate to Obstetric History
        onView(withText("Current Pregnancy")).check(matches(isDisplayed()));
        onView(withId(R.id.next_icon)).perform(click());
        Thread.sleep(2000);
        onView(withText("Obstetric History")).check(matches(isDisplayed()));
        onView(withText("1")).perform(scrollTo(), click());
        onView(withText("NEXT")).perform(scrollTo(), click());
        Thread.sleep(2000);

        onView(withText("Medical History")).check(matches(isDisplayed()));
        onView(withText("Any allergies?")).check(matches(isDisplayed()));
        onView(withText("Penicillin")).perform(scrollTo(), click());
        onView(withText("Any surgeries?")).check(matches(isDisplayed()));
        onView(withText("Removal of ovarian cysts")).perform(scrollTo(), click());
        onView(withId(R.id.scroll_view)).perform(ViewActions.swipeUp());
        onView(withText("Any chronic or past health conditions?")).check(matches(isDisplayed()));
        onView(withText("Hypertension")).perform(scrollTo(), click());

        onView(withText("NEXT")).perform(scrollTo(), click());
        Thread.sleep(2000);

//        onView(withText("Immunisation Status")).check(matches(isDisplayed()));
//        onView(withText("Fully immunized")).perform(scrollTo(), click());
//        onView(withText("No doses")).perform(scrollTo(), click());
//        onView(withText("NEXT")).perform(scrollTo(), click());
//        Thread.sleep(2000);
//
//        onView(withText("Medications")).check(matches(isDisplayed()));
//        onView(withText("Antacids")).perform(scrollTo(), click());
//        onView(withText("Aspirin")).perform(scrollTo(), click());
//        onView(withId(R.id.scroll_view)).perform(ViewActions.swipeUp());
//        onView(withText("Other antibiotics")).perform(scrollTo(), click());
//        onView(withText("Hematinc")).perform(scrollTo(), click());
//
//        onView(withText("NEXT")).perform(scrollTo(), click());
    }
    @AfterClass
    public static void tearDown() throws InterruptedException {
        // Perform logout
        onView(withContentDescription("Me")).perform(click());
        onView(withId(R.id.logout_text)).perform(click());
        Thread.sleep(1500);
        onView(withId(R.id.login_login_btn)).check(matches(isDisplayed()));
    }
}
