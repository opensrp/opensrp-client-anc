package org.smartregister.anc.activity;

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
import org.smartregister.anc.event.ClientDetailsFetchedEvent;
import org.smartregister.anc.task.FetchProfileDataTask;
import org.smartregister.anc.util.Constants;
import org.smartregister.anc.util.JsonFormUtils;
import org.smartregister.view.activity.SecuredActivity;

/**
 * Created by ndegwamartin on 16/07/2018.
 */
public abstract class BaseProfileActivity extends SecuredActivity implements AppBarLayout.OnOffsetChangedListener, View.OnClickListener {
    protected AppBarLayout appBarLayout;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private boolean appBarTitleIsShown = true;
    private int appBarLayoutScrollRange = -1;
    protected String womanName;

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

}
