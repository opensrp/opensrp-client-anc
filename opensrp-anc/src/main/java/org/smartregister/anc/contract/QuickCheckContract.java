package org.smartregister.anc.contract;

import android.content.Context;

import org.smartregister.anc.domain.QuickCheckConfiguration;
import org.smartregister.configurableviews.model.Field;


public interface QuickCheckContract {
    public interface Presenter {
        void setReason(Field reason);

        QuickCheckConfiguration getConfig();
    }

    public interface View {
        Context getContext();
    }
}
