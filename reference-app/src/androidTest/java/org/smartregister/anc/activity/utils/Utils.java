package org.smartregister.anc.activity.utils;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import androidx.test.espresso.core.internal.deps.guava.collect.Iterables;
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import androidx.test.runner.lifecycle.Stage;

import com.vijay.jsonwizard.activities.JsonFormActivity;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
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


//    public static Matcher<View> withRecyclerViewId(final int recyclerViewId) {
//        return new TypeSafeMatcher<View>() {
//            @Override
//            public void describeTo(Description description) {
//                description.appendText("RecyclerView with ID: " + recyclerViewId);
//            }
//            @Override
//            public boolean matchesSafely(View view) {
//                return view.getId() == recyclerViewId;
//            }
//        };
//    }

}

