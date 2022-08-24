package org.smartregister.anc.library.util;

/**
 * Created by ndegwamartin on 14/03/2018.
 */

public abstract class ConstantsUtils {
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
    public static final String EXTENDED_RADIO_BUTTON = "extended_radio_button";
    public static final String DEFAULT_VALUES = "default_values";
    public static final String PREVIOUS_CONTACT_NO = "previous_contact_no";
    public static final String GLOBAL_PREVIOUS = "global_previous";
    public static final String GLOBAL = "global";
    public static final String REQUIRED_FIELDS = "required_fields";
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
    public static final String GEST_AGE_OPENMRS = "gest_age_openmrs";
    public static final String WOM_IMAGE = "wom_image";
    public static final String INDEX = "index";
    public static final String BOTTOM_SECTION = "bottom_section";
    public static final String DISPLAY_RECORD_BUTTON = "display_record_button";
    public static final String FILTER_OPTIONS_SOURCE = "filter_options_source";
    public static final String FILTER_OPTIONS = "filter_options";
    public static final String FILTERED_ITEMS = "filtered_items";
    public static final String PREVIOUS = "previous";
    public static final String IS_FILTERED = "is_filtered";
    public static final String CONTACT_DATE = "contact_date";
    public static final String CONTACT_SCHEDULE = "contact_schedule";
    public static final String ATTENTION_FLAG_FACTS = "attention_flag_facts";
    public static final String WEIGHT_GAIN = "weight_gain";
    public static final String PHYS_SYMPTOMS = "phys_symptoms";
    public static final String DATE_TODAY_HIDDEN = "date_today_hidden";
    public static final String AGE = "age";
    public static final String CONTACT_NO = "contact_no";
    public static final String OTHER_FOR = "other_for";
    public static final String OTHER = "other";
    public static final String CONTINUE = "Continue";
    public static final String DUE = "Due";
    public static final String DUE_BHASA = "Jatuh Tempo";
    public static final String OPEN_TEST_TASKS = "open_test_tasks";
    public static final String ANDROID_SWITCHER = "android:switcher:";
    public static final String IS_FIRST_CONTACT = "is_first_contact";


    public interface Properties {
        String CAN_SAVE_SITE_INITIAL_SETTING = "CAN_SAVE_INITIAL_SITE_SETTING";
        String MAX_CONTACT_SCHEDULE_DISPLAYED = "MAX_CONTACT_SCHEDULE_DISPLAYED";
        String DUE_CHECK_STRATEGY = "DUE_CHECK_STRATEGY";
        String WIDGET_VALUE_TRANSLATED = "widget.value.translated";
    }

    public interface DueCheckStrategy {
        String CHECK_FOR_FIRST_CONTACT = "check_for_first_contact";
    }

    public interface TemplateUtils {
        interface SiteCharacteristics {
            String TEAM_ID = "teamId";
            String TEAM = "team";
            String LOCATION_ID = "locationId";
            String PROVIDER_ID = "providerId";
        }
    }

    public static class AncRadioButtonOptionTypesUtils {
        public static final String DONE_TODAY = "done_today";
        public static final String DONE_EARLIER = "done_earlier";
        public static final String ORDERED = "ordered";
        public static final String NOT_DONE = "not_done";
        public static final String DONE = "done";
    }

    public static class AncRadioButtonOptionTextUtils {
        public static final String DONE_TODAY = "Dilakukan hari ini";
        public static final String DONE_EARLIER = "Pernah dilakukan";
        public static final String ORDERED = "Sedang dalam proses";
        public static final String NOT_DONE = "Tidak dilakukan";
        public static final String DONE = "Dilakukan";
    }

    public static class ConfigurationUtils {
        public static final String LOGIN = "login";
        public static final String HOME_REGISTER = "home_register";
    }

    public static class IdentifierUtils {
        public static final String ANC_ID = "ANC_ID";
    }

    public static final class EventTypeUtils {
        public static final String REGISTRATION = "ANC Registration";
        public static final String UPDATE_REGISTRATION = "Update ANC Registration";
        public static final String QUICK_CHECK = "Quick Check";
        public static final String CLOSE = "ANC Close";
        public static final String SITE_CHARACTERISTICS = "Site Characteristics";
        public static final String CONTACT_VISIT = "Contact Visit";
    }

    public static class JsonFormUtils {
        public static final String ANC_REGISTER = "anc_register";
        public static final String ANC_CLOSE = "anc_close";
        public static final String ANC_PROFILE = "anc_profile";
        public static final String ANC_PROFILE_ENCOUNTER_TYPE = "Profile";
        public static final String ANC_SYMPTOMS_FOLLOW_UP = "anc_symptoms_follow_up";
        public static final String ANC_PHYSICAL_EXAM = "anc_physical_exam";
        public static final String ANC_TEST = "anc_test";
        public static final String ANC_COUNSELLING_TREATMENT = "anc_counselling_treatment";
        public static final String ANC_TEST_ENCOUNTER_TYPE = "Tests";
        public static final String ANC_COUNSELLING_TREATMENT_ENCOUNTER_TYPE = "Counselling and Treatment";
        public static final String ANC_SITE_CHARACTERISTICS = "anc_site_characteristics";
        public static final String ANC_QUICK_CHECK = "anc_quick_check";
        public static final String ANC_TEST_TASKS = "anc_test_tasks";
        public static final String ANC_TEST_TASKS_ENCOUNTER_TYPE = "Contact Tasks";
    }

