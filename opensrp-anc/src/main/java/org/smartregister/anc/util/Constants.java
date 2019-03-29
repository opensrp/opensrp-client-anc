package org.smartregister.anc.util;

/**
 * Created by ndegwamartin on 14/03/2018.
 */

public abstract class Constants {
    public static final String SQLITE_DATE_TIME_FORMAT = "yyyy-MM-dd";
    public static final String CONTACT_DATE_FORMAT = "dd/MM/yyyy";
    public static final String CONTACT_SUMMARY_DATE_FORMAT = "dd MMMM yyyy";
    public static final String VIEW_CONFIGURATION_PREFIX = "ViewConfiguration_";
    public static final String FORM = "form";
    public static final String ACCORDION_INFO_TEXT = "accordion_info_text";
    public static final String ACCORDION_INFO_TITLE = "accordion_info_title";
    public static final String DISPLAY_BOTTOM_SECTION = "display_bottom_section";
    public static final String NEXT = "next";

    public static final String GLOBAL_IDENTIFIER = "identifier";
    public static final int DELIVERY_DATE_WEEKS = 40;
    public static final String EXPANSION_PANEL = "expansion_panel";
    public static final String ANC_RADIO_BUTTON = "anc_radio_button";
    public static final String DEFAULT_VALUES = "default_values";
    public static final String PREVIOUS_CONTACT_NO = "previous_contact_no";
    public static final String GLOBAL_PREVIOUS = "global_previous";
    public static final String EDITABLE_FIELDS = "editable_fields";
    public static final String FALSE = "false";
    public static final String DANGER_SIGNS = "danger_signs";
    public static final String DANGER_NONE = "danger_none";
    public static final String CALL = "Call";
    public static final String START_CONTACT = "Start Contact";
    public static final String CONTINUE_CONTACT = "Continue Contact";
    public static final String ORIGIN = "origin";
    public static final String FIRST_NAME = "firstName";
    public static final String LAST_NAME = "lastName";
    public static final String EDD = "edd";
    public static final String DOB = "dob";
    public static final String PHONE_NUMBER = "phoneNumber";
    public static final String ALT_CONTACT_NAME = "altContactName";
    public static final String CONTACT = "Contact";
    public static final String CURRENT_OPENSRP_ID = "current_opensrp_id";
    public static final String FORM_SUBMISSION_IDS = "form_submission_ids";
    public static final String REFERRAL = "referral";
    public static final String GEST_AGE = "gest_age";
    public static final String WOM_IMAGE = "wom_image";

    public static class ANC_RADIO_BUTTON_OPTION_TYPES {
        public static final String DONE_TODAY = "done_today";
        public static final String DONE_EARLIER = "done_earlier";
        public static final String ORDERED = "ordered";
        public static final String NOT_DONE = "not_done";
        public static final String DONE = "done";

    }

    public static class ANC_RADIO_BUTTON_OPTION_TEXT {
        public static final String DONE_TODAY = "Done today";
        public static final String DONE_EARLIER = "Done earlier";
        public static final String ORDERED = "Ordered";
        public static final String NOT_DONE = "Not done";
        public static final String DONE = "Done";

    }

    public static class CONFIGURATION {
        public static final String LOGIN = "login";
        public static final String HOME_REGISTER = "home_register";
    }


    public static class IDENTIFIER {
        public static final String ANC_ID = "ANC_ID";
    }

    public static final class EventType {
        public static final String REGISTRATION = "ANC Registration";
        public static final String UPDATE_REGISTRATION = "Update ANC Registration";
        public static final String QUICK_CHECK = "Quick Check";
        public static final String CLOSE = "ANC Close";
        public static final String SITE_CHARACTERISTICS = "Site Characteristics";
        public static final String CONTACT_VISIT = "Contact Visit";
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
        public static final String ANC_QUICK_CHECK = "anc_quick_check";
    }

    public static class JSON_FORM_KEY {
        public static final String ENTITY_ID = "entity_id";
        public static final String OPTIONS = "options";
        public static final String ENCOUNTER_LOCATION = "encounter_location";
        public static final String ENCOUNTER_TYPE = "encounter_type";
        public static final String ATTRIBUTES = "attributes";
        public static final String DEATH_DATE = "deathdate";
        public static final String DEATH_DATE_APPROX = "deathdateApprox";
        public static final String ANC_CLOSE_REASON = "anc_close_reason";
        public static final String DOB_ENTERED = "dob_entered";
        public static final String AGE_ENTERED = "age_entered";

    }

    public static class JSON_FORM_EXTRA {
        public static final String CONTACT = "contact";
        public static final String JSON = "json";

    }

    public static class PREF_KEY {
        public static final String SITE_CHARACTERISTICS = "site_characteristics";
        public static final String POPULATION_CHARACTERISTICS = "population_characteristics";

    }

    public static final class KEY {
        public static final String KEY = "key";
        public static final String VALUE = "value";
        public static final String TREE = "tree";
        public static final String DEFAULT = "default";
        public static final String PHOTO = "photo";
        public static final String AGE_ENTERED = "age_entered";
        public static final String STEP = "step";
        public static final String TYPE = "type";
        public static final String FORM = "form";
        public static final String CONTACT_NO = "contact_no";
        public static final String LAST_CONTACT_DATE = "last_contact_date";
        public static final String SECONDARY_VALUES = "secondary_values";
        public static final String PARENT_SECONDARY_KEY = "parent_secondary_key";

    }

    public static final class INTENT_KEY {
        public static final String BASE_ENTITY_ID = "base_entity_id";
        public static final String JSON = "json";
        public static final String TO_RESCHEDULE = "to_reschedule";
        public static final String IS_REMOTE_LOGIN = "is_remote_login";
        public static final String CONTACT_NO = "contact_number";
        public static final String FORM_NAME = "form_name";
        public static final String CLIENT_MAP = "client_map";
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

    public static class DETAILS_KEY {
        public static final String CONTACT_SHEDULE = "contact_schedule";
        public static final String ATTENTION_FLAG_FACTS = "attention_flag_facts";
        public static final String PREVIOUS_CONTACTS = "previous_contacts";

    }

    public static final class ALERT_STATUS {
        public static final String DUE = "due";
        public static final String OVERDUE = "overdue";
        public static final String NOT_DUE = "not_due";
        public static final String DELIVERY_DUE = "delivery_due";
        public static final String IN_PROGRESS = "in_progress";
        public static final String EXPIRED = "expired";
        public static final String TODAY = "today";
        public static final String ACTIVE = "active";
    }

    public static class FILE_CATEGORY {
        public static final String PROFILE_PIC = "profilepic";

    }

    public static class RULES_FILE {
        public static final String CONTACT_RULES = "contact-rules.yml";
        public static final String ALERT_RULES = "alert-rules.yml";

    }

    public static class EC_FILE {
        public static final String CLIENT_CLASSIFICATION = "ec_client_classification.json";
        public static final String CLIENT_FIELDS = "ec_client_fields.json";

    }

    public static class JSON_FORM_CONSTANTS {
        public static final String CONTACT_CONTAINER = "container";
    }

    public static class PREFIX {
        public static final String PREVIOUS = "previous_";
    }

    public static class SUFFIX {
        public static final String VALUE = "_value";
        public static final String OTHER = "_other";
        public static final String ABNORMAL = "_abnormal";
        public static final String ABNORMAL_OTHER = ABNORMAL + OTHER;

    }

    public static class BOOLEAN {
        public static final String TRUE = "true";
    }

    public static class ATTENTION_FLAG {
        public static final String RED = "red_attention_flag";
        public static final String YELLOW = "yellow_attention_flag";
    }
}
