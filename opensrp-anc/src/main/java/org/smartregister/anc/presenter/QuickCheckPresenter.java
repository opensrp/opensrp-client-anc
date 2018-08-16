package org.smartregister.anc.presenter;

import org.smartregister.anc.R;
import org.smartregister.anc.contract.QuickCheckContract;
import org.smartregister.anc.domain.QuickCheckConfiguration;
import org.smartregister.anc.util.ConfigHelper;
import org.smartregister.configurableviews.model.Field;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class QuickCheckPresenter implements QuickCheckContract.Presenter {

    private WeakReference<QuickCheckContract.View> viewReference;
    private QuickCheckConfiguration config;

    private Field selectedReason;
    private Set<Field> specificComplaints = new HashSet<>();
    private Set<Field> selectedDangerSigns = new HashSet<>();

    public QuickCheckPresenter(QuickCheckContract.View view) {
        this.viewReference = new WeakReference<>(view);
        this.config = ConfigHelper.defaultQuickCheckConfiguration(view.getContext());
    }

    @Override
    public void setReason(Field reason) {
        this.selectedReason = reason;
        if (reason.getDisplayName().equals(getView().getString(R.string.specific_complaint))) {
            getView().displayComplaintLayout();
        } else {
            this.specificComplaints.clear();
            getView().notifyComplaintAdapter();

            getView().hideComplaintLayout();
        }

        getView().displayDangerSignLayout();

        getView().displayNavigationLayout();
    }

    @Override
    public QuickCheckConfiguration getConfig() {
        return config;
    }

    @Override
    public Set<Field> currentComplaintsOrDangerSigns(boolean isDangerSign) {
        return isDangerSign ? this.selectedDangerSigns : this.specificComplaints;
    }

    @Override
    public boolean containsComplaintOrDangerSign(Field field, boolean isDangerSign) {
        Set<Field> currentList = currentComplaintsOrDangerSigns(isDangerSign);
        return currentList.contains(field);
    }

    @Override
    public void addToComplaintsOrDangerList(Field field, boolean isChecked, boolean isDangerSign) {
        Set<Field> currentList = currentComplaintsOrDangerSigns(isDangerSign);

        if (isChecked) {

            if (isDangerSign) {
                // If Danger None, clear danger list and add the field
                if (field.getDisplayName().equals(getView().getString(R.string.danger_none))) {
                    currentList.clear();
                    currentList.add(field);

                    getView().notifyDangerSignAdapter();
                    getView().hideReferButton();

                } else {
                    currentList.add(field);

                    // If not Danger None, remove it if it exists
                    Field dangerNone = getField(currentList, getView().getString(R.string.danger_none));
                    if (dangerNone != null) {
                        currentList.remove(dangerNone);
                        getView().notifyDangerSignAdapter();
                    }

                    getView().displayReferButton();
                }
            } else {
                if (field.getDisplayName().equals(getView().getString(R.string.complaint_other_specify))) {
                    getView().showSpecifyEditText();
                }

                currentList.add(field);
            }
        } else {
            if (field.getDisplayName().equals(getView().getString(R.string.complaint_other_specify))) {
                getView().hideSpecifyEditText();
            }

            currentList.remove(field);
        }

    }

    @Override
    public void proceedToNormalContact() {
        if (selectedReason == null) {
            getView().displayToast(R.string.validation_no_reason_selected);
            return;
        }

        if (selectedReason.getDisplayName().equals(getView().getString(R.string.specific_complaint)) && specificComplaints.isEmpty()) {
            getView().displayToast(R.string.validation_no_specific_complaint);
            return;
        }

        if (selectedDangerSigns.isEmpty()) {
            getView().displayToast(R.string.validation_no_danger_sign);
            return;
        }

    }

    @Override
    public void referAndCloseContact() {
        // Refer and Close Contact
    }

    private Field getField(Set<Field> set, String displayName) {
        for (Field field : set) {
            if (field.getDisplayName().equals(displayName)) {
                return field;
            }
        }
        return null;
    }

    private QuickCheckContract.View getView() {
        if (viewReference != null)
            return viewReference.get();
        else
            return null;
    }
}
