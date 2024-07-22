package org.smartregister.anc.activity.anc;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;

import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSubstring;
import static androidx.test.espresso.matcher.ViewMatchers.withText;



import android.app.Activity;

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

public class RegisterFamilyMemberPageTests {

 @Rule

    public ActivityScenarioRule<LoginActivity> mActivityScenario = new ActivityScenarioRule<>(LoginActivity.class);

 Utils utils = new Utils();



@Test
public void aSetUp() throws InterruptedException {
 utils.logIn(Constants.ancConstants.ancUsername, Constants.ancConstants.ancPassword);
}

 @Test
 public void bAddAFamilyMember() throws Throwable {
    utils.addAFamilyMember();
     Thread.sleep(3000);
     onView(withText(Configs.TestDataConfigs.firstAndLastName)).check(matches(isDisplayed()));

 }

//Always run below test after the test above
@Test
 public void cRemoveFamilyMemberAdded() throws Throwable {

     onView(withText(Configs.TestDataConfigs.firstAndLastName)).perform(click());
     Thread.sleep(2000);
     onView(withId(R.id.overflow_menu_item)).perform(click());
     onView(withText("Close ANC Record")).perform(click());
     Thread.sleep(2000);
     Activity activity = utils.getCurrentActivity();
     onView(withId(Utils.getViewId((JsonFormActivity) activity, "step1:anc_close_reason"))).perform(click());
     onView(withSubstring("Moved away")).perform(click());
     onView(withId(R.id.action_save)).perform(click());


 }

 @Test
    public void dAddMemberWithMissingMandatoryFields() throws Throwable {
     onView(withId(R.id.action_register)).perform(click());
     Activity activity = utils.getCurrentActivity();
     onView(withId(Utils.getViewId((JsonFormActivity) activity, "step1:first_name"))).perform(typeText(Configs.TestDataConfigs.firstName), closeSoftKeyboard());
     onView(withId(Utils.getViewId((JsonFormActivity) activity, "step1:last_name"))).perform(typeText(Configs.TestDataConfigs.lastName), closeSoftKeyboard());
     onView(withId(Utils.getViewId((JsonFormActivity) activity, "step1:dob_unknown"))).perform(click());
     onView(withId(Utils.getViewId((JsonFormActivity) activity, "step1:age_entered"))).perform(typeText(Configs.TestDataConfigs.clientAge),closeSoftKeyboard());
     onView(withId(Utils.getViewId((JsonFormActivity) activity, "step1:home_address"))).perform(typeText(Configs.TestDataConfigs.clientAddress), closeSoftKeyboard());
     onView(withId(Utils.getViewId((JsonFormActivity) activity, "step1:phone_number"))).perform(typeText(Configs.TestDataConfigs.phoneNumber), closeSoftKeyboard());
     onView(withId(Utils.getViewId((JsonFormActivity) activity, "step1:reminders"))).perform(click());
     onView(withId(R.id.action_save)).perform(click());
     onView(withSubstring("Found 1 error(s) in the form. Please correct them to submit.")).check(matches(isDisplayed()));
     Thread.sleep(500);

 }














}
