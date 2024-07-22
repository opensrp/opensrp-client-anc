package org.smartregister.anc.activity.anc;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
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
@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class RemoveFamilyMemberTest {
    @Rule
    public ActivityScenarioRule<LoginActivity> mActivityScenario = new ActivityScenarioRule<>(LoginActivity.class);

    Utils utils = new Utils();


    @Test

    public void aSetUp() throws InterruptedException {
        utils.logIn(Constants.ancConstants.ancUsername, Constants.ancConstants.ancPassword);
    }

    @Test
    public void bRemoveByMovedAway() throws Throwable {
        utils.addAFamilyMember();
        Thread.sleep(1500);
        onView(withText(Configs.TestDataConfigs.firstAndLastName)).perform(click());
        Thread.sleep(1000);
        onView(withId(R.id.overflow_menu_item)).perform(click());
        onView(withText("Close ANC Record")).perform(click());
        Thread.sleep(2000);
        Activity activity = utils.getCurrentActivity();
        onView(withId(Utils.getViewId((JsonFormActivity) activity, "step1:anc_close_reason"))).perform(click());
        onView(withSubstring("Moved away")).perform(click());
        onView(withId(R.id.action_save)).perform(click());
           Thread.sleep(1000);


    }
    @Test
    public void cRemoveByDeath() throws Throwable {
        utils.addAFamilyMember();
        Thread.sleep(1000);
        onView(withText(Configs.TestDataConfigs.firstAndLastName)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.overflow_menu_item)).perform(click());
        onView(withText("Close ANC Record")).perform(click());
        Thread.sleep(500);
        Activity activity = utils.getCurrentActivity();
        onView(withId(Utils.getViewId((JsonFormActivity) activity, "step1:anc_close_reason"))).perform(click());
        onView(withSubstring("Woman died")).perform(click());
        Thread.sleep(1000);
       onView(withId(utils.getViewId((JsonFormActivity) activity, "step1:death_date"))).perform(click());
        onView(withId(R.id.ok_button)).perform(click());
        onView(withSubstring("Cause of death")).perform(click());
        onView(withSubstring("Eclampsia")).perform(click());
       onView(withId(R.id.action_save)).perform(click());
        Thread.sleep(1000);


    }
        @Test
    public void dRemoveByOther() throws Throwable {
        utils.addAFamilyMember();
       Thread.sleep(1500);
        onView(withText(Configs.TestDataConfigs.firstAndLastName)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.overflow_menu_item)).perform(click());
        onView(withText("Close ANC Record")).perform(click());
        Thread.sleep(2000);
        Activity activity = utils.getCurrentActivity();
        onView(withId(Utils.getViewId((JsonFormActivity) activity, "step1:anc_close_reason"))).perform(click());
        onView(withSubstring("Other")).perform(click());
        onView(withId(R.id.action_save)).perform(click());
            Thread.sleep(1000);


  }
    @Test
    public void eRemoveByMiscarriage() throws Throwable {
        utils.addAFamilyMember();
        Thread.sleep(1500);
        onView(withText(Configs.TestDataConfigs.firstAndLastName)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.overflow_menu_item)).perform(click());
        onView(withText("Close ANC Record")).perform(click());
        Thread.sleep(500);
        Activity activity = utils.getCurrentActivity();
        onView(withId(Utils.getViewId((JsonFormActivity) activity, "step1:anc_close_reason"))).perform(click());
        onView(withSubstring("Miscarriage")).perform(click());

        onView(withId(Utils.getViewId((JsonFormActivity) activity, "step1:miscarriage_abortion_date"))).perform(click());

        onView(withId(R.id.ok_button)).perform(click());
        onView(withId(R.id.action_save)).perform(click());
        Thread.sleep(1000);


    }


    @Test
    public void fRemoveByStillBirth() throws Throwable {
        utils.addAFamilyMember();
        Thread.sleep(1500);
        onView(withText(Configs.TestDataConfigs.firstAndLastName)).perform(click());
        Thread.sleep(2000);
        onView(withId(R.id.overflow_menu_item)).perform(click());
        onView(withText("Close ANC Record")).perform(click());
        Thread.sleep(500);
        Activity activity = utils.getCurrentActivity();
        onView(withId(Utils.getViewId((JsonFormActivity) activity, "step1:anc_close_reason"))).perform(click());
        onView(withSubstring("Stillbirth")).perform(click());
        onView(withId(Utils.getViewId((JsonFormActivity) activity, "step1:delivery_date"))).perform(click());
        onView(withId(R.id.ok_button)).perform(click());
        onView(withId(Utils.getViewId((JsonFormActivity) activity, "step1:delivery_place"))).perform(click());
        onView(withSubstring("Health facility")).perform(click());
        onView(withId(R.id.action_save)).perform(click());
      //  Thread.sleep(1000);


    }

    @Test
    public void gRemoveByAbortion() throws Throwable {
        utils.addAFamilyMember();
        Thread.sleep(1500);
        onView(withText(Configs.TestDataConfigs.firstAndLastName)).perform(click());
        Thread.sleep(1500);
        onView(withId(R.id.overflow_menu_item)).perform(click());
        onView(withText("Close ANC Record")).perform(click());
        Thread.sleep(500);
        Activity activity = utils.getCurrentActivity();
        onView(withId(Utils.getViewId((JsonFormActivity) activity, "step1:anc_close_reason"))).perform(click());
        onView(withSubstring("Abortion")).perform(click());
        onView(withId(Utils.getViewId((JsonFormActivity) activity, "step1:miscarriage_abortion_date"))).perform(click());
        onView(withId(R.id.ok_button)).perform(click());
        onView(withId(R.id.action_save)).perform(click());
        Thread.sleep(1000);


    }

