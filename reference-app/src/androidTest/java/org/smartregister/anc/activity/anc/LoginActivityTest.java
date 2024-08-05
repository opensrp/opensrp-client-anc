package org.smartregister.anc.activity.anc;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;

import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;

import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static androidx.test.espresso.matcher.ViewMatchers.withText;
import org.smartregister.anc.activity.LoginActivity;



import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;


import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.smartregister.anc.R;
import org.smartregister.anc.activity.utils.Constants;
import org.smartregister.anc.activity.utils.Utils;


@LargeTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {
    Utils utils = new Utils();

    @Rule
    public ActivityScenarioRule<LoginActivity> mActivityScenario = new ActivityScenarioRule<>(LoginActivity.class);




    @Test
    public void eTestShowPassword(){
        onView(withId(R.id.login_password_edit_text)).perform(typeText(Constants.ancConstants.ancPassword),closeSoftKeyboard());
        onView(withId(R.id.login_show_password_checkbox)).perform(click(),closeSoftKeyboard());
        onView(withId(R.id.login_password_edit_text)).check(matches(withText(Constants.ancConstants.ancPassword)));

    }

    @Test
    public void fTestSuccessfulLogin() throws InterruptedException {
        onView(withId(R.id.login_user_name_edit_text)).perform(typeText(Constants.ancConstants.ancUsername), closeSoftKeyboard());
        onView(withId(R.id.login_password_edit_text)).perform(typeText(Constants.ancConstants.ancPassword), closeSoftKeyboard());
        onView(withId(R.id.login_login_btn)).perform(click());
        Thread.sleep(30000);
        //fix error caused by the id edt_search
        onView(withId(R.id.edt_search)).check(matches(isDisplayed()));

   }

    @Test
    public void cTestIncorrectUsername() throws InterruptedException {
        onView(withId(R.id.login_user_name_edit_text)).perform(typeText("Beba"),closeSoftKeyboard());
        onView(withId(R.id.login_password_edit_text)).perform(typeText(Constants.ancConstants.ancPassword),closeSoftKeyboard());
        onView(withId(R.id.login_login_btn)).perform(click());
        Thread.sleep(20000);
        onView(withText("Please check the credentials")).check(matches(isDisplayed()));



    }


    @Test
    public void dTestIncorrectPassword() throws InterruptedException {
        onView(withId(R.id.login_user_name_edit_text)).perform(typeText(Constants.ancConstants.ancUsername),closeSoftKeyboard());
        onView(withId(R.id.login_password_edit_text)).perform(typeText("koko"),closeSoftKeyboard());
        onView(withId(R.id.login_login_btn)).perform(click());
        Thread.sleep(20000);
        onView(withText("Please check the credentials")).check(matches(isDisplayed()));


    }

    @Test
    public void aTestEmptyUsername() throws InterruptedException {
        onView(withId(R.id.login_user_name_edit_text)).perform(typeText(" "),closeSoftKeyboard());
        onView(withId(R.id.login_password_edit_text)).perform(typeText(Constants.ancConstants.ancPassword),closeSoftKeyboard());
        onView(withId(R.id.login_login_btn)).perform(click());
        Thread.sleep(20000);
        onView(withText("Please check the credentials")).check(matches(isDisplayed()));


    }

    @Test
    public void bTestEmptyPassword() throws InterruptedException {
        onView(withId(R.id.login_user_name_edit_text)).perform(typeText(Constants.ancConstants.ancPassword),closeSoftKeyboard());
        onView(withId(R.id.login_password_edit_text)).perform(typeText(" "),closeSoftKeyboard());
        onView(withId(R.id.login_login_btn)).perform(click());
        Thread.sleep(20000);
        onView(withText("Please check the credentials")).check(matches(isDisplayed()));


    }
}
