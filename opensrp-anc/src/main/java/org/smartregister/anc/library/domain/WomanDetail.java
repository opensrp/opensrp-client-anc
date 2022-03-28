package org.smartregister.anc.library.domain;

/**
 * Created by ndegwamartin on 14/01/2019.
 */
public class WomanDetail {
    private Integer nextContact;

    private String baseEntityId;
    private String nextContactDate;
    private String yellowFlags;
    private String redFlags;
    private String contactStatus;
    private String previousContactStatus;
    private String lastContactRecordDate;

    private Integer yellowFlagCount;
    private Integer redFlagCount;

    private boolean referral;

    public Integer getNextContact() {
        return nextContact;
    }

    public void setNextContact(Integer nextContact) {
        this.nextContact = nextContact;
    }

    public String getBaseEntityId() {
        return baseEntityId;
    }

    public void setBaseEntityId(String baseEntityId) {
        this.baseEntityId = baseEntityId;
    }

    public String getNextContactDate() {
        return nextContactDate;
    }

    public void setNextContactDate(String nextContactDate) {
        this.nextContactDate = nextContactDate;
    }

    public String getYellowFlags() {
        return yellowFlags;
    }

    public void setYellowFlags(String yellowFlags) {
        this.yellowFlags = yellowFlags;
    }

    public String getRedFlags() {
        return redFlags;
    }

    public void setRedFlags(String redFlags) {
        this.redFlags = redFlags;
    }

    public Integer getYellowFlagCount() {
        return yellowFlagCount;
    }

    public void setYellowFlagCount(Integer yellowFlagCount) {
        this.yellowFlagCount = yellowFlagCount;
    }

    public Integer getRedFlagCount() {
        return redFlagCount;
    }

    public void setRedFlagCount(Integer redFlagCount) {
        this.redFlagCount = redFlagCount;
    }

    public String getContactStatus() {
        return contactStatus;
    }

    public void setContactStatus(String contactStatus) {
        this.contactStatus = contactStatus;
    }

    public boolean isReferral() {
        return referral;
    }

    public void setReferral(boolean referral) {
        this.referral = referral;
    }

    public String getLastContactRecordDate() {
        return lastContactRecordDate;
    }

    public void setLastContactRecordDate(String lastContactRecordDate) {
        this.lastContactRecordDate = lastContactRecordDate;
    }

    public String getPreviousContactStatus() {
        return previousContactStatus;
    }

    public void setPreviousContactStatus(String previousContactStatus) {
        this.previousContactStatus = previousContactStatus;
    }
}
