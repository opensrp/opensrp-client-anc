package org.smartregister.anc.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;
import org.smartregister.anc.R;
import org.smartregister.anc.adapter.PagerAdapter;
import org.smartregister.anc.application.AncApplication;
import org.smartregister.anc.barcode.Barcode;
import org.smartregister.anc.barcode.BarcodeIntentIntegrator;
import org.smartregister.anc.barcode.BarcodeIntentResult;
import org.smartregister.anc.event.ShowProgressDialogEvent;
import org.smartregister.anc.event.TriggerSyncEvent;
import org.smartregister.anc.fragment.BaseRegisterFragment;
import org.smartregister.anc.fragment.HomeRegisterFragment;
import org.smartregister.anc.util.Constants;
import org.smartregister.anc.util.Utils;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.configurableviews.ConfigurableViewsLibrary;
import org.smartregister.domain.FetchStatus;
import org.smartregister.provider.SmartRegisterClientsProvider;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.DetailsRepository;
import org.smartregister.view.activity.SecuredNativeSmartRegisterActivity;
import org.smartregister.view.viewpager.OpenSRPViewPager;

import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by keyman on 26/06/2018.
 */

public abstract class BaseRegisterActivity extends SecuredNativeSmartRegisterActivity {

    public static final String TAG = BaseRegisterActivity.class.getCanonicalName();

    public static String TOOLBAR_TITLE = BaseRegisterActivity.class.getPackage() + ".toolbarTitle";

    private ProgressDialog progressDialog;

    private final int MINIUM_LANG_COUNT = 2;


    @Bind(R.id.view_pager)
    protected OpenSRPViewPager mPager;
    private FragmentPagerAdapter mPagerAdapter;

    private static final int REQUEST_CODE_GET_JSON = 3432;
    public static final String EXTRA_CLIENT_DETAILS = "client_details";
    private CommonPersonObjectClient clientDetails;

