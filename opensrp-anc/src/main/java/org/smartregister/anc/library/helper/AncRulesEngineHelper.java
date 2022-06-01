package org.smartregister.anc.library.helper;

import android.content.Context;
import android.text.TextUtils;

import org.smartregister.anc.library.R;
import org.smartregister.anc.library.constants.ANCJsonFormConstants;
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
import org.jeasy.rules.support.YamlRuleDefinitionReader;
import org.joda.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.anc.library.rule.AlertRule;
import org.smartregister.anc.library.rule.ContactRule;
import org.smartregister.anc.library.util.ANCFormUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import timber.log.Timber;

public class AncRulesEngineHelper extends RulesEngineHelper {
    private final String RULE_FOLDER_PATH = "rule/";
    private final Context context;
    private final RulesEngine inferentialRulesEngine;
    private final RulesEngine defaultRulesEngine;
    private final Map<String, Rules> ruleMap;
    private JSONObject mJsonObject = new JSONObject();
    private final YamlRuleDefinitionReader yamlRuleDefinitionReader = new YamlRuleDefinitionReader();
    private final MVELRuleFactory mvelRuleFactory = new MVELRuleFactory(yamlRuleDefinitionReader);

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

    private Rules getRulesFromAsset(String fileName) {
        try {
            if (!ruleMap.containsKey(fileName)) {
                BufferedReader bufferedReader =
                        new BufferedReader(new InputStreamReader(context.getAssets().open(fileName)));
                ruleMap.put(fileName, mvelRuleFactory.createRules(bufferedReader));
            }
            return ruleMap.get(fileName);
        } catch (IOException e) {
            Timber.e(e, "%s getRulesFromAsset()", this.getClass().getCanonicalName());
            return null;
        } catch (Exception e) {
            Timber.e(e, "%s getRulesFromAsset()", this.getClass().getCanonicalName());
            return null;
        }
    }

    protected void processInferentialRules(Rules rules, Facts facts) {
        inferentialRulesEngine.fire(rules, facts);
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

    protected void processDefaultRules(Rules rules, Facts facts) {
        defaultRulesEngine.fire(rules, facts);
    }

    public boolean getRelevance(Facts relevanceFacts, String rule) {
        relevanceFacts.put("helper", this);
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

    /**
     * Get weight category
     * @param bmi - float value of Body Mass Index
     * @return - key for weight category
     */
    public String weightCat(float bmi) {
        if (bmi < 18.5) return "underweight";
        if (bmi >= 18.5 && bmi < 25) return "normal";
        if (bmi >= 25 && bmi < 30) return "overweight";
        return "obese";
    }

    /**
     * Get string display of weight category from strings.xml
     * @param bmi - float value of Body Mass Index
     * @return String of weight category value
     */
    public String weightCatString(float bmi) {
        String weightCat = this.weightCat(bmi);
        int resId = this.context.getResources().getIdentifier("weight_" + weightCat, "string", this.context.getPackageName());
        return this.context.getString(resId);
    }

    /**
     * Get MUAC category
     * @param muac - float value of woman's MUAC
     * @return String of MUAC category
     */
    public String muacCat(float muac) {
        if (muac > 23.5) return "malnourished";
        return "normal";
    }
    /**
     * Get string display of MUAC category from strings.xml
     * @param muac - float value of MUAC
     * @return String of MUAC category value
     */
    public String muacCatString(float muac) {
        String muacCat = this.muacCat(muac);
        int resId = this.context.getResources().getIdentifier("muac_" + muacCat, "string", this.context.getPackageName());
        return this.context.getString(resId);
    }

    /**
     * Get expected pregnancy weight gain based on BMI
     * @param bmi - float value of Body Mass Index
     * @return - String of expected weight gain in kg
     */
    public String expWeightGain(float bmi) {
        String weightCat = this.weightCat(bmi);
        if (weightCat.equals("underweight")) return "12.5 - 18";
        if (weightCat.equals("normal")) return "11.5 - 16";
        if (weightCat.equals("overweight")) return "7 - 11.5";
        return "5 - 9";
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
            String key = ANCFormUtils.removeKeyPrefix(widget, step);
            if (mJsonObject.has(step)) {
                JSONObject stepsObject = mJsonObject.getJSONObject(step);
                JSONArray fields = stepsObject.getJSONArray(ANCJsonFormConstants.FIELDS);
                if (fields.length() > 1)
                    for (int i = 0; i < fields.length(); i++) {
                        JSONObject accordionObject = fields.getJSONObject(i);
                        if (accordionObject.getString(ANCJsonFormConstants.KEY).equals(accordion)) {
                            JSONArray value = accordionObject.optJSONArray(ANCJsonFormConstants.VALUE);
                            if (value == null) {
                                return result;
                            }
                            result = ANCFormUtils.obtainValue(key, value);
                            break;
                        }
                    }
            }
        }
        return result;
    }

    public boolean compareDateAgainstContactDate(String firstDate, String contactDate) throws ParseException {
        int comparisonValue = compareTwoDates(firstDate, convertContactDateToTestDate(contactDate));
        boolean isLessOrEqual = false;
        if (comparisonValue == -1 || comparisonValue == 0) {
            isLessOrEqual = true;
        }
        return isLessOrEqual;
    }

    /**
     * Given two dates compare if they are equal
     *
     * @param firstDate  the first date entered
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

    public String convertContactDateToTestDate(String contactDate) throws ParseException {
        String convertedContactDate = "";
        if (!TextUtils.isEmpty(contactDate)) {
            Date lastContactDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(contactDate);
            if (lastContactDate != null) {
                convertedContactDate =
                        new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(lastContactDate);
            }
        }
        return convertedContactDate;
    }

    /**
     * Gets the dates string and the duration to be added then does a date comparison with the current date
     *
     * @param dateString {@link String}
     * @param duration   {@link String}
     * @return comparison {@link Integer}
     */
    public int compareDateWithDurationsAddedAgainstToday(String dateString, String duration) {
        return compareDateAgainstToday(addDuration(dateString, duration));
    }

    /**
     * Compares date against today's date
     *
     * @param theDate passed as first date to first date
     * @return -1 if date is before today, 0 if equal, 1 if date is greater than today's date and -2
     * otherwise
     */
    public int compareDateAgainstToday(String theDate) {
        return compareTwoDates(theDate, (new LocalDate()).toString(FormUtils.NATIIVE_FORM_DATE_FORMAT_PATTERN));
    }

    /**
     * Calculate weeks and days from days integer.
     */
    public String getWeeksAndDaysFromDays(Integer days) {
        double weeks = Math.round(Math.floor(days / 7));
        Integer dayz = days % 7;

        return String.format(this.context.getString(R.string.lmp_gest_age_format), weeks, dayz);
    }

    /**
     * Add weeks string to a number.
     * @return
     */
    public String getWeeksStringFromInteger(Integer weeks) {
        return String.format(this.context.getString(R.string.ga_weeks), weeks.toString());
    }

    public String getBooleanString() {
        return "HAI";
    }

    public String getPositiveNegativeString(String value) {
        if (value == "positive") {
            return this.context.getString(R.string.value_positive);
        }
        return this.context.getString(R.string.value_negative);
    }

}
