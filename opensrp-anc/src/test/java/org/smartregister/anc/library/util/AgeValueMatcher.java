package org.smartregister.anc.library.util;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.json.JSONException;
import org.json.JSONObject;
import org.skyscreamer.jsonassert.ValueMatcher;

/**
 * Created by ndegwamartin on 09/02/2019.
 */
public class AgeValueMatcher implements ValueMatcher {
    @Override
    public boolean equal(Object o1, Object o2) {
        try {
            return ((JSONObject) o1).get(JsonFormConstants.VALUE).toString().equals(((JSONObject) o2).get(JsonFormConstants.VALUE).toString());
        } catch (JSONException e) {
            return false;
        }
    }
}
