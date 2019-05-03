package org.smartregister.anc.helper;

import android.content.Context;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.rules.RuleConstant;
import com.vijay.jsonwizard.rules.RulesEngineHelper;
import com.vijay.jsonwizard.utils.FormUtils;

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rule;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.jeasy.rules.core.DefaultRulesEngine;
import org.jeasy.rules.core.InferenceRulesEngine;
import org.jeasy.rules.core.RulesEngineParameters;
import org.jeasy.rules.mvel.MVELRule;
import org.jeasy.rules.mvel.MVELRuleFactory;
import org.joda.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.anc.rule.AlertRule;
import org.smartregister.anc.rule.ContactRule;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.smartregister.anc.util.ContactJsonFormUtils.obtainValue;

public class AncRulesEngineHelper extends RulesEngineHelper {
    private Context context;
    private RulesEngine inferentialRulesEngine;
    private RulesEngine defaultRulesEngine;
    private Map<String, Rules> ruleMap;
    private final String RULE_FOLDER_PATH = "rule/";
    private JSONObject mJsonObject = new JSONObject();

    public AncRulesEngineHelper(Context context) {
        this.context = context;
        this.inferentialRulesEngine = new InferenceRulesEngine();
        RulesEngineParameters parameters = new RulesEngineParameters().skipOnFirstAppliedRule(true);
        this.defaultRulesEngine = new DefaultRulesEngine(parameters);
        this.ruleMap = new HashMap<>();

    }

    public void setJsonObject(JSONObject jsonObject) {
        mJsonObject = jsonObject;
    }

    private Rules getRulesFromAsset(String fileName) {
        try {
            if (!ruleMap.containsKey(fileName)) {

                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(context.getAssets().open(fileName)));
                ruleMap.put(fileName, MVELRuleFactory.createRulesFrom(bufferedReader));
            }
            return ruleMap.get(fileName);
        } catch (IOException e) {
            Log.e(ContactRule.class.getName(), e.getMessage(), e);
            return null;
        }
    }

    protected void processInferentialRules(Rules rules, Facts facts) {

        inferentialRulesEngine.fire(rules, facts);
    }

    protected void processDefaultRules(Rules rules, Facts facts) {

        defaultRulesEngine.fire(rules, facts);
    }

    public List<Integer> getContactVisitSchedule(ContactRule contactRule, String rulesFile) {

        Facts facts = new Facts();
        facts.put(ContactRule.RULE_KEY, contactRule);

        Rules rules = getRulesFromAsset(RULE_FOLDER_PATH + rulesFile);
        if (rules == null) {
            return null;
        }

        processInferentialRules(rules, facts);

        Set<Integer> contactList = contactRule.set;
        List<Integer> list = new ArrayList<>(contactList);
        Collections.sort(list);

        return list;
    }

    public String getButtonAlertStatus(AlertRule alertRule, String rulesFile) {

        Facts facts = new Facts();
        facts.put(AlertRule.RULE_KEY, alertRule);

        Rules rules = getRulesFromAsset(RULE_FOLDER_PATH + rulesFile);
        if (rules == null) {
            return null;
        }

        processDefaultRules(rules, facts);

        return alertRule.buttonStatus;
    }

    public boolean getRelevance(Facts relevanceFacts, String rule) {

        relevanceFacts.put(RuleConstant.IS_RELEVANT, false);

        Rules rules = new Rules();
        Rule mvelRule = new MVELRule().name(UUID.randomUUID().toString()).when(rule).then("isRelevant = true;");
        rules.register(mvelRule);

        processDefaultRules(rules, relevanceFacts);

        return relevanceFacts.get(RuleConstant.IS_RELEVANT);
    }

    /**
     * Strips the ANC Gestation age to just get me the weeks age
     *
     * @param gestAge {@link String}
     * @return ga {@link String}
     */
    public String stripGaNumber(String gestAge) {
        String ga = "";
        if (!TextUtils.isEmpty(gestAge)) {
            String[] gestAgeSplit = gestAge.split(" ");
            if (gestAgeSplit.length >= 1) {
                int gaWeeks = Integer.parseInt(gestAgeSplit[0]);
                ga = String.valueOf(gaWeeks);
            }
        }
        return ga;
    }

    /***
     * Gets value form accordion
     * @param accordion accordion to get the value from
     * @param widget to get its value
     * @return return empty when no value is found otherwise return the value
     */
    public String getValueFromAccordion(String accordion, String widget) throws JSONException {
        String result = "";
        if (mJsonObject.length() > 0) {
            String[] splitWidget = widget.split("_");
            String step = splitWidget[0];
            String key = getKey(widget, step);
            if (mJsonObject.has(step)) {
                JSONObject stepsObject = mJsonObject.getJSONObject(step);
                JSONArray fields = stepsObject.getJSONArray(JsonFormConstants.FIELDS);
                if (fields.length() > 1)
                    for (int i = 0; i < fields.length(); i++) {
                        JSONObject accordionObject = fields.getJSONObject(i);
                        if (accordionObject.getString(JsonFormConstants.KEY).equals(accordion)) {
                            JSONArray value = accordionObject.optJSONArray(JsonFormConstants.VALUE);
                            if (value == null) {
                                return result;
                            }
                            result = obtainValue(key, value);
                            break;
                        }
                    }
            }
        }
        return result;
    }

    private String getKey(String widget, String step) {
        return widget.replace(step + "_", "");
    }

    /**
     * @param source
     */
    public void filterCheckboxOptions(String source) {
        Log.i("Our json object", mJsonObject.toString());
        String myString = source;
        myString.split(":");
    }

    /**
     * Given two dates compare if they are equal
     * @param firstDate the first date entered
     * @param secondDate the second date entered
     * @return returns {-1} when first date occurs before second date, {0} when both dates are equal
     * {1} when second date is greater than first date and {-2} if any of the dates passed is null
     * or is empty
     */
    public int compareTwoDates(String firstDate, String secondDate) {
        if (!TextUtils.isEmpty(firstDate) && !TextUtils.isEmpty(secondDate)) {
            Calendar dateOne = FormUtils.getDate(firstDate);
            Calendar dateTwo = FormUtils.getDate(secondDate);
            if (dateOne.before(dateTwo)) {
                return -1;
            } else if (dateOne.equals(dateTwo)) {
                return 0;
            } else {
                return 1;
            }
        }
        return -2;
    }

    /**
     * Compares date against today's date
     * @param theDate passed as first date to first date
     * @return -1 if date is before today, 0 if equal, 1 if date is greater than today's date and -2
     * otherwise
     */
    public int compareDateAgainstToday(String theDate){
        return compareTwoDates(theDate, (new LocalDate()).toString(FormUtils.NATIIVE_FORM_DATE_FORMAT_PATTERN));
    }
}
