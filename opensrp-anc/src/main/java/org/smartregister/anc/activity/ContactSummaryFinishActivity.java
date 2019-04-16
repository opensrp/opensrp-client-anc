package org.smartregister.anc.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.jeasy.rules.api.Facts;
import org.json.JSONObject;
import org.smartregister.anc.R;
import org.smartregister.anc.adapter.ContactSummaryFinishAdapter;
import org.smartregister.anc.application.AncApplication;
import org.smartregister.anc.contract.ProfileContract;
import org.smartregister.anc.domain.YamlConfig;
import org.smartregister.anc.model.PartialContact;
import org.smartregister.anc.presenter.ProfilePresenter;
import org.smartregister.anc.repository.PartialContactRepository;
import org.smartregister.anc.repository.PatientRepository;
import org.smartregister.anc.util.Constants;
import org.smartregister.anc.util.ContactJsonFormUtils;
import org.smartregister.anc.util.DBConstants;
import org.smartregister.anc.util.FilePath;
import org.smartregister.anc.util.Utils;
import org.smartregister.helper.ImageRenderHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ndegwamartin on 10/07/2018.
 */
public class ContactSummaryFinishActivity extends BaseProfileActivity implements ProfileContract.View {

    private TextView nameView;
    private TextView ageView;
    private TextView gestationAgeView;
    private TextView ancIdView;
    private ImageView imageView;
    private ImageRenderHelper imageRenderHelper;
    private Facts facts = new Facts();
    private static final String TAG = ContactSummaryFinishActivity.class.getCanonicalName();
    private List<YamlConfig> yamlConfigList = new ArrayList<>();
    private String baseEntityId;
    private MenuItem saveFinishMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        baseEntityId = getIntent().getStringExtra(Constants.INTENT_KEY.BASE_ENTITY_ID);

        setUpViews();

