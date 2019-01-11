package org.smartregister.anc.domain;

import org.smartregister.configurableviews.model.Field;

import java.util.Set;

public class QuickCheck {

    private Field selectedReason;
    private Set<Field> specificComplaints;
    private Set<Field> selectedDangerSigns;
    private String otherSpecify;

    private String proceedToContact;
    private String referAndCloseContact;
    private String yes;
    private String no;

    private Boolean hasDangerSigns;
    private Boolean proceedRefer;
    private Boolean treat;
    private Integer contactNumber;

    public Field getSelectedReason() {
        return selectedReason;
    }

    public void setSelectedReason(Field selectedReason) {
        this.selectedReason = selectedReason;
    }

    public Set<Field> getSpecificComplaints() {
        return specificComplaints;
    }

    public void setSpecificComplaints(Set<Field> specificComplaints) {
        this.specificComplaints = specificComplaints;
    }

    public Set<Field> getSelectedDangerSigns() {
        return selectedDangerSigns;
    }

    public void setSelectedDangerSigns(Set<Field> selectedDangerSigns) {
        this.selectedDangerSigns = selectedDangerSigns;
    }

    public String getOtherSpecify() {
        return otherSpecify;
    }

    public void setOtherSpecify(String otherSpecify) {
        this.otherSpecify = otherSpecify;
    }

    public String getProceedToContact() {
        return proceedToContact;
    }

    public void setProceedToContact(String proceedToContact) {
        this.proceedToContact = proceedToContact;
    }

    public String getReferAndCloseContact() {
        return referAndCloseContact;
    }

    public void setReferAndCloseContact(String referAndCloseContact) {
        this.referAndCloseContact = referAndCloseContact;
    }

    public String getYes() {
        return yes;
    }

    public void setYes(String yes) {
        this.yes = yes;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public Boolean getHasDangerSigns() {
        return hasDangerSigns;
    }

    public void setHasDangerSigns(Boolean hasDangerSigns) {
        this.hasDangerSigns = hasDangerSigns;
    }

    public Boolean getProceedRefer() {
        return proceedRefer;
    }

    public void setProceedRefer(Boolean proceedRefer) {
        this.proceedRefer = proceedRefer;
    }

    public Boolean getTreat() {
        return treat;
    }

    public void setTreat(Boolean treat) {
        this.treat = treat;
    }

    public Integer getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(Integer contactNumber) {
        this.contactNumber = contactNumber;
    }
}
