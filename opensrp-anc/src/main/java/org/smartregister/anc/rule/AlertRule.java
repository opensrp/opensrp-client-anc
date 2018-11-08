package org.smartregister.anc.rule;

import org.joda.time.LocalDate;

public class AlertRule {

    public String buttonStatus = "not_due";

    public String nextContactDate;
    public Integer gestationAge;

    public AlertRule(Integer gestationAge, String nextContactDate) {

        this.nextContactDate = nextContactDate;
        this.gestationAge = gestationAge;
    }

    public String getButtonStatus() {
        return buttonStatus;
    }

    public boolean isOverdueWithDays(String date, Integer days) {

        LocalDate nextContact = new LocalDate(date);

        LocalDate todayDate = new LocalDate();

        return todayDate.isAfter(nextContact.plusDays(days - 1));

    }

    public boolean isDueWithinDays(String date, Integer days) {

        LocalDate nextContact = new LocalDate(date);

        LocalDate todayDate = new LocalDate();

        return todayDate.isAfter(nextContact.minusDays(1)) && todayDate.isBefore(nextContact.plusDays(days));

    }
}
