package org.smartregister.anc.activity.anc;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withSubstring;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.Matchers.is;

import android.app.Activity;

import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.vijay.jsonwizard.activities.JsonFormActivity;

import org.json.JSONObject;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.runners.MethodSorters;
import org.smartregister.Context;
import org.smartregister.anc.R;
import org.smartregister.anc.activity.LoginActivity;
import org.smartregister.anc.activity.utils.Constants;
import org.smartregister.anc.activity.utils.Utils;
@LargeTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4.class)

public class RegisterFamilyMemberPageTests {

 @Rule

    public ActivityScenarioRule<LoginActivity> mActivityScenario = new ActivityScenarioRule<>(LoginActivity.class);

 Utils utils = new Utils();
 //JsonFormActivity jsonFormActivity = new JsonFormActivity();


 @Test
 public void a_SetUp() throws InterruptedException {
     utils.logIn(Constants.ancConstants.ancUsername, Constants.ancConstants.ancPassword);
 }

 @Test
 public void b_AddAFamilyMember() throws Throwable {
     onView(withId(R.id.action_register)).perform(click());
     //get Activity
     Activity activity = utils.getCurrentActivity();
     onView(withId(Utils.getViewId((JsonFormActivity) activity, "step1:first_name"))).perform(typeText("espresso"), closeSoftKeyboard());
     onView(withId(Utils.getViewId((JsonFormActivity) activity, "step1:last_name"))).perform(typeText("Tester "), closeSoftKeyboard());
     onView(withId(Utils.getViewId((JsonFormActivity) activity, "step1:dob_unknown"))).perform(click());
     onView(withId(Utils.getViewId((JsonFormActivity) activity, "step1:age_entered"))).perform(typeText("28"), closeSoftKeyboard());
     onView(withId(Utils.getViewId((JsonFormActivity) activity, "step1:home_address"))).perform(typeText("28th street Ng"), closeSoftKeyboard());
     onView(withId(Utils.getViewId((JsonFormActivity) activity, "step1:phone_number"))).perform(typeText("+254701000000"), closeSoftKeyboard());
     onView(withId(Utils.getViewId((JsonFormActivity) activity, "step1:reminders"))).perform(click());
     onView(withSubstring("Yes")).perform(click());
     onView(withId(R.id.action_save)).perform(click());
     Thread.sleep(3000);
     onView(withText("espresso Tester")).check(matches(isDisplayed()));

 }

//Always run below test after the test above
@Test
 public void c_RemoveFamilyMemberAdded() throws Throwable {

     onView(withText("Espresso Tester")).perform(click());
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
    public void d_AddMemberWithMissingMandatoryFields() throws Throwable {
     onView(withId(R.id.action_register)).perform(click());
     Activity activity = utils.getCurrentActivity();
     onView(withId(Utils.getViewId((JsonFormActivity) activity, "step1:first_name"))).perform(typeText("espresso"), closeSoftKeyboard());
     onView(withId(Utils.getViewId((JsonFormActivity) activity, "step1:last_name"))).perform(typeText("Tester "), closeSoftKeyboard());
     onView(withId(Utils.getViewId((JsonFormActivity) activity, "step1:dob_unknown"))).perform(click());
     onView(withId(Utils.getViewId((JsonFormActivity) activity, "step1:age_entered"))).perform(typeText("28"), closeSoftKeyboard());
     onView(withId(Utils.getViewId((JsonFormActivity) activity, "step1:home_address"))).perform(typeText("28th street Ng"), closeSoftKeyboard());
     onView(withId(Utils.getViewId((JsonFormActivity) activity, "step1:phone_number"))).perform(typeText("+254701000000"), closeSoftKeyboard());
     onView(withId(Utils.getViewId((JsonFormActivity) activity, "step1:reminders"))).perform(click());
     onView(withId(R.id.action_save)).perform(click());
     onView(withSubstring("Found 1 error(s) in the form. Please correct them to submit.")).check(matches(isDisplayed()));
     Thread.sleep(500);

 }





     //    JSONObject  jsonObject = jsonFormActivity.getmJSONObject();
//    jsonObject.getString("first_name");










}
