package org.smartregister.anc.presenter;

import org.smartregister.anc.R;
import org.smartregister.anc.contract.QuickCheckContract;
import org.smartregister.anc.domain.QuickCheckConfiguration;
import org.smartregister.anc.interactor.QuickCheckInteractor;
import org.smartregister.anc.util.ConfigHelper;
import org.smartregister.configurableviews.model.Field;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;

public class QuickCheckPresenter implements QuickCheckContract.Presenter, QuickCheckContract.InteractorCallback {

    private WeakReference<QuickCheckContract.View> viewReference;
    private QuickCheckConfiguration config;

    private QuickCheckContract.Interactor interactor;

    private Field selectedReason;
    private Set<Field> specificComplaints = new HashSet<>();
    private Set<Field> selectedDangerSigns = new HashSet<>();

    private String baseEntityId;

    public QuickCheckPresenter(QuickCheckContract.View view) {
        this.viewReference = new WeakReference<>(view);
        this.interactor = new QuickCheckInteractor();
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
    public void proceedToNormalContact(String specify) {
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

        QuickCheck quickCheck = populate(specify, true, null);

        interactor.saveQuickCheckEvent(quickCheck, baseEntityId, this);

    }

    @Override
    public void referAndCloseContact(String specify, Boolean referred) {
        QuickCheck quickCheck = populate(specify, false, referred);
        interactor.saveQuickCheckEvent(quickCheck, baseEntityId, this);
    }

    @Override
    public void setBaseEntityId(String baseEntityId) {
        this.baseEntityId = baseEntityId;
    }

    @Override
    public void quickCheckSaved(boolean saved) {
        getView().displayToast(R.string.proceed_to_normal_contact);
        getView().dismiss();
    }

    private Field getField(Set<Field> set, String displayName) {
        for (Field field : set) {
            if (field.getDisplayName().equals(displayName)) {
                return field;
            }
        }
        return null;
    }

    private QuickCheck populate(String specify, Boolean proceed, Boolean refer) {
        QuickCheck quickCheck = new QuickCheck();
        quickCheck.selectedReason = selectedReason;
        quickCheck.specificComplaints = specificComplaints;
        quickCheck.selectedDangerSigns = selectedDangerSigns;
        quickCheck.otherSpecify = specify;

        quickCheck.proceedToContact = getView().getString(R.string.proceed_to_normal_contact);
        quickCheck.referAndCloseContact = getView().getString(R.string.refer_and_close_contact);
        quickCheck.yes = getView().getString(R.string.yes);
        quickCheck.no = getView().getString(R.string.no);

        quickCheck.hasDangerSigns = hasDangerSigns();
        quickCheck.isProceed = proceed;
        quickCheck.isReferred = refer;

        return quickCheck;
    }

    private boolean hasDangerSigns() {
        if (selectedDangerSigns == null) {
            return false;
        } else if (selectedDangerSigns.size() == 1) {
            Field field = selectedDangerSigns.iterator().next();
            return !field.getDisplayName().equals(getView().getString(R.string.danger_none));

        }
        return true;

    }

    private QuickCheckContract.View getView() {
        if (viewReference != null)
            return viewReference.get();
        else
            return null;
    }

    public class QuickCheck {
        public Field selectedReason;
        public Set<Field> specificComplaints;
        public Set<Field> selectedDangerSigns;
        public String otherSpecify;

        public String proceedToContact;
        public String referAndCloseContact;
        public String yes;
        public String no;

        public Boolean hasDangerSigns;
        public Boolean isProceed;
        public Boolean isReferred;

    }
}
