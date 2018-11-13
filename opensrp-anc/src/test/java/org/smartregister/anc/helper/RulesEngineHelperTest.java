package org.smartregister.anc.helper;

import junit.framework.Assert;

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.anc.activity.BaseUnitTest;
import org.smartregister.anc.rule.AlertRule;
import org.smartregister.anc.rule.ContactRule;
import org.smartregister.anc.util.Constants;

import java.util.Arrays;
import java.util.List;

/**
 * Created by ndegwamartin on 09/11/2018.
 */
public class RulesEngineHelperTest extends BaseUnitTest {

    private static final String ALERT_RULE_FIELD_TODAY_DATE = "todayDate";

    private RulesEngineHelper rulesEngineHelper;

    @Mock
    private Rules rules;

    @Mock
    private Facts facts;

    @Mock
    private RulesEngine inferentialRulesEngine;

    @Mock
    private RulesEngine defaultRulesEngine;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        rulesEngineHelper = new RulesEngineHelper(RuntimeEnvironment.application);
    }

    @Test
    public void testRulesEngineHelperConstructorCreatesValidInstance() {
        Assert.assertNotNull(rulesEngineHelper);
    }

    @Test
    public void testProcessInferentialRulesInvokesCorrectRulesEngineWithCorrectParameters() {

        Whitebox.setInternalState(rulesEngineHelper, "inferentialRulesEngine", inferentialRulesEngine);
        Whitebox.setInternalState(rulesEngineHelper, "defaultRulesEngine", defaultRulesEngine);

        rulesEngineHelper.processInferentialRules(rules, facts);

        Mockito.verify(inferentialRulesEngine).fire(rules, facts);
        Mockito.verify(defaultRulesEngine, Mockito.times(0)).fire(rules, facts);
    }

    @Test
    public void testProcessDefaultRulesInvokesCorrectRulesEngineWithCorrectParameters() {

        Whitebox.setInternalState(rulesEngineHelper, "inferentialRulesEngine", inferentialRulesEngine);
        Whitebox.setInternalState(rulesEngineHelper, "defaultRulesEngine", defaultRulesEngine);

        rulesEngineHelper.processDefaultRules(rules, facts);

        Mockito.verify(defaultRulesEngine).fire(rules, facts);
        Mockito.verify(inferentialRulesEngine, Mockito.times(0)).fire(rules, facts);
    }

    @Test
    public void testGetButtonAlertStatusReturnsNotDueForUndueDate() {

        //Not due
        AlertRule alertRule = new AlertRule(30, "2019-07-09");
        Whitebox.setInternalState(alertRule, ALERT_RULE_FIELD_TODAY_DATE, new LocalDate("2018-11-09"));

        String buttonAlertStatus = rulesEngineHelper.getButtonAlertStatus(alertRule, "alert-rules.yml");

        Assert.assertEquals(Constants.ALERT_STATUS.NOT_DUE, buttonAlertStatus);
    }


    @Test
    public void testGetButtonAlertStatusReturnsDueForADueDate() {
        //Due
        AlertRule alertRule = new AlertRule(30, "2018-11-09");
        Whitebox.setInternalState(alertRule, ALERT_RULE_FIELD_TODAY_DATE, new LocalDate("2018-11-09"));

        String buttonAlertStatus = rulesEngineHelper.getButtonAlertStatus(alertRule, "alert-rules.yml");

        Assert.assertEquals(Constants.ALERT_STATUS.DUE, buttonAlertStatus);
    }


    @Test
    public void testGetButtonAlertStatusReturnsOverDueForOverdueDate() {

        //OverDue
        AlertRule alertRule = new AlertRule(30, "2018-11-01");
        Whitebox.setInternalState(alertRule, ALERT_RULE_FIELD_TODAY_DATE, new LocalDate("2018-11-09"));

        String buttonAlertStatus = rulesEngineHelper.getButtonAlertStatus(alertRule, "alert-rules.yml");

        Assert.assertEquals(Constants.ALERT_STATUS.OVERDUE, buttonAlertStatus);
    }


    @Test
    public void testGetButtonAlertStatusReturnsExpiredForDueDeliveryDate() {

        //expired
        AlertRule alertRule = new AlertRule(41, "2018-11-11");

        String buttonAlertStatus = rulesEngineHelper.getButtonAlertStatus(alertRule, "alert-rules.yml");

        Assert.assertEquals(Constants.ALERT_STATUS.EXPIRED, buttonAlertStatus);
    }


    @Test
    public void testGetButtonAlertStatusReturnsExpiredOverdueForOverdueDeliveryDate() {

        //Expired Overdue
        AlertRule alertRule = new AlertRule(41, "2018-10-09");
        Whitebox.setInternalState(alertRule, ALERT_RULE_FIELD_TODAY_DATE, new LocalDate("2018-11-09"));

        String buttonAlertStatus = rulesEngineHelper.getButtonAlertStatus(alertRule, "alert-rules.yml");

        Assert.assertEquals(Constants.ALERT_STATUS.EXPIRED_OVERDUE, buttonAlertStatus);
    }

    @Test
    public void testGetContactVisitScheduleInvokesInferentialRulesEngineProcessInferentialRulesWithCorrectParams() {

        ContactRule contactRule = new ContactRule(20, true, DUMMY_BASE_ENTITY_ID);

        RulesEngineHelper rulesEngineHelperSpy = Mockito.spy(rulesEngineHelper);
        List<Integer> scheduleWeeksList = rulesEngineHelperSpy.getContactVisitSchedule(contactRule, "contact-rules.yml");

        Assert.assertNotNull(scheduleWeeksList);

        Mockito.verify(rulesEngineHelperSpy).processInferentialRules(ArgumentMatchers.any(Rules.class), ArgumentMatchers.any(Facts.class));

    }

    @Test
    public void testGetContactVisitScheduleGeneratesCorrectScheduleAt4Weeks() {

        ContactRule contactRule = new ContactRule(4, true, DUMMY_BASE_ENTITY_ID);

        RulesEngineHelper rulesEngineHelperSpy = Mockito.spy(rulesEngineHelper);
        List<Integer> scheduleWeeksList = rulesEngineHelperSpy.getContactVisitSchedule(contactRule, "contact-rules.yml");

        Assert.assertNotNull(scheduleWeeksList);

        Assert.assertEquals(Arrays.asList(new Integer[]{12, 20, 26, 30, 34, 36, 38, 40, 41}), scheduleWeeksList);

    }

    @Test
    public void testGetContactVisitScheduleGeneratesCorrectScheduleAt12Weeks() {

        ContactRule contactRule = new ContactRule(12, true, DUMMY_BASE_ENTITY_ID);

        RulesEngineHelper rulesEngineHelperSpy = Mockito.spy(rulesEngineHelper);
        List<Integer> scheduleWeeksList = rulesEngineHelperSpy.getContactVisitSchedule(contactRule, "contact-rules.yml");

        Assert.assertNotNull(scheduleWeeksList);

        Assert.assertEquals(Arrays.asList(new Integer[]{20, 26, 30, 34, 36, 38, 40, 41}), scheduleWeeksList);

    }

    @Test
    public void testGetContactVisitScheduleGeneratesCorrectScheduleAt20Weeks() {

        ContactRule contactRule = new ContactRule(20, true, DUMMY_BASE_ENTITY_ID);

        RulesEngineHelper rulesEngineHelperSpy = Mockito.spy(rulesEngineHelper);
        List<Integer> scheduleWeeksList = rulesEngineHelperSpy.getContactVisitSchedule(contactRule, "contact-rules.yml");

        Assert.assertNotNull(scheduleWeeksList);

        Assert.assertEquals(Arrays.asList(new Integer[]{26, 30, 34, 36, 38, 40, 41}), scheduleWeeksList);

    }

    @Test
    public void testGetContactVisitScheduleGeneratesCorrectScheduleAt28Weeks() {

        ContactRule contactRule = new ContactRule(28, true, DUMMY_BASE_ENTITY_ID);

        RulesEngineHelper rulesEngineHelperSpy = Mockito.spy(rulesEngineHelper);
        List<Integer> scheduleWeeksList = rulesEngineHelperSpy.getContactVisitSchedule(contactRule, "contact-rules.yml");

        Assert.assertNotNull(scheduleWeeksList);

        Assert.assertEquals(Arrays.asList(new Integer[]{32, 34, 36, 38, 40, 41}), scheduleWeeksList);

    }

    @Test
    public void testGetContactVisitScheduleGeneratesCorrectScheduleAt40Weeks() {

        ContactRule contactRule = new ContactRule(40, true, DUMMY_BASE_ENTITY_ID);

        RulesEngineHelper rulesEngineHelperSpy = Mockito.spy(rulesEngineHelper);
        List<Integer> scheduleWeeksList = rulesEngineHelperSpy.getContactVisitSchedule(contactRule, "contact-rules.yml");

        Assert.assertNotNull(scheduleWeeksList);

        Assert.assertEquals(Arrays.asList(new Integer[]{40, 41}), scheduleWeeksList);

    }

}
