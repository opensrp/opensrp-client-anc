package org.smartregister.anc.activity.anc;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSubstring;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

//import static org.smartregister.anc.activity.utils.Utils.withRecyclerViewId;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;

import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

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

public class HomePageActivityTest {
    @Rule
    public ActivityScenarioRule<LoginActivity> mActivityScenario = new ActivityScenarioRule<>(LoginActivity.class);

    private Utils utils = new Utils();

    @Test
    public void asetUp() throws InterruptedException {
        utils.logIn(Constants.ancConstants.ancUsername, Constants.ancConstants.ancPassword);
    }

    @Test
    public void bSearchBarPresent() {
        onView(withId(R.id.search_bar_layout)).check(matches(isDisplayed()));

    }

    @Test
    public void cSearchPatientByName() throws InterruptedException {

        onView(withId(R.id.edt_search)).perform(typeText(Configs.TestDataConfigs.clientName), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.patient_name)).check(matches(isDisplayed()));
        Thread.sleep(1000);
        onView(withId(R.id.btn_search_cancel)).perform(click());
    }

    @Test
    public void cSearchPatientByID() throws InterruptedException {

        onView(withId(R.id.edt_search)).perform(typeText(Configs.TestDataConfigs.clientID), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.patient_name)).check(matches(isDisplayed()));
        Thread.sleep(1000);
        onView(withId(R.id.btn_search_cancel)).perform(click());
    }

    @Test
    public void dAdvancedSearch() throws InterruptedException {
        onView(withId(R.id.action_search)).perform(click());
        onView(withId(R.id.qrCodeButton)).check(matches(isDisplayed()));
        Thread.sleep(1000);
        onView(withId(R.id.action_clients)).perform(click());


    }

    @Test
    public void eOpenLibrary() throws InterruptedException {
        onView(withId(R.id.action_library)).perform(click());
        onView(withId(R.id.library_toolbar_title)).check(matches(isDisplayed()));
        Thread.sleep(1000);
        onView(withId(R.id.action_clients)).perform(click());


    }

    @Test
    public void fUserCanAccessANCRegistrationForm() throws InterruptedException {
        onView(withContentDescription("Register")).perform(click());
        Thread.sleep(1500);
        onView(withId(R.id.scan_button)).check(matches(isDisplayed()));
    }

    @Test
    public void gUserCanAccessProfile() throws InterruptedException {
        onView(withContentDescription("Me")).perform(click());
        Thread.sleep(1500);
        onView(withId(R.id.locationImageView)).check(matches(isDisplayed()));
    }

    @Test
    public void hUserCanClickOnAPatient() throws InterruptedException {
        onView(allOf(withId(R.id.recycler_view), isDisplayed()))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        Thread.sleep(2000);
        onView(withId(R.id.btn_profile_registration_info)).check(matches(isDisplayed()));
    }

    @Test
    public void iUserCanClickOnNextButtonOnRegister() throws InterruptedException {
        Thread.sleep(2000);
        onView(allOf(withId(R.id.recycler_view), isDisplayed()))
                .perform(RecyclerViewActions.scrollToPosition(20));
        Thread.sleep(2000);
        onView(withId(R.id.btn_next_page)).perform(click());
        onView(withId(R.id.btn_previous_page)).check(matches(isDisplayed()));
    }

    @Test
    public void jUserCanClickOnThePreviousBtnOnRegister() throws InterruptedException {
        Thread.sleep(2000);
        onView(allOf(withId(R.id.recycler_view), isDisplayed()))
                .perform(RecyclerViewActions.scrollToPosition(20));
        Thread.sleep(2000);
        onView(withId(R.id.btn_next_page)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.btn_previous_page)).perform(scrollTo()).perform(click());
        Thread.sleep(2000);
        onView(withText("Page 1 of 9")).perform(scrollTo()).check(matches(isDisplayed()));
    }

    @Test
    public void kLogOut() throws InterruptedException {
        utils.logOut();
    }
}


