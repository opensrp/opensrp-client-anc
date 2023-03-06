package org.smartregister.anc.library.helper;

import android.content.Context;

import com.vijay.jsonwizard.rules.RuleConstant;
import com.vijay.jsonwizard.rules.RulesEngineFactory;

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rule;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

public class AncRulesEngineFactory extends RulesEngineFactory {
    private Map<String, String> globalValues;
    private AncRulesEngineHelper ancRulesEngineHelper;
    private String selectedRuleName;


    public AncRulesEngineFactory(Context context, Map<String, String> globalValues, JSONObject mJSONObject) {
        super(context, globalValues);
        this.ancRulesEngineHelper = new AncRulesEngineHelper(context);
        this.ancRulesEngineHelper.setJsonObject(mJSONObject);
        this.globalValues = globalValues;
    }

    @Override
    protected Facts initializeFacts(Facts facts) {
        if (globalValues != null) {
            for (Map.Entry<String, String> entry : globalValues.entrySet()) {
                facts.put(RuleConstant.PREFIX.GLOBAL + entry.getKey(), getValue(entry.getValue()));
            }

            facts.asMap().putAll(globalValues);
        }

        selectedRuleName = facts.get(RuleConstant.SELECTED_RULE);

        facts.put("helper", ancRulesEngineHelper);
        return facts;
    }

    @Override
    public boolean beforeEvaluate(Rule rule, Facts facts) {
        return selectedRuleName != null && selectedRuleName.equals(rule.getName());
    }

    @Override
    public void beforeExecute(Rule rule, Facts facts) {
        super.beforeExecute(rule, facts);

        Timber.e("Putting facts in beforeExecute");
        HashMap<String, Object> myMap = new HashMap<>();
        facts.put("facts", myMap);
    }

    @Override
    public void onSuccess(Rule rule, Facts facts) {
        super.onSuccess(rule, facts);

        Timber.e("Putting facts in onSuccess  ");
        HashMap<String, Object> myMap = facts.get("facts");

        for (String key :
                myMap.keySet()) {
            facts.put(key, myMap.get(key));
        }

        facts.remove("facts");
    }

    @Override
    public void onFailure(Rule rule, Facts facts, Exception exception) {
        super.onFailure(rule, facts, exception);

        Timber.e("Putting facts in onFailure");
        facts.remove("facts");
    }
}