        mProfilePresenter = new ProfilePresenter(this);
        imageRenderHelper = new ImageRenderHelper(this);
        loadContactSummaryData();

    }

    private void setUpViews() {

        ageView = findViewById(R.id.textview_age);
        gestationAgeView = findViewById(R.id.textview_gestation_age);
        ancIdView = findViewById(R.id.textview_anc_id);
        nameView = findViewById(R.id.textview_name);
        imageView = findViewById(R.id.imageview_profile);

        findViewById(R.id.btn_profile_registration_info).setVisibility(View.GONE);

        collapsingToolbarLayout.setTitleEnabled(false);
        actionBar.setTitle(String.format(this.getString(R.string.contact_number),
                getIntent().getExtras().getInt(Constants.INTENT_KEY.CONTACT_NO)));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        // When user click home menu item then quit this activity.
        if (itemId == android.R.id.home) {
            PatientRepository.updateEDDDate(baseEntityId, null); //Reset EDD
            super.onBackPressed();
        } else {
            saveFinishForm();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_contact_summary_finish_activity, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        saveFinishMenuItem = menu.findItem(R.id.save_finish_menu_item);
        saveFinishMenuItem.setEnabled(false);//initially disable

        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected int getViewLayoutId() {
        return R.layout.activity_contact_summary_finish;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mProfilePresenter.onDestroy(isChangingConfigurations());
    }

    @Override
    protected void onCreation() { //Overriden from Secured Activity
    }

    @Override
    protected void onResumption() {//Overriden from Secured Activity

    }

    @Override
    public void setProfileName(String fullName) {
        this.womanName = fullName;
        nameView.setText(fullName);
    }

    @Override
    public void setProfileID(String ancId) {
        ancIdView.setText("ID: " + ancId);
    }

    @Override
    public void setProfileAge(String age) {
        ageView.setText("AGE " + age);

    }

    @Override
    public void setProfileGestationAge(String gestationAge) {
        gestationAgeView.setText(gestationAge != null ? "GA: " + gestationAge + " WEEKS" : "GA");
    }

    @Override
    public void setProfileImage(String baseEntityId) {
        imageRenderHelper.refreshProfileImage(baseEntityId, imageView, R.drawable.ic_woman_with_baby);
    }

    @Override
    public void setPhoneNumber(String phoneNumber) {
        //Overridden
    }

    @Override
    public String getIntentString(String intentKey) {

        return getIntent().getStringExtra(intentKey);
    }

    @Override
    public void displayToast(int stringID) {
        Utils.showShortToast(this, this.getString(stringID));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        //Overriden
    }

    private void saveFinishForm() {


        new AsyncTask<Void, Void, Void>() {
            private HashMap<String, String> newWomanProfileDetails;

            @Override
            protected void onPreExecute() {
                showProgressDialog(R.string.please_wait_message);
                progressDialog.setMessage(
                        String.format(context().applicationContext().getString(R.string.finalizing_contact),
                                getIntent().getExtras().getInt(Constants.INTENT_KEY.CONTACT_NO)) + " data");
                progressDialog.show();
            }

            @Override
            protected Void doInBackground(Void... nada) {
                try {
                    HashMap<String, String> womanProfileDetails =
                            (HashMap<String, String>) PatientRepository.getWomanProfileDetails(getIntent().getExtras().getString(Constants.INTENT_KEY.BASE_ENTITY_ID));
                    int contactNo = getIntent().getExtras().getInt(Constants.INTENT_KEY.CONTACT_NO);
                    if (contactNo < 0) {
                        womanProfileDetails.put(Constants.REFERRAL, String.valueOf(contactNo));
                    }
                   newWomanProfileDetails =  mProfilePresenter.saveFinishForm(womanProfileDetails);

                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                }

                return null;

            }

            @Override
            protected void onPostExecute(Void result) {


                hideProgressDialog();

                Intent contactSummaryIntent = new Intent(ContactSummaryFinishActivity.this,
                        ContactSummarySendActivity.class);
                contactSummaryIntent.putExtra(Constants.INTENT_KEY.BASE_ENTITY_ID,
                        getIntent().getExtras().getString(Constants.INTENT_KEY.BASE_ENTITY_ID));
                contactSummaryIntent.putExtra(Constants.INTENT_KEY.CLIENT_MAP, newWomanProfileDetails);

                startActivity(contactSummaryIntent);

            }
        }.execute();


    }


    private void process() throws Exception {
        //Get actual Data
        JSONObject object;

        List<PartialContact> partialContacts = getPartialContactRepository()
                .getPartialContacts(getIntent().getStringExtra(Constants.INTENT_KEY.BASE_ENTITY_ID),
                        getIntent().getIntExtra(Constants.INTENT_KEY.CONTACT_NO, 1));

        for (PartialContact partialContact : partialContacts) {
            if (partialContact.getFormJsonDraft() != null || partialContact.getFormJson() != null) {
                object = new JSONObject(
                        partialContact.getFormJsonDraft() != null ? partialContact.getFormJsonDraft() : partialContact
                                .getFormJson());
                ContactJsonFormUtils.processRequiredStepsField(facts, object, this);
            }
        }

        Iterable<Object> ruleObjects = AncApplication.getInstance().readYaml(FilePath.FILE.CONTACT_SUMMARY);

        yamlConfigList = new ArrayList<>();
        for (Object ruleObject : ruleObjects) {
            YamlConfig yamlConfig = (YamlConfig) ruleObject;
            yamlConfigList.add(yamlConfig);
        }
    }

    protected void loadContactSummaryData() {
        try {

            new AsyncTask<Void, Void, Void>() {

                @Override
                protected void onPreExecute() {
                    showProgressDialog(R.string.please_wait_message);
                    progressDialog.setMessage(
                            String.format(context().applicationContext().getString(R.string.summarizing_contact_number),
                                    getIntent().getExtras().getInt(Constants.INTENT_KEY.CONTACT_NO)) + " data");
                    progressDialog.show();
                }

                @Override
                protected Void doInBackground(Void... nada) {
                    try {

                        process();
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage(), e);
                    }

                    return null;

                }

                @Override
                protected void onPostExecute(Void result) {

                    String edd = facts.get(DBConstants.KEY.EDD);
                    String contactNo = String.valueOf(getIntent().getExtras().getInt(Constants.INTENT_KEY.CONTACT_NO));

                    if (edd != null && saveFinishMenuItem != null) {

                        PatientRepository.updateEDDDate(baseEntityId, Utils.reverseHyphenSeperatedValues(edd, "-"));

                        saveFinishMenuItem.setEnabled(true);

                    } else if (edd == null && contactNo.contains("-")) {
                        saveFinishMenuItem.setEnabled(true);
                    }

                    ContactSummaryFinishAdapter adapter = new ContactSummaryFinishAdapter(ContactSummaryFinishActivity.this,
                            yamlConfigList, facts);
                    adapter.notifyDataSetChanged();

                    // set up the RecyclerView
                    RecyclerView recyclerView = findViewById(R.id.contact_summary_finish_recycler);
                    recyclerView.setLayoutManager(new LinearLayoutManager(ContactSummaryFinishActivity.this));
                    recyclerView.setAdapter(adapter);
                    //  ((TextView) findViewById(R.id.section_details)).setText(crazyOutput);
                    hideProgressDialog();

                    //load profile details

                    mProfilePresenter.refreshProfileView(baseEntityId);

                }
            }.execute();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    protected PartialContactRepository getPartialContactRepository() {
        return AncApplication.getInstance().getPartialContactRepository();
    }
}

