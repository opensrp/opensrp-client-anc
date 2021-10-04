package org.smartregister.anc.library.constants;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.anc.library.R;

/**
 * Created by Muhammad Indra (indra@alcasha.com) at 03/10/2021
 * Helper to convert string weight into resource string.xml and use it in helper
 */
public enum IMTWeight {
    UNDERWEIGHT(R.string.underweight), NORMAL_WEIGHT(R.string.normal_weight), OVERWEIGHT(R.string.overweight), OBESE(R.string.obese);

    private final int resourceString;

    IMTWeight(int resourceString) {
        this.resourceString = resourceString;
    }

    public int getResourceString() {
        return resourceString;
    }

    public static IMTWeight fromString(String input) {
        input = StringUtils.normalizeSpace(input.toUpperCase()).replaceAll(" ", "_");
        return IMTWeight.valueOf(input);
    }
}