    public static class JsonFormKeyUtils {
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

        //
        public static final String ANC_ID = "anc_id";
        public static final String STEP1 = "step1";
        public static final String FIELDS = "fields";
        public static final String VILLAGE = "village";
        public static final String PREVIOUS_VISITS = "previous_visits";
        public static final String VISIT_DATE = "visit_date";
        public static final String PREVIOUS_VISITS_MAP = "previous_visits_map";
    }

    public static class JsonFormExtraUtils {
        public static final String CONTACT = "contact";
        public static final String JSON = "json";
    }

    public static class PrefKeyUtils {
        public static final String SITE_CHARACTERISTICS = "site_characteristics";
        public static final String POPULATION_CHARACTERISTICS = "population_characteristics";
        public static String FORM_INVISIBLE_REQUIRED_FIELDS = "anc.invisible.req.fields";
    }

    public static final class KeyUtils {
        public static final String KEY = "key";
        public static final String VALUE = "value";
        public static final String TYPE = "type";
        public static final String TREE = "tree";
        public static final String DEFAULT = "default";
        public static final String PHOTO = "photo";
        public static final String AGE_ENTERED = "age_entered";
        public static final String STEP = "step";
        public static final String FORM = "form";
        public static final String CONTACT_NO = "contact_no";
        public static final String LAST_CONTACT_DATE = "last_contact_date";
        public static final String SECONDARY_VALUES = "secondary_values";
        public static final String PARENT_SECONDARY_KEY = "parent_secondary_key";
    }

    public static final class IntentKeyUtils {
        public static final String BASE_ENTITY_ID = "base_entity_id";
        public static final String JSON = "json";
        public static final String TO_RESCHEDULE = "to_reschedule";
        public static final String IS_REMOTE_LOGIN = "is_remote_login";
        public static final String CONTACT_NO = "contact_number";
        public static final String FORM_NAME = "form_name";
        public static final String CLIENT_MAP = "client_map";
        public static final String UNDONE_VALUES = "undone_values";
        public static final String LIBRARY_HEADER = "library_header";
    }

    public static class DetailsKeyUtils {
        public static final String CONTACT_SCHEDULE = "contact_schedule";
        public static final String ATTENTION_FLAG_FACTS = "attention_flag_facts";
        public static final String PREVIOUS_CONTACTS = "previous_contacts";
        public static final String OPEN_TEST_TASKS = "open_test_tasks";
    }

    public static final class AlertStatusUtils {
        public static final String DUE = "due";
        public static final String OVERDUE = "overdue";
        public static final String NOT_DUE = "not_due";
        public static final String DELIVERY_DUE = "delivery_due";
        public static final String IN_PROGRESS = "in_progress";
        public static final String EXPIRED = "expired";
        public static final String TODAY = "today";
        public static final String ACTIVE = "active";
    }

    public static final class PrescriptionUtils{
        public static final String VITA = "vita";
        public static final String MAG_CALC = "mag_calc";
        public static final String NAUSEA_PHARMA = "nausea_pharma";
        public static final String ALBEN_MEBEN = "alben_meben";
        public static final String ANTACID = "antacid";
        public static final String PENICILLIN = "penicillin";
        public static final String ANTIBIOTIC = "antibiotic";
        public static final String PREP = "prep";
        public static final String SP = "sp";
        public static final String IFA = "ifa";
        public static final String IFA_MEDICATION = "ifa_medication";
        public static final String ASPIRIN = "aspirin";
        public static final String CALCIUM = "calcium";
    }

    public static class FileCategoryUtils {
        public static final String PROFILE_PIC = "profilepic";

    }

    public static class RulesFileUtils {
        public static final String CONTACT_RULES = "contact-rules.yml";
        public static final String ALERT_RULES = "alert-rules.yml";

    }

    public static class EcFileUtils {
        public static final String CLIENT_CLASSIFICATION = "ec_client_classification.json";
        public static final String CLIENT_FIELDS = "ec_client_fields.json";

    }

    public static class PrefixUtils {
        public static final String PREVIOUS = "previous_";
    }

    public static class SuffixUtils {
        public static final String VALUE = "_value";
        public static final String OTHER = "_other";
        public static final String ABNORMAL = "_abnormal";
        public static final String ABNORMAL_OTHER = ABNORMAL + OTHER;
    }

    public static class BooleanUtils {
        public static final String TRUE = "true";
    }

    public static class AttentionFlagUtils {
        public static final String RED = "red_attention_flag";
        public static final String YELLOW = "yellow_attention_flag";
    }

    public static class ClientUtils {
        public static final String ANC_ID = "ANC_ID";
    }

    public static class SettingsSyncParamsUtils {
        public static final String LOCATION_ID = "locationId";
        public static final String IDENTIFIER = "identifier";
    }
}
