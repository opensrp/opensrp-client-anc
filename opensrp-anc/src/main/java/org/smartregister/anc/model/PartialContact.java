package org.smartregister.anc.model;

public class PartialContact extends Contact {

    private String formJsonDraft;
    private Boolean isFinalized;

    public String getFormJsonDraft() {
        return formJsonDraft;
    }

    public void setFormJsonDraft(String formJson) {
        this.formJsonDraft = formJson;
    }

    public Boolean getFinalized() {
        return isFinalized;
    }

    public void setFinalized(Boolean finalized) {
        isFinalized = finalized;
    }

}
