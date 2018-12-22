package org.smartregister.anc.contract;

import android.content.Intent;

import org.smartregister.repository.AllSharedPreferences;

import java.util.Map;

/**
 * Created by ndegwamartin on 13/07/2018.
 */
public interface ProfileContract {

    interface Presenter {

        ProfileContract.View getProfileView();

        void onDestroy(boolean isChangingConfiguration);

        void refreshProfileView(String baseEntityId);

        void processFormDetailsSave(Intent data, AllSharedPreferences allSharedPreferences);

        void refreshProfileTopSection(Map<String, String> client);

        void saveFinishForm(Map<String, String> client);
    }

    interface View {

        void setProfileName(String fullName);

        void setProfileID(String ancId);

        void setProfileAge(String age);

        void setProfileGestationAge(String gestationAge);

        void setProfileImage(String baseEntityId);

        void showProgressDialog(int messageStringIdentifier);

        void hideProgressDialog();

        void displayToast(int resourceId);

        String getIntentString(String intentKey);

        void setWomanPhoneNumber(String phoneNumber);

    }

    interface Interactor {

        void onDestroy(boolean isChangingConfiguration);

        void refreshProfileView(String baseEntityId);

    }
}
