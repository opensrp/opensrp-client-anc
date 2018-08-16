package org.smartregister.anc.contract;

import android.content.Context;

import org.smartregister.anc.domain.QuickCheckConfiguration;
import org.smartregister.configurableviews.model.Field;

import java.util.List;
import java.util.Set;


public interface QuickCheckContract {
    public interface Presenter {

        void setReason(Field reason);

        QuickCheckConfiguration getConfig();

        Set<Field> currentComplaintsOrDangerSigns(boolean isDangerSign);

        boolean containsComplaintOrDangerSign(Field field, boolean isDangerSign);

        void addToComplaintsOrDangerList(Field field, boolean isChecked, boolean isDangerSign);

        void proceedToNormalContact();

        void referAndCloseContact();

    }

    public interface View {

        Context getContext();

        String getString(int resId);

        void displayComplaintLayout();

        void hideComplaintLayout();

        void notifyComplaintAdapter();

        void displayDangerSignLayout();

        void notifyDangerSignAdapter();

        void showSpecifyEditText();

        void hideSpecifyEditText();

        void displayNavigationLayout();

        void displayReferButton();

        void hideReferButton();

        void displayToast(int resourceId);

    }

}
