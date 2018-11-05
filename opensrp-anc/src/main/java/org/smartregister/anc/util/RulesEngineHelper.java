package org.smartregister.anc.util;

import android.content.Context;
import android.util.Log;

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.jeasy.rules.core.InferenceRulesEngine;
import org.jeasy.rules.mvel.MVELRuleFactory;
import org.smartregister.anc.rule.ContactRule;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class RulesEngineHelper {
    private Context context;
    private RulesEngine rulesEngine;

    public RulesEngineHelper(Context context) {
        this.context = context;
        this.rulesEngine = new InferenceRulesEngine();

    }

    public List<Integer> contactRules(ContactRule contactRule, String rulesFile) {

        Facts facts = new Facts();
        facts.put("contactRule", contactRule);

        Rules rules = getRulesFromAsset("rule/" + rulesFile);
        if (rules == null) {
            return null;
        }

        processRules(rules, facts);

        Set<Integer> contactList = contactRule.set;
        List<Integer> list = new ArrayList<>(contactList);
        Collections.sort(list);

        return list;
    }

    private Rules getRulesFromAsset(String fileName) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(context.getAssets().open(fileName)));
            return MVELRuleFactory.createRulesFrom(br);
        } catch (IOException e) {
            Log.e(ContactRule.class.getName(), e.getMessage(), e);
            return null;
        }
    }

    protected void processRules(Rules rules, Facts facts) {

        rulesEngine.fire(rules, facts);
    }
}