//    @Test
//    public void hRemoveByOther() throws Throwable {
//        utils.addAFamilyMember();
//        onView(withText(Configs.TestDataConfigs.firstAndLastName)).perform(click());
//        Thread.sleep(500);
//        onView(withId(R.id.overflow_menu_item)).perform(click());
//        onView(withText("Close ANC Record")).perform(click());
//        Thread.sleep(500);
//        Activity activity = utils.getCurrentActivity();
//        onView(withId(Utils.getViewId((JsonFormActivity) activity, "step1:anc_close_reason"))).perform(click());
//        onView(withSubstring("Other")).perform(click());
//        onView(withId(R.id.action_save)).perform(click());
//
//
//
//    }

    @Test
    public void iRemoveByLiveBirth() throws Throwable {
        utils.addAFamilyMember();
        Thread.sleep(1500);
        onView(withText(Configs.TestDataConfigs.firstAndLastName)).perform(click());
        Thread.sleep(1500);
        onView(withId(R.id.overflow_menu_item)).perform(click());
        onView(withText("Close ANC Record")).perform(click());
        Thread.sleep(500);
        Activity activity = utils.getCurrentActivity();
        onView(withId(Utils.getViewId((JsonFormActivity) activity, "step1:anc_close_reason"))).perform(click());
        Thread.sleep(500);
        onView(withSubstring("Live birth")).perform(click());
        Thread.sleep(500);
        onView(withId(Utils.getViewId((JsonFormActivity) activity, "step1:delivery_date"))).perform(click());
        onView(withId(R.id.ok_button)).perform(click());
        Thread.sleep(500);
        onView(withSubstring("Place of delivery")).perform(click());
        onView(withSubstring("Health facility")).perform(click());
        onView(withId(R.id.action_save)).perform(click());
        Thread.sleep(1000);


    }
    @Test
    public void jRemoveByFalsePregnancy() throws Throwable {
        utils.addAFamilyMember();
        Thread.sleep(1500);
        onView(withText(Configs.TestDataConfigs.firstAndLastName)).perform(click());
        Thread.sleep(1500);
        onView(withId(R.id.overflow_menu_item)).perform(click());
        onView(withText("Close ANC Record")).perform(click());
        Thread.sleep(500);
        Activity activity = utils.getCurrentActivity();
        onView(withId(Utils.getViewId((JsonFormActivity) activity, "step1:anc_close_reason"))).perform(click());
        onView(withSubstring("False pregnancy")).perform(click());
        onView(withId(R.id.action_save)).perform(click());
        Thread.sleep(1000);



    }

    @Test
    public void kRemoveByLostToFollowUp() throws Throwable {
        utils.addAFamilyMember();
        Thread.sleep(1500);
        onView(withText(Configs.TestDataConfigs.firstAndLastName)).perform(click());
        Thread.sleep(1500);
        onView(withId(R.id.overflow_menu_item)).perform(click());
        onView(withText("Close ANC Record")).perform(click());
        Thread.sleep(500);
        Activity activity = utils.getCurrentActivity();
        onView(withId(Utils.getViewId((JsonFormActivity) activity, "step1:anc_close_reason"))).perform(click());
        onView(withSubstring("Lost to follow-up")).perform(click());
        onView(withId(R.id.action_save)).perform(click());
        Thread.sleep(1000);



    }

    @Test
    public void lRemoveByWrongEntry() throws Throwable {
        utils.addAFamilyMember();
        Thread.sleep(1500);
        onView(withText(Configs.TestDataConfigs.firstAndLastName)).perform(click());
        Thread.sleep(1500);
        onView(withId(R.id.overflow_menu_item)).perform(click());
        onView(withText("Close ANC Record")).perform(click());
        Thread.sleep(500);
        Activity activity = utils.getCurrentActivity();
        onView(withId(Utils.getViewId((JsonFormActivity) activity, "step1:anc_close_reason"))).perform(click());
        onView(withSubstring("Wrong entry")).perform(click());
        onView(withId(R.id.action_save)).perform(click());
        Thread.sleep(1000);



    }






}
