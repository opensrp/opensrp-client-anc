package org.smartregister.anc.contract;

import java.util.Map;

/**
 * Created by ndegwamartin on 27/08/2018.
 */
public interface SiteCharacteristicsContract {

    interface Presenter {

        SiteCharacteristicsContract.View getSiteCharacteristicsView();

        void launchSiteCharacteristicsForm();

        void onDestroy(boolean isChangingConfiguration);

        void saveSiteCharacteristics(String jsonString);
    }

    interface View {

        void launchSiteCharacteristicsSettingsForm();

        void showProgressDialog(int messageStringIdentifier);

        void hideProgressDialog();

        void goToHomeRegisterPage();

    }

    interface Model {

        Map<String, String> processSiteCharacteristics(String jsonString);
    }

    interface Interactor {

        void saveSiteCharacteristics(Map<String, String> siteCharacteristicsSettingsMap);

    }
}
