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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.rules.RuleConstant;

import org.jeasy.rules.api.Facts;
import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.anc.R;
import org.smartregister.anc.adapter.ContactSummaryFinishAdapter;
import org.smartregister.anc.application.AncApplication;
import org.smartregister.anc.contract.ProfileContract;
import org.smartregister.anc.domain.ContactSummary;
import org.smartregister.anc.domain.ContactSummaryItem;
import org.smartregister.anc.helper.ImageRenderHelper;
import org.smartregister.anc.model.PartialContact;
import org.smartregister.anc.presenter.ProfilePresenter;
import org.smartregister.anc.util.Constants;
import org.smartregister.anc.util.DBConstants;
import org.smartregister.anc.util.Utils;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

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
    private Random random = new Random();
    private Yaml yaml;
    private static final String CONFIG_FOLDER_PATH = "config/";
    private static final String TAG = ContactSummaryFinishActivity.class.getCanonicalName();
    private List<ContactSummary> contactSummaryList = new ArrayList<>();
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setUpViews();

        mProfilePresenter = new ProfilePresenter(this);

        imageRenderHelper = new ImageRenderHelper(this);

        Constructor constructor = new Constructor(ContactSummary.class);
        TypeDescription customTypeDescription = new TypeDescription(ContactSummary.class);
        customTypeDescription.addPropertyParameters(ContactSummaryItem.FIELD_CONTACT_SUMMARY_ITEMS, ContactSummaryItem.class);
        constructor.addTypeDescription(customTypeDescription);
        yaml = new Yaml(constructor);
        loadContactSummaryData();



        gson = new Gson();

    }

    private void setUpViews() {

        ageView = findViewById(R.id.textview_age);
        gestationAgeView = findViewById(R.id.textview_gestation_age);
        ancIdView = findViewById(R.id.textview_anc_id);
        nameView = findViewById(R.id.textview_name);
        imageView = findViewById(R.id.imageview_profile);

        findViewById(R.id.btn_profile_registration_info).setVisibility(View.GONE);

        ImageButton backButton = findViewById(R.id.cancel_button);
        backButton.setImageResource(R.drawable.ic_arrow_back_white);
        backButton.setOnClickListener(this);

        ((TextView) findViewById(R.id.top_patient_name)).setText(String.format(this.getString(R.string.contact_number), getIntent().getExtras().getInt(Constants.INTENT_KEY.CONTACT_NO)));
        Button finalize = findViewById(R.id.finalize_contact);
        finalize.setText(R.string.save_and_finish);
        finalize.setEnabled(false);
        finalize.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        super.onClick(view);

        switch (view.getId()) {
            case R.id.cancel_button:
                super.onBackPressed();
                break;

            case R.id.finalize_contact:
                saveFinishForm();
                break;

            default:
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        // When user click home menu item then quit this activity.
        if (itemId == android.R.id.home) {
            finish();
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
    public void onResume() {
        super.onResume();
        String baseEntityId = getIntent().getStringExtra(Constants.INTENT_KEY.BASE_ENTITY_ID);
        mProfilePresenter.refreshProfileView(baseEntityId);
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
        imageRenderHelper.refreshProfileImage(baseEntityId, imageView);
    }

    @Override
    public void setWomanPhoneNumber(String phoneNumber) {
        //Overridden
    }

    @Override
    public String getIntentString(String intentKey) {

        return this.getIntent().getStringExtra(intentKey);
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
        try {

            CommonPersonObjectClient pc = (CommonPersonObjectClient) getIntent().getExtras().get(Constants.INTENT_KEY.CLIENT);

            mProfilePresenter.saveFinishForm(pc.getDetails());

            Intent contactSummaryIntent = new Intent(this, ContactSummarySendActivity.class);
            contactSummaryIntent.putExtra(Constants.INTENT_KEY.BASE_ENTITY_ID, getIntent().getExtras().getString(Constants.INTENT_KEY.BASE_ENTITY_ID));

            startActivity(contactSummaryIntent);

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

    }

    private void processRequiredStepsField(JSONObject object) throws Exception {
        if (object != null) {
            Iterator<String> keys = object.keys();

            while (keys.hasNext()) {
                String key = keys.next();

                if (key.startsWith(RuleConstant.STEP)) {
                    JSONArray stepArray = object.getJSONObject(key).getJSONArray(JsonFormConstants.FIELDS);

                    for (int i = 0; i < stepArray.length(); i++) {

                        JSONObject fieldObject = stepArray.getJSONObject(i);

                        String fieldKey = getKey(fieldObject);
                        String fieldValue = getValue(fieldObject);

                        if (fieldKey != null && fieldValue != null) {

                            facts.put(fieldKey, isList(fieldValue) ?  gson.fromJson(fieldValue, ArrayList.class) : fieldValue);
                        }


                    }

                }
            }
        }
    }


    private boolean isList(String value) {
        return !value.isEmpty() && value.charAt(0) == '[';
    }

    private void process() throws Exception {


//Get actual Data
        JSONObject object;

        List<PartialContact> partialContacts = AncApplication.getInstance().getPartialContactRepository().getPartialContacts(getIntent().getStringExtra(Constants.INTENT_KEY.BASE_ENTITY_ID), getIntent().getIntExtra(Constants.INTENT_KEY.CONTACT_NO, 1));

        for (PartialContact partialContact : partialContacts) {
            if (partialContact.getFormJson() != null) {
                object = new JSONObject(partialContact.getFormJson());
                processRequiredStepsField(object);
            }

        }


        Iterable<Object> ruleObjects = readYaml(Constants.CONFIG_FILE.CONTACT_SUMMARY);

        contactSummaryList = new ArrayList<>();
        for (Object ruleObject : ruleObjects) {
            ContactSummary contactSummary = (ContactSummary) ruleObject;
            contactSummaryList.add(contactSummary);
        }
    }

    private String getKey(JSONObject jsonObject) throws Exception {

        return jsonObject.has(JsonFormConstants.KEY) ? jsonObject.getString(JsonFormConstants.KEY) : null;
    }

    private String getValue(JSONObject jsonObject) throws Exception {
        int max = 30;
        int min = 1;
        return jsonObject.has(JsonFormConstants.VALUE) ? jsonObject.getString(JsonFormConstants.VALUE) : String.valueOf(random.nextInt(max - min + 1) + min);
    }

    private void loadContactSummaryData() {

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                showProgressDialog(R.string.please_wait_message);
                progressDialog.setMessage("Summarizing contact " + String.format(context().applicationContext().getString(R.string.contact_number), getIntent().getExtras().getInt(Constants.INTENT_KEY.CONTACT_NO)) + " data");
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
                edd = "2018-02-20";
                if (edd != null) {
                    setProfileGestationAge(String.valueOf(Utils.getGestationAgeFromEDDate(edd)));

                }


                ContactSummaryFinishAdapter adapter = new ContactSummaryFinishAdapter(ContactSummaryFinishActivity.this, contactSummaryList, facts);


                // set up the RecyclerView
                RecyclerView recyclerView = findViewById(R.id.contact_summary_finish_recycler);
                recyclerView.setLayoutManager(new LinearLayoutManager(ContactSummaryFinishActivity.this));
                recyclerView.setAdapter(adapter);
                //  ((TextView) findViewById(R.id.section_details)).setText(crazyOutput);
                hideProgressDialog();


            }
        }.execute();
    }

    public java.lang.Iterable<Object> readYaml(String filename) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(this.getAssets().open((CONFIG_FOLDER_PATH + filename)));
        return yaml.loadAll(inputStreamReader);
    }
}

