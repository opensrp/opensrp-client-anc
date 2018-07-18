package org.smartregister.anc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.View;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;
import org.smartregister.anc.R;
import org.smartregister.anc.application.AncApplication;
import org.smartregister.anc.contract.RegisterContract;
import org.smartregister.anc.event.ClientDetailsFetchedEvent;
import org.smartregister.anc.event.PictureUpdatedEvent;
import org.smartregister.anc.fragment.BaseProfileFragment;
import org.smartregister.anc.task.FetchProfileDataTask;
import org.smartregister.anc.util.Constants;
import org.smartregister.anc.util.JsonFormUtils;
import org.smartregister.anc.util.Utils;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.view.activity.SecuredActivity;

import java.io.File;

/**
 * Created by ndegwamartin on 16/07/2018.
 */
public abstract class BaseProfileActivity extends SecuredActivity implements AppBarLayout.OnOffsetChangedListener, View.OnClickListener, RegisterContract.InteractorCallBack {
    protected AppBarLayout appBarLayout;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private boolean appBarTitleIsShown = true;
    private int appBarLayoutScrollRange = -1;
    protected String womanName;
    protected File currentfile;
    private RegisterContract.Interactor registerInteractor;

    private static final String TAG = BaseProfileFragment.class.getCanonicalName();

    private static final int REQUEST_CODE_GET_JSON = 3432;
    private static final int REQUEST_TAKE_PHOTO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        findViewById(R.id.btn_profile_registration_info).setOnClickListener(this);

        Toolbar toolbar = findViewById(R.id.collapsing_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        appBarLayout = findViewById(R.id.collapsing_toolbar_appbarlayout);

        // Set collapsing tool bar title.
        collapsingToolbarLayout = appBarLayout.findViewById(R.id.collapsing_toolbar_layout);

        appBarLayout.addOnOffsetChangedListener(this);
    }

    @Override
    public void onClick(View view) {
        String baseEntityId = getIntent().getStringExtra(Constants.INTENT_KEY.BASE_ENTITY_ID);
        new FetchProfileDataTask(null).execute(baseEntityId);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void startFormForEdit(ClientDetailsFetchedEvent event) {
        if (event != null) {

            String formMetadata = JsonFormUtils.getAutoPopulatedJsonEditFormString(this, event.getWomanClient());
            // startFormActivity(Constants.JSON_FORM.ANC_REGISTER, event.getWomanClient().get(DBConstants.KEY.BASE_ENTITY_ID), formMetadata);
            try {

                JsonFormUtils.startFormForEdit(this, 1, formMetadata);

            } catch (Exception e) {
                Log.e("TAG", e.getMessage());
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    public AppBarLayout getProfileAppBarLayout() {
        return appBarLayout;
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

        if (appBarLayoutScrollRange == -1) {
            appBarLayoutScrollRange = appBarLayout.getTotalScrollRange();
        }
        if (appBarLayoutScrollRange + verticalOffset == 0) {

            collapsingToolbarLayout.setTitle(womanName);
            appBarTitleIsShown = true;
        } else if (appBarTitleIsShown) {
            collapsingToolbarLayout.setTitle(" ");
            appBarTitleIsShown = false;
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        AllSharedPreferences allSharedPreferences = AncApplication.getInstance().getContext().allSharedPreferences();
        if (requestCode == REQUEST_CODE_GET_JSON && resultCode == RESULT_OK) {
            processFormDetailsSave(data, allSharedPreferences);

        } else if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {

            processPhotoUpload(allSharedPreferences, data.getStringExtra(Constants.INTENT_KEY.BASE_ENTITY_ID));

        }
    }


    protected void processFormDetailsSave(Intent data, AllSharedPreferences allSharedPreferences) {
        try {
            String jsonString = data.getStringExtra("json");
            Log.d("JSONResult", jsonString);

            JSONObject form = new JSONObject(jsonString);
            if (form.getString(JsonFormUtils.ENCOUNTER_TYPE).equals(Constants.EventType.CLOSE) || form.getString(JsonFormUtils.ENCOUNTER_TYPE).equals(Constants.EventType.UPDATE_REGISTRATION)) {

                Pair<Client, Event> values = JsonFormUtils.processRegistrationForm(jsonString, allSharedPreferences.fetchRegisteredANM());
                registerInteractor.saveRegistration(values, jsonString, true, this);
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    protected void processPhotoUpload(AllSharedPreferences allSharedPreferences, String baseEntityId) {
        try {
            String imageLocation = currentfile.getAbsolutePath();

            JsonFormUtils.saveImage(allSharedPreferences.fetchRegisteredANM(), baseEntityId, imageLocation);

            Utils.postStickyEvent(new PictureUpdatedEvent());

        } catch (Exception e) {
            Utils.showToast(this, "Error occurred saving image...");
            Log.e(TAG, e.getMessage());
        }
    }

}
