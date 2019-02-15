package org.smartregister.anc.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.bottomnavigation.LabelVisibilityMode;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.vision.barcode.Barcode;
import com.vijay.jsonwizard.activities.JsonFormActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.anc.R;
import org.smartregister.anc.application.AncApplication;
import org.smartregister.anc.contract.RegisterContract;
import org.smartregister.anc.domain.AttentionFlag;
import org.smartregister.anc.domain.Contact;
import org.smartregister.anc.event.PatientRemovedEvent;
import org.smartregister.anc.event.ShowProgressDialogEvent;
import org.smartregister.anc.fragment.AdvancedSearchFragment;
import org.smartregister.anc.fragment.HomeRegisterFragment;
import org.smartregister.anc.fragment.LibraryFragment;
import org.smartregister.anc.fragment.MeFragment;
import org.smartregister.anc.fragment.SortFilterFragment;
import org.smartregister.anc.presenter.RegisterPresenter;
import org.smartregister.anc.repository.PatientRepository;
import org.smartregister.anc.util.Constants;
import org.smartregister.anc.util.ContactJsonFormUtils;
import org.smartregister.anc.util.DBConstants;
import org.smartregister.anc.util.JsonFormUtils;
import org.smartregister.anc.util.Utils;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.configurableviews.ConfigurableViewsLibrary;
import org.smartregister.configurableviews.model.Field;
import org.smartregister.domain.FetchStatus;
import org.smartregister.helper.BottomNavigationHelper;
import org.smartregister.listener.BottomNavigationListener;
import org.smartregister.view.activity.BaseRegisterActivity;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by keyman on 26/06/2018.
 */

public class HomeRegisterActivity extends BaseRegisterActivity implements RegisterContract.View {
    public static final String TAG = HomeRegisterActivity.class.getCanonicalName();
    private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");

