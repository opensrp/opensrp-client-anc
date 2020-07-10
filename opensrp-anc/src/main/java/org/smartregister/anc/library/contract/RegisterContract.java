package org.smartregister.anc.library.contract;

import androidx.core.util.Pair;

import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONObject;
import org.smartregister.anc.library.domain.AttentionFlag;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.view.LocationPickerView;
import org.smartregister.view.contract.BaseRegisterContract;

import java.util.List;

/**
 * Created by keyamn on 27/06/2018.
 */
public interface RegisterContract {

    interface View extends BaseRegisterContract.View {

        void showLanguageDialog(List<String> displayValues);

        void showAttentionFlagsDialog(List<AttentionFlag> attentionFlags);

    }

    interface Presenter extends BaseRegisterContract.Presenter {

        void saveLanguage(String language);

        void startForm(String formName, String entityId, String metatata, LocationPickerView locationPickerView)
                throws Exception;

        void startForm(String formName, String entityId, String metadata, String currentLocationId) throws Exception;

        void saveRegistrationForm(String jsonString, boolean isEditMode);

        void closeAncRecord(String jsonString);

    }

    interface Model {
        void registerViewConfigurations(List<String> viewIdentifiers);

        void unregisterViewConfiguration(List<String> viewIdentifiers);

        void saveLanguage(String language);

        String getLocationId(String locationName);

        Pair<Client, Event> processRegistration(String jsonString);

        JSONObject getFormAsJson(String formName, String entityId, String currentLocationId) throws Exception;

        String getInitials();
    }

    interface Interactor {
        void onDestroy(boolean isChangingConfiguration);

        void getNextUniqueId(Triple<String, String, String> triple, RegisterContract.InteractorCallBack callBack);

        void saveRegistration(final Pair<Client, Event> pair, final String jsonString, final boolean isEditMode,
                              final RegisterContract.InteractorCallBack callBack);

        void removeWomanFromANCRegister(String closeFormJsonString, String providerId);

    }

    interface InteractorCallBack {
        void onUniqueIdFetched(Triple<String, String, String> triple, String entityId);

        void onNoUniqueId();

        void setBaseEntityRegister(String baseEntityId);

        void onRegistrationSaved(boolean isEdit);
    }
}
