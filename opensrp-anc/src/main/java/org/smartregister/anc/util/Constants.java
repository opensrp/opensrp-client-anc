package org.smartregister.anc.util;

import org.smartregister.anc.BuildConfig;

/**
 * Created by ndegwamartin on 14/03/2018.
 */

public abstract class Constants {
	public static final String SQLITE_DATE_TIME_FORMAT = "yyyy-MM-dd";
	public static final String CONTACT_DATE_FORMAT = "dd/MM/yyyy";
	public static final String CONTACT_SUMMARY_DATE_FORMAT = "dd MMMM yyyy";
	public static final long MAX_SERVER_TIME_DIFFERENCE = BuildConfig.MAX_SERVER_TIME_DIFFERENCE;
	public static final String VIEW_CONFIGURATION_PREFIX = "ViewConfiguration_";
	public static final boolean TIME_CHECK = BuildConfig.TIME_CHECK;
	
	public static final String GLOBAL_IDENTIFIER = "identifier";
	public static final String ANC_ID = "ANC_ID";
	public static final int DELIVERY_DATE_WEEKS = 40;
	public static final String NATIVE_ACCORDION = "native_accordion";
	public static final String ANC_RADIO_BUTTON = "anc_radio_button";
	
	public static class ANC_RADIO_BUTTON_OPTION_TYPES {
		public static final String DONE_TODAY = "done_today";
		public static final String DONE_EARLIER = "done_earlier";
		public static final String ORDERED = "ordered";
		public static final String NOT_DONE = "not_done";
		
	}
	
	public static final String ACCORDION_INFO_TEXT = "accordion_info_text";
	public static final String ACCORDION_INFO_TITLE = "accordion_info_title";
	public static final String DISPLAY_BOTTOM_SECTION = "display_bottom_section";
	public static final String NEXT = "next";
	
	public static class CONFIGURATION {
		public static final String LOGIN = "login";
		public static final String HOME_REGISTER = "home_register";
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
	
	public static final class KEY {
		public static final String KEY = "key";
		public static final String VALUE = "value";
		public static final String TREE = "tree";
		public static final String DEFAULT = "default";
		public static final String PHOTO = "photo";
		public static final String TYPE = "type";
		
	}
	
	public static final class INTENT_KEY {
		public static final String BASE_ENTITY_ID = "base_entity_id";
		public static final String JSON = "json";
		public static final String CLIENT = "client";
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
	
	public static class DETAILS_KEY {
		public static final String CONTACT_SHEDULE = "contact_schedule";
		
	}
	
	public static final class ALERT_STATUS {
		public static final String DUE = "due";
		public static final String OVERDUE = "overdue";
		public static final String NOT_DUE = "not_due";
		public static final String DELIVERY_DUE = "delivery_due";
		public static final String IN_PROGRESS = "in_progress";
		public static final String EXPIRED = "expired";
	}
	
	public static class FILE_CATEGORY {
		public static final String PROFILE_PIC = "profilepic";
		
	}
	
}
