package org.smartregister.anc.library.domain;

/**
 * Created by wizard on 04/05/19.
 */
public class TestResults {
    private String gestAge;
    private String testDate;
    private String testValue;

    public TestResults(String gestAge, String testDate, String testValue) {
        this.gestAge = gestAge;
        this.testDate = testDate;
        this.testValue = testValue;
    }

    public String getGestAge() {
        return gestAge;
    }

    public void setGestAge(String gestAge) {
        this.gestAge = gestAge;
    }

    public String getTestDate() {
        return testDate;
    }

    public void setTestDate(String testDate) {
        this.testDate = testDate;
    }

    public String getTestValue() {
        return testValue;
    }

    public void setTestValue(String testValue) {
        this.testValue = testValue;
    }
}
