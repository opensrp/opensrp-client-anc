package org.smartregister.anc.presenter;

import org.smartregister.anc.R;
import org.smartregister.anc.contract.QuickCheckContract;
import org.smartregister.anc.domain.QuickCheck;
import org.smartregister.anc.domain.QuickCheckConfiguration;
import org.smartregister.anc.interactor.QuickCheckInteractor;
import org.smartregister.anc.util.ConfigHelper;
import org.smartregister.configurableviews.model.Field;

import java.lang.ref.WeakReference;
import java.util.Collection;
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
    private Integer contactNo;

    public QuickCheckPresenter(QuickCheckContract.View view) {
        this.viewReference = new WeakReference<>(view);
        this.interactor = new QuickCheckInteractor();
        this.config = ConfigHelper.defaultQuickCheckConfiguration(view.getContext());
    }

    @Override
    public void setReason(Field reason) {
        if (reason == null) {
            getView().displayToast(R.string.validation_no_reason_selected);
            return;
        }

        this.selectedReason = reason;
        if (reason.getDisplayName().equals(getView().getString(R.string.specific_complaint))) {
            getView().displayComplaintLayout();
        } else {
            this.specificComplaints.clear();
            getView().notifyComplaintAdapter();

            getView().hideComplaintLayout();
        }

        getView().displayDangerSignLayout();

    }

    @Override
    public QuickCheckConfiguration getConfig() {
        return config;
    }

    @Override
    public boolean containsComplaintOrDangerSign(Field field, boolean isDangerSign) {
        Set<Field> currentList = currentComplaintsOrDangerSigns(isDangerSign);
        return currentList.contains(field);
    }

    @Override
    public void modifyComplaintsOrDangerList(Field field, boolean isChecked, boolean isDangerSign) {
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

                getView().displayNavigationLayout();
            } else { if (field.getDisplayName().equals(getView().getString(R.string.complaint_other_specify))) {
                    getView().enableSpecifyEditText();
                }

                currentList.add(field);
            }
        } else {
            if (!isDangerSign && field.getDisplayName().equals(getView().getString(R.string.complaint_other_specify))) {
                getView().disableSpecifyEditText();
            }

            currentList.remove(field);
        }

    }

    @Override
    public void proceedToNormalContact(String specify) {
        boolean proceed = true;

        if (hasValidationErrors(proceed)) {
            return;
        }

        QuickCheck quickCheck = populate(specify, proceed, null);
        interactor.saveQuickCheckEvent(quickCheck, baseEntityId, this);

    }

    @Override
    public void referAndCloseContact(String specify, Boolean referred) {
        boolean proceed = false;
        if (hasValidationErrors(proceed)) {
            return;
        }

        QuickCheck quickCheck = populate(specify, proceed, referred);
        interactor.saveQuickCheckEvent(quickCheck, baseEntityId, this);
    }

    @Override
    public void setBaseEntityId(String baseEntityId) {
        this.baseEntityId = baseEntityId;
    }

    @Override
    public void setContactNumber(Integer contactNumber) {
        this.contactNo = contactNumber;
    }

    @Override
    public void quickCheckSaved(boolean proceed, boolean saved) {
        if (!saved) {
            getView().displayToast(R.string.validation_unable_to_save_quick_check);
        } else if (proceed) {
            getView().proceedToContact(baseEntityId, contactNo);
        }
        getView().dismiss();
    }

    @Override
    public Field getField(Collection<Field> set, String displayName) {
        for (Field field : set) {
            if (field.getDisplayName().equals(displayName)) {
                return field;
            }
        }
        return null;
    }

    private Set<Field> currentComplaintsOrDangerSigns(boolean isDangerSign) {
        return isDangerSign ? this.selectedDangerSigns : this.specificComplaints;
    }

    private QuickCheck populate(String specify, Boolean proceed, Boolean treat) {
        QuickCheck quickCheck = new QuickCheck();
        quickCheck.setSelectedReason(selectedReason);
        quickCheck.setSpecificComplaints(specificComplaints);
        quickCheck.setSelectedDangerSigns(selectedDangerSigns);
        quickCheck.setOtherSpecify(specify);

        quickCheck.setProceedToContact(getView().getString(R.string.proceed_to_normal_contact));
        quickCheck.setReferAndCloseContact(getView().getString(R.string.refer_and_close_contact));
        quickCheck.setYes(getView().getString(R.string.yes));
        quickCheck.setNo(getView().getString(R.string.no));

        quickCheck.setHasDangerSigns(hasDangerSigns());
        quickCheck.setProceedRefer(proceed);
        quickCheck.setTreat(treat);

        return quickCheck;
    }

    private boolean hasDangerSigns() {
        for (Field dangerSign : selectedDangerSigns) {
            if (dangerSign.getDisplayName().equals(getView().getString(R.string.danger_none))) {
                return false;
            }
        }

        return true;
    }

    private boolean hasValidationErrors(boolean proceed) {

        if (selectedReason == null) {
            getView().displayToast(R.string.validation_no_reason_selected);
            return true;
        }

        if (selectedReason.getDisplayName().equals(getView().getString(R.string.specific_complaint)) && specificComplaints.isEmpty()) {
            getView().displayToast(R.string.validation_no_specific_complaint);
            return true;
        }

        if (selectedDangerSigns.isEmpty()) {
            getView().displayToast(R.string.validation_no_danger_sign);
            return true;
        }

        if (!proceed && getField(selectedDangerSigns, getView().getString(R.string.danger_none)) != null) {
            getView().displayToast(R.string.validation_no_valid_danger_sign);
            return true;
        }

        return false;
    }

    private QuickCheckContract.View getView() {
        if (viewReference != null)
            return viewReference.get();
        else
            return null;
    }

    // Test methods

    public void setConfig(QuickCheckConfiguration config) {
        this.config = config;
    }

    public void setSpecificComplaints(Set<Field> specificComplaints) {
        this.specificComplaints = specificComplaints;
    }

    public Set<Field> getSpecificComplaints() {
        return specificComplaints;
    }

    public void setSelectedDangerSigns(Set<Field> selectedDangerSigns) {
        this.selectedDangerSigns = selectedDangerSigns;
    }

    public Set<Field> getSelectedDangerSigns() {
        return selectedDangerSigns;
    }

    public void setSelectedReason(Field selectedReason) {
        this.selectedReason = selectedReason;
    }

    public void setInteractor(QuickCheckContract.Interactor interactor) {
        this.interactor = interactor;
    }

}
