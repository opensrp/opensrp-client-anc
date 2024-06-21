package org.smartregister.anc.activity.utils;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import org.smartregister.anc.R;

public class Utils {


    public void logIn(String username, String password) throws InterruptedException {
        onView(withId(R.id.login_user_name_edit_text)).perform(typeText(username), closeSoftKeyboard());
        onView(withId(R.id.login_password_edit_text)).perform(typeText(password), closeSoftKeyboard());
        onView(withId(R.id.login_login_btn)).perform(click());
        Thread.sleep(30000);
    }
}
