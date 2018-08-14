package org.smartregister.anc.presenter;

import org.smartregister.anc.contract.QuickCheckContract;
import org.smartregister.anc.domain.QuickCheckConfiguration;
import org.smartregister.anc.util.ConfigHelper;
import org.smartregister.configurableviews.model.Field;

import java.lang.ref.WeakReference;

public class QuickCheckPresenter implements QuickCheckContract.Presenter {

    private WeakReference<QuickCheckContract.View> viewReference;
    private QuickCheckConfiguration config;
    private Field reason;

    public QuickCheckPresenter(QuickCheckContract.View view) {
        this.viewReference = new WeakReference<>(view);
        this.config = ConfigHelper.defaultQuickCheckConfiguration(view.getContext());
    }

    @Override
    public void setReason(Field reason) {
        this.reason = reason;
    }

    @Override
    public QuickCheckConfiguration getConfig() {
        return config;
    }

    private QuickCheckContract.View getView() {
        if (viewReference != null)
            return viewReference.get();
        else
            return null;
    }
}
