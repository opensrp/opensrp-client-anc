package org.smartregister.anc.presenter;

import org.smartregister.anc.contract.MeContract;
import org.smartregister.anc.model.MeFragmentModel;

import java.lang.ref.WeakReference;

public class MePresenter implements MeContract.Presenter {
    private WeakReference<MeContract.View> viewReference;
    private MeContract.Model model;

    public MePresenter(MeContract.View view) {
        model = new MeFragmentModel();
        viewReference = new WeakReference<>(view);
    }

    @Override
    public void updateInitials() {
        String initials = model.getInitials();
        if (initials != null) {
            getView().updateInitialsText(initials);
        }
    }

    @Override
    public void updateName() {
        String name = model.getName();
        if (name != null) {
            getView().updateNameText(name);
        }
    }

    @Override
    public String getBuildDate() {
        return model.getBuildDate();
    }

    private MeContract.View getView() {
        if (viewReference != null)
            return viewReference.get();
        else
            return null;
    }
}
