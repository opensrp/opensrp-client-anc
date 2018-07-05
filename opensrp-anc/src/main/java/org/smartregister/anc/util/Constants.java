package org.smartregister.anc.util;

import org.smartregister.anc.BuildConfig;

/**
 * Created by ndegwamartin on 14/03/2018.
 */

public class Constants {
    public static final int OPENMRS_UNIQUE_ID_INITIAL_BATCH_SIZE = BuildConfig.OPENMRS_UNIQUE_ID_INITIAL_BATCH_SIZE;
    public static final int OPENMRS_UNIQUE_ID_BATCH_SIZE = BuildConfig.OPENMRS_UNIQUE_ID_BATCH_SIZE;
    public static final int OPENMRS_UNIQUE_ID_SOURCE = BuildConfig.OPENMRS_UNIQUE_ID_SOURCE;

    public static final String IS_REMOTE_LOGIN = "is_remote_login";
    public static final long MAX_SERVER_TIME_DIFFERENCE = BuildConfig.MAX_SERVER_TIME_DIFFERENCE;
    public static final String VIEW_CONFIGURATION_PREFIX = "ViewConfiguration_";

    public static final boolean TIME_CHECK = BuildConfig.TIME_CHECK;
    public static final String CURRENT_LOCATION_ID = "CURRENT_LOCATION_ID";

    public static class CONFIGURATION {
        public static final String LOGIN = "login";
        public static final String HOME_REGISTER = "home_register";
    }

    public static final class EventType {
        public static final String REGISTRATION = "ANC Registration";
        public static final String UPDATE_REGISTRATION = "Update Registration";
        public static final String CLOSE = "ANC Close";
    }

    public static class JSON_FORM {
        public static final String ANC_REGISTRATION = "anc_register";
        public static final String ANC_CLOSE = "anc_close";
    }

    public static final class ServiceType {

        public static final int AUTO_SYNC = 1;
        public static final int PULL_UNIQUE_IDS = 4;
        public static final int IMAGE_UPLOAD = 8;
        public static final int PULL_VIEW_CONFIGURATIONS = 9;

    }

}
