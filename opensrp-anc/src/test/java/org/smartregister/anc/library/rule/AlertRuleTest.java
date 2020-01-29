package org.smartregister.anc.library.rule;

import junit.framework.Assert;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.smartregister.anc.library.activity.BaseUnitTest;
import org.smartregister.anc.library.util.ConstantsUtils;

/**
 * Created by ndegwamartin on 08/11/2018.
 */
public class AlertRuleTest extends BaseUnitTest {
    private static final String ALERT_RULE_FIELD_TODAY_DATE = "todayDate";
    private AlertRule alertRule;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        Integer gestationalAge = 20;
        String nextContactDate = "2018-09-08";

        alertRule = new AlertRule(gestationalAge, nextContactDate);
    }

    @Test
    public void testAlertRuleInstantiatesCorrectly() {
        Assert.assertNotNull(alertRule);
    }

    @Test
    public void testGetButtonStatusReturnsCorrectDefaultValue() {
        Assert.assertEquals(ConstantsUtils.AlertStatusUtils.NOT_DUE, alertRule.getButtonStatus());
    }

    @Test
    public void testIsOverdueWithDaysReturnsCorrectValueForNonOverdueDates() {

        Integer gestationalAge = 20;
        String nextContactDate = "2018-11-19";

        alertRule = new AlertRule(gestationalAge, nextContactDate);
        Whitebox.setInternalState(alertRule, ALERT_RULE_FIELD_TODAY_DATE, new LocalDate("2018-11-10"));

        boolean result = alertRule.isOverdueWithDays(7);

        Assert.assertFalse(result);

        //Not overdue on 6th day
        Whitebox.setInternalState(alertRule, ALERT_RULE_FIELD_TODAY_DATE, new LocalDate("2018-11-25"));

        result = alertRule.isOverdueWithDays(7);

        Assert.assertFalse(result);

    }

    @Test
    public void testIsOverdueWithDaysReturnsCorrectValueForOverdueDates() {

        Integer gestationalAge = 20;
        String nextContactDate = "2018-11-19";
//Large overdue
        alertRule = new AlertRule(gestationalAge, nextContactDate);
        Whitebox.setInternalState(alertRule, ALERT_RULE_FIELD_TODAY_DATE, new LocalDate("2018-12-26"));

        boolean result = alertRule.isOverdueWithDays(7);

        Assert.assertTrue(result);

        // Overdue on 7th day

        Whitebox.setInternalState(alertRule, ALERT_RULE_FIELD_TODAY_DATE, new LocalDate("2018-11-26"));

        result = alertRule.isOverdueWithDays(7);

        Assert.assertTrue(result);
    }

    @Test
    public void testIsDueWithinDaysReturnsCorrectValueForDueDates() {

        //Due Today
        Integer gestationalAge = 20;
        String nextContactDate = "2018-11-10";

        alertRule = new AlertRule(gestationalAge, nextContactDate);
        Whitebox.setInternalState(alertRule, ALERT_RULE_FIELD_TODAY_DATE, new LocalDate("2018-11-10"));

        boolean result = alertRule.isDueWithinDays(7);

        Assert.assertTrue(result);

        //Due Tomorrow
        alertRule = new AlertRule(gestationalAge, nextContactDate);
        Whitebox.setInternalState(alertRule, ALERT_RULE_FIELD_TODAY_DATE, new LocalDate("2018-11-9"));

        result = alertRule.isDueWithinDays(7);

        Assert.assertFalse(result);

        //Today 1 days after next contact
        alertRule = new AlertRule(gestationalAge, nextContactDate);
        Whitebox.setInternalState(alertRule, ALERT_RULE_FIELD_TODAY_DATE, new LocalDate("2018-11-11"));

        result = alertRule.isDueWithinDays(7);

        Assert.assertTrue(result);

        //Today 6 days after next contact
        alertRule = new AlertRule(gestationalAge, nextContactDate);
        Whitebox.setInternalState(alertRule, ALERT_RULE_FIELD_TODAY_DATE, new LocalDate("2018-11-16"));

        result = alertRule.isDueWithinDays(7);

        Assert.assertTrue(result);
    }

    @Test
    public void testIsDueWithinDaysReturnsCorrectValueForNonDueDates() {

        //Not due
        Integer gestationalAge = 20;
        String nextContactDate = "2018-11-10";

        alertRule = new AlertRule(gestationalAge, nextContactDate);
        Whitebox.setInternalState(alertRule, ALERT_RULE_FIELD_TODAY_DATE, new LocalDate("2018-11-01"));

        boolean result = alertRule.isDueWithinDays(7);

        Assert.assertFalse(result);

        //Not Due 1 day to contact date
        alertRule = new AlertRule(gestationalAge, nextContactDate);
        Whitebox.setInternalState(alertRule, ALERT_RULE_FIELD_TODAY_DATE, new LocalDate("2018-11-09"));

        result = alertRule.isDueWithinDays(7);

        Assert.assertFalse(result);


        //Not Due 7 days after contact date (is Over due)
        alertRule = new AlertRule(gestationalAge, nextContactDate);
        Whitebox.setInternalState(alertRule, ALERT_RULE_FIELD_TODAY_DATE, new LocalDate("2018-11-17"));

        result = alertRule.isDueWithinDays(7);

        Assert.assertFalse(result);
    }
}
