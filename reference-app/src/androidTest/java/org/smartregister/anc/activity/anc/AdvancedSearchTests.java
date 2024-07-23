package org.smartregister.anc.activity.anc;


import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.swipeUp;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.anything;
import static org.hamcrest.core.AllOf.allOf;

import android.view.View;
import android.widget.DatePicker;

import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.PickerActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.espresso.matcher.ViewMatchers;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
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


public class AdvancedSearchTests {
    @Rule
    public ActivityScenarioRule<LoginActivity> mActivityScenario = new ActivityScenarioRule<>(LoginActivity.class);

    private Utils utils = new Utils();

    @Test
    public void A_setUp() throws InterruptedException {
        utils.logIn(Constants.ancConstants.ancUsername, Constants.ancConstants.ancPassword);
    }

    @Test
    public void userCanSearchOutsideAndInsideMyHealthFacilityByFirstName() throws InterruptedException {
        onView(withId(R.id.action_search)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.first_name)).perform(typeText(Configs.TestDataConfigs.firstName),ViewActions.closeSoftKeyboard());
        onView(withId(R.id.search)).perform(click());
        Thread.sleep(5000);
        Matcher<View> parentMatcher = withId(R.id.list_view_layout);
        onView(allOf(withId(R.id.recycler_view), withParent(parentMatcher))).perform(RecyclerViewActions.scrollTo(ViewMatchers.hasDescendant(withText("Terence Howard")))).check(matches(isDisplayed()));

    }
    @Test
    public void userCanSearchOutsideAndInsideMyHealthFacilityByLastName() throws InterruptedException {
        onView(withId(R.id.action_search)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.last_name)).perform(typeText(Configs.TestDataConfigs.lastName),ViewActions.closeSoftKeyboard());
        onView(withId(R.id.search)).perform(click());
        Matcher<View> parentMatcher = withId(R.id.list_view_layout);
        Thread.sleep(5000);
        onView(allOf(withId(R.id.recycler_view), withParent(parentMatcher))).perform(RecyclerViewActions.scrollTo(ViewMatchers.hasDescendant(withText("Two Direction")))).check(matches(isDisplayed()));

    }

    @Test
    public void userCanSearchOutsideAndInsideMyHealthFacilityByANCID() throws InterruptedException {
        onView(withId(R.id.action_search)).perform(click());
        Thread.sleep(2000);
        onView(allOf(withId(R.id.anc_id),isDescendantOfA(withId(R.id.nested_scroll_view)), withHint("ANC ID"))).perform(typeText(Configs.TestDataConfigs.clientID),ViewActions.closeSoftKeyboard());
        onView(withId(R.id.search)).perform(click());
        Thread.sleep(5000);
        Matcher<View> parentMatcher = withId(R.id.list_view_layout);
        onView(allOf(withId(R.id.recycler_view), withParent(parentMatcher))).perform(RecyclerViewActions.scrollTo(ViewMatchers.hasDescendant(withText("ID: 7138845")))).check(matches(isDisplayed()));

    }

    @Test
    public void userCanSearchOutsideAndInsideMyHealthFacilityByExpectedDateOfDelivery() throws InterruptedException {
        onView(withId(R.id.action_search)).perform(click());
        Thread.sleep(2000);
        onView(withHint("Expected date of delivery")).perform(click());
        onView(isAssignableFrom(DatePicker.class)).perform(PickerActions.setDate(2025, 4, 25));
        onView(withText("OK")).perform(click());
        onView(withId(R.id.search)).perform(click());
        Thread.sleep(5000);
        Matcher<View> parentMatcher = withId(R.id.list_view_layout);
        onView(allOf(withId(R.id.recycler_view), withParent(parentMatcher))).perform(RecyclerViewActions.scrollTo(ViewMatchers.hasDescendant(withText(Configs.TestDataConfigs.clientName2)))).check(matches(isDisplayed()));

    }
    @Test
    public void userCanSearchOutsideAndInsideMyHealthFacilityByDateOfBirth() throws InterruptedException{
        onView(withId(R.id.action_search)).perform(click());
        Thread.sleep(2000);
        onView(withHint("Expected date of delivery")).perform(swipeUp());
        onView(withId(R.id.dob)).perform(click());
        onView(isAssignableFrom(DatePicker.class)).perform(PickerActions.setDate(1992, 7, 19));
        onView(withText("OK")).perform(click());
        onView(withId(R.id.search)).perform(click());
        Thread.sleep(5000);
        Matcher<View> parentMatcher = withId(R.id.list_view_layout);
        onView(allOf(withId(R.id.recycler_view), withParent(parentMatcher))).perform(RecyclerViewActions.scrollTo(ViewMatchers.hasDescendant(withText(Configs.TestDataConfigs.clientName2)))).check(matches(isDisplayed()));

    }
    @Test
    public void userCanSearchOutsideAndInsideMyHealthFacilityByPhoneNumber() throws InterruptedException{
        onView(withId(R.id.action_search)).perform(click());
        Thread.sleep(2000);
        onView(withHint("Expected date of delivery")).perform(swipeUp());
        onView(withId(R.id.dob)).perform(swipeUp());
        onView(withId(R.id.phone_number)).perform(typeText(Configs.TestDataConfigs.phoneNumber));
        onView(withId(R.id.search)).perform(click());
        Thread.sleep(5000);
        Matcher<View> parentMatcher = withId(R.id.list_view_layout);
        onView(allOf(withId(R.id.recycler_view), withParent(parentMatcher))).perform(RecyclerViewActions.scrollTo(ViewMatchers.hasDescendant(withText(Configs.TestDataConfigs.clientName2)))).check(matches(isDisplayed()));

    }
    @Test
    public void userCanSearchOutsideAndInsideMyHealthFacilityByAlternateName()throws InterruptedException {
        onView(withId(R.id.action_search)).perform(click());
        Thread.sleep(2000);
        onView(withHint("Expected date of delivery")).perform(swipeUp());
        onView(withId(R.id.edd)).perform(swipeUp());
        onView(withId(R.id.dob)).perform(swipeUp());
        onView(withId(R.id.phone_number)).perform(swipeUp());
        onView(withId(R.id.alternate_contact_name)).perform(typeText(Configs.TestDataConfigs.alternateContactName),ViewActions.closeSoftKeyboard());
        onView(withId(R.id.search)).perform(click());
        Thread.sleep(5000);
        Matcher<View> parentMatcher = withId(R.id.list_view_layout);
        onView(allOf(withId(R.id.recycler_view), withParent(parentMatcher))).perform(RecyclerViewActions.scrollTo(ViewMatchers.hasDescendant(withText(Configs.TestDataConfigs.clientName2)))).check(matches(isDisplayed()));

    }
    @Test
    public void userCanSearchOutsideAndInsideMyHealthFacilityByScanningAQRCOde() throws InterruptedException{
        onView(withId(R.id.action_search)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.qrCodeButton)).perform(click());
        onView(withText("Scan QR Code")).check(matches(isDisplayed()));
    }
}