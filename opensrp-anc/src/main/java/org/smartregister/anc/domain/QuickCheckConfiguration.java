package org.smartregister.anc.domain;

import org.smartregister.configurableviews.model.BaseConfiguration;
import org.smartregister.configurableviews.model.Field;

import java.util.List;

public class QuickCheckConfiguration extends BaseConfiguration {

    private List<Field> reasons;
    private List<Field> complaints;
    private List<Field> dangerSigns;

    public void setReasons(List<Field> reasons) {
        this.reasons = reasons;
    }

    public List<Field> getReasons() {
        return reasons;
    }

    public void setComplaints(List<Field> complaints) {
        this.complaints = complaints;
    }

    public List<Field> getComplaints() {
        return complaints;
    }

    public void setDangerSigns(List<Field> dangerSigns) {
        this.dangerSigns = dangerSigns;
    }

    public List<Field> getDangerSigns() {
        return dangerSigns;
    }
}
