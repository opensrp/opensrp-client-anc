package org.smartregister.anc.library.fragment;


import static org.robolectric.shadows.ShadowInstrumentation.getInstrumentation;

import android.app.Activity;
import android.content.Intent;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.Form;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;
import org.robolectric.Robolectric;
import org.robolectric.android.controller.ActivityController;
import org.smartregister.anc.library.activity.BaseActivityUnitTest;
import org.smartregister.anc.library.activity.ProfileActivity;
import org.smartregister.anc.library.model.Task;
import org.smartregister.anc.library.presenter.ProfilePresenter;
import org.smartregister.anc.library.util.ConstantsUtils;
import org.smartregister.anc.library.util.DBConstantsUtils;

import java.util.HashMap;

import timber.log.Timber;

public class ProfileTasksFragmentTest extends BaseActivityUnitTest {
    private ProfileActivity profileActivity;
    private ActivityController<ProfileActivity> controller;
    private ProfileTasksFragment profileTasksFragment;
    private ProfileActivity spyActivity;

    @Mock
    private ProfilePresenter presenter;

    @Before
    public void setUp() {
        super.setUp();
    }

    @Override
    protected Activity getActivity() {
        return profileActivity;
    }

    @Override
    protected ActivityController getActivityController() {
        return controller;
    }

    @Test
    public void testFragmentInstance() {
        Intent testIntent = new Intent();
        testIntent.putExtra(ConstantsUtils.IntentKeyUtils.BASE_ENTITY_ID, DUMMY_BASE_ENTITY_ID);
        HashMap<String, String> map = new HashMap<>();
        map.put(DBConstantsUtils.KeyUtils.CONTACT_STATUS, ConstantsUtils.AlertStatusUtils.ACTIVE);
        map.put(DBConstantsUtils.KeyUtils.LAST_CONTACT_RECORD_DATE, "10-12-2018");
        map.put(DBConstantsUtils.KeyUtils.NEXT_CONTACT, "3");
        testIntent.putExtra(ConstantsUtils.IntentKeyUtils.CLIENT_MAP, map);

        controller = Robolectric.buildActivity(ProfileActivity.class, testIntent).create().start().resume();
        profileActivity = controller.get();

        spyActivity = Mockito.spy(profileActivity);
        Whitebox.setInternalState(profileActivity, "presenter", presenter);
        profileTasksFragment = ProfileTasksFragment.newInstance(spyActivity.getIntent().getExtras());
        startFragment(profileTasksFragment);
        Assert.assertNotNull(profileTasksFragment);
        Assert.assertEquals(profileTasksFragment.getTaskList().size(), 0);
    }

    private void startFragment(Fragment fragment) {
        FragmentManager fragmentManager = spyActivity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(fragment, null);
        fragmentTransaction.commit();

        getActivity().runOnUiThread(() -> spyActivity.getSupportFragmentManager().executePendingTransactions());

        getInstrumentation().waitForIdleSync();
    }

    @Test
    public void testFragmentInstanceWithNullBundle() {
        Intent testIntent = new Intent();
        testIntent.putExtra(ConstantsUtils.IntentKeyUtils.BASE_ENTITY_ID, DUMMY_BASE_ENTITY_ID);
        HashMap<String, String> map = new HashMap<>();
        map.put(DBConstantsUtils.KeyUtils.CONTACT_STATUS, ConstantsUtils.AlertStatusUtils.NOT_DUE);
        map.put(DBConstantsUtils.KeyUtils.LAST_CONTACT_RECORD_DATE, "10-12-2018");
        map.put(DBConstantsUtils.KeyUtils.NEXT_CONTACT, "3");
        testIntent.putExtra(ConstantsUtils.IntentKeyUtils.CLIENT_MAP, map);

        controller = Robolectric.buildActivity(ProfileActivity.class, testIntent).create().start().resume();
        profileActivity = controller.get();

        spyActivity = Mockito.spy(profileActivity);
        Whitebox.setInternalState(profileActivity, "presenter", presenter);
        profileTasksFragment = ProfileTasksFragment.newInstance(null);
        startFragment(profileTasksFragment);
        Assert.assertNotNull(profileTasksFragment);
        Assert.assertEquals(profileTasksFragment.getTaskList().size(), 0);
    }

