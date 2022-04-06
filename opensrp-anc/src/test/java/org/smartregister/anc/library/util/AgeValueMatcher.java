package org.smartregister.anc.library.util;

import org.smartregister.anc.library.constants.ANCJsonFormConstants;

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
            return ((JSONObject) o1).get(ANCJsonFormConstants.VALUE).toString().equals(((JSONObject) o2).get(ANCJsonFormConstants.VALUE).toString());
        } catch (JSONException e) {
            return false;
        }
    }
}
