package org.smartregister.anc.library.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.smartregister.anc.library.AncLibrary;
import org.smartregister.anc.library.R;
import org.smartregister.anc.library.adapter.ProfileViewPagerAdapter;
import org.smartregister.anc.library.contract.ProfileContract;
import org.smartregister.anc.library.event.ClientDetailsFetchedEvent;
import org.smartregister.anc.library.event.PatientRemovedEvent;
import org.smartregister.anc.library.fragment.ProfileContactsFragment;
import org.smartregister.anc.library.fragment.ProfileOverviewFragment;
import org.smartregister.anc.library.fragment.ProfileTasksFragment;
import org.smartregister.anc.library.presenter.ProfilePresenter;
import org.smartregister.anc.library.util.ANCJsonFormUtils;
import org.smartregister.anc.library.util.ConstantsUtils;
import org.smartregister.anc.library.util.DBConstantsUtils;
import org.smartregister.anc.library.util.Utils;
import org.smartregister.anc.library.view.CopyToClipboardDialog;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.util.PermissionUtils;
import org.smartregister.util.StringUtil;
import org.smartregister.view.activity.BaseProfileActivity;

import java.io.Serializable;
import java.util.HashMap;

import timber.log.Timber;

//import androidx.fragment.app.Fragment;
//import androidx.viewpager.widget.ViewPager;

/**
 * Created by ndegwamartin on 10/07/2018.
 */
public class ProfileActivity extends BaseProfileActivity implements ProfileContract.View {
    public static final String CLOSE_ANC_RECORD = "Close ANC Record";
    private TextView nameView;
    private TextView ageView;
    private TextView gestationAgeView;
    private TextView ancIdView;
    private ImageView imageView;
    private String phoneNumber;
    private HashMap<String, String> detailMap;
    private String buttonAlertStatus;
    private Button dueButton;
    private TextView taskTabCount;
    private String contactNo;

    @Override
    protected void onCreation() {
        super.onCreation();
    }

    @Override
    protected void onResumption() {
        super.onResumption();
        String baseEntityId = getIntent().getStringExtra(ConstantsUtils.IntentKeyUtils.BASE_ENTITY_ID);
        registerEventBus();
        ((ProfilePresenter) presenter).refreshProfileView(baseEntityId);
        getTasksCount(baseEntityId);
    }

    protected void registerEventBus() {
        EventBus.getDefault().register(this);
    }

    private void getTasksCount(String baseEntityId) {
        if (StringUtils.isNotBlank(baseEntityId) && StringUtils.isNotBlank(contactNo)) {
            ((ProfilePresenter) presenter).getTaskCount(baseEntityId);
        }
    }

    @Override
    protected void initializePresenter() {
        presenter = new ProfilePresenter(this);
    }

    @Override
    protected void setupViews() {
        super.setupViews();
        getButtonAlertStatus();
        ageView = findViewById(R.id.textview_detail_two);
        gestationAgeView = findViewById(R.id.textview_detail_three);
        ancIdView = findViewById(R.id.textview_detail_one);
        nameView = findViewById(R.id.textview_name);
        imageView = findViewById(R.id.imageview_profile);
        dueButton = findViewById(R.id.profile_overview_due_button);
        updateTasksTabTitle();
    }

    private void getButtonAlertStatus() {
        detailMap = (HashMap<String, String>) getIntent().getSerializableExtra(ConstantsUtils.IntentKeyUtils.CLIENT_MAP);
        contactNo = String.valueOf(Utils.getTodayContact(detailMap.get(DBConstantsUtils.KeyUtils.NEXT_CONTACT)));
        buttonAlertStatus = Utils.processContactDoneToday(detailMap.get(DBConstantsUtils.KeyUtils.LAST_CONTACT_RECORD_DATE),
                ConstantsUtils.AlertStatusUtils.ACTIVE.equals(detailMap.get(DBConstantsUtils.KeyUtils.CONTACT_STATUS)) ?
                        ConstantsUtils.AlertStatusUtils.IN_PROGRESS : "");
    }

    protected void updateTasksTabTitle() {
        ConstraintLayout taskTabTitleLayout = (ConstraintLayout) LayoutInflater.from(this).inflate(R.layout.tasks_tab_title, null);
        TextView taskTabTitle = taskTabTitleLayout.findViewById(R.id.tasks_title);
        taskTabTitle.setText(this.getString(R.string.tasks));
        taskTabCount = taskTabTitleLayout.findViewById(R.id.tasks_count);

        getTabLayout().getTabAt(2).setCustomView(taskTabTitleLayout);
    }