    @Test
    public void testCreateAccordionValues() {
        try {
            Intent testIntent = new Intent();
            testIntent.putExtra(ConstantsUtils.IntentKeyUtils.BASE_ENTITY_ID, DUMMY_BASE_ENTITY_ID);
            HashMap<String, String> map = new HashMap<>();
            map.put(DBConstantsUtils.KeyUtils.CONTACT_STATUS, ConstantsUtils.AlertStatusUtils.NOT_DUE);
            map.put(DBConstantsUtils.KeyUtils.LAST_CONTACT_RECORD_DATE, "10-12-2018");
            map.put(DBConstantsUtils.KeyUtils.NEXT_CONTACT, "3");
            testIntent.putExtra(ConstantsUtils.IntentKeyUtils.CLIENT_MAP, map);

            controller = Robolectric.buildActivity(ProfileActivity.class, testIntent).create().start().resume();
            profileActivity = controller.get();

            spyActivity = Mockito.spy(profileActivity);
            Whitebox.setInternalState(profileActivity, "presenter", presenter);
            profileTasksFragment = ProfileTasksFragment.newInstance(null);
            startFragment(profileTasksFragment);
            String taskForm = "{\"validate_on_submit\":true,\"display_scroll_bars\":true,\"count\":\"1\",\"encounter_type\":\"Contact Tasks\",\"entity_id\":\"\",\"relational_id\":\"\",\"form_version\":\"0.0.1\",\"metadata\":{\"start\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"start\",\"openmrs_entity_id\":\"163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"value\":\"2020-01-30 06:53:27\"},\"end\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"end\",\"openmrs_entity_id\":\"163138AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"value\":\"2020-01-30 06:53:32\"},\"today\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"encounter\",\"openmrs_entity_id\":\"encounter_date\",\"value\":\"30-01-2020\"},\"deviceid\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"deviceid\",\"openmrs_entity_id\":\"163149AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"value\":\"358240051111110\"},\"subscriberid\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"subscriberid\",\"openmrs_entity_id\":\"163150AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"value\":\"310260000000000\"},\"simserial\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"simserial\",\"openmrs_entity_id\":\"163151AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"value\":\"89014103211118510720\"},\"phonenumber\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"phonenumber\",\"openmrs_entity_id\":\"163152AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"value\":\"+15555215554\"},\"encounter_location\":\"\",\"look_up\":{\"entity_id\":\"\",\"value\":\"\"}},\"global_previous\":[\"blood_type_test_status\",\"hiv_test_partner_result\",\"hiv_test_status\",\"hiv_test_result\",\"hiv_test_partner_status\",\"hepb_test_status\",\"hepc_test_status\",\"syph_test_status\",\"tb_screening_status\",\"hiv_positive\",\"urine_test_status\",\"hb_test_status\",\"syphilis_positive\",\"hepc_positive\",\"syph_test_status\",\"ultrasound\",\"hepb_positive\"],\"step1\":{\"display_back_button\":\"true\",\"title\":\"Hepatitis C test\",\"fields\":[{\"key\":\"hepc_test_status\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"1118AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"label\":\"Hepatitis C test\",\"label_text_style\":\"bold\",\"text_color\":\"#000000\",\"type\":\"extended_radio_button\",\"options\":[{\"key\":\"done_today\",\"text\":\"Done today\",\"type\":\"done_today\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165383AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"done_earlier\",\"text\":\"Done earlier\",\"type\":\"done_earlier\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165385AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"ordered\",\"text\":\"Ordered\",\"type\":\"ordered\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165384AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"not_done\",\"text\":\"Not done\",\"type\":\"not_done\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"1118AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}],\"v_required\":{\"value\":true},\"index\":\"0\",\"value\":\"not_done\",\"step\":\"step1\",\"is-rule-check\":true},{\"key\":\"spacer\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"spacer\",\"type\":\"spacer\",\"spacer_height\":\"10dp\",\"index\":\"1\"},{\"key\":\"hepc_test_notdone\",\"openmrs_entity_parent\":\"161474AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165182AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"type\":\"check_box\",\"label\":\"Reason\",\"label_text_style\":\"bold\",\"options\":[{\"key\":\"stock_out\",\"text\":\"Stock out\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165183AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"value\":true},{\"key\":\"expired_stock\",\"text\":\"Expired stock\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165299AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"value\":true},{\"key\":\"other\",\"text\":\"Other (specify)\",\"openmrs_entity_parent\":\"161474AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"5622AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}],\"v_required\":{\"value\":true},\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"tests_relevance_rules.yml\"}}},\"index\":\"2\",\"value\":[\"expired_stock\",\"stock_out\"],\"is_visible\":true,\"step\":\"step1\",\"is-rule-check\":true},{\"key\":\"hepc_test_notdone_other\",\"openmrs_entity_parent\":\"161474AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165435AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"type\":\"edit_text\",\"hint\":\"Specify\",\"edit_type\":\"name\",\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"tests_relevance_rules.yml\"}}},\"index\":\"3\",\"is_visible\":false},{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"key\":\"hepc_test_date_today_hidden\",\"type\":\"hidden\",\"label_text_style\":\"bold\",\"text_color\":\"#000000\",\"calculation\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"tests_calculation_rules.yml\"}}},\"index\":\"4\",\"value\":\"0\"},{\"key\":\"hepc_test_date\",\"openmrs_entity_parent\":\"161474AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"163724AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"type\":\"date_picker\",\"hint\":\"Hep C test date\",\"expanded\":\"false\",\"max_date\":\"today\",\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"tests_relevance_rules.yml\"}}},\"calculation\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"tests_calculation_rules.yml\"}}},\"v_required\":{\"value\":true,\"err\":\"Select the date of the Hepatitis C test.\"},\"index\":\"5\",\"is_visible\":false},{\"key\":\"hepc_test_type\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165437AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"type\":\"native_radio\",\"label\":\"Hep C test type\",\"label_info_text\":\"Anti-HCV laboratory-based immunoassay is the preferred method for testing for Hep C infection in pregnancy. If immunoassay is not available, Anti-HCV rapid diagnostic test (RDT) is recommended over Dried Blood Spot (DBS) Anti-HCV testing.\",\"label_info_title\":\"Hep C test type\",\"label_text_style\":\"bold\",\"options\":[{\"key\":\"anti_hcv_lab_based\",\"text\":\"Anti-HCV laboratory-based immunoassay (recommended)\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"1325AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"anti_hcv_rdt\",\"text\":\"Anti-HCV rapid diagnostic test (RDT)\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165302AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"anti_hcv_dbs\",\"text\":\"Dried Blood Spot (DBS) anti-HCV test\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"161471AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}],\"v_required\":{\"value\":true},\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"tests_relevance_rules.yml\"}}},\"index\":\"6\",\"is_visible\":false,\"step\":\"step1\",\"is-rule-check\":true},{\"key\":\"hcv_lab_ima\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"1325AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"type\":\"native_radio\",\"label\":\"Anti-HCV laboratory-based immunoassay (recommended)\",\"label_text_style\":\"bold\",\"options\":[{\"key\":\"positive\",\"text\":\"Positive\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"703AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"negative\",\"text\":\"Negative\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"664AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}],\"v_required\":{\"value\":true},\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"tests_relevance_rules.yml\"}}},\"index\":\"7\",\"step\":\"step1\",\"is-rule-check\":true,\"is_visible\":false},{\"key\":\"hcv_rdt\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165302AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"type\":\"native_radio\",\"label\":\"Anti-HCV rapid diagnostic test (RDT)\",\"label_text_style\":\"bold\",\"options\":[{\"key\":\"positive\",\"text\":\"Positive\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"703AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"negative\",\"text\":\"Negative\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"664AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}],\"v_required\":{\"value\":true},\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"tests_relevance_rules.yml\"}}},\"index\":\"8\",\"step\":\"step1\",\"is-rule-check\":true,\"is_visible\":false},{\"key\":\"hcv_dbs\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"161471AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"type\":\"native_radio\",\"label\":\"Dried Blood Spot (DBS) anti-HCV test\",\"label_text_style\":\"bold\",\"options\":[{\"key\":\"positive\",\"text\":\"Positive\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"703AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"negative\",\"text\":\"Negative\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"664AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}],\"v_required\":{\"value\":true},\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"tests_relevance_rules.yml\"}}},\"index\":\"9\",\"step\":\"step1\",\"is-rule-check\":true,\"is_visible\":false},{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"28AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"key\":\"hepc_positive\",\"type\":\"hidden\",\"label_text_style\":\"bold\",\"text_color\":\"#000000\",\"calculation\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"tests_calculation_rules.yml\"}}},\"index\":\"10\",\"value\":\"0\",\"step\":\"step1\",\"is-rule-check\":true},{\"key\":\"hepatitis_c_danger_toaster\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"type\":\"toaster_notes\",\"text\":\"Hep C positive diagnosis!\",\"toaster_info_text\":\"Counselling and referral required.\",\"toaster_info_title\":\"Hep C positive diagnosis!\",\"toaster_type\":\"problem\",\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"tests_relevance_rules.yml\"}}},\"index\":\"11\",\"is_visible\":false}]},\"invisible_required_fields\":\"[hcv_dbs, hcv_lab_ima, hepc_test_type, hepc_test_date, hcv_rdt]\",\"details\":{\"appVersionName\":\"1.7.13-SNAPSHOT\",\"formVersion\":\"0.0.1\"}}";
            JSONObject form = new JSONObject(taskForm);

            JSONArray fields = Whitebox.invokeMethod(profileTasksFragment, "createAccordionValues", form);
            Assert.assertNotNull(fields);
            Assert.assertEquals(4, fields.length());
        } catch (JSONException e) {
            Timber.e(e, " --> testCreateAccordionValues");
        } catch (Exception e) {
            Timber.e(e, " --> testCreateAccordionValues");
        }
    }

    @Test
    public void testUpdateTaskValue() {
        try {
            Intent testIntent = new Intent();
            testIntent.putExtra(ConstantsUtils.IntentKeyUtils.BASE_ENTITY_ID, DUMMY_BASE_ENTITY_ID);
            HashMap<String, String> map = new HashMap<>();
            map.put(DBConstantsUtils.KeyUtils.CONTACT_STATUS, ConstantsUtils.AlertStatusUtils.NOT_DUE);
            map.put(DBConstantsUtils.KeyUtils.LAST_CONTACT_RECORD_DATE, "10-12-2018");
            map.put(DBConstantsUtils.KeyUtils.NEXT_CONTACT, "3");
            testIntent.putExtra(ConstantsUtils.IntentKeyUtils.CLIENT_MAP, map);

            controller = Robolectric.buildActivity(ProfileActivity.class, testIntent).create().start().resume();
            profileActivity = controller.get();

            spyActivity = Mockito.spy(profileActivity);
            Whitebox.setInternalState(profileActivity, "presenter", presenter);
            profileTasksFragment = ProfileTasksFragment.newInstance(null);
            startFragment(profileTasksFragment);
            String taskForm = "{\"validate_on_submit\":true,\"display_scroll_bars\":true,\"count\":\"1\",\"encounter_type\":\"Contact Tasks\",\"entity_id\":\"\",\"relational_id\":\"\",\"form_version\":\"0.0.1\",\"metadata\":{\"start\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"start\",\"openmrs_entity_id\":\"163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"value\":\"2020-01-30 06:53:27\"},\"end\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"end\",\"openmrs_entity_id\":\"163138AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"value\":\"2020-01-30 06:53:32\"},\"today\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"encounter\",\"openmrs_entity_id\":\"encounter_date\",\"value\":\"30-01-2020\"},\"deviceid\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"deviceid\",\"openmrs_entity_id\":\"163149AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"value\":\"358240051111110\"},\"subscriberid\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"subscriberid\",\"openmrs_entity_id\":\"163150AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"value\":\"310260000000000\"},\"simserial\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"simserial\",\"openmrs_entity_id\":\"163151AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"value\":\"89014103211118510720\"},\"phonenumber\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"phonenumber\",\"openmrs_entity_id\":\"163152AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"value\":\"+15555215554\"},\"encounter_location\":\"\",\"look_up\":{\"entity_id\":\"\",\"value\":\"\"}},\"global_previous\":[\"blood_type_test_status\",\"hiv_test_partner_result\",\"hiv_test_status\",\"hiv_test_result\",\"hiv_test_partner_status\",\"hepb_test_status\",\"hepc_test_status\",\"syph_test_status\",\"tb_screening_status\",\"hiv_positive\",\"urine_test_status\",\"hb_test_status\",\"syphilis_positive\",\"hepc_positive\",\"syph_test_status\",\"ultrasound\",\"hepb_positive\"],\"step1\":{\"display_back_button\":\"true\",\"title\":\"Hepatitis C test\",\"fields\":[{\"key\":\"hepc_test_status\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"1118AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"label\":\"Hepatitis C test\",\"label_text_style\":\"bold\",\"text_color\":\"#000000\",\"type\":\"extended_radio_button\",\"options\":[{\"key\":\"done_today\",\"text\":\"Done today\",\"type\":\"done_today\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165383AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"done_earlier\",\"text\":\"Done earlier\",\"type\":\"done_earlier\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165385AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"ordered\",\"text\":\"Ordered\",\"type\":\"ordered\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165384AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"not_done\",\"text\":\"Not done\",\"type\":\"not_done\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"1118AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}],\"v_required\":{\"value\":true},\"index\":\"0\",\"value\":\"not_done\",\"step\":\"step1\",\"is-rule-check\":true},{\"key\":\"spacer\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"spacer\",\"type\":\"spacer\",\"spacer_height\":\"10dp\",\"index\":\"1\"},{\"key\":\"hepc_test_notdone\",\"openmrs_entity_parent\":\"161474AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165182AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"type\":\"check_box\",\"label\":\"Reason\",\"label_text_style\":\"bold\",\"options\":[{\"key\":\"stock_out\",\"text\":\"Stock out\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165183AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"value\":true},{\"key\":\"expired_stock\",\"text\":\"Expired stock\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165299AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"value\":true},{\"key\":\"other\",\"text\":\"Other (specify)\",\"openmrs_entity_parent\":\"161474AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"5622AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}],\"v_required\":{\"value\":true},\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"tests_relevance_rules.yml\"}}},\"index\":\"2\",\"value\":[\"expired_stock\",\"stock_out\"],\"is_visible\":true,\"step\":\"step1\",\"is-rule-check\":true},{\"key\":\"hepc_test_notdone_other\",\"openmrs_entity_parent\":\"161474AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165435AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"type\":\"edit_text\",\"hint\":\"Specify\",\"edit_type\":\"name\",\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"tests_relevance_rules.yml\"}}},\"index\":\"3\",\"is_visible\":false},{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"key\":\"hepc_test_date_today_hidden\",\"type\":\"hidden\",\"label_text_style\":\"bold\",\"text_color\":\"#000000\",\"calculation\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"tests_calculation_rules.yml\"}}},\"index\":\"4\",\"value\":\"0\"},{\"key\":\"hepc_test_date\",\"openmrs_entity_parent\":\"161474AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"163724AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"type\":\"date_picker\",\"hint\":\"Hep C test date\",\"expanded\":\"false\",\"max_date\":\"today\",\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"tests_relevance_rules.yml\"}}},\"calculation\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"tests_calculation_rules.yml\"}}},\"v_required\":{\"value\":true,\"err\":\"Select the date of the Hepatitis C test.\"},\"index\":\"5\",\"is_visible\":false},{\"key\":\"hepc_test_type\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165437AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"type\":\"native_radio\",\"label\":\"Hep C test type\",\"label_info_text\":\"Anti-HCV laboratory-based immunoassay is the preferred method for testing for Hep C infection in pregnancy. If immunoassay is not available, Anti-HCV rapid diagnostic test (RDT) is recommended over Dried Blood Spot (DBS) Anti-HCV testing.\",\"label_info_title\":\"Hep C test type\",\"label_text_style\":\"bold\",\"options\":[{\"key\":\"anti_hcv_lab_based\",\"text\":\"Anti-HCV laboratory-based immunoassay (recommended)\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"1325AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"anti_hcv_rdt\",\"text\":\"Anti-HCV rapid diagnostic test (RDT)\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165302AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"anti_hcv_dbs\",\"text\":\"Dried Blood Spot (DBS) anti-HCV test\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"161471AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}],\"v_required\":{\"value\":true},\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"tests_relevance_rules.yml\"}}},\"index\":\"6\",\"is_visible\":false,\"step\":\"step1\",\"is-rule-check\":true},{\"key\":\"hcv_lab_ima\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"1325AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"type\":\"native_radio\",\"label\":\"Anti-HCV laboratory-based immunoassay (recommended)\",\"label_text_style\":\"bold\",\"options\":[{\"key\":\"positive\",\"text\":\"Positive\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"703AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"negative\",\"text\":\"Negative\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"664AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}],\"v_required\":{\"value\":true},\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"tests_relevance_rules.yml\"}}},\"index\":\"7\",\"step\":\"step1\",\"is-rule-check\":true,\"is_visible\":false},{\"key\":\"hcv_rdt\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165302AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"type\":\"native_radio\",\"label\":\"Anti-HCV rapid diagnostic test (RDT)\",\"label_text_style\":\"bold\",\"options\":[{\"key\":\"positive\",\"text\":\"Positive\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"703AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"negative\",\"text\":\"Negative\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"664AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}],\"v_required\":{\"value\":true},\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"tests_relevance_rules.yml\"}}},\"index\":\"8\",\"step\":\"step1\",\"is-rule-check\":true,\"is_visible\":false},{\"key\":\"hcv_dbs\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"161471AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"type\":\"native_radio\",\"label\":\"Dried Blood Spot (DBS) anti-HCV test\",\"label_text_style\":\"bold\",\"options\":[{\"key\":\"positive\",\"text\":\"Positive\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"703AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"negative\",\"text\":\"Negative\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"664AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}],\"v_required\":{\"value\":true},\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"tests_relevance_rules.yml\"}}},\"index\":\"9\",\"step\":\"step1\",\"is-rule-check\":true,\"is_visible\":false},{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"28AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"key\":\"hepc_positive\",\"type\":\"hidden\",\"label_text_style\":\"bold\",\"text_color\":\"#000000\",\"calculation\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"tests_calculation_rules.yml\"}}},\"index\":\"10\",\"value\":\"0\",\"step\":\"step1\",\"is-rule-check\":true},{\"key\":\"hepatitis_c_danger_toaster\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"type\":\"toaster_notes\",\"text\":\"Hep C positive diagnosis!\",\"toaster_info_text\":\"Counselling and referral required.\",\"toaster_info_title\":\"Hep C positive diagnosis!\",\"toaster_type\":\"problem\",\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"tests_relevance_rules.yml\"}}},\"index\":\"11\",\"is_visible\":false}]},\"invisible_required_fields\":\"[hcv_dbs, hcv_lab_ima, hepc_test_type, hepc_test_date, hcv_rdt]\",\"details\":{\"appVersionName\":\"1.7.13-SNAPSHOT\",\"formVersion\":\"0.0.1\"}}";
            JSONObject form = new JSONObject(taskForm);

            JSONArray fields = Whitebox.invokeMethod(profileTasksFragment, "createAccordionValues", form);
            Assert.assertNotNull(fields);
            Assert.assertEquals(4, fields.length());

            Whitebox.setInternalState(profileTasksFragment, "currentTask", getTask());
            Task task = Whitebox.invokeMethod(profileTasksFragment, "updateTaskValue", fields);
            Assert.assertNotNull(task);
            Assert.assertEquals("myTask", task.getKey());

            JSONObject taskValue = new JSONObject(task.getValue());
            Assert.assertTrue(taskValue.has(JsonFormConstants.VALUE));

            JSONArray values = taskValue.getJSONArray(JsonFormConstants.VALUE);
            Assert.assertNotNull(values);
            Assert.assertEquals(4, values.length());
        } catch (JSONException e) {
            Timber.e(e, " --> testUpdateTaskValue");
        } catch (Exception e) {
            Timber.e(e, " --> testUpdateTaskValue");
        }
    }

    private Task getTask() {
        Task task = new Task(DUMMY_BASE_ENTITY_ID, "myTask", String.valueOf(new JSONObject()), true, true);
        task.setId(Long.valueOf(1));
        return task;
    }

    @Test
    public void testUpdateTaskValueWithNullForm() {
        try {
            Intent testIntent = new Intent();
            testIntent.putExtra(ConstantsUtils.IntentKeyUtils.BASE_ENTITY_ID, DUMMY_BASE_ENTITY_ID);
            HashMap<String, String> map = new HashMap<>();
            map.put(DBConstantsUtils.KeyUtils.CONTACT_STATUS, ConstantsUtils.AlertStatusUtils.EXPIRED);
            map.put(DBConstantsUtils.KeyUtils.LAST_CONTACT_RECORD_DATE, "10-12-2018");
            map.put(DBConstantsUtils.KeyUtils.NEXT_CONTACT, "3");
            testIntent.putExtra(ConstantsUtils.IntentKeyUtils.CLIENT_MAP, map);

            controller = Robolectric.buildActivity(ProfileActivity.class, testIntent).create().start().resume();
            profileActivity = controller.get();
            JSONObject jsonObject = null;

            spyActivity = Mockito.spy(profileActivity);
            Whitebox.setInternalState(profileActivity, "presenter", presenter);
            profileTasksFragment = ProfileTasksFragment.newInstance(null);
            startFragment(profileTasksFragment);

            JSONArray fields = Whitebox.invokeMethod(profileTasksFragment, "createAccordionValues", jsonObject);
            Assert.assertNotNull(fields);
            Assert.assertEquals(0, fields.length());

            Whitebox.setInternalState(profileTasksFragment, "currentTask", getTask());
            Task task = Whitebox.invokeMethod(profileTasksFragment, "updateTaskValue", fields);
            Assert.assertNotNull(task);
            Assert.assertEquals("myTask", task.getKey());

            JSONObject taskValue = new JSONObject(task.getValue());
            Assert.assertNotNull(taskValue);
        } catch (JSONException e) {
            Timber.e(e, " --> testUpdateTaskValueWithNullForm");
        } catch (Exception e) {
            Timber.e(e, " --> testUpdateTaskValueWithNullForm");
        }
    }

    @Test
    public void testGetForm() throws Exception {
        Intent testIntent = new Intent();
        testIntent.putExtra(ConstantsUtils.IntentKeyUtils.BASE_ENTITY_ID, DUMMY_BASE_ENTITY_ID);
        HashMap<String, String> map = new HashMap<>();
        map.put(DBConstantsUtils.KeyUtils.CONTACT_STATUS, ConstantsUtils.AlertStatusUtils.EXPIRED);
        map.put(DBConstantsUtils.KeyUtils.LAST_CONTACT_RECORD_DATE, "10-12-2018");
        map.put(DBConstantsUtils.KeyUtils.NEXT_CONTACT, "3");
        testIntent.putExtra(ConstantsUtils.IntentKeyUtils.CLIENT_MAP, map);

        controller = Robolectric.buildActivity(ProfileActivity.class, testIntent).create().start().resume();
        profileActivity = controller.get();

        spyActivity = Mockito.spy(profileActivity);
        Whitebox.setInternalState(profileActivity, "presenter", presenter);
        profileTasksFragment = ProfileTasksFragment.newInstance(null);
        startFragment(profileTasksFragment);

        Form form = Whitebox.invokeMethod(profileTasksFragment, "getForm");
        Assert.assertNotNull(form);
        Assert.assertEquals(form.getName(), "Contact Tasks");
    }

    @Test
    public void testOnActivityResult() {
        String taskForm = "{\"validate_on_submit\":true,\"display_scroll_bars\":true,\"count\":\"1\",\"encounter_type\":\"Contact Tasks\",\"entity_id\":\"\",\"relational_id\":\"\",\"form_version\":\"0.0.1\",\"metadata\":{\"start\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"start\",\"openmrs_entity_id\":\"163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"value\":\"2020-01-30 06:53:27\"},\"end\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"end\",\"openmrs_entity_id\":\"163138AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"value\":\"2020-01-30 06:53:32\"},\"today\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"encounter\",\"openmrs_entity_id\":\"encounter_date\",\"value\":\"30-01-2020\"},\"deviceid\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"deviceid\",\"openmrs_entity_id\":\"163149AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"value\":\"358240051111110\"},\"subscriberid\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"subscriberid\",\"openmrs_entity_id\":\"163150AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"value\":\"310260000000000\"},\"simserial\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"simserial\",\"openmrs_entity_id\":\"163151AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"value\":\"89014103211118510720\"},\"phonenumber\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"phonenumber\",\"openmrs_entity_id\":\"163152AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"value\":\"+15555215554\"},\"encounter_location\":\"\",\"look_up\":{\"entity_id\":\"\",\"value\":\"\"}},\"global_previous\":[\"blood_type_test_status\",\"hiv_test_partner_result\",\"hiv_test_status\",\"hiv_test_result\",\"hiv_test_partner_status\",\"hepb_test_status\",\"hepc_test_status\",\"syph_test_status\",\"tb_screening_status\",\"hiv_positive\",\"urine_test_status\",\"hb_test_status\",\"syphilis_positive\",\"hepc_positive\",\"syph_test_status\",\"ultrasound\",\"hepb_positive\"],\"step1\":{\"display_back_button\":\"true\",\"title\":\"Hepatitis C test\",\"fields\":[{\"key\":\"hepc_test_status\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"1118AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"label\":\"Hepatitis C test\",\"label_text_style\":\"bold\",\"text_color\":\"#000000\",\"type\":\"extended_radio_button\",\"options\":[{\"key\":\"done_today\",\"text\":\"Done today\",\"type\":\"done_today\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165383AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"done_earlier\",\"text\":\"Done earlier\",\"type\":\"done_earlier\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165385AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"ordered\",\"text\":\"Ordered\",\"type\":\"ordered\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165384AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"not_done\",\"text\":\"Not done\",\"type\":\"not_done\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"1118AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}],\"v_required\":{\"value\":true},\"index\":\"0\",\"value\":\"not_done\",\"step\":\"step1\",\"is-rule-check\":true},{\"key\":\"spacer\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"spacer\",\"type\":\"spacer\",\"spacer_height\":\"10dp\",\"index\":\"1\"},{\"key\":\"hepc_test_notdone\",\"openmrs_entity_parent\":\"161474AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165182AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"type\":\"check_box\",\"label\":\"Reason\",\"label_text_style\":\"bold\",\"options\":[{\"key\":\"stock_out\",\"text\":\"Stock out\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165183AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"value\":true},{\"key\":\"expired_stock\",\"text\":\"Expired stock\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165299AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"value\":true},{\"key\":\"other\",\"text\":\"Other (specify)\",\"openmrs_entity_parent\":\"161474AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"5622AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}],\"v_required\":{\"value\":true},\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"tests_relevance_rules.yml\"}}},\"index\":\"2\",\"value\":[\"expired_stock\",\"stock_out\"],\"is_visible\":true,\"step\":\"step1\",\"is-rule-check\":true},{\"key\":\"hepc_test_notdone_other\",\"openmrs_entity_parent\":\"161474AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165435AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"type\":\"edit_text\",\"hint\":\"Specify\",\"edit_type\":\"name\",\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"tests_relevance_rules.yml\"}}},\"index\":\"3\",\"is_visible\":false},{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"key\":\"hepc_test_date_today_hidden\",\"type\":\"hidden\",\"label_text_style\":\"bold\",\"text_color\":\"#000000\",\"calculation\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"tests_calculation_rules.yml\"}}},\"index\":\"4\",\"value\":\"0\"},{\"key\":\"hepc_test_date\",\"openmrs_entity_parent\":\"161474AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"163724AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"type\":\"date_picker\",\"hint\":\"Hep C test date\",\"expanded\":\"false\",\"max_date\":\"today\",\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"tests_relevance_rules.yml\"}}},\"calculation\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"tests_calculation_rules.yml\"}}},\"v_required\":{\"value\":true,\"err\":\"Select the date of the Hepatitis C test.\"},\"index\":\"5\",\"is_visible\":false},{\"key\":\"hepc_test_type\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165437AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"type\":\"native_radio\",\"label\":\"Hep C test type\",\"label_info_text\":\"Anti-HCV laboratory-based immunoassay is the preferred method for testing for Hep C infection in pregnancy. If immunoassay is not available, Anti-HCV rapid diagnostic test (RDT) is recommended over Dried Blood Spot (DBS) Anti-HCV testing.\",\"label_info_title\":\"Hep C test type\",\"label_text_style\":\"bold\",\"options\":[{\"key\":\"anti_hcv_lab_based\",\"text\":\"Anti-HCV laboratory-based immunoassay (recommended)\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"1325AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"anti_hcv_rdt\",\"text\":\"Anti-HCV rapid diagnostic test (RDT)\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165302AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"anti_hcv_dbs\",\"text\":\"Dried Blood Spot (DBS) anti-HCV test\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"161471AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}],\"v_required\":{\"value\":true},\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"tests_relevance_rules.yml\"}}},\"index\":\"6\",\"is_visible\":false,\"step\":\"step1\",\"is-rule-check\":true},{\"key\":\"hcv_lab_ima\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"1325AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"type\":\"native_radio\",\"label\":\"Anti-HCV laboratory-based immunoassay (recommended)\",\"label_text_style\":\"bold\",\"options\":[{\"key\":\"positive\",\"text\":\"Positive\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"703AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"negative\",\"text\":\"Negative\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"664AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}],\"v_required\":{\"value\":true},\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"tests_relevance_rules.yml\"}}},\"index\":\"7\",\"step\":\"step1\",\"is-rule-check\":true,\"is_visible\":false},{\"key\":\"hcv_rdt\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165302AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"type\":\"native_radio\",\"label\":\"Anti-HCV rapid diagnostic test (RDT)\",\"label_text_style\":\"bold\",\"options\":[{\"key\":\"positive\",\"text\":\"Positive\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"703AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"negative\",\"text\":\"Negative\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"664AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}],\"v_required\":{\"value\":true},\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"tests_relevance_rules.yml\"}}},\"index\":\"8\",\"step\":\"step1\",\"is-rule-check\":true,\"is_visible\":false},{\"key\":\"hcv_dbs\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"161471AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"type\":\"native_radio\",\"label\":\"Dried Blood Spot (DBS) anti-HCV test\",\"label_text_style\":\"bold\",\"options\":[{\"key\":\"positive\",\"text\":\"Positive\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"703AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"negative\",\"text\":\"Negative\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"664AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}],\"v_required\":{\"value\":true},\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"tests_relevance_rules.yml\"}}},\"index\":\"9\",\"step\":\"step1\",\"is-rule-check\":true,\"is_visible\":false},{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"28AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"key\":\"hepc_positive\",\"type\":\"hidden\",\"label_text_style\":\"bold\",\"text_color\":\"#000000\",\"calculation\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"tests_calculation_rules.yml\"}}},\"index\":\"10\",\"value\":\"0\",\"step\":\"step1\",\"is-rule-check\":true},{\"key\":\"hepatitis_c_danger_toaster\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"type\":\"toaster_notes\",\"text\":\"Hep C positive diagnosis!\",\"toaster_info_text\":\"Counselling and referral required.\",\"toaster_info_title\":\"Hep C positive diagnosis!\",\"toaster_type\":\"problem\",\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"tests_relevance_rules.yml\"}}},\"index\":\"11\",\"is_visible\":false}]},\"invisible_required_fields\":\"[hcv_dbs, hcv_lab_ima, hepc_test_type, hepc_test_date, hcv_rdt]\",\"details\":{\"appVersionName\":\"1.7.13-SNAPSHOT\",\"formVersion\":\"0.0.1\"}}";
        Intent testIntent = new Intent();
        testIntent.putExtra(ConstantsUtils.IntentKeyUtils.JSON, taskForm);
        testIntent.putExtra(ConstantsUtils.IntentKeyUtils.BASE_ENTITY_ID, DUMMY_BASE_ENTITY_ID);
        HashMap<String, String> map = new HashMap<>();
        map.put(DBConstantsUtils.KeyUtils.CONTACT_STATUS, ConstantsUtils.AlertStatusUtils.EXPIRED);
        map.put(DBConstantsUtils.KeyUtils.LAST_CONTACT_RECORD_DATE, "10-12-2018");
        map.put(DBConstantsUtils.KeyUtils.NEXT_CONTACT, "3");
        testIntent.putExtra(ConstantsUtils.IntentKeyUtils.CLIENT_MAP, map);

        controller = Robolectric.buildActivity(ProfileActivity.class, testIntent).create().start().resume();
        profileActivity = controller.get();

        spyActivity = Mockito.spy(profileActivity);
        Whitebox.setInternalState(profileActivity, "presenter", presenter);
        profileTasksFragment = ProfileTasksFragment.newInstance(null);
        startFragment(profileTasksFragment);

        ProfileTasksFragment profileTasksFragmentSpy = Mockito.spy(profileTasksFragment);
        profileTasksFragmentSpy.setCurrentTask(getTask());

        profileTasksFragmentSpy.onActivityResult(1213, 1233, testIntent);
        Mockito.verify(profileTasksFragmentSpy, Mockito.times(1)).getPresenter();
    }

    @Test
    public void testStartTaskForm() throws JSONException {
        String taskForm = "{\"validate_on_submit\":true,\"display_scroll_bars\":true,\"count\":\"1\",\"encounter_type\":\"Contact Tasks\",\"entity_id\":\"\",\"relational_id\":\"\",\"form_version\":\"0.0.1\",\"metadata\":{\"start\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"start\",\"openmrs_entity_id\":\"163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"value\":\"2020-01-30 06:53:27\"},\"end\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"end\",\"openmrs_entity_id\":\"163138AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"value\":\"2020-01-30 06:53:32\"},\"today\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"encounter\",\"openmrs_entity_id\":\"encounter_date\",\"value\":\"30-01-2020\"},\"deviceid\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"deviceid\",\"openmrs_entity_id\":\"163149AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"value\":\"358240051111110\"},\"subscriberid\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"subscriberid\",\"openmrs_entity_id\":\"163150AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"value\":\"310260000000000\"},\"simserial\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"simserial\",\"openmrs_entity_id\":\"163151AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"value\":\"89014103211118510720\"},\"phonenumber\":{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_data_type\":\"phonenumber\",\"openmrs_entity_id\":\"163152AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"value\":\"+15555215554\"},\"encounter_location\":\"\",\"look_up\":{\"entity_id\":\"\",\"value\":\"\"}},\"global_previous\":[\"blood_type_test_status\",\"hiv_test_partner_result\",\"hiv_test_status\",\"hiv_test_result\",\"hiv_test_partner_status\",\"hepb_test_status\",\"hepc_test_status\",\"syph_test_status\",\"tb_screening_status\",\"hiv_positive\",\"urine_test_status\",\"hb_test_status\",\"syphilis_positive\",\"hepc_positive\",\"syph_test_status\",\"ultrasound\",\"hepb_positive\"],\"step1\":{\"display_back_button\":\"true\",\"title\":\"Hepatitis C test\",\"fields\":[{\"key\":\"hepc_test_status\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"1118AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"label\":\"Hepatitis C test\",\"label_text_style\":\"bold\",\"text_color\":\"#000000\",\"type\":\"extended_radio_button\",\"options\":[{\"key\":\"done_today\",\"text\":\"Done today\",\"type\":\"done_today\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165383AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"done_earlier\",\"text\":\"Done earlier\",\"type\":\"done_earlier\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165385AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"ordered\",\"text\":\"Ordered\",\"type\":\"ordered\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165384AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"not_done\",\"text\":\"Not done\",\"type\":\"not_done\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"1118AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}],\"v_required\":{\"value\":true},\"index\":\"0\",\"value\":\"not_done\",\"step\":\"step1\",\"is-rule-check\":true},{\"key\":\"spacer\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"spacer\",\"type\":\"spacer\",\"spacer_height\":\"10dp\",\"index\":\"1\"},{\"key\":\"hepc_test_notdone\",\"openmrs_entity_parent\":\"161474AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165182AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"type\":\"check_box\",\"label\":\"Reason\",\"label_text_style\":\"bold\",\"options\":[{\"key\":\"stock_out\",\"text\":\"Stock out\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165183AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"value\":true},{\"key\":\"expired_stock\",\"text\":\"Expired stock\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165299AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"value\":true},{\"key\":\"other\",\"text\":\"Other (specify)\",\"openmrs_entity_parent\":\"161474AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"5622AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}],\"v_required\":{\"value\":true},\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"tests_relevance_rules.yml\"}}},\"index\":\"2\",\"value\":[\"expired_stock\",\"stock_out\"],\"is_visible\":true,\"step\":\"step1\",\"is-rule-check\":true},{\"key\":\"hepc_test_notdone_other\",\"openmrs_entity_parent\":\"161474AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165435AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"type\":\"edit_text\",\"hint\":\"Specify\",\"edit_type\":\"name\",\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"tests_relevance_rules.yml\"}}},\"index\":\"3\",\"is_visible\":false},{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"key\":\"hepc_test_date_today_hidden\",\"type\":\"hidden\",\"label_text_style\":\"bold\",\"text_color\":\"#000000\",\"calculation\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"tests_calculation_rules.yml\"}}},\"index\":\"4\",\"value\":\"0\"},{\"key\":\"hepc_test_date\",\"openmrs_entity_parent\":\"161474AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"163724AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"type\":\"date_picker\",\"hint\":\"Hep C test date\",\"expanded\":\"false\",\"max_date\":\"today\",\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"tests_relevance_rules.yml\"}}},\"calculation\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"tests_calculation_rules.yml\"}}},\"v_required\":{\"value\":true,\"err\":\"Select the date of the Hepatitis C test.\"},\"index\":\"5\",\"is_visible\":false},{\"key\":\"hepc_test_type\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165437AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"type\":\"native_radio\",\"label\":\"Hep C test type\",\"label_info_text\":\"Anti-HCV laboratory-based immunoassay is the preferred method for testing for Hep C infection in pregnancy. If immunoassay is not available, Anti-HCV rapid diagnostic test (RDT) is recommended over Dried Blood Spot (DBS) Anti-HCV testing.\",\"label_info_title\":\"Hep C test type\",\"label_text_style\":\"bold\",\"options\":[{\"key\":\"anti_hcv_lab_based\",\"text\":\"Anti-HCV laboratory-based immunoassay (recommended)\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"1325AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"anti_hcv_rdt\",\"text\":\"Anti-HCV rapid diagnostic test (RDT)\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165302AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"anti_hcv_dbs\",\"text\":\"Dried Blood Spot (DBS) anti-HCV test\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"161471AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}],\"v_required\":{\"value\":true},\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"tests_relevance_rules.yml\"}}},\"index\":\"6\",\"is_visible\":false,\"step\":\"step1\",\"is-rule-check\":true},{\"key\":\"hcv_lab_ima\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"1325AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"type\":\"native_radio\",\"label\":\"Anti-HCV laboratory-based immunoassay (recommended)\",\"label_text_style\":\"bold\",\"options\":[{\"key\":\"positive\",\"text\":\"Positive\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"703AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"negative\",\"text\":\"Negative\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"664AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}],\"v_required\":{\"value\":true},\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"tests_relevance_rules.yml\"}}},\"index\":\"7\",\"step\":\"step1\",\"is-rule-check\":true,\"is_visible\":false},{\"key\":\"hcv_rdt\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"165302AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"type\":\"native_radio\",\"label\":\"Anti-HCV rapid diagnostic test (RDT)\",\"label_text_style\":\"bold\",\"options\":[{\"key\":\"positive\",\"text\":\"Positive\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"703AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"negative\",\"text\":\"Negative\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"664AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}],\"v_required\":{\"value\":true},\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"tests_relevance_rules.yml\"}}},\"index\":\"8\",\"step\":\"step1\",\"is-rule-check\":true,\"is_visible\":false},{\"key\":\"hcv_dbs\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"161471AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"type\":\"native_radio\",\"label\":\"Dried Blood Spot (DBS) anti-HCV test\",\"label_text_style\":\"bold\",\"options\":[{\"key\":\"positive\",\"text\":\"Positive\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"703AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"},{\"key\":\"negative\",\"text\":\"Negative\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"664AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\"}],\"v_required\":{\"value\":true},\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"tests_relevance_rules.yml\"}}},\"index\":\"9\",\"step\":\"step1\",\"is-rule-check\":true,\"is_visible\":false},{\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"concept\",\"openmrs_entity_id\":\"28AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"key\":\"hepc_positive\",\"type\":\"hidden\",\"label_text_style\":\"bold\",\"text_color\":\"#000000\",\"calculation\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"tests_calculation_rules.yml\"}}},\"index\":\"10\",\"value\":\"0\",\"step\":\"step1\",\"is-rule-check\":true},{\"key\":\"hepatitis_c_danger_toaster\",\"openmrs_entity_parent\":\"\",\"openmrs_entity\":\"\",\"openmrs_entity_id\":\"\",\"type\":\"toaster_notes\",\"text\":\"Hep C positive diagnosis!\",\"toaster_info_text\":\"Counselling and referral required.\",\"toaster_info_title\":\"Hep C positive diagnosis!\",\"toaster_type\":\"problem\",\"relevance\":{\"rules-engine\":{\"ex-rules\":{\"rules-file\":\"tests_relevance_rules.yml\"}}},\"index\":\"11\",\"is_visible\":false}]},\"invisible_required_fields\":\"[hcv_dbs, hcv_lab_ima, hepc_test_type, hepc_test_date, hcv_rdt]\",\"details\":{\"appVersionName\":\"1.7.13-SNAPSHOT\",\"formVersion\":\"0.0.1\"}}";
        Intent testIntent = new Intent();
        testIntent.putExtra(ConstantsUtils.IntentKeyUtils.JSON, taskForm);
        testIntent.putExtra(ConstantsUtils.IntentKeyUtils.BASE_ENTITY_ID, DUMMY_BASE_ENTITY_ID);
        HashMap<String, String> map = new HashMap<>();
        map.put(DBConstantsUtils.KeyUtils.CONTACT_STATUS, ConstantsUtils.AlertStatusUtils.EXPIRED);
        map.put(DBConstantsUtils.KeyUtils.LAST_CONTACT_RECORD_DATE, "10-12-2018");
        map.put(DBConstantsUtils.KeyUtils.NEXT_CONTACT, "3");
        testIntent.putExtra(ConstantsUtils.IntentKeyUtils.CLIENT_MAP, map);

        controller = Robolectric.buildActivity(ProfileActivity.class, testIntent).create().start().resume();
        profileActivity = controller.get();

        spyActivity = Mockito.spy(profileActivity);
        Whitebox.setInternalState(profileActivity, "presenter", presenter);
        profileTasksFragment = ProfileTasksFragment.newInstance(null);
        startFragment(profileTasksFragment);

        ProfileTasksFragment profileTasksFragmentSpy = Mockito.spy(profileTasksFragment);
        profileTasksFragmentSpy.setCurrentTask(getTask());

        profileTasksFragmentSpy.startTaskForm(new JSONObject(taskForm), getTask());
        String taskKey = profileTasksFragmentSpy.getCurrentTask().getKey();
        Assert.assertEquals(taskKey, "myTask");
    }

    @After
    public void tearDown() {
        destroyController();
    }
}
