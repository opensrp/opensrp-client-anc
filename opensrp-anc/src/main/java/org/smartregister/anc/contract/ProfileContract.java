package org.smartregister.anc.contract;

/**
 * Created by ndegwamartin on 13/07/2018.
 */
public interface ProfileContract {

    interface Presenter {

        ProfileContract.View getProfileView();

        void onDestroy(boolean isChangingConfiguration);

        void refreshProfileView(String baseEntityId);

    }

    interface View {

        void setProfileName(String fullName);

        void setProfileID(String ancId);

        void setProfileAge(String age);

        void setProfileGestationAge(String gestationAge);

        void setProfileImage(String baseEntityId);
    }

    interface Interactor {

        void onDestroy(boolean isChangingConfiguration);

        void refreshProfileView(String baseEntityId);

    }
}
