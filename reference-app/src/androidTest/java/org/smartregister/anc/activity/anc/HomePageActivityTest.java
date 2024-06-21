package org.smartregister.anc.activity.anc;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

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
    public void B_searchBarPresent() {
        onView(withId(R.id.search_bar_layout)).check(matches(isDisplayed()));

    }
}

