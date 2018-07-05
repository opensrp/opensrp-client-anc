package org.smartregister.anc.presenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Pair;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONObject;
import org.smartregister.anc.R;
import org.smartregister.anc.application.AncApplication;
import org.smartregister.anc.contract.RegisterContract;
import org.smartregister.anc.helper.LocationHelper;
import org.smartregister.anc.interactor.RegisterInteractor;
import org.smartregister.anc.util.JsonFormUtils;
import org.smartregister.anc.util.Utils;
import org.smartregister.anc.view.LocationPickerView;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.configurableviews.ConfigurableViewsLibrary;
import org.smartregister.repository.AllSharedPreferences;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;

/**
 * Created by keyamn on 27/06/2018.
 */
public class RegisterPresenter implements RegisterContract.Presenter, RegisterContract.InteractorCallBack {

    public static final String TAG = RegisterPresenter.class.getName();

    private WeakReference<RegisterContract.View> viewReference;
    private RegisterContract.Interactor interactor;

    public RegisterPresenter(RegisterContract.View view) {
        viewReference = new WeakReference<>(view);
        interactor = new RegisterInteractor();
    }

    @Override
    public void registerViewConfigurations(List<String> viewIdentifiers) {
        ConfigurableViewsLibrary.getInstance().getConfigurableViewsHelper().registerViewConfigurations(viewIdentifiers);
    }

    @Override
    public void unregisterViewConfiguration(List<String> viewIdentifiers) {
        ConfigurableViewsLibrary.getInstance().getConfigurableViewsHelper().unregisterViewConfiguration(viewIdentifiers);
    }

    @Override
    public void saveLanguage(String language) {
        Map<String, String> langs = AncApplication.getJsonSpecHelper().getAvailableLanguagesMap();
        Utils.saveLanguage(Utils.getKeyByValue(langs, language));

        getView().displayToast(language + " selected");
    }

    @Override
    public void startForm(String formName, String entityId, String metadata, LocationPickerView locationPickerView) throws Exception {
        String currentLocationId = getlocationId(locationPickerView);
        startForm(formName, entityId, metadata, currentLocationId);
    }

    @Override
    public void startForm(String formName, String entityId, String metadata, String currentLocationId) throws Exception {

        if (StringUtils.isBlank(entityId)) {
            Triple<String, String, String> triple = Triple.of(formName, metadata, currentLocationId);
            interactor.getNextUniqueId(triple, this);
            return;
        }

        JSONObject form = JsonFormUtils.getFormAsJson(getView().getContext(), formName, entityId, currentLocationId);
        getView().startFormActivity(form);

    }

    @Override
    public void saveForm(String jsonString) {

        try {

            Context context = getView().getContext();
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

            AllSharedPreferences allSharedPreferences = new AllSharedPreferences(preferences);
            String providerId = allSharedPreferences.fetchRegisteredANM();

            Pair<Client, Event> pair = JsonFormUtils.processRegistration(jsonString, providerId);
            if (pair == null) {
                return;
            }

            Client baseClient = pair.first;
            Event baseEvent = pair.second;

            /*ECSyncHelper ecUpdater = ECSyncHelper.getInstance(context);

            JsonFormUtils.tagSyncMetadata(baseEvent);

            if (baseClient != null) {
                JSONObject clientJson = new JSONObject(gson.toJson(baseClient));


                if (isEditMode) {
                    mergeAndSaveClient(context, baseClient);
                } else {

                    ecUpdater.addClient(baseClient.getBaseEntityId(), clientJson);
                }
            }

            if (baseEvent != null) {
                JSONObject eventJson = new JSONObject(gson.toJson(baseEvent));
                ecUpdater.addEvent(baseEvent.getBaseEntityId(), eventJson);
            }


            long lastSyncTimeStamp = allSharedPreferences.fetchLastUpdatedAtDate(0);
            Date lastSyncDate = new Date(lastSyncTimeStamp);

            if (isEditMode) {

                // Unassign current OPENSRP ID
                if (baseClient != null) {
                    String newOpenSRPId = baseClient.getIdentifier(DBConstants.KEY.OPENSRP_ID).replace("-", "");
                    String currentOpenSRPId = getString(jsonForm, CURRENT_OPENSRP_ID).replace("-", "");
                    if (!newOpenSRPId.equals(currentOpenSRPId)) {
                        //OPENSRP ID was changed
                        AncApplication.getInstance().getUniqueIdRepository().open(currentOpenSRPId);
                    }
                }

            } else {

                String opensrpId = baseClient.getIdentifier(DBConstants.KEY.OPENSRP_ID);
                //mark OPENSRP ID as used
                AncApplication.getInstance().getUniqueIdRepository().close(opensrpId);
            }


            String imageLocation = getFieldValue(fields, imageKey);
            saveImage(context, providerId, entityId, imageLocation);

            ClientProcessorForJava.getInstance(context).processClient(ecUpdater.getEvents(lastSyncDate, BaseRepository.TYPE_Unsynced));
            allSharedPreferences.saveLastUpdatedAtDate(lastSyncDate.getTime());
            */
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }


    private String getlocationId(LocationPickerView locationPickerView) {
        return LocationHelper.getInstance().getOpenMrsLocationId(locationPickerView.getSelectedItem());
    }

    @Override
    public void onNoUniqueId() {
        getView().displayShortToast(R.string.no_openmrs_id);
    }

    @Override
    public void onUniqueIdFetched(Triple<String, String, String> triple, String entityId) {
        try {
            startForm(triple.getLeft(), entityId, triple.getMiddle(), triple.getRight());
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
            getView().displayToast(R.string.error_unable_to_start_form);
        }
    }

    private RegisterContract.View getView() {
        if (viewReference != null)
            return viewReference.get();
        else
            return null;
    }

    public void onDestroy(boolean isChangingConfiguration) {
        viewReference = null;//set to null on destroy
    }

}
