package org.smartregister.anc.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.smartregister.anc.R;
import org.smartregister.anc.application.AncApplication;
import org.smartregister.anc.contract.ProfileContract;
import org.smartregister.anc.event.ClientDetailsFetchedEvent;
import org.smartregister.anc.event.PatientRemovedEvent;
import org.smartregister.anc.task.FetchProfileDataTask;
import org.smartregister.anc.util.Constants;
import org.smartregister.anc.util.JsonFormUtils;
import org.smartregister.anc.util.Utils;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.view.activity.SecuredActivity;

/**
 * Created by ndegwamartin on 16/07/2018.
 */
public abstract class BaseProfileActivity extends SecuredActivity implements AppBarLayout.OnOffsetChangedListener, View.OnClickListener {
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private boolean appBarTitleIsShown = true;
    private int appBarLayoutScrollRange = -1;

    protected String womanName;
    protected AppBarLayout appBarLayout;
    protected ProgressDialog progressDialog;
    protected ProfileContract.Presenter mProfilePresenter;
    public final String TAG = BaseProfileActivity.class.getCanonicalName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getViewLayoutId());

        findViewById(R.id.btn_profile_registration_info).setOnClickListener(this);

        Toolbar toolbar = findViewById(R.id.collapsing_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        appBarLayout = findViewById(R.id.collapsing_toolbar_appbarlayout);
        if (appBarLayout != null) {
            // Set collapsing tool bar title.
            collapsingToolbarLayout = appBarLayout.findViewById(R.id.collapsing_toolbar_layout);

            appBarLayout.addOnOffsetChangedListener(this);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_profile_registration_info:

                String baseEntityId = getIntent().getStringExtra(Constants.INTENT_KEY.BASE_ENTITY_ID);
                new FetchProfileDataTask(true).execute(baseEntityId);

                break;
            default:
                break;
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void startFormForEdit(ClientDetailsFetchedEvent event) {
        if (event != null && event.isEditMode()) {

            String formMetadata = JsonFormUtils.getAutoPopulatedJsonEditRegisterFormString(this, event.getWomanClient());
            try {

                JsonFormUtils.startFormForEdit(this, JsonFormUtils.REQUEST_CODE_GET_JSON, formMetadata);

            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }

    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void refreshProfileTopSection(ClientDetailsFetchedEvent event) {
        if (event != null && !event.isEditMode()) {
            Utils.removeStickyEvent(event);
            mProfilePresenter.refreshProfileTopSection(event.getWomanClient());
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void removePatient(PatientRemovedEvent event) {
        if (event != null) {
            Utils.removeStickyEvent(event);
            hideProgressDialog();
            finish();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        registerEventBus();
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
        if (requestCode == JsonFormUtils.REQUEST_CODE_GET_JSON && resultCode == RESULT_OK) {
            mProfilePresenter.processFormDetailsSave(data, allSharedPreferences);

        }
    }

    public void showProgressDialog(int saveMessageStringIdentifier) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
            progressDialog.setTitle(getString(saveMessageStringIdentifier));
            progressDialog.setMessage(getString(R.string.please_wait_message));
        }
        if (!isFinishing())
            progressDialog.show();
    }

    public void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    protected void registerEventBus() {

        EventBus.getDefault().register(this);
    }

    protected abstract int getViewLayoutId();
}
