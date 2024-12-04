package org.smartregister.anc.activity.utils;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withInputType;
import static androidx.test.espresso.matcher.ViewMatchers.withSubstring;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

import static org.apache.commons.lang3.Validate.isAssignableFrom;

import android.app.Activity;

import androidx.test.espresso.core.internal.deps.guava.collect.Iterables;
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import androidx.test.runner.lifecycle.Stage;

import com.vijay.jsonwizard.activities.JsonFormActivity;

import org.junit.Test;
import org.smartregister.anc.R;


public class Utils {


    public void logIn(String username, String password) throws InterruptedException {
        onView(withId(R.id.login_user_name_edit_text)).perform(typeText(username), closeSoftKeyboard());
        onView(withId(R.id.login_password_edit_text)).perform(typeText(password), closeSoftKeyboard());
        onView(withId(R.id.login_login_btn)).perform(click());
        Thread.sleep(30000);
    }




    public Activity getCurrentActivity() throws Throwable {
        getInstrumentation().waitForIdleSync();
        final Activity[] activity = new Activity[1];
        runOnUiThread(() -> {
            java.util.Collection<Activity> activities = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED);
            activity[0] = (Activity) Iterables.getOnlyElement(activities);
        });
        return activity[0];
    }

    public static int getViewId(JsonFormActivity jsonFormActivity, String key)
    {
        return jsonFormActivity.getFormDataView(key).getId();


    }

    public void addAFamilyMember() throws Throwable {
        onView(withId(R.id.action_register)).perform(click());
        //get Activity
        Activity activity = getCurrentActivity();
        onView(withId(Utils.getViewId((JsonFormActivity) activity, "step1:first_name"))).perform(typeText(Configs.TestDataConfigs.firstName), closeSoftKeyboard());
        onView(withId(Utils.getViewId((JsonFormActivity) activity, "step1:last_name"))).perform(typeText(Configs.TestDataConfigs.lastName), closeSoftKeyboard());
        onView(withId(Utils.getViewId((JsonFormActivity) activity, "step1:dob_unknown"))).perform(click());
        onView(withId(Utils.getViewId((JsonFormActivity) activity, "step1:age_entered"))).perform(typeText(Configs.TestDataConfigs.clientAge),closeSoftKeyboard());
        onView(withId(Utils.getViewId((JsonFormActivity) activity, "step1:home_address"))).perform(typeText(Configs.TestDataConfigs.clientAddress), closeSoftKeyboard());
        onView(withId(Utils.getViewId((JsonFormActivity) activity, "step1:phone_number"))).perform(typeText(Configs.TestDataConfigs.phoneNumber), closeSoftKeyboard());
        onView(withId(Utils.getViewId((JsonFormActivity) activity, "step1:reminders"))).perform(click());
        onView(withSubstring("Yes")).perform(click());
        onView(withId(R.id.action_save)).perform(click());
        Thread.sleep(3000);
        onView(withText(Configs.TestDataConfigs.firstAndLastName)).check(matches(isDisplayed()));

    }

    public void logOut() throws InterruptedException {
        onView(withContentDescription("Me")).perform(click());
        onView(withId(R.id.logout_text)).perform(click());
        Thread.sleep(1500);
        onView(withId(R.id.login_login_btn)).check(matches(isDisplayed()));

    }

}







