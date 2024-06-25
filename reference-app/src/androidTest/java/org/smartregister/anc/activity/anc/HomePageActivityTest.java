package org.smartregister.anc.activity.anc;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSubstring;

import androidx.test.espresso.action.ViewActions;
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
    public void A_setUp() throws InterruptedException {
        utils.logIn(Constants.ancConstants.ancUsername, Constants.ancConstants.ancPassword);
    }

    @Test
    public void B_SearchBarPresent() {
        onView(withId(R.id.search_bar_layout)).check(matches(isDisplayed()));

    }
    @Test
    public void C_SearchPatientByName() throws InterruptedException {

        onView(withId(R.id.edt_search)).perform(typeText(Configs.TestDataConfigs.clientName), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.patient_name)).check(matches(isDisplayed()));
        Thread.sleep(1000);
        onView(withId(R.id.btn_search_cancel)).perform(click());
      }
        @Test
    public void C_SearchPatientByID() throws InterruptedException {

        onView(withId(R.id.edt_search)).perform(typeText(Configs.TestDataConfigs.clientID), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.patient_name)).check(matches(isDisplayed()));
        Thread.sleep(1000);
        onView(withId(R.id.btn_search_cancel)).perform(click());
      }

    @Test
    public void D_AdvancedSearch() throws InterruptedException {
        onView(withId(R.id.action_search)).perform(click());
        Thread.sleep(2000);
//        onView(withId(R.id.anc_id)).check(matches(isDisplayed()));
//        Thread.sleep(2000);
        onView(withId(R.id.action_clients)).perform(click());


    }

    @Test
    public void E_OpenLibrary() throws InterruptedException {
        onView(withId(R.id.action_library)).perform(click());
        Thread.sleep(2000);
      //  onView(withId(R.id.library_item_layout)).atPosition(1).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.library_toolbar_title)).check(matches(isDisplayed()));
        onView(withId(R.id.action_clients)).perform(click());


    }
}