    private AlertDialog recordBirthAlertDialog;
    private AlertDialog attentionFlagAlertDialog;
    private AlertDialog languageDialog;
    private View attentionFlagDialogView;
    private boolean isAdvancedSearch = false;
    private String advancedSearchQrText = "";
    private HashMap<String, String> advancedSearchFormData = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        recordBirthAlertDialog = createAlertDialog();
        createAttentionFlagsAlertDialog();
    }

    @Override
    public BaseRegisterFragment getRegisterFragment() {
        return new HomeRegisterFragment();
    }

    @Override
    protected Fragment[] getOtherFragments() {
        ADVANCED_SEARCH_POSITION = 1;
        SORT_FILTER_POSITION = 2;
        ME_POSITION = 3;
        LIBRARY_POSITION = 4;

        Fragment[] fragments = new Fragment[4];
        fragments[ADVANCED_SEARCH_POSITION - 1] = new AdvancedSearchFragment();
        fragments[SORT_FILTER_POSITION - 1] = new SortFilterFragment();
        fragments[ME_POSITION - 1] = new MeFragment();
        fragments[LIBRARY_POSITION - 1] = new LibraryFragment();

        return fragments;
    }

    @Override
    protected void initializePresenter() {
        presenter = new RegisterPresenter(this);
    }

    @Override
    protected void registerBottomNavigation() {
        bottomNavigationHelper = new BottomNavigationHelper();
        bottomNavigationView = findViewById(org.smartregister.R.id.bottom_navigation);
        if (bottomNavigationView != null) {
            bottomNavigationView.getMenu().add(
                    Menu.NONE, org.smartregister.R.string.action_me, Menu.NONE, org.smartregister.R.string.me)
                    .setIcon(bottomNavigationHelper
                            .writeOnDrawable(org.smartregister.R.drawable.bottom_bar_initials_background, userInitials,
                                    getResources()));
            bottomNavigationView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);

            BottomNavigationListener bottomNavigationListener = new BottomNavigationListener(this);
            bottomNavigationView.setOnNavigationItemSelectedListener(bottomNavigationListener);
        }
    }

    @Override
    public List<String> getViewIdentifiers() {
        return Arrays.asList(Constants.CONFIGURATION.HOME_REGISTER);
    }


    public void updateSortAndFilter(List<Field> filterList, Field sortField) {
        ((HomeRegisterFragment) mBaseFragment).updateSortAndFilter(filterList, sortField);
        switchToBaseFragment();
    }

    public void startAdvancedSearch() {
        try {
            mPager.setCurrentItem(ADVANCED_SEARCH_POSITION, false);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }

    }

    @Override
    public void showLanguageDialog(final List<String> displayValues) {

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1,
                displayValues.toArray(new String[displayValues.size()])) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView) super.getView(position, convertView, parent);
                view.setTextColor(
                        ConfigurableViewsLibrary.getInstance().getContext().getColorResource(R.color.customAppThemeBlue));

                return view;
            }
        };

        languageDialog = createLanguageDialog(adapter, displayValues);
        languageDialog.show();
    }

    public AlertDialog createLanguageDialog(ArrayAdapter<String> adapter, final List<String> displayValues) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(this.getString(R.string.select_language));
        builder.setSingleChoiceItems(adapter, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selectedItem = displayValues.get(which);
                ((RegisterContract.Presenter) presenter).saveLanguage(selectedItem);
                dialog.dismiss();
            }
        });

        return builder.create();
    }


    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        switchToAdvancedSearchFromBarcode();
    }

    /**
     * Forces the Home register activity to open the the Advanced search fragment after the barcode activity is closed (as
     * long as it was opened from the advanced search page)
     */
    private void switchToAdvancedSearchFromBarcode() {
        if (isAdvancedSearch) {
            switchToFragment(ADVANCED_SEARCH_POSITION);
            setSelectedBottomBarMenuItem(org.smartregister.R.id.action_search);
            setAdvancedFragmentSearchTerm(advancedSearchQrText);
            setFormData(advancedSearchFormData);
            advancedSearchQrText = "";
            isAdvancedSearch = false;
            advancedSearchFormData = new HashMap<>();
        }
    }


    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    @Subscribe (threadMode = ThreadMode.MAIN)
    public void showProgressDialogHandler(ShowProgressDialogEvent showProgressDialogEvent) {
        if (showProgressDialogEvent != null) {
            showProgressDialog(R.string.saving_dialog_title);
        }
    }

    @Subscribe (sticky = true, threadMode = ThreadMode.MAIN)
    public void removePatientHandler(PatientRemovedEvent event) {
        if (event != null) {
            Utils.removeStickyEvent(event);
            refreshList(FetchStatus.fetched);
            hideProgressDialog();
        }
    }

    @Override
    public void startRegistration() {
        startFormActivity(Constants.JSON_FORM.ANC_REGISTER, null, null);
    }

    @Override
    public void startFormActivity(String formName, String entityId, String metaData) {
        try {
            if (mBaseFragment instanceof HomeRegisterFragment) {
                String locationId = AncApplication.getInstance().getContext().allSharedPreferences()
                        .getPreference(AllConstants.CURRENT_LOCATION_ID);
                ((RegisterPresenter) presenter).startForm(formName, entityId, metaData, locationId);
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
            displayToast(getString(R.string.error_unable_to_start_form));
        }

    }

    @Override
    public void startFormActivity(JSONObject form) {
        Intent intent = new Intent(this, JsonFormActivity.class);
        intent.putExtra(Constants.JSON_FORM_EXTRA.JSON, form.toString());
        startActivityForResult(intent, JsonFormUtils.REQUEST_CODE_GET_JSON);
    }

    @Override
    protected void onActivityResultExtended(int requestCode, int resultCode, Intent data) {
        if (requestCode == JsonFormUtils.REQUEST_CODE_GET_JSON && resultCode == RESULT_OK) {
            try {
                String jsonString = data.getStringExtra(Constants.JSON_FORM_EXTRA.JSON);
                Log.d("JSONResult", jsonString);

                JSONObject form = new JSONObject(jsonString);
                switch (form.getString(JsonFormUtils.ENCOUNTER_TYPE)) {
                    case Constants.EventType.REGISTRATION:
                        ((RegisterContract.Presenter) presenter).saveForm(jsonString, false);
                        break;
                    case Constants.EventType.CLOSE:
                        ((RegisterContract.Presenter) presenter).closeAncRecord(jsonString);
                        break;
                    case Constants.EventType.QUICK_CHECK:
                        Contact contact = new Contact();
                        contact.setContactNumber(getIntent().getIntExtra(Constants.INTENT_KEY.CONTACT_NO, 0));
                        ContactJsonFormUtils
                                .persistPartial(getIntent().getStringExtra(Constants.INTENT_KEY.BASE_ENTITY_ID), contact);
                        PatientRepository
                                .updateContactVisitStartDate(getIntent().getStringExtra(Constants.INTENT_KEY.BASE_ENTITY_ID),
                                        Utils.getDBDateToday());
                        break;
                    default:
                        break;
                }
            } catch (Exception e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AllConstants.BARCODE.BARCODE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                Barcode barcode = data.getParcelableExtra(AllConstants.BARCODE.BARCODE_KEY);
                Log.d("Scanned QR Code", barcode.displayValue);

                Fragment fragment = findFragmentByPosition(currentPage);
                if (fragment instanceof AdvancedSearchFragment) {
                    advancedSearchQrText = barcode.displayValue;
                } else {
                    mBaseFragment.onQRCodeSucessfullyScanned(barcode.displayValue);
                    mBaseFragment.setSearchTerm(barcode.displayValue);
                }

            } else {
                Log.i("", "NO RESULT FOR QR CODE");
            }
        } else {
            onActivityResultExtended(requestCode, resultCode, data);
        }
    }

    private void setAdvancedFragmentSearchTerm(String searchTerm) {
        mBaseFragment.setUniqueID(searchTerm);
    }

    private void setFormData(HashMap<String, String> formData) {
        mBaseFragment.setAdvancedSearchFormData(formData);
    }


    public void showRecordBirthPopUp(CommonPersonObjectClient client) {
        //This is required
        getIntent()
                .putExtra(Constants.INTENT_KEY.BASE_ENTITY_ID, client.getColumnmaps().get(DBConstants.KEY.BASE_ENTITY_ID));

        recordBirthAlertDialog.setMessage(String.format(this.getString(R.string.record_birth_popup_message),
                Utils.getGestationAgeFromEDDate(client.getColumnmaps().get(DBConstants.KEY.EDD)),
                Utils.convertDateFormat(Utils.dobStringToDate(client.getColumnmaps().get(DBConstants.KEY.EDD)),
                        dateFormatter),
                Utils.getDuration(client.getColumnmaps().get(DBConstants.KEY.EDD)),
                client.getColumnmaps().get(DBConstants.KEY.FIRST_NAME)));
        recordBirthAlertDialog.show();
    }


    @NonNull
    protected AlertDialog createAlertDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(getString(R.string.record_birth) + "?");

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.cancel).toUpperCase(),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.record_birth).toUpperCase(),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        JsonFormUtils.launchANCCloseForm(HomeRegisterActivity.this);
                    }
                });
        return alertDialog;
    }

    @Override
    public void showAttentionFlagsDialog(List<AttentionFlag> attentionFlags) {
        ViewGroup redFlagsContainer = attentionFlagDialogView.findViewById(R.id.red_flags_container);
        ViewGroup yellowFlagsContainer = attentionFlagDialogView.findViewById(R.id.yellow_flags_container);

        redFlagsContainer.removeAllViews();
        yellowFlagsContainer.removeAllViews();

        int yellowFlagCount = 0;
        int redFlagCount = 0;

        for (AttentionFlag flag : attentionFlags) {
            if (flag.isRedFlag()) {
                LinearLayout redRow = (LinearLayout) LayoutInflater.from(this)
                        .inflate(R.layout.alert_dialog_attention_flag_row_red, redFlagsContainer, false);
                ((TextView) redRow.getChildAt(1)).setText(flag.getTitle());
                redFlagsContainer.addView(redRow);
                redFlagCount += 1;
            } else {

                LinearLayout yellowRow = (LinearLayout) LayoutInflater.from(this)
                        .inflate(R.layout.alert_dialog_attention_flag_row_yellow, yellowFlagsContainer, false);
                ((TextView) yellowRow.getChildAt(1)).setText(flag.getTitle());
                yellowFlagsContainer.addView(yellowRow);
                yellowFlagCount += 1;
            }
        }

        ((View) redFlagsContainer.getParent()).setVisibility(redFlagCount > 0 ? View.VISIBLE : View.GONE);
        ((View) yellowFlagsContainer.getParent()).setVisibility(yellowFlagCount > 0 ? View.VISIBLE : View.GONE);

        attentionFlagAlertDialog.show();
    }

    @NonNull
    protected AlertDialog createAttentionFlagsAlertDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        attentionFlagDialogView = LayoutInflater.from(this).inflate(R.layout.alert_dialog_attention_flag, null);
        dialogBuilder.setView(attentionFlagDialogView);

        attentionFlagDialogView.findViewById(R.id.closeButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attentionFlagAlertDialog.dismiss();
            }
        });

        attentionFlagAlertDialog = dialogBuilder.create();

        return attentionFlagAlertDialog;
    }

    @Override
    public void updateInitialsText(String initials) {
        this.userInitials = initials;
    }

    public void switchToBaseFragment() {
        switchToFragment(BASE_REG_POSITION);
    }

    public void setSelectedBottomBarMenuItem(int itemId) {
        bottomNavigationView.setSelectedItemId(itemId);
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = findFragmentByPosition(currentPage);
        if (fragment instanceof AdvancedSearchFragment) {
            ((AdvancedSearchFragment) fragment).onBackPressed();
            return;
        } else if (fragment instanceof BaseRegisterFragment) {
            setSelectedBottomBarMenuItem(org.smartregister.R.id.action_clients);
            BaseRegisterFragment registerFragment = (BaseRegisterFragment) fragment;
            if (registerFragment.onBackPressed()) {
                return;
            }
        }
        if (currentPage == 0) {
            super.onBackPressed();
        } else {
            switchToBaseFragment();
            setSelectedBottomBarMenuItem(org.smartregister.R.id.action_clients);
        }
    }

    public void setAdvancedSearch(boolean advancedSearch) {
        isAdvancedSearch = advancedSearch;
    }

    public void setAdvancedSearchFormData(HashMap<String, String> advancedSearchFormData) {
        this.advancedSearchFormData = advancedSearchFormData;
    }
}
