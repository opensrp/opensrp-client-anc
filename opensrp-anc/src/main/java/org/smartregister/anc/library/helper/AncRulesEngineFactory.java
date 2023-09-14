package org.smartregister.anc.library.helper;

import android.content.Context;


import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.rules.RulesEngineFactory;

import org.jeasy.rules.api.Facts;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

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
        return extractValues(facts);
    }

    /*
     *  Extract value from JSON
     */
    protected Facts extractValues(Facts facts) {
        try {
            for (String key : facts.asMap().keySet()) {
                Object value = facts.get(key);
                if (value instanceof String) {
                    if (((String) value).startsWith("{") && ((String) value).endsWith("}")) {
                        String extractedValue = new JSONObject(((String) value)).getString(JsonFormConstants.VALUE);
                        facts.put(key, extractedValue);
                    } else {
                        facts.put(key, value);
                    }
                } else if (value instanceof List) {
                    JSONArray valuesArray = new JSONArray(value.toString());
                    ArrayList<String> updatedArray = new ArrayList<>();
                    for (int i = 0; i < valuesArray.length(); i++) {
                        String keyVal = valuesArray.optString(i);
                        if (keyVal.startsWith("{") && keyVal.endsWith("}")) {
                            String extractedValue = new JSONObject(keyVal).getString(JsonFormConstants.VALUE);
                            updatedArray.add(extractedValue);
                        } else {
                            updatedArray.add(keyVal);
                        }
                    }
                    facts.put(key, updatedArray);
                }
            }
        } catch (Exception e) {
            Timber.e(e);
        }
        return facts;
    }

}
