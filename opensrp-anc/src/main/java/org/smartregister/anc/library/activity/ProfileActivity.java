package org.smartregister.anc.library.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.smartregister.anc.library.AncLibrary;
import org.smartregister.anc.library.R;
import org.smartregister.anc.library.adapter.ViewPagerAdapter;
import org.smartregister.anc.library.contract.ProfileContract;
import org.smartregister.anc.library.event.ClientDetailsFetchedEvent;
import org.smartregister.anc.library.event.PatientRemovedEvent;
import org.smartregister.anc.library.fragment.ProfileContactsFragment;
import org.smartregister.anc.library.fragment.ProfileOverviewFragment;
import org.smartregister.anc.library.fragment.ProfileTasksFragment;
import org.smartregister.anc.library.presenter.ProfilePresenter;
import org.smartregister.anc.library.util.ConstantsUtils;
import org.smartregister.anc.library.util.DBConstantsUtils;
import org.smartregister.anc.library.util.JsonFormUtils;
import org.smartregister.anc.library.util.Utils;
import org.smartregister.anc.library.view.CopyToClipboardDialog;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.util.PermissionUtils;
import org.smartregister.view.activity.BaseProfileActivity;

import java.util.HashMap;

/**
 * Created by ndegwamartin on 10/07/2018.
 */
public class ProfileActivity extends BaseProfileActivity implements ProfileContract.View {

    public static final String CLOSE_ANC_RECORD = "Close ANC Record";
    private static final String TAG = ProfileActivity.class.getCanonicalName();
    private TextView nameView;
    private TextView ageView;
    private TextView gestationAgeView;
    private TextView ancIdView;
    private ImageView imageView;
    private String phoneNumber;
    private HashMap<String, String> detailMap;
    private String buttonAlertStatus;

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        int itemId = item.getItemId();
        // When user click home menu item then quit this activity.
        if (itemId == android.R.id.home) {
            Utils.navigateToHomeRegister(this, false, AncLibrary.getInstance().getActivityConfiguration().getHomeRegisterActivityClass());
        } else {

            String contactButtonText = getString(R.string.start_contact);

            if (buttonAlertStatus.equals(ConstantsUtils.AlertStatusUtils.TODAY)) {

                contactButtonText = String.format(getString(R.string.contact_recorded_today_no_break),
                        Utils.getTodayContact(detailMap.get(DBConstantsUtils.KeyUtils.NEXT_CONTACT)));

            } else if (buttonAlertStatus.equals(ConstantsUtils.AlertStatusUtils.IN_PROGRESS)) {

                contactButtonText = String.format(getString(R.string.continue_contact),
                        Integer.valueOf(detailMap.get(DBConstantsUtils.KeyUtils.NEXT_CONTACT)));
            }


            AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);

            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
            arrayAdapter.add(getString(R.string.call));
            arrayAdapter.add(contactButtonText);
            arrayAdapter.add(getString(R.string.close_anc_record));

            builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String textClicked = arrayAdapter.getItem(which);
                    if (textClicked != null) {
                        switch (textClicked) {
                            case ConstantsUtils.CALL:
                                launchPhoneDialer(phoneNumber);
                                break;
                            case ConstantsUtils.START_CONTACT:
                            case ConstantsUtils.CONTINUE_CONTACT:
                                continueToContact();
                                break;
                            case CLOSE_ANC_RECORD:
                                JsonFormUtils.launchANCCloseForm(ProfileActivity.this);
                                break;
                            default:
                                if (textClicked.startsWith("Continue")) {
                                    continueToContact();
                                }

                                break;
                        }
                    }

                    dialog.dismiss();
                }

            });
            builderSingle.show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_profile_activity, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy(isChangingConfigurations());

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        AllSharedPreferences allSharedPreferences = AncLibrary.getInstance().getContext().allSharedPreferences();
        if (requestCode == JsonFormUtils.REQUEST_CODE_GET_JSON && resultCode == Activity.RESULT_OK) {
            ((ProfilePresenter) presenter).processFormDetailsSave(data, allSharedPreferences);

        }
    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    protected void launchPhoneDialer(String phoneNumber) {
        if (isPermissionGranted()) {
            try {
                Intent intent = getTelephoneIntent(phoneNumber);
                startActivity(intent);
            } catch (Exception e) {

                Log.i(TAG, "No dial application so we launch copy to clipboard...");
                CopyToClipboardDialog copyToClipboardDialog = new CopyToClipboardDialog(this, R.style.copy_clipboard_dialog);
                copyToClipboardDialog.setContent(phoneNumber);
                copyToClipboardDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                copyToClipboardDialog.show();
            }
        }
    }

    private void continueToContact() {
        if (!buttonAlertStatus.equals(ConstantsUtils.AlertStatusUtils.TODAY)) {

            String baseEntityId = detailMap.get(DBConstantsUtils.KeyUtils.BASE_ENTITY_ID);

            if (StringUtils.isNotBlank(baseEntityId)) {
                Utils.proceedToContact(baseEntityId, detailMap, ProfileActivity.this);
            }
        }
    }

    protected boolean isPermissionGranted() {
        return PermissionUtils.isPermissionGranted(this, Manifest.permission.READ_PHONE_STATE,
                PermissionUtils.PHONE_STATE_PERMISSION_REQUEST_CODE);
    }

    @NonNull
    protected Intent getTelephoneIntent(String phoneNumber) {
        return new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null));
    }

    @Override
    protected void onResumption() {
        super.onResumption();
        String baseEntityId = getIntent().getStringExtra(ConstantsUtils.IntentKeyUtils.BASE_ENTITY_ID);
        ((ProfilePresenter) presenter).refreshProfileView(baseEntityId);
        registerEventBus();
    }

    protected void registerEventBus() {
        EventBus.getDefault().register(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.profile_overview_due_button) {

            String baseEntityId = getIntent().getStringExtra(ConstantsUtils.IntentKeyUtils.BASE_ENTITY_ID);

            if (StringUtils.isNotBlank(baseEntityId)) {
                Utils.proceedToContact(baseEntityId, detailMap, getActivity());
            }

        } else {
            super.onClick(view);
        }

    }

    @Override
    protected void initializePresenter() {
        presenter = new ProfilePresenter(this);
    }

    @Override
    protected void setupViews() {
        super.setupViews();

        ageView = findViewById(R.id.textview_detail_two);
        gestationAgeView = findViewById(R.id.textview_detail_three);
        ancIdView = findViewById(R.id.textview_detail_one);
        nameView = findViewById(R.id.textview_name);
        imageView = findViewById(R.id.imageview_profile);

        getButtonAlertStatus();
    }

    private void getButtonAlertStatus() {

        detailMap = (HashMap<String, String>) getIntent().getSerializableExtra(ConstantsUtils.IntentKeyUtils.CLIENT_MAP);

        buttonAlertStatus = Utils.processContactDoneToday(detailMap.get(DBConstantsUtils.KeyUtils.LAST_CONTACT_RECORD_DATE),
                ConstantsUtils.AlertStatusUtils.ACTIVE.equals(detailMap.get(DBConstantsUtils.KeyUtils.CONTACT_STATUS)) ?
                        ConstantsUtils.AlertStatusUtils.IN_PROGRESS : "");

    }

    @Override
    protected ViewPager setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        ProfileOverviewFragment profileOverviewFragment = ProfileOverviewFragment.newInstance(this.getIntent().getExtras());
        ProfileContactsFragment profileContactsFragment = ProfileContactsFragment.newInstance(this.getIntent().getExtras());
        ProfileTasksFragment profileTasksFragment = ProfileTasksFragment.newInstance(this.getIntent().getExtras());

        adapter.addFragment(profileOverviewFragment, this.getString(R.string.overview));
        adapter.addFragment(profileContactsFragment, this.getString(R.string.contacts));
        adapter.addFragment(profileTasksFragment, this.getString(R.string.tasks));

        viewPager.setAdapter(adapter);

        return viewPager;
    }

    @Override
    protected void fetchProfileData() {
        String baseEntityId = getIntent().getStringExtra(ConstantsUtils.IntentKeyUtils.BASE_ENTITY_ID);
        ((ProfilePresenter) presenter).fetchProfileData(baseEntityId);
    }

    private Activity getActivity() {
        return this;

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void startFormForEdit(ClientDetailsFetchedEvent event) {
        if (event != null && event.isEditMode()) {

            String formMetadata = JsonFormUtils.getAutoPopulatedJsonEditRegisterFormString(this, event.getWomanClient());
            try {

                JsonFormUtils.startFormForEdit(this, JsonFormUtils.REQUEST_CODE_GET_JSON, formMetadata);

            } catch (Exception e) {
                Log.e("TAG", e.getMessage());
            }
        }

    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void refreshProfileTopSection(ClientDetailsFetchedEvent event) {
        if (event != null && !event.isEditMode()) {
            Utils.removeStickyEvent(event);
            ((ProfilePresenter) presenter).refreshProfileTopSection(event.getWomanClient());
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
    public void setProfileName(String fullName) {
        this.patientName = fullName;
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
        imageRenderHelper.refreshProfileImage(baseEntityId, imageView, Utils.getProfileImageResourceIdentifier());
    }

    @Override
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PermissionUtils.PHONE_STATE_PERMISSION_REQUEST_CODE: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    launchPhoneDialer(phoneNumber);
                } else {
                    displayToast(R.string.allow_phone_call_management);
                }
                return;
            }
            default:
                break;
        }
    }
}

