package org.smartregister.anc.library.domain;

import java.util.List;

public class TestResultsDialog {
    private String testTitle;
    private List<TestResults> testResultsList;

    public TestResultsDialog(String testTitle, List<TestResults> testResultsList) {
        this.testTitle = testTitle;
        this.testResultsList = testResultsList;
    }

    public String getTestTitle() {
        return testTitle;
    }

    public void setTestTitle(String testTitle) {
        this.testTitle = testTitle;
    }

    public List<TestResults> getTestResultsList() {
        return testResultsList;
    }

    public void setTestResultsList(List<TestResults> testResultsList) {
        this.testResultsList = testResultsList;
    }
}
