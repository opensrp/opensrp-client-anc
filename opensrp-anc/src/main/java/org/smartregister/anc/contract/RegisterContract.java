package org.smartregister.anc.contract;

import android.content.Context;
import android.util.Pair;

import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONObject;
import org.smartregister.anc.domain.AttentionFlag;
import org.smartregister.anc.view.LocationPickerView;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.FetchStatus;

import java.util.List;
import java.util.Map;

/**
 * Created by keyamn on 27/06/2018.
 */
public interface RegisterContract {
    interface Presenter {

        void registerViewConfigurations(List<String> viewIdentifiers);

        void unregisterViewConfiguration(List<String> viewIdentifiers);

        void saveLanguage(String language);

        void startForm(String formName, String entityId, String metatata, LocationPickerView locationPickerView) throws Exception;

        void startForm(String formName, String entityId, String metadata, String currentLocationId) throws Exception;

        void saveForm(String jsonString, boolean isEditMode);

        void closeAncRecord(String jsonString);

        void onDestroy(boolean isChangingConfiguration);

        void saveSiteCharacteristics(String jsonString);
    }

    interface View {

        Context getContext();

        void displaySyncNotification();

        void displayToast(int resourceId);

        void displayToast(String message);

        void displayShortToast(int resourceId);

        void showLanguageDialog(List<String> displayValues);

        void startFormActivity(JSONObject form);

        void refreshList(final FetchStatus fetchStatus);

        void showProgressDialog(int messageStringIdentifier);

        void hideProgressDialog();

        void showAttentionFlagsDialog(List<AttentionFlag> attentionFlags);
    }

    interface Model {
        void registerViewConfigurations(List<String> viewIdentifiers);

        void unregisterViewConfiguration(List<String> viewIdentifiers);

        void saveLanguage(String language);

        String getLocationId(String locationName);

        Pair<Client, Event> processRegistration(String jsonString);

        JSONObject getFormAsJson(String formName, String entityId,
                                 String currentLocationId) throws Exception;

        Map<String, String> processSiteCharacteristics(String jsonString);
    }

    interface Interactor {
        void onDestroy(boolean isChangingConfiguration);

        void getNextUniqueId(Triple<String, String, String> triple, RegisterContract.InteractorCallBack callBack);

        void saveRegistration(final Pair<Client, Event> pair, final String jsonString, final boolean isEditMode, final RegisterContract.InteractorCallBack callBack);

        void removeWomanFromANCRegister(String closeFormJsonString, String providerId);

        void saveSiteCharacteristics(Map<String, String> siteCharacteristicsSettingsMap);

    }

    public interface InteractorCallBack {
        void onUniqueIdFetched(Triple<String, String, String> triple, String entityId);

        void onNoUniqueId();

        void onRegistrationSaved(boolean isEdit);
    }
}
