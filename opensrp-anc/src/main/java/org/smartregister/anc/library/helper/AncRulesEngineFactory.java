package org.smartregister.anc.library.helper;

import android.content.Context;


import com.vijay.jsonwizard.rules.RulesEngineFactory;

import org.jeasy.rules.api.Facts;
import org.json.JSONObject;

import java.util.Map;

public class AncRulesEngineFactory extends RulesEngineFactory {

    private final AncRulesEngineHelper ancRulesEngineHelper;


    public AncRulesEngineFactory(Context context, Map<String, String> globalValues, JSONObject mJSONObject) {
        super(context, globalValues);
        this.ancRulesEngineHelper = new AncRulesEngineHelper(context);
        this.ancRulesEngineHelper.setJsonObject(mJSONObject);

    }

    @Override
    protected Facts initializeFacts(Facts facts) {
        super.initializeFacts(facts);
        facts.put("helper", ancRulesEngineHelper);
        return facts;
    }

}
