package org.smartregister.anc.library.contract;

import android.content.Context;
import android.content.Intent;

import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.view.contract.BaseProfileContract;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ndegwamartin on 13/07/2018.
 */
public interface ProfileContract {

    interface Presenter extends BaseProfileContract.Presenter {
        ProfileContract.View getProfileView();

        void fetchProfileData(String baseEntityId);

        void refreshProfileView(String baseEntityId);

        void processFormDetailsSave(Intent data, AllSharedPreferences allSharedPreferences);

        void refreshProfileTopSection(Map<String, String> client);

        HashMap<String, String> saveFinishForm(Map<String, String> client, Context context);

        void getTaskCount(String baseEntityId);

        void createContactSummaryPdf();
    }

    interface View extends BaseProfileContract.View {

        void setProfileName(String fullName);

        void setProfileID(String ancId);

        void setProfileAge(String age);

        void setProfileGestationAge(String gestationAge);

        void setProfileImage(String baseEntityId);

        void setPhoneNumber(String phoneNumber);

        void setTaskCount(String taskCount);

        void createContactSummaryPdf();

    }

    interface Interactor {

        void onDestroy(boolean isChangingConfiguration);

        void refreshProfileView(String baseEntityId, boolean isForEdit);

        String getTaskCount(String baseEntityId);
    }
}
