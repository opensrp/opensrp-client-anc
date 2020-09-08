package org.smartregister.anc.library.event;

/**
 * Created by ndegwamartin on 11/04/2018.
 */

public class PatientRemovedEvent extends BaseEvent {
    private String closedNature;

    public String getClosedNature() {
        return closedNature;
    }

    public void setClosedNature(String closedNature) {
        this.closedNature = closedNature;
    }
}
