package org.smartregister.anc.activity.anc;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.doubleClick;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static androidx.test.espresso.matcher.ViewMatchers.withSubstring;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.app.Activity;

import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.PickerActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.vijay.jsonwizard.activities.JsonFormActivity;

import org.junit.AfterClass;
import org.junit.Before;
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

    @Before
    public void A_setUp() throws InterruptedException {
        utils.logIn(Constants.ancConstants.ancUsername, Constants.ancConstants.ancPassword);
        Thread.sleep(1000);
    }

    @Test
    public void B_StartContactVisitAndNavigateToProfile() throws Throwable {

        onView(withId(R.id.edt_search)).perform(typeText(Configs.TestDataConfigs.clientName), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.patient_name)).check(matches(isDisplayed()));
        onView(withId(R.id.due_button_wrapper)).perform(click());
        Thread.sleep(2000);

        onView(withSubstring("Profile")).perform(click());
        Thread.sleep(2000);

        onView(withText("Demographic Info")).check(matches(isDisplayed()));
        onView(withText("Primary")).perform(click());
        onView(withText("Married or living together")).perform(scrollTo(), click());
        onView(withText("Formal employment")).perform(scrollTo(), click());
        onView(withText("NEXT")).perform(scrollTo(), click());

//      Skip Current Pregnancy page and navigate to Obstetric History
        onView(withText("Current Pregnancy")).check(matches(isDisplayed()));
        onView(withId(R.id.next_icon)).perform(click());
        Thread.sleep(2000);

//        onView(withText("Current Pregnancy")).check(matches(isDisplayed()));
//        onView(withSubstring("specify date")).perform(click());
//        onView(withId(R.id.date_picker)).perform(PickerActions.setDate(2024, 02,07));
//        Thread.sleep(1000);
//        onView(withText("DONE")).perform(click());
//        Thread.sleep(1000);
//
//
//        Activity activity = utils.getCurrentActivity();
//        onView(withSubstring("ultrasound date")).perform(scrollTo(), click());
//        onView(withId(R.id.date_picker)).perform(PickerActions.setDate(2024, 04,24));
//        Thread.sleep(1000);
//        onView(withText("DONE")).perform(click());
//        Thread.sleep(1000);
//
//
//        onView(withId(Utils.getViewId((JsonFormActivity) activity, "step2:ultrasound_gest_age_wks"))).perform(scrollTo(),
//                typeText("6"), ViewActions.closeSoftKeyboard());
//        Thread.sleep(1000);
//        onView(withId(Utils.getViewId((JsonFormActivity) activity, "step2:ultrasound_gest_age_days"))).perform(scrollTo(),
//                typeText("3"), ViewActions.closeSoftKeyboard());
//        Thread.sleep(1000);
//        onView(withId(Utils.getViewId((JsonFormActivity) activity, "step2:ultrasound_gest_age_selection"))).perform(click());
//        Thread.sleep(3000);
//        onView(withText("NEXT")).perform(scrollTo(), click());
//        Thread.sleep(1000);

        onView(withText("Obstetric History")).check(matches(isDisplayed()));
        onView(withText("1")).perform(scrollTo(), click());
        onView(withText("NEXT")).perform(scrollTo(), click());
        Thread.sleep(2000);

        onView(withText("Medical History")).check(matches(isDisplayed()));
        onView(withText("Any allergies?")).check(matches(isDisplayed()));
        onView(withText("Calcium")).perform(scrollTo(), click());
        onView(withText("Any surgeries?")).check(matches(isDisplayed()));
        onView(withId(R.id.scroll_view)).perform(ViewActions.swipeUp());
        onView(withText("Removal of ovarian cysts")).perform(scrollTo(), click());
        onView(withId(R.id.scroll_view)).perform(ViewActions.swipeUp());
        onView(withText("Any chronic or past health conditions?")).check(matches(isDisplayed()));
        onView(withId(R.id.scroll_view)).perform(ViewActions.swipeUp());
        onView(withText("Hypertension")).perform(scrollTo(), click());

        onView(withText("NEXT")).perform(scrollTo(), click());
        Thread.sleep(2000);

        onView(withText("Immunisation Status")).check(matches(isDisplayed()));
        onView(withText("TTCV immunisation status")).check(matches(isDisplayed()));
        onView(withText("Fully immunized")).perform(scrollTo(), click());
        onView(withText("Flu immunisation status")).check(matches(isDisplayed()));
        onView(withText("No doses")).perform(scrollTo(), click());
        onView(withText("NEXT")).perform(scrollTo(), click());
        Thread.sleep(2000);

        onView(withText("Medications")).check(matches(isDisplayed()));
        onView(withText("Antacids")).perform(scrollTo(), click());
        onView(withText("Aspirin")).perform(scrollTo(), click());
        onView(withId(R.id.scroll_view)).perform(ViewActions.swipeUp());
        onView(withText("Other antibiotics")).perform(scrollTo(), click());
        onView(withText("Asthma")).perform(scrollTo(), click());

        onView(withText("NEXT")).perform(scrollTo(), click());
        Thread.sleep(2000);

        onView(withText("Woman's Behaviour")).check(matches(isDisplayed()));
        onView(withText("Daily caffeine intake")).check(matches(isDisplayed()));
        onView(withText("More than 2 cups of coffee (brewed, filtered, instant or espresso)")).perform(scrollTo(), click());
        onView(withText("Uses tobacco products?")).check(matches(isDisplayed()));
        onView(withText("Yes")).perform(scrollTo(), click());
        onView(withText("Anyone in the household smokes tobacco products?")).check(matches(isDisplayed()));
        onView(withText("Yes")).perform(scrollTo(), click());
        onView(withText("Uses (male or female) condoms during sex?")).check(matches(isDisplayed()));
        onView(withText("Yes")).perform(scrollTo(), click());
        onView(withText("Clinical enquiry for alcohol and other substance use done?")).check(matches(isDisplayed()));
        onView(withText("Yes")).perform(scrollTo(), click());
        onView(withText("Uses alcohol and/or other substances?")).check(matches(isDisplayed()));
        onView(withText("None")).perform(scrollTo(), click());

        onView(withText("NEXT")).perform(scrollTo(), click());
        Thread.sleep(2000);

        onView(withText("Partner's HIV Status")).check(matches(isDisplayed()));
        onView(withText("Negative")).perform(scrollTo(), click());
        onView(withText("SUBMIT")).perform(scrollTo(), click());
        Thread.sleep(2000);

    }
    @AfterClass
    public static void tearDown() throws InterruptedException {
        onView(withContentDescription("Me")).perform(click());
        onView(withId(R.id.logout_text)).perform(click());
        Thread.sleep(1500);
        onView(withId(R.id.login_login_btn)).check(matches(isDisplayed()));
    }
}
