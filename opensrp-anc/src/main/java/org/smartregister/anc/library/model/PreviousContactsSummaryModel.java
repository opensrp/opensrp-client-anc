package org.smartregister.anc.library.model;

import org.jeasy.rules.api.Facts;

public class PreviousContactsSummaryModel {
    private String contactNumber;
    private String createdAt;
    private Facts visitFacts;

    public PreviousContactsSummaryModel() {
    }

    public PreviousContactsSummaryModel(String contactNumber, String createdAt, Facts visitFacts) {
        this.contactNumber = contactNumber;
        this.createdAt = createdAt;
        this.visitFacts = visitFacts;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Facts getVisitFacts() {
        return visitFacts;
    }

    public void setVisitFacts(Facts visitFacts) {
        this.visitFacts = visitFacts;
    }
}