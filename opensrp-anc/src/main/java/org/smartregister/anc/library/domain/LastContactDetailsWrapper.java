package org.smartregister.anc.library.domain;

import org.jeasy.rules.api.Facts;

import java.util.List;

public class LastContactDetailsWrapper {
    private String contactNo;
    private String contactDate;
    private List<YamlConfigWrapper> extraInformation;
    private Facts facts;

    public LastContactDetailsWrapper(String contactNo, String contactDate, List<YamlConfigWrapper> extraInformation,
                                     Facts facts) {
        this.contactNo = contactNo;
        this.contactDate = contactDate;
        this.extraInformation = extraInformation;
        this.facts = facts;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public String getContactDate() {
        return contactDate;
    }

    public void setContactDate(String contactDate) {
        this.contactDate = contactDate;
    }

    public List<YamlConfigWrapper> getExtraInformation() {
        return extraInformation;
    }

    public void setExtraInformation(List<YamlConfigWrapper> extraInformation) {
        this.extraInformation = extraInformation;
    }

    public Facts getFacts() {
        return facts;
    }

    public void setFacts(Facts facts) {
        this.facts = facts;
    }
}