    @Override
    protected ViewPager setupViewPager(ViewPager viewPager) {
        ProfileViewPagerAdapter adapter = new ProfileViewPagerAdapter(getSupportFragmentManager());

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
    public void onClick(View view) {
        if (view.getId() == R.id.profile_overview_due_button) {
            String baseEntityId = getIntent().getStringExtra(ConstantsUtils.IntentKeyUtils.BASE_ENTITY_ID);

            if (StringUtils.isNotBlank(baseEntityId)) {
                Utils.proceedToContact(baseEntityId, detailMap, getActivity());
                finish();
            }

        } else {
            super.onClick(view);
        }
    }

    @Override
    protected void fetchProfileData() {
        String baseEntityId = getIntent().getStringExtra(ConstantsUtils.IntentKeyUtils.BASE_ENTITY_ID);
        ((ProfilePresenter) presenter).fetchProfileData(baseEntityId);
    }

    private Activity getActivity() {
        return this;
    }

    @Override
    public void onBackPressed() {
        Utils.navigateToHomeRegister(this, false, AncLibrary.getInstance().getActivityConfiguration().getHomeRegisterActivityClass());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PermissionUtils.PHONE_STATE_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                launchPhoneDialer(phoneNumber);
            } else {
                displayToast(R.string.allow_phone_call_management);
            }
        }
    }

    protected void launchPhoneDialer(String phoneNumber) {
        if (isPermissionGranted()) {
            try {
                Intent intent = getTelephoneIntent(phoneNumber);
                startActivity(intent);
            } catch (Exception e) {
                Timber.i("No dial application so we launch copy to clipboard...");
                CopyToClipboardDialog copyToClipboardDialog = new CopyToClipboardDialog(this, R.style.copy_clipboard_dialog);
                copyToClipboardDialog.setContent(phoneNumber);
                copyToClipboardDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                copyToClipboardDialog.show();
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
    public boolean onOptionsItemSelected(final MenuItem item) {
        int itemId = item.getItemId();
        // When user click home menu item then quit this activity.
        if (itemId == android.R.id.home) {
            Utils.navigateToHomeRegister(this, false, AncLibrary.getInstance().getActivityConfiguration().getHomeRegisterActivityClass());
        } else {
            String contactButtonText = getString(R.string.start_contact);

            if (buttonAlertStatus.equals(ConstantsUtils.AlertStatusUtils.TODAY)) {
                contactButtonText = String.format(getString(R.string.contact_recorded_today_no_break), Utils.getTodayContact(detailMap.get(DBConstantsUtils.KeyUtils.NEXT_CONTACT)));
            } else if (buttonAlertStatus.equals(ConstantsUtils.AlertStatusUtils.IN_PROGRESS)) {
                contactButtonText = String.format(getString(R.string.continue_contact), Integer.valueOf(detailMap.get(DBConstantsUtils.KeyUtils.NEXT_CONTACT)));
            }

            attachAlertDialog(contactButtonText);
        }
        return super.onOptionsItemSelected(item);
    }

    private void attachAlertDialog(String contactButtonText) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        arrayAdapter.add(getString(R.string.call));
        arrayAdapter.add(contactButtonText);
        arrayAdapter.add(getString(R.string.close_anc_record));

        builderSingle.setAdapter(arrayAdapter, (dialog, which) -> {
            switch (which) {
                case 0:
                    launchPhoneDialer(phoneNumber);
                    break;
                case 2:
                    ANCJsonFormUtils.launchANCCloseForm(ProfileActivity.this);
                    break;
                default:
                    continueToContact();
                    break;
            }

            dialog.dismiss();
        });
        builderSingle.show();
    }

    private void continueToContact() {
        if (!buttonAlertStatus.equals(ConstantsUtils.AlertStatusUtils.TODAY)) {
            String baseEntityId = detailMap.get(DBConstantsUtils.KeyUtils.BASE_ENTITY_ID);

            if (StringUtils.isNotBlank(baseEntityId)) {
                Utils.proceedToContact(baseEntityId, detailMap, ProfileActivity.this);
            }
        }
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
        //super.onActivityResult(requestCode, resultCode, data);
        AllSharedPreferences allSharedPreferences = AncLibrary.getInstance().getContext().allSharedPreferences();
        if (requestCode == ANCJsonFormUtils.REQUEST_CODE_GET_JSON && resultCode == Activity.RESULT_OK) {
            ((ProfilePresenter) presenter).processFormDetailsSave(data, allSharedPreferences);
        } else {
            Fragment currentFragment = getSupportFragmentManager().findFragmentByTag(ConstantsUtils.ANDROID_SWITCHER + R.id.viewpager + ":" + viewPager.getCurrentItem()); //This might be dirty we maybe can find a better way to do it.
            if (currentFragment instanceof ProfileTasksFragment) {
                currentFragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void startFormForEdit(ClientDetailsFetchedEvent event) {
        if (event != null && event.isEditMode()) {
            String formMetadata = ANCJsonFormUtils.getAutoPopulatedJsonEditRegisterFormString(this, event.getWomanClient());
            try {
                ANCJsonFormUtils.startFormForEdit(this, ANCJsonFormUtils.REQUEST_CODE_GET_JSON, formMetadata);
            } catch (Exception e) {
                Timber.e(e, " --> startFormForEdit");
            }
        }

    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void refreshProfileTopSection(ClientDetailsFetchedEvent event) {
        if (event != null && !event.isEditMode()) {
            Utils.removeStickyEvent(event);
            ((ProfilePresenter) presenter).refreshProfileTopSection(event.getWomanClient());
           if(!contactNo.equals(String.valueOf(Utils.getTodayContact(event.getWomanClient().get(DBConstantsUtils.KeyUtils.NEXT_CONTACT))))) {
               Intent previousIntent =  getIntent();
               previousIntent.putExtra(ConstantsUtils.IntentKeyUtils.CLIENT_MAP, (Serializable) event.getWomanClient());
               setIntent(previousIntent);
               setupViewPager(viewPager);
               getButtonAlertStatus();
               updateTasksTabTitle();
               getTasksCount(event.getWomanClient().get(ConstantsUtils.IntentKeyUtils.BASE_ENTITY_ID));
           }
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
    public void setTaskCount(String taskCount) {
        getTaskTabCount().setText(taskCount);
    }

    @Override
    public void createContactSummaryPdf() {
        //overridden
    }

    public TextView getTaskTabCount() {
        return taskTabCount;
    }

    public Button getDueButton() {
        return dueButton;
    }

    public ProfilePresenter getProfilePresenter() {
        return (ProfilePresenter) presenter;
    }
}
