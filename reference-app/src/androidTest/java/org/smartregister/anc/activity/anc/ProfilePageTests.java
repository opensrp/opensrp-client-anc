package org.smartregister.anc.activity.anc;


import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isClickable;
import static androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withTagValue;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

//import androidx.test.espresso.contrib.RecyclerViewActions;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;

import androidx.test.espresso.action.ScrollToAction;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
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

public class ProfilePageTests {

@Rule
    public ActivityScenarioRule<LoginActivity> mActivityScenario =  new ActivityScenarioRule<>(LoginActivity.class);

Utils utils = new Utils();

    @Test
    public void A_SetUp() throws InterruptedException {
        utils.logIn(Constants.ancConstants.ancUsername, Constants.ancConstants.ancPassword);
    }

    @Test
    public void B_UserLocationIsDisplayed() {
        onView(withContentDescription("Me")).perform(click());
        onView(withId(R.id.facility_selection)).perform(click());
        onView(withId(R.id.locations_lv)).check(matches(isDisplayed()));
        onView(withId(R.id.locations_lv)).perform(click());
    }



    @Test
    public void C_ChangeLanguageToBahasa() throws InterruptedException {
        onView(withContentDescription("Me")).perform(click());
        onView(withId(R.id.language_switcher_text)).perform(click());
        onView(withText("Bahasa (Indonesia)")).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.opensrp_logo_image_view)).check(matches(isDisplayed()));
        Thread.sleep(1000);
    }


    @Test
    public void E_ChangeLanguageToFrench() throws InterruptedException {
        onView(withContentDescription("Me")).perform(click());
        onView(withId(R.id.language_switcher_text)).perform(click());
        onView(withText("French")).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.opensrp_logo_image_view)).check(matches(isDisplayed()));
        Thread.sleep(1000);
    }

    @Test
    public void F_ChangeLanguageToPortuguese() throws InterruptedException {
        onView(withContentDescription("Moi")).perform(click());
        onView(withId(R.id.language_switcher_text)).perform(click());
        onView(withText("Portuguese (Brazil)")).perform(click());
        Thread.sleep(3000);
        onView(withId(R.id.opensrp_logo_image_view)).check(matches(isDisplayed()));
        Thread.sleep(1000);
    }

    @Test
    public void G_ChangeLanguageToEnglish() throws InterruptedException {
        onView(withContentDescription("Eu")).perform(click());
        onView(withId(R.id.language_switcher_text)).perform(click());
        onView(withText("English")).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.opensrp_logo_image_view)).check(matches(isDisplayed()));
        Thread.sleep(1000);
    }
        @Test
    public void H_LoadPopulationXstics() throws InterruptedException {
        onView(withContentDescription("Me")).perform(click());
        onView(withId(R.id.pop_characteristics_text)).perform(click());
        onView(withId(R.id.characteristics_toolbar_title)).check(matches(isDisplayed()));
        Thread.sleep(1500);

    }

        @Test
    public void I_PopulationXsticScrollDown()  {
        onView(withContentDescription("Me")).perform(click());
        onView(withId(R.id.pop_characteristics_text)).perform(click());
      onView(withId(R.id.population_characteristics)).perform(RecyclerViewActions.scrollTo(hasDescendant(withText("Syphilis prevalence 5% or higher")))).check(matches(isDisplayed()));


    }

    //how to add banner tests 'infor tip')



    @Test
    public void J_LoadSiteXstics()  {
        onView(withContentDescription("Me")).perform(click());
        onView(withId(R.id.site_characteristics_text)).perform(click());
        onView(withId(R.id.characteristics_toolbar_title)).check(matches(isDisplayed()));


    }
    @Test
    public void K_EditSiteXstics() throws InterruptedException {
        onView(withContentDescription("Me")).perform(click());
        onView(withId(R.id.site_characteristics_text)).perform(click());
        onView(withId(R.id.characteristics_toolbar_edit)).perform(click());
        Thread.sleep(1500);
        onView(allOf(withText("Yes"),
             isDisplayed())).perform(click());
        onView(withId(R.id.action_save)).perform(click());
        onView(withId(R.id.opensrp_logo_image_view)).check(matches(isDisplayed()));

    }
    //Device to device sync tests skipped because module is not implemented

    @Test
    public void L_LogOut() throws InterruptedException {
        onView(withContentDescription("Me")).perform(click());
        onView(withId(R.id.logout_text)).perform(click());
                Thread.sleep(1500);
        onView(withId(R.id.login_login_btn)).check(matches(isDisplayed()));

    }

}