    private Fragment mBaseFragment = null;
    ////////////////////////////////////////////////
    public DetailsRepository detailsRepository;
    private Map<String, String> details;
    private static final int REQUEST_TAKE_PHOTO = 1;
    private File currentfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_register);
        ButterKnife.bind(this);

        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            Serializable serializable = extras.getSerializable(EXTRA_CLIENT_DETAILS);
            if (serializable != null && serializable instanceof CommonPersonObjectClient) {
                clientDetails = (CommonPersonObjectClient) serializable;
            }
        }

        /*detailsRepository = detailsRepository == null ? AncApplication.getInstance().getContext().detailsRepository() : detailsRepository;
        if (clientDetails != null) {
            details = detailsRepository.getAllDetailsForClient(clientDetails.entityId());
            Utils.putAll(details, clientDetails.getColumnmaps());
        }*/


        Fragment[] otherFragments = {};

        mBaseFragment = getRegisterFragment();
        mBaseFragment.setArguments(this.getIntent().getExtras());

        // Instantiate a ViewPager and a PagerAdapter.
        mPagerAdapter = new PagerAdapter(getSupportFragmentManager(), mBaseFragment, otherFragments);
        mPager.setOffscreenPageLimit(otherFragments.length);
        mPager.setAdapter(mPagerAdapter);

    }

    protected abstract Fragment getRegisterFragment();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_register, menu);
        if (AncApplication.getJsonSpecHelper().getAvailableLanguages().size() < MINIUM_LANG_COUNT) {
            invalidateOptionsMenu();
            MenuItem item = menu.findItem(R.id.action_language);
            item.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_language) {
            this.showLanguageDialog();
            return true;
        } else if (id == R.id.action_logout) {
            logOutUser();
            return true;

        } else if (id == R.id.action_sync) {

            TriggerSyncEvent syncEvent = new TriggerSyncEvent();
            syncEvent.setManualSync(true);
            AncApplication.getInstance().triggerSync(syncEvent);

            Snackbar syncStatusSnackbar = Snackbar.make(this.getWindow().getDecorView(), R.string.manual_sync_triggered, Snackbar.LENGTH_LONG);
            syncStatusSnackbar.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void logOutUser() {
        AncApplication.getInstance().logoutCurrentUser();
    }

    public void showLanguageDialog() {


        final List<String> displayValues = ConfigurableViewsLibrary.getJsonSpecHelper().getAvailableLanguages();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, displayValues.toArray(new String[displayValues.size()])) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView view = (TextView) super.getView(position, convertView, parent);
                view.setTextColor(ConfigurableViewsLibrary.getInstance().getContext().getColorResource(R.color.customAppThemeBlue));

                return view;
            }
        };
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(this.getString(R.string.select_language));
        builder.setSingleChoiceItems(adapter, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selectedItem = displayValues.get(which);
                Map<String, String> langs = AncApplication.getJsonSpecHelper().getAvailableLanguagesMap();
                Utils.saveLanguage(Utils.getKeyByValue(langs, selectedItem));
                // Utils.postEvent(new LanguageConfigurationEvent(false));
                Utils.showToast(getApplicationContext(), selectedItem + " selected");
                dialog.dismiss();
            }
        });

        final AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected DefaultOptionsProvider getDefaultOptionsProvider() {
        return null;
    }

    @Override
    protected NavBarOptionsProvider getNavBarOptionsProvider() {
        return null;
    }

    @Override
    protected SmartRegisterClientsProvider clientsProvider() {
        return null;
    }

    @Override
    protected void setupViews() {//Implement Abstract Method
    }

    @Override
    protected void onResumption() {
        ConfigurableViewsLibrary.getInstance().getConfigurableViewsHelper().registerViewConfigurations(getViewIdentifiers());
    }

    @Override
    protected void onInitialization() {//Implement Abstract Method
    }

    @Override
    public void startRegistration() {//Implement Abstract Method
    }

    public void refreshList(final FetchStatus fetchStatus) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            BaseRegisterFragment registerFragment = (BaseRegisterFragment) findFragmentByPosition(0);
            if (registerFragment != null && fetchStatus.equals(FetchStatus.fetched)) {
                registerFragment.refreshListView();
            }
        } else {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    BaseRegisterFragment registerFragment = (BaseRegisterFragment) findFragmentByPosition(0);
                    if (registerFragment != null && fetchStatus.equals(FetchStatus.fetched)) {
                        registerFragment.refreshListView();
                    }
                }
            });
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showProgressDialog(ShowProgressDialogEvent showProgressDialogEvent) {
        if (showProgressDialogEvent != null) {
            showProgressDialog();
        }
    }

    public void showProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle(getString(R.string.saving_dialog_title));
        progressDialog.setMessage(getString(R.string.please_wait_message));
        if (!isFinishing())
            progressDialog.show();
    }

    public void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }


    public Fragment findFragmentByPosition(int position) {
        FragmentPagerAdapter fragmentPagerAdapter = mPagerAdapter;
        return getSupportFragmentManager().findFragmentByTag("android:switcher:" + mPager.getId() + ":" + fragmentPagerAdapter.getItemId(position));
    }


    @Override
    protected void onStop() {
        super.onStop();
        ConfigurableViewsLibrary.getInstance().getConfigurableViewsHelper().unregisterViewConfiguration(getViewIdentifiers());
    }

    public abstract List<String> getViewIdentifiers();


    public void startQrCodeScanner() {
        BarcodeIntentIntegrator barcodeIntentIntegrator = new BarcodeIntentIntegrator(this);
        barcodeIntentIntegrator.addExtra(Barcode.SCAN_MODE, Barcode.QR_MODE);
        barcodeIntentIntegrator.initiateScan();
    }

    @Override
    public void startFormActivity(String formName, String entityId, String metaData) {
        try {
            if (mBaseFragment instanceof HomeRegisterFragment) {
                //LocationPickerView locationPickerView = ((HomeRegisterFragment) mBaseFragment).getLocationPickerView();
                //String locationId = LocationHelper.getInstance().getOpenMrsLocationId(locationPickerView.getSelectedItem());
                //JsonFormUtils.startForm(this, context(), REQUEST_CODE_GET_JSON, formName, entityId, metaData, locationId);
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_GET_JSON && resultCode == RESULT_OK) {
            try {
                String jsonString = data.getStringExtra("json");
                Log.d("JSONResult", jsonString);

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                AllSharedPreferences allSharedPreferences = new AllSharedPreferences(preferences);

                JSONObject form = new JSONObject(jsonString);
                /*if (form.getString(JsonFormUtils.ENCOUNTER_TYPE).equals(Constants.EventType.REGISTRATION)) {

                    JsonFormUtils.saveForm(this, AncApplication.getInstance().getContext(), jsonString, allSharedPreferences.fetchRegisteredANM());
                }*/
            } catch (Exception e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }

        } else if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            String imageLocation = currentfile.getAbsolutePath();
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            AllSharedPreferences allSharedPreferences = new AllSharedPreferences(preferences);

            //JsonFormUtils.saveImage(this, allSharedPreferences.fetchRegisteredANM(), clientDetails.entityId(), imageLocation);

        } else if (requestCode == BarcodeIntentIntegrator.REQUEST_CODE && resultCode == RESULT_OK) {
            BarcodeIntentResult res = BarcodeIntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (StringUtils.isNotBlank(res.getContents())) {
                Log.d("Scanned QR Code", res.getContents());
                ((HomeRegisterFragment) mBaseFragment).onQRCodeSucessfullyScanned(res.getContents());
                ((HomeRegisterFragment) mBaseFragment).setSearchTerm(res.getContents());
            } else Log.i("", "NO RESULT FOR QR CODE");
        }
    }


}
