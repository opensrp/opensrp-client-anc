package org.smartregister.anc.library.helper;

import org.smartregister.anc.library.util.DBConstants;

/**
 * Created by ndegwamartin on 28/01/2018.
 */

public class DBQueryHelper {

    public static final String getHomePatientRegisterCondition() {
        return DBConstants.KEY.DATE_REMOVED + " IS NULL ";
    }
}
