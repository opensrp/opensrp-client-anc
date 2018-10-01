package org.smartregister.anc.util;

import org.smartregister.anc.BuildConfig;

/**
 * Created by ndegwamartin on 14/03/2018.
 */

public abstract class Constants {
    public static final String SQLITE_DATE_TIME_FORMAT = "yyyy-MM-dd";
    public static final int OPENMRS_UNIQUE_ID_INITIAL_BATCH_SIZE = BuildConfig.OPENMRS_UNIQUE_ID_INITIAL_BATCH_SIZE;
    public static final int OPENMRS_UNIQUE_ID_BATCH_SIZE = BuildConfig.OPENMRS_UNIQUE_ID_BATCH_SIZE;
    public static final int OPENMRS_UNIQUE_ID_SOURCE = BuildConfig.OPENMRS_UNIQUE_ID_SOURCE;

    public static final long MAX_SERVER_TIME_DIFFERENCE = BuildConfig.MAX_SERVER_TIME_DIFFERENCE;
    public static final String VIEW_CONFIGURATION_PREFIX = "ViewConfiguration_";

    public static final boolean TIME_CHECK = BuildConfig.TIME_CHECK;
    public static final String CURRENT_LOCATION_ID = "CURRENT_LOCATION_ID";

    public static final String LAST_SYNC_TIMESTAMP = "LAST_SYNC_TIMESTAMP";
    public static final String LAST_CHECK_TIMESTAMP = "LAST_SYNC_CHECK_TIMESTAMP";

    public static final String GLOBAL_IDENTIFIER = "identifier";
    public static final String ANC_ID = "ANC_ID";

    public static class CONFIGURATION {
        public static final String LOGIN = "login";
        public static final String HOME_REGISTER = "home_register";
    }

    public static class SITE_CHARACTERISTICS_KEY {
        public static final String IPV_ASSESS = "site_ipv_assess";
        public static final String HIV = "site_anc_hiv";
        public static final String ULTRASOUND = "site_ultrasound";
        public static final String BP_TOOL = "site_bp_tool";
    }

    public static final class EventType {
        public static final String REGISTRATION = "ANC Registration";
        public static final String UPDATE_REGISTRATION = "Update ANC Registration";
        public static final String QUICK_CHECK = "Quick Check";
        public static final String CLOSE = "ANC Close";
        public static final String SITE_CHARACTERISTICS = "Site Characteristics";
    }

    public static class JSON_FORM {
        public static final String ANC_REGISTER = "anc_register";
        public static final String ANC_CLOSE = "anc_close";
        public static final String ANC_PROFILE = "anc_profile";
        public static final String ANC_SYMPTOMS_FOLLOW_UP = "anc_symptoms_follow_up";
        public static final String ANC_PHYSICAL_EXAM = "anc_physical_exam";
        public static final String ANC_TEST = "anc_test";
        public static final String ANC_COUNSELLING_TREATMENT = "anc_counselling_treatment";
        public static final String ANC_SITE_CHARACTERISTICS = "anc_site_characteristics";
    }

    public static class JSON_FORM_KEY {
        public static final String ENTITY_ID = "entity_id";
        public static final String OPTIONS = "options";
        public static final String ENCOUNTER_LOCATION = "encounter_location";
        public static final String ATTRIBUTES = "attributes";
        public static final String DEATH_DATE = "deathdate";
        public static final String DEATH_DATE_APPROX = "deathdateApprox";
        public static final String ANC_CLOSE_REASON = "anc_close_reason";

    }

    public static class JSON_FORM_EXTRA {
        public static final String CONTACT = "contact";
        public static final String JSON = "json";

    }

    public static class PREF_KEY {
        public static final String SITE_CHARACTERISTICS = "site_characteristics";
        public static final String POPULATION_CHARACTERISTICS = "population_characteristics";

    }

    public static final class ServiceType {
        public static final int AUTO_SYNC = 1;
        public static final int PULL_UNIQUE_IDS = 4;
        public static final int IMAGE_UPLOAD = 8;
        public static final int PULL_VIEW_CONFIGURATIONS = 9;

    }

    public static final class KEY {
        public static final String SEX = "Sex";
        public static final String KEY = "key";
        public static final String VALUE = "value";
        public static final String TREE = "tree";
        public static final String DEFAULT = "default";
        public static final String PHOTO = "photo";
        public static final String TYPE = "type";
        public static final String LABEL = "label";
        public static final String DESCRIPTION = "description";
        public static final String SETTING_CONFIGURATIONS = "settingConfigurations";
        public static final String VALIDATED_RECORDS =  "validated_records";

    }

    public static final class INTENT_KEY {
        public static final String BASE_ENTITY_ID = "base_entity_id";
        public static final String JSON = "json";
        public static final String WHO_ANC_ID = "who_anc_id";
        public static final String TO_RESCHEDULE = "to_reschedule";
        public static final String IS_REMOTE_LOGIN = "is_remote_login";
    }

    public static class OPENMRS {
        public static final String ENTITY = "openmrs_entity";
        public static final String ENTITY_ID = "openmrs_entity_id";
    }

    public static class ENTITY {
        public static final String PERSON = "person";
    }

    public static class BOOLEAN_INT {
        public static final int TRUE = 1;
    }

    public static final class SyncFilters {

        public static final String FILTER_TEAM_ID = "teamId";
    }
    public static  final class DUMMY_DATA{
        public static final String DUMMY_ENTITY_ID = "3342e64b-cafd-46ea-9fc7-0561acb7c8a1";
    }
}
