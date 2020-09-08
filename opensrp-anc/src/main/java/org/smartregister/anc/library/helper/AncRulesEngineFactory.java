package org.smartregister.anc.library.helper;

import android.content.Context;

import com.vijay.jsonwizard.rules.RuleConstant;
import com.vijay.jsonwizard.rules.RulesEngineFactory;

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rule;
import org.json.JSONObject;

import java.util.Map;

public class AncRulesEngineFactory extends RulesEngineFactory {
    private AncRulesEngineHelper ancRulesEngineHelper;
    private String selectedRuleName;
    private Facts globalFacts;


    public AncRulesEngineFactory(Context context, Map<String, String> globalValues, JSONObject mJSONObject) {
        super(context, globalValues);
        this.ancRulesEngineHelper = new AncRulesEngineHelper(context);
        this.ancRulesEngineHelper.setJsonObject(mJSONObject);

        if (globalValues != null) {
            globalFacts = new Facts();
            for (Map.Entry<String, String> entry : globalValues.entrySet()) {
                globalFacts.put(RuleConstant.PREFIX.GLOBAL + entry.getKey(), getValue(entry.getValue()));
            }
        }
    }

    @Override
    protected Facts initializeFacts(Facts facts) {
        if (globalFacts != null) {
            facts.asMap().putAll(globalFacts.asMap());
        }
        selectedRuleName = facts.get(RuleConstant.SELECTED_RULE);
        facts.put("helper", ancRulesEngineHelper);
        return facts;
    }

    @Override
    public boolean beforeEvaluate(Rule rule, Facts facts) {
        return selectedRuleName != null && selectedRuleName.equals(rule.getName());
    }
}
