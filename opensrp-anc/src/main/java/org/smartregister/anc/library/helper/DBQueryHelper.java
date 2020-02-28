package org.smartregister.anc.library.helper;

import org.smartregister.anc.library.util.DBConstantsUtils;

/**
 * Created by ndegwamartin on 28/01/2018.
 */

public class DBQueryHelper {
    public static final String getHomePatientRegisterCondition() {
        return DBConstantsUtils.KeyUtils.DATE_REMOVED + " IS NULL";
    }
}
