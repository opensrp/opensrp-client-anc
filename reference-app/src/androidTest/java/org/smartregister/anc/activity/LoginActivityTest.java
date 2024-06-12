package org.smartregister.anc.activity;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import org.smartregister.anc.activity.LoginActivity;



import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.TypeTextAction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.smartregister.anc.R;

@LargeTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> mActivityScenario = new ActivityScenarioRule<>(LoginActivity.class);
    public String correctPassword = "Amani123";




    @Test
    public void E_testShowPassword(){
        onView(withId(R.id.login_password_edit_text)).perform(typeText(correctPassword),closeSoftKeyboard());
        onView(withId(R.id.login_show_password_checkbox)).perform(click(),closeSoftKeyboard());
        onView(withId(R.id.login_password_edit_text)).check(matches(withText(correctPassword)));

    }

    @Test
    public void F_testSuccessfulLogin() throws InterruptedException {
        onView(withId(R.id.login_user_name_edit_text)).perform(typeText("demo"), closeSoftKeyboard());
        onView(withId(R.id.login_password_edit_text)).perform(typeText(correctPassword), closeSoftKeyboard());
        onView(withId(R.id.login_login_btn)).perform(click());
        Thread.sleep(30000);
        //fix error caused by the id edt_search
        onView(withId(R.id.edt_search)).check(matches(isDisplayed()));

   }

    @Test
    public void C_testIncorrectUsername() throws InterruptedException {
        onView(withId(R.id.login_user_name_edit_text)).perform(typeText("Beba"),closeSoftKeyboard());
        onView(withId(R.id.login_password_edit_text)).perform(typeText(correctPassword),closeSoftKeyboard());
        onView(withId(R.id.login_login_btn)).perform(click());
        Thread.sleep(20000);
        onView(withText("Please check the credentials")).check(matches(isDisplayed()));



    }


    @Test
    public void D_testIncorrectPassword() throws InterruptedException {
        onView(withId(R.id.login_user_name_edit_text)).perform(typeText("demo"),closeSoftKeyboard());
        onView(withId(R.id.login_password_edit_text)).perform(typeText("mani"),closeSoftKeyboard());
        onView(withId(R.id.login_login_btn)).perform(click());
        Thread.sleep(20000);
        onView(withText("Please check the credentials")).check(matches(isDisplayed()));


    }

    @Test
    public void A_testEmptyUsername() throws InterruptedException {
        onView(withId(R.id.login_user_name_edit_text)).perform(typeText(" "),closeSoftKeyboard());
        onView(withId(R.id.login_password_edit_text)).perform(typeText(correctPassword),closeSoftKeyboard());
        onView(withId(R.id.login_login_btn)).perform(click());
        Thread.sleep(20000);
        onView(withText("Please check the credentials")).check(matches(isDisplayed()));


    }

    @Test
    public void B_testEmptyPassword() throws InterruptedException {
        onView(withId(R.id.login_user_name_edit_text)).perform(typeText("Beba"),closeSoftKeyboard());
        onView(withId(R.id.login_password_edit_text)).perform(typeText(" "),closeSoftKeyboard());
        onView(withId(R.id.login_login_btn)).perform(click());
        Thread.sleep(20000);
        onView(withText("Please check the credentials")).check(matches(isDisplayed()));


    }








}
