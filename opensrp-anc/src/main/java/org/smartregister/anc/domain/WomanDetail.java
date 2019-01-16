package org.smartregister.anc.domain;

/**
 * Created by ndegwamartin on 14/01/2019.
 */
public class WomanDetail {
    Integer nextContact;

    String baseEntityId;
    String nextContactDate;
    String yellowFlags;
    String redFlags;

    Integer yellowFlagCount;
    Integer redFlagCount;

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
}
