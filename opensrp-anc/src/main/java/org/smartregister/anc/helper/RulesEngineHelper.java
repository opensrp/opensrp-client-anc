package org.smartregister.anc.helper;

import android.content.Context;
import android.util.Log;

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.jeasy.rules.core.DefaultRulesEngine;
import org.jeasy.rules.core.InferenceRulesEngine;
import org.jeasy.rules.core.RulesEngineParameters;
import org.jeasy.rules.mvel.MVELRuleFactory;
import org.smartregister.anc.rule.AlertRule;
import org.smartregister.anc.rule.ContactRule;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RulesEngineHelper {
    private Context context;
    private RulesEngine inferentialRulesEngine;
    private RulesEngine defaultRulesEngine;
    private Map<String, Rules> ruleMap;
    private final String RULE_FOLDER_PATH = "rule/";
    private final String CONFIG_FOLDER_PATH = "config/";
    private Yaml yaml;

    public RulesEngineHelper(Context context) {
        this.context = context;
        this.inferentialRulesEngine = new InferenceRulesEngine();
        RulesEngineParameters parameters = new RulesEngineParameters().skipOnFirstAppliedRule(true);
        this.defaultRulesEngine = new DefaultRulesEngine(parameters);
        this.ruleMap = new HashMap<>();

        yaml = new Yaml();

    }

    private Rules getRulesFromAsset(String fileName) {
        try {
            if (!ruleMap.containsKey(fileName)) {

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(context.getAssets().open(fileName)));
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


    public Iterable<Object> readYaml(String filename) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(context.getAssets().open((CONFIG_FOLDER_PATH + filename)));
        return yaml.loadAll(inputStreamReader);
    }
}
