package org.smartregister.anc.library.rule;

import org.joda.time.LocalDate;
import org.smartregister.anc.library.util.ConstantsUtils;

//All date formats ISO 8601 yyyy-mm-dd

/**
 * Created by ndegwamartin on 09/11/2018.
 */
public class AlertRule {

    public static final String RULE_KEY = "alertRule";
    public String buttonStatus = ConstantsUtils.AlertStatusUtils.NOT_DUE;

    public String nextContactDate;
    public Integer gestationAge;
    private LocalDate todayDate;

    public AlertRule(Integer gestationAge, String nextContactDate) {

        this.nextContactDate = nextContactDate;
        this.gestationAge = gestationAge;

        this.todayDate = new LocalDate();
    }

    public String getButtonStatus() {
        return buttonStatus;
    }

    public boolean isOverdueWithDays(Integer days) {

        LocalDate nextContact = new LocalDate(this.nextContactDate);

        return todayDate.isAfter(nextContact.plusDays(days - 1));

    }

    public boolean isDueWithinDays(Integer days) {

        LocalDate nextContact = new LocalDate(this.nextContactDate);

        return todayDate.isAfter(nextContact.minusDays(1)) && todayDate.isBefore(nextContact.plusDays(days));

    }
}
