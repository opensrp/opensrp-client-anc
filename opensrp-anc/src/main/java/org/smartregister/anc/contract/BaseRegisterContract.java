package org.smartregister.anc.contract;

import android.content.Context;

import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONObject;
import org.smartregister.anc.view.LocationPickerView;

import java.util.List;

/**
 * Created by keyamn on 27/06/2018.
 */
public class BaseRegisterContract {
    public interface Presenter {

        void registerViewConfigurations(List<String> viewIdentifiers);

        void unregisterViewConfiguration(List<String> viewIdentifiers);

        void availableLanguages();

        void saveLanguage(String language);

        void logOutUser();

        void triggerSync();

        BaseRegisterContract.View getView();

        void startForm(String formName, String entityId, String metatata, LocationPickerView locationPickerView) throws Exception;

        void startForm(String formName, String entityId, String metadata, String currentLocationId) throws Exception;

        void onDestroy(boolean isChangingConfiguration);
    }

    public interface View {

        Context getContext();

        void displaySyncNotification();

        void displayToast(int resourceId);

        void displayToast(String message);

        void displayShortToast(int resourceId);

        void showLanguageDialog(List<String> displayValues);

        void startFormActivity(JSONObject form);

    }

    public interface Interactor {
        void getNextUniqueId(Triple<String, String, String> triple, BaseRegisterContract.InteractorCallBack callBack);
    }

    public interface InteractorCallBack {
        void onUniqueIdFetched(Triple<String, String, String> triple, String entityId);

        void onNoUniqueId();
    }
}
