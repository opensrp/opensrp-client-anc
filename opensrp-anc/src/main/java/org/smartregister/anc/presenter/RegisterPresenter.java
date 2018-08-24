package org.smartregister.anc.presenter;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Pair;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONObject;
import org.smartregister.anc.R;
import org.smartregister.anc.contract.RegisterContract;
import org.smartregister.anc.interactor.RegisterInteractor;
import org.smartregister.anc.model.RegisterModel;
import org.smartregister.anc.util.Constants;
import org.smartregister.anc.view.LocationPickerView;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.FetchStatus;
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
    private RegisterContract.Model model;

    public RegisterPresenter(RegisterContract.View view) {
        viewReference = new WeakReference<>(view);
        interactor = new RegisterInteractor();
        model = new RegisterModel();
    }

    public void setModel(RegisterContract.Model model) {
        this.model = model;
    }

    public void setInteractor(RegisterContract.Interactor interactor) {
        this.interactor = interactor;
    }

    @Override
    public void registerViewConfigurations(List<String> viewIdentifiers) {
        model.registerViewConfigurations(viewIdentifiers);
    }

    @Override
    public void unregisterViewConfiguration(List<String> viewIdentifiers) {
        model.unregisterViewConfiguration(viewIdentifiers);
    }

    @Override
    public void saveLanguage(String language) {
        model.saveLanguage(language);
        getView().displayToast(language + " selected");
    }

    @Override
    public void startForm(String formName, String entityId, String metadata, LocationPickerView locationPickerView) throws Exception {
        if (locationPickerView == null || StringUtils.isBlank(locationPickerView.getSelectedItem())) {
            getView().displayToast(R.string.no_location_picker);
        } else {
            String currentLocationId = model.getLocationId(locationPickerView.getSelectedItem());
            startForm(formName, entityId, metadata, currentLocationId);
        }
    }

    @Override
    public void startForm(String formName, String entityId, String metadata, String currentLocationId) throws Exception {

        if (StringUtils.isBlank(entityId)) {
            Triple<String, String, String> triple = Triple.of(formName, metadata, currentLocationId);
            interactor.getNextUniqueId(triple, this);
            return;
        }

        JSONObject form = model.getFormAsJson(formName, entityId, currentLocationId);
        getView().startFormActivity(form);

    }

    @Override
    public void closeAncRecord(String jsonString) {

        try {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getView().getContext());
            AllSharedPreferences allSharedPreferences = new AllSharedPreferences(preferences);

            Log.d("JSONResult", jsonString);
            getView().showProgressDialog(jsonString.contains(Constants.EventType.CLOSE) ? R.string.removing_dialog_title : R.string.saving_dialog_title);

            interactor.removeWomanFromANCRegister(jsonString, allSharedPreferences.fetchRegisteredANM());

        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));

        }
    }

    @Override
    public void saveForm(String jsonString, boolean isEditMode) {

        try {

            getView().showProgressDialog(R.string.saving_dialog_title);

            Pair<Client, Event> pair = model.processRegistration(jsonString);
            if (pair == null) {
                return;
            }

            interactor.saveRegistration(pair, jsonString, isEditMode, this);

        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
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

    @Override
    public void onRegistrationSaved(boolean isEdit) {
        getView().refreshList(FetchStatus.fetched);
        getView().hideProgressDialog();
    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {

        viewReference = null;//set to null on destroy
        // Inform interactor
        interactor.onDestroy(isChangingConfiguration);
        // Activity destroyed set interactor to null
        if (!isChangingConfiguration) {
            interactor = null;
            model = null;
        }
    }

    @Override
    public void saveSiteCharacteristics(String jsonString) {
        Map<String, String> settings = model.processSiteCharacteristics(jsonString);
        interactor.saveSiteCharacteristics(settings);
    }

    private RegisterContract.View getView() {
        if (viewReference != null)
            return viewReference.get();
        else
            return null;
    }

}
