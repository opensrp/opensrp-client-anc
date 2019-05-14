package org.smartregister.anc.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.common.reflect.TypeToken;
import com.vijay.jsonwizard.activities.JsonFormActivity;
import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.jeasy.rules.api.Facts;
import org.joda.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.anc.BuildConfig;
import org.smartregister.anc.R;
import org.smartregister.anc.activity.EditJsonFormActivity;
import org.smartregister.anc.application.AncApplication;
import org.smartregister.anc.domain.YamlConfigItem;
import org.smartregister.anc.domain.YamlConfigWrapper;
import org.smartregister.anc.model.ContactSummaryModel;
import org.smartregister.anc.repository.PatientRepository;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.clientandeventmodel.FormEntityConstants;
import org.smartregister.domain.Photo;
import org.smartregister.domain.ProfileImage;
import org.smartregister.domain.form.FormLocation;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.exception.JsonFormMissingStepCountException;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.repository.ImageRepository;
import org.smartregister.util.AssetHandler;
import org.smartregister.util.FormUtils;
import org.smartregister.util.ImageUtils;
import org.smartregister.view.LocationPickerView;
import org.smartregister.view.activity.DrishtiApplication;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by keyman on 27/06/2018.
 */
public class JsonFormUtils extends org.smartregister.util.JsonFormUtils {
    public static final String METADATA = "metadata";
    public static final String ENCOUNTER_TYPE = "encounter_type";
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
    public static final SimpleDateFormat EDD_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    public static final String READ_ONLY = "read_only";
    public static final int REQUEST_CODE_GET_JSON = 3432;
    private static final String TAG = JsonFormUtils.class.getCanonicalName();

    public static JSONObject getFormAsJson(JSONObject form, String formName, String id, String currentLocationId)
    throws Exception {
        if (form == null) {
            return null;
        }

        String entityId = id;
        form.getJSONObject(METADATA).put(ENCOUNTER_LOCATION, currentLocationId);

        if (Constants.JSON_FORM.ANC_REGISTER.equals(formName)) {
            if (StringUtils.isNotBlank(entityId)) {
                entityId = entityId.replace("-", "");
            }

            // Inject opensrp id into the form
            JSONArray field = fields(form);
            JSONObject ancId = getFieldJSONObject(field, DBConstants.KEY.ANC_ID);
            if (ancId != null) {
                ancId.remove(JsonFormUtils.VALUE);
                ancId.put(JsonFormUtils.VALUE, entityId);
            }

        } else if (Constants.JSON_FORM.ANC_CLOSE.equals(formName)) {
            if (StringUtils.isNotBlank(entityId)) {
                // Inject entity id into the remove form
                form.remove(JsonFormUtils.ENTITY_ID);
                form.put(JsonFormUtils.ENTITY_ID, entityId);
            }
        } else {
            Log.w(TAG, "Unsupported form requested for launch " + formName);
        }
        Log.d(TAG, "form is " + form.toString());
        return form;
    }

    public static Triple<Boolean, JSONObject, JSONArray> validateParameters(String jsonString) {

        JSONObject jsonForm = toJSONObject(jsonString);
        JSONArray fields = fields(jsonForm);

        return Triple.of(jsonForm != null && fields != null, jsonForm, fields);
    }

    public static Pair<Client, Event> processRegistrationForm(AllSharedPreferences allSharedPreferences, String jsonString) {

        try {
            Triple<Boolean, JSONObject, JSONArray> registrationFormParams = validateParameters(jsonString);

            if (!registrationFormParams.getLeft()) {
                return null;
            }

            JSONObject jsonForm = registrationFormParams.getMiddle();
            JSONArray fields = registrationFormParams.getRight();

            String entityId = getString(jsonForm, ENTITY_ID);
            if (StringUtils.isBlank(entityId)) {
                entityId = generateRandomUUIDString();
            }

            String encounterType = getString(jsonForm, ENCOUNTER_TYPE);
            JSONObject metadata = getJSONObject(jsonForm, METADATA);

            // String lastLocationName = null;
            // String lastLocationId = null;
            // TODO Replace values for location questions with their corresponding location IDs


            JSONObject lastInteractedWith = new JSONObject();
            lastInteractedWith.put(Constants.KEY.KEY, DBConstants.KEY.LAST_INTERACTED_WITH);
            lastInteractedWith.put(Constants.KEY.VALUE, Calendar.getInstance().getTimeInMillis());
            fields.put(lastInteractedWith);

            JSONObject dobUnknownObject = getFieldJSONObject(fields, DBConstants.KEY.DOB_UNKNOWN);
            JSONArray options = getJSONArray(dobUnknownObject, Constants.JSON_FORM_KEY.OPTIONS);
            JSONObject option = getJSONObject(options, 0);
            String dobUnKnownString = option != null ? option.getString(VALUE) : null;

            if (StringUtils.isNotBlank(dobUnKnownString)) {
                dobUnknownObject.put(VALUE, Boolean.valueOf(dobUnKnownString) ? 1 : 0);
            }

            //initialize first contact values
            JSONObject nextContactJSONObject = getFieldJSONObject(fields, DBConstants.KEY.NEXT_CONTACT);
            if (nextContactJSONObject.has(JsonFormConstants.VALUE) &&
                    "".equals(nextContactJSONObject.getString(JsonFormConstants.VALUE))) {
                nextContactJSONObject.put(VALUE, 1);
            }

            JSONObject nextContactDateJSONObject = getFieldJSONObject(fields, DBConstants.KEY.NEXT_CONTACT_DATE);
            if (nextContactDateJSONObject.has(JsonFormConstants.VALUE) &&
                    "".equals(nextContactDateJSONObject.getString(JsonFormConstants.VALUE))) {
                nextContactDateJSONObject.put(VALUE, Utils.convertDateFormat(Calendar.getInstance().getTime(), Utils.DB_DF));
            }

            FormTag formTag = new FormTag();
            formTag.providerId = allSharedPreferences.fetchRegisteredANM();
            formTag.appVersion = BuildConfig.VERSION_CODE;
            formTag.databaseVersion = BuildConfig.DATABASE_VERSION;


            Client baseClient = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag, entityId);
            Event baseEvent = org.smartregister.util.JsonFormUtils
                    .createEvent(fields, metadata, formTag, entityId, encounterType, DBConstants.WOMAN_TABLE_NAME);

            JsonFormUtils.tagSyncMetadata(allSharedPreferences, baseEvent);// tag docs

            return Pair.create(baseClient, baseEvent);
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
            return null;
        }
    }

    public static void mergeAndSaveClient(Client baseClient) throws Exception {
        JSONObject updatedClientJson = new JSONObject(org.smartregister.util.JsonFormUtils.gson.toJson(baseClient));

        JSONObject originalClientJsonObject =
                AncApplication.getInstance().getEcSyncHelper().getClient(baseClient.getBaseEntityId());

        JSONObject mergedJson = org.smartregister.util.JsonFormUtils.merge(originalClientJsonObject, updatedClientJson);

        //TODO Save edit log ?

        AncApplication.getInstance().getEcSyncHelper().addClient(baseClient.getBaseEntityId(), mergedJson);
    }

    public static void saveImage(String providerId, String entityId, String imageLocation) {
        if (StringUtils.isBlank(imageLocation)) {
            return;
        }

        File file = FileUtil.createFileFromPath(imageLocation);

        if (!file.exists()) {
            return;
        }

        Bitmap compressedBitmap = AncApplication.getInstance().getCompressor().compressToBitmap(file);

        if (compressedBitmap == null || StringUtils.isBlank(providerId) || StringUtils.isBlank(entityId)) {
            return;
        }

        OutputStream os = null;
        try {

            if (entityId != null && !entityId.isEmpty()) {
                final String absoluteFileName = DrishtiApplication.getAppDir() + File.separator + entityId + ".JPEG";

                File outputFile = FileUtil.createFileFromPath(absoluteFileName);
                os = FileUtil.createFileOutputStream(outputFile);
                Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.JPEG;
                if (compressFormat != null) {
                    compressedBitmap.compress(compressFormat, 100, os);
                } else {
                    throw new IllegalArgumentException(
                            "Failed to save static image, could not retrieve image compression format from name " +
                                    absoluteFileName);
                }
                // insert into the db
                ProfileImage profileImage = new ProfileImage();
                profileImage.setImageid(UUID.randomUUID().toString());
                profileImage.setAnmId(providerId);
                profileImage.setEntityID(entityId);
                profileImage.setFilepath(absoluteFileName);
                profileImage.setFilecategory(Constants.FILE_CATEGORY.PROFILE_PIC);
                profileImage.setSyncStatus(ImageRepository.TYPE_Unsynced);
                ImageRepository imageRepo = AncApplication.getInstance().getContext().imageRepository();
                imageRepo.add(profileImage);
            }

        } catch (FileNotFoundException e) {
            Log.e(TAG, "Failed to save static image to disk");
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    Log.e(TAG, "Failed to close static images output stream after attempting to write image");
                }
            }
        }
    }

    public static String getString(String jsonString, String field) {
        return getString(toJSONObject(jsonString), field);
    }

    public static String getFieldValue(String jsonString, String key) {
        JSONObject jsonForm = toJSONObject(jsonString);
        if (jsonForm == null) {
            return null;
        }

        JSONArray fields = fields(jsonForm);
        if (fields == null) {
            return null;
        }

        return getFieldValue(fields, key);

    }

    public static JSONObject getFieldJSONObject(JSONArray jsonArray, String key) {
        if (jsonArray == null || jsonArray.length() == 0) {
            return null;
        }

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = getJSONObject(jsonArray, i);
            String keyVal = getString(jsonObject, KEY);
            if (keyVal != null && keyVal.equals(key)) {
                return jsonObject;
            }
        }
        return null;
    }

    private static LocationPickerView createLocationPickerView(Context context) {
        try {
            return new LocationPickerView(context);

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return null;
        }
    }

    public static String getAutoPopulatedJsonEditRegisterFormString(Context context, Map<String, String> womanClient) {
        try {
            JSONObject form = FormUtils.getInstance(context).getFormJson(Constants.JSON_FORM.ANC_REGISTER);
            LocationPickerView lpv = createLocationPickerView(context);
            if (lpv != null) {
                lpv.init();
            }
            JsonFormUtils.addWomanRegisterHierarchyQuestions(form);
            Log.d(TAG, "Form is " + form.toString());
            if (form != null) {
                form.put(JsonFormUtils.ENTITY_ID, womanClient.get(DBConstants.KEY.BASE_ENTITY_ID));
                form.put(JsonFormUtils.ENCOUNTER_TYPE, Constants.EventType.UPDATE_REGISTRATION);

                JSONObject metadata = form.getJSONObject(JsonFormUtils.METADATA);
                String lastLocationId =
                        lpv != null ? LocationHelper.getInstance().getOpenMrsLocationId(lpv.getSelectedItem()) : "";

                metadata.put(JsonFormUtils.ENCOUNTER_LOCATION, lastLocationId);

                form.put(Constants.CURRENT_OPENSRP_ID, womanClient.get(DBConstants.KEY.ANC_ID).replace("-", ""));

                //inject opensrp id into the form
                JSONObject stepOne = form.getJSONObject(JsonFormUtils.STEP1);
                JSONArray jsonArray = stepOne.getJSONArray(JsonFormUtils.FIELDS);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    processPopulatableFields(womanClient, jsonObject);

                }

                return form.toString();
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

        return "";
    }

    protected static void processPopulatableFields(Map<String, String> womanClient, JSONObject jsonObject)
    throws JSONException {

        if (jsonObject.getString(JsonFormUtils.KEY).equalsIgnoreCase(Constants.JSON_FORM_KEY.DOB_ENTERED)) {
            getDobUsingEdd(womanClient, jsonObject, DBConstants.KEY.DOB);

        } else if (jsonObject.getString(JsonFormUtils.KEY).equalsIgnoreCase(DBConstants.KEY.HOME_ADDRESS)) {
            String homeAddress = womanClient.get(DBConstants.KEY.HOME_ADDRESS);
            jsonObject.put(JsonFormUtils.VALUE, homeAddress);

        } else if (jsonObject.getString(JsonFormUtils.KEY).equalsIgnoreCase(Constants.WOM_IMAGE)) {
            getPhotoFieldValue(womanClient, jsonObject);
        } else if (jsonObject.getString(JsonFormUtils.KEY).equalsIgnoreCase(DBConstants.KEY.DOB_UNKNOWN)) {
            jsonObject.put(JsonFormUtils.READ_ONLY, false);
            JSONObject optionsObject = jsonObject.getJSONArray(Constants.JSON_FORM_KEY.OPTIONS).getJSONObject(0);
            optionsObject.put(JsonFormUtils.VALUE, womanClient.get(DBConstants.KEY.DOB_UNKNOWN));

        } else if (jsonObject.getString(JsonFormUtils.KEY).equalsIgnoreCase(Constants.KEY.AGE_ENTERED)) {
            jsonObject.put(JsonFormUtils.READ_ONLY, false);
            if (StringUtils.isNotBlank(womanClient.get(DBConstants.KEY.DOB))) {
                jsonObject.put(JsonFormUtils.VALUE, Utils.getAgeFromDate(womanClient.get(DBConstants.KEY.DOB)));
            }

        } else if (jsonObject.getString(JsonFormUtils.KEY).equalsIgnoreCase(DBConstants.KEY.EDD)) {
            formatEdd(womanClient, jsonObject, DBConstants.KEY.EDD);

        } else if (jsonObject.getString(JsonFormUtils.KEY).equalsIgnoreCase(DBConstants.KEY.ANC_ID)) {
            jsonObject.put(JsonFormUtils.VALUE, womanClient.get(DBConstants.KEY.ANC_ID).replace("-", ""));

        } else if (womanClient.containsKey(jsonObject.getString(JsonFormUtils.KEY))) {
            jsonObject.put(JsonFormUtils.READ_ONLY, false);
            jsonObject.put(JsonFormUtils.VALUE, womanClient.get(jsonObject.getString(JsonFormUtils.KEY)));
        } else {
            Log.e(TAG, "ERROR:: Unprocessed Form Object Key " + jsonObject.getString(JsonFormUtils.KEY));
        }
    }

    private static void getDobUsingEdd(Map<String, String> womanClient, JSONObject jsonObject, String birthDate)
    throws JSONException {
        String dobString = womanClient.get(birthDate);
        if (StringUtils.isNotBlank(dobString)) {
            Date dob = Utils.dobStringToDate(dobString);
            if (dob != null) {
                jsonObject.put(JsonFormUtils.VALUE, DATE_FORMAT.format(dob));
            }
        }
    }

    private static void formatEdd(Map<String, String> womanClient, JSONObject jsonObject, String eddDate)
    throws JSONException {
        String eddString = womanClient.get(eddDate);
        if (StringUtils.isNotBlank(eddString)) {
            Date edd = Utils.dobStringToDate(eddString);
            if (edd != null) {
                jsonObject.put(JsonFormUtils.VALUE, EDD_DATE_FORMAT.format(edd));
            }
        }
    }

    private static void getPhotoFieldValue(Map<String, String> womanClient, JSONObject jsonObject) throws JSONException {
        Photo photo = ImageUtils.profilePhotoByClientID(womanClient.get(DBConstants.KEY.BASE_ENTITY_ID),
                Utils.getProfileImageResourceIdentifier());

        if (photo != null && StringUtils.isNotBlank(photo.getFilePath())) {
            jsonObject.put(JsonFormUtils.VALUE, photo.getFilePath());

        }
    }

    public static void addWomanRegisterHierarchyQuestions(JSONObject form) {
        try {
            JSONArray questions = form.getJSONObject("step1").getJSONArray("fields");
            ArrayList<String> allLevels = new ArrayList<>();
            allLevels.add("Country");
            allLevels.add("Province");
            allLevels.add("District");
            allLevels.add("City/Town");
            allLevels.add("Health Facility");
            allLevels.add(Utils.HOME_ADDRESS);


            ArrayList<String> healthFacilities = new ArrayList<>();
            healthFacilities.add(Utils.HOME_ADDRESS);


            List<String> defaultFacility = LocationHelper.getInstance().generateDefaultLocationHierarchy(healthFacilities);
            List<FormLocation> upToFacilities =
                    LocationHelper.getInstance().generateLocationHierarchyTree(false, healthFacilities);

            String defaultFacilityString = AssetHandler.javaToJsonString(defaultFacility, new TypeToken<List<String>>() {
            }.getType());

            String upToFacilitiesString = AssetHandler.javaToJsonString(upToFacilities, new TypeToken<List<FormLocation>>() {
            }.getType());

            for (int i = 0; i < questions.length(); i++) {
                if (questions.getJSONObject(i).getString(Constants.KEY.KEY).equalsIgnoreCase(DBConstants.KEY.HOME_ADDRESS)) {
                    if (StringUtils.isNotBlank(upToFacilitiesString)) {
                        questions.getJSONObject(i).put(Constants.KEY.TREE, new JSONArray(upToFacilitiesString));
                    }
                    if (StringUtils.isNotBlank(defaultFacilityString)) {
                        questions.getJSONObject(i).put(Constants.KEY.DEFAULT, defaultFacilityString);
                    }
                }
            }

        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public static void startFormForEdit(Activity context, int jsonFormActivityRequestCode, String metaData) {
        Intent intent = new Intent(context, EditJsonFormActivity.class);
        intent.putExtra(Constants.INTENT_KEY.JSON, metaData);

        Log.d(TAG, "form is " + metaData);

        context.startActivityForResult(intent, jsonFormActivityRequestCode);

    }

    public static Triple<Boolean, Event, Event> saveRemovedFromANCRegister(AllSharedPreferences allSharedPreferences,
                                                                           String jsonString, String providerId) {

        try {

            boolean isDeath = false;
            Triple<Boolean, JSONObject, JSONArray> registrationFormParams = validateParameters(jsonString);

            if (!registrationFormParams.getLeft()) {
                return null;
            }

            JSONObject jsonForm = registrationFormParams.getMiddle();
            JSONArray fields = registrationFormParams.getRight();

            String encounterType = getString(jsonForm, ENCOUNTER_TYPE);
            JSONObject metadata = getJSONObject(jsonForm, METADATA);

            String encounterLocation = null;

            try {
                encounterLocation = metadata.getString(Constants.JSON_FORM_KEY.ENCOUNTER_LOCATION);
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage());
            }

            Date encounterDate = new Date();
            String entityId = getString(jsonForm, ENTITY_ID);

            Event event = (Event) new Event().withBaseEntityId(entityId) //should be different for main and subform
                    .withEventDate(encounterDate).withEventType(encounterType).withLocationId(encounterLocation)
                    .withProviderId(providerId).withEntityType(DBConstants.WOMAN_TABLE_NAME)
                    .withFormSubmissionId(generateRandomUUIDString()).withDateCreated(new Date());
            JsonFormUtils.tagSyncMetadata(allSharedPreferences, event);

            for (int i = 0; i < fields.length(); i++) {
                JSONObject jsonObject = getJSONObject(fields, i);

                String value = getString(jsonObject, VALUE);
                if (StringUtils.isNotBlank(value)) {
                    addObservation(event, jsonObject);
                    if (jsonObject.get(JsonFormUtils.KEY).equals(Constants.JSON_FORM_KEY.ANC_CLOSE_REASON)) {
                        isDeath = "Woman Died".equalsIgnoreCase(value);
                    }
                }
            }

            if (metadata != null) {
                Iterator<?> keys = metadata.keys();

                while (keys.hasNext()) {
                    String key = (String) keys.next();
                    JSONObject jsonObject = getJSONObject(metadata, key);
                    String value = getString(jsonObject, VALUE);
                    if (StringUtils.isNotBlank(value)) {
                        String entityVal = getString(jsonObject, OPENMRS_ENTITY);
                        if (entityVal != null) {
                            if (entityVal.equals(CONCEPT)) {
                                addToJSONObject(jsonObject, KEY, key);
                                addObservation(event, jsonObject);

                            } else if (entityVal.equals(ENCOUNTER)) {
                                String entityIdVal = getString(jsonObject, OPENMRS_ENTITY_ID);
                                if (entityIdVal.equals(FormEntityConstants.Encounter.encounter_date.name())) {
                                    Date eDate = formatDate(value, false);
                                    if (eDate != null) {
                                        event.setEventDate(eDate);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            //Update Child Entity to include death date
            Event updateChildDetailsEvent =
                    (Event) new Event().withBaseEntityId(entityId) //should be different for main and subform
                            .withEventDate(encounterDate).withEventType(Constants.EventType.UPDATE_REGISTRATION)
                            .withLocationId(encounterLocation).withProviderId(providerId)
                            .withEntityType(DBConstants.WOMAN_TABLE_NAME).withFormSubmissionId(generateRandomUUIDString())
                            .withDateCreated(new Date());
            JsonFormUtils.tagSyncMetadata(allSharedPreferences, updateChildDetailsEvent);

            return Triple.of(isDeath, event, updateChildDetailsEvent);

        } catch (Exception e) {
            Log.e(TAG, "", e);
        }
        return null;
    }

    private static Event tagSyncMetadata(AllSharedPreferences allSharedPreferences, Event event) {
        String providerId = allSharedPreferences.fetchRegisteredANM();
        event.setProviderId(providerId);
        event.setLocationId(allSharedPreferences.fetchDefaultLocalityId(providerId));
        event.setChildLocationId(LocationHelper.getInstance().getChildLocationId());
        event.setTeam(allSharedPreferences.fetchDefaultTeam(providerId));
        event.setTeamId(allSharedPreferences.fetchDefaultTeamId(providerId));
        //event.setVersion(BuildConfig.EVENT_VERSION);
        event.setClientApplicationVersion(BuildConfig.VERSION_CODE);
        event.setClientDatabaseVersion(BuildConfig.DATABASE_VERSION);
        return event;
    }

    public static void launchANCCloseForm(Activity activity) {
        try {
            Intent intent = new Intent(activity, JsonFormActivity.class);
            JSONObject form = FormUtils.getInstance(activity).getFormJson(Constants.JSON_FORM.ANC_CLOSE);
            if (form != null) {
                form.put(Constants.JSON_FORM_KEY.ENTITY_ID,
                        activity.getIntent().getStringExtra(Constants.INTENT_KEY.BASE_ENTITY_ID));
                intent.putExtra(Constants.INTENT_KEY.JSON, form.toString());
                activity.startActivityForResult(intent, JsonFormUtils.REQUEST_CODE_GET_JSON);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public static void launchSiteCharacteristicsForm(Activity activity) {
        try {
            Intent intent = new Intent(activity, JsonFormActivity.class);
            JSONObject form = FormUtils.getInstance(activity).getFormJson(Constants.JSON_FORM.ANC_SITE_CHARACTERISTICS);
            if (form != null) {
                form.put(Constants.JSON_FORM_KEY.ENTITY_ID,
                        activity.getIntent().getStringExtra(Constants.INTENT_KEY.BASE_ENTITY_ID));
                intent.putExtra(Constants.INTENT_KEY.JSON, form.toString());
                activity.startActivityForResult(intent, JsonFormUtils.REQUEST_CODE_GET_JSON);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public static Map<String, String> processSiteCharacteristics(String jsonString) {
        try {
            Triple<Boolean, JSONObject, JSONArray> registrationFormParams = validateParameters(jsonString);
            if (!registrationFormParams.getLeft()) {
                return null;
            }

            Map<String, String> settings = new HashMap<>();
            JSONArray fields =
                    registrationFormParams.getMiddle().getJSONObject(JsonFormUtils.STEP1).getJSONArray(JsonFormUtils.FIELDS);

            for (int i = 0; i < fields.length(); i++) {
                if (!"label".equals(fields.getJSONObject(i).getString(Constants.KEY.TYPE))) {
                    settings.put(fields.getJSONObject(i).getString(Constants.KEY.KEY),
                            StringUtils.isBlank(fields.getJSONObject(i).getString(Constants.KEY.VALUE)) ? "0" :
                                    fields.getJSONObject(i).getString(Constants.KEY.VALUE));
                }

            }

            return settings;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return null;
        }
    }

    public static String getAutoPopulatedSiteCharacteristicsEditFormString(Context context,
                                                                           Map<String, String> characteristics) {
        try {
            JSONObject form = FormUtils.getInstance(context).getFormJson(Constants.JSON_FORM.ANC_SITE_CHARACTERISTICS);
            Log.d(TAG, "Form is " + form.toString());
            if (form != null) {
                form.put(JsonFormUtils.ENCOUNTER_TYPE, Constants.EventType.SITE_CHARACTERISTICS);

                JSONObject stepOne = form.getJSONObject(JsonFormUtils.STEP1);
                JSONArray jsonArray = stepOne.getJSONArray(JsonFormUtils.FIELDS);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (characteristics.containsKey(jsonObject.getString(JsonFormUtils.KEY))) {
                        jsonObject.put(JsonFormUtils.READ_ONLY, false);
                        jsonObject.put(JsonFormUtils.VALUE,
                                "true".equals(characteristics.get(jsonObject.getString(JsonFormUtils.KEY))) ? "1" : "0");
                    }

                }

                return form.toString();
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

        return "";
    }

    public static Pair<Event, Event> createContactVisitEvent(List<String> formSubmissionIDs,
                                                             Map<String, String> womanDetails) {
        if (formSubmissionIDs.size() < 1 && womanDetails.get(Constants.REFERRAL) == null) {
            return null;
        }

        try {

            String contactNo = womanDetails.get(DBConstants.KEY.NEXT_CONTACT);
            String contactStartDate = womanDetails.get(DBConstants.KEY.VISIT_START_DATE);
            String baseEntityId = womanDetails.get(DBConstants.KEY.BASE_ENTITY_ID);

            Event contactVisitEvent = (Event) new Event().withBaseEntityId(baseEntityId).withEventDate(new Date())
                    .withEventType(Constants.EventType.CONTACT_VISIT).withEntityType(DBConstants.CONTACT_ENTITY_TYPE)
                    .withFormSubmissionId(JsonFormUtils.generateRandomUUIDString())
                    .withDateCreated(getContactStartDate(contactStartDate));

            String currentContactNo;
            if (womanDetails.get(Constants.REFERRAL) == null) {
                currentContactNo = Constants.CONTACT + " " + contactNo;
            } else {
                currentContactNo = Constants.CONTACT + " " + womanDetails.get(Constants.REFERRAL);
            }
            contactVisitEvent.addDetails(Constants.CONTACT, currentContactNo);
            contactVisitEvent.addDetails(Constants.FORM_SUBMISSION_IDS, formSubmissionIDs.toString());

            JsonFormUtils.tagSyncMetadata(AncApplication.getInstance().getContext().userService().getAllSharedPreferences(),
                    contactVisitEvent);

            PatientRepository.updateContactVisitStartDate(baseEntityId, null);//reset contact visit date


            //Update client
            EventClientRepository db = AncApplication.getInstance().getEventClientRepository();
            JSONObject clientForm = db.getClientByBaseEntityId(baseEntityId);

            JSONObject attributes = clientForm.getJSONObject(Constants.JSON_FORM_KEY.ATTRIBUTES);
            attributes.put(DBConstants.KEY.NEXT_CONTACT, contactNo);
            attributes.put(DBConstants.KEY.NEXT_CONTACT_DATE, womanDetails.get(DBConstants.KEY.NEXT_CONTACT_DATE));
            attributes.put(DBConstants.KEY.LAST_CONTACT_RECORD_DATE,
                    womanDetails.get(DBConstants.KEY.LAST_CONTACT_RECORD_DATE));
            attributes.put(DBConstants.KEY.CONTACT_STATUS, womanDetails.get(DBConstants.KEY.CONTACT_STATUS));
            attributes.put(DBConstants.KEY.YELLOW_FLAG_COUNT, womanDetails.get(DBConstants.KEY.YELLOW_FLAG_COUNT));
            attributes.put(DBConstants.KEY.RED_FLAG_COUNT, womanDetails.get(DBConstants.KEY.RED_FLAG_COUNT));
            attributes.put(DBConstants.KEY.EDD, womanDetails.get(DBConstants.KEY.EDD));
            clientForm.put(Constants.JSON_FORM_KEY.ATTRIBUTES, attributes);

            FormTag formTag = new FormTag();
            formTag.providerId = AncApplication.getInstance().getContext().allSharedPreferences().fetchRegisteredANM();
            formTag.appVersion = BuildConfig.VERSION_CODE;
            formTag.databaseVersion = BuildConfig.DATABASE_VERSION;
            formTag.childLocationId = LocationHelper.getInstance().getChildLocationId();
            formTag.locationId = LocationHelper.getInstance().getParentLocationId();

            db.addorUpdateClient(baseEntityId, clientForm);

            Event updateClientEvent = createUpdateClientDetailsEvent(baseEntityId);
            return Pair.create(contactVisitEvent, updateClientEvent);

        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
            return null;
        }

    }

    protected static Event createUpdateClientDetailsEvent(String baseEntityId) {

        Event updateChildDetailsEvent = (Event) new Event().withBaseEntityId(baseEntityId).withEventDate(new Date())
                .withEventType(Constants.EventType.UPDATE_REGISTRATION).withEntityType(DBConstants.WOMAN_TABLE_NAME)
                .withFormSubmissionId(generateRandomUUIDString()).withDateCreated(new Date());

        JsonFormUtils
                .tagSyncMetadata(AncApplication.getInstance().getContext().allSharedPreferences(), updateChildDetailsEvent);

        return updateChildDetailsEvent;
    }

    public static Event processContactFormEvent(JSONObject jsonForm, String baseEntityId) {
        AllSharedPreferences allSharedPreferences = AncApplication.getInstance().getContext().allSharedPreferences();

        JSONArray fields = null;
        try {
            fields = getMultiStepFormFields(jsonForm);
        } catch (JsonFormMissingStepCountException e) {
            e.printStackTrace();
        }

        String entityId = getString(jsonForm, ENTITY_ID);
        if (StringUtils.isBlank(entityId)) {
            entityId = baseEntityId;
        }

        String encounterType = getString(jsonForm, ENCOUNTER_TYPE);
        JSONObject metadata = getJSONObject(jsonForm, METADATA);

        FormTag formTag = new FormTag();
        formTag.providerId = allSharedPreferences.fetchRegisteredANM();
        formTag.appVersion = BuildConfig.VERSION_CODE;
        formTag.databaseVersion = BuildConfig.DATABASE_VERSION;

        Event baseEvent = org.smartregister.util.JsonFormUtils
                .createEvent(fields, metadata, formTag, entityId, encounterType, DBConstants.WOMAN_TABLE_NAME);

        JsonFormUtils.tagSyncMetadata(allSharedPreferences, baseEvent);// tag docs

        return baseEvent;
    }

    private static Date getContactStartDate(String contactStartDate) {
        try {
            return new LocalDate(contactStartDate).toDate();
        } catch (Exception e) {
            return new LocalDate().toDate();
        }
    }

    public Template getTemplate(String rawTemplate) {
        Template template = new Template();

        if (rawTemplate.contains(":")) {
            String[] templateArray = rawTemplate.split(":");
            if (templateArray.length == 1) {
                template.title = templateArray[0].trim();
            } else if (templateArray.length > 1) {
                template.title = templateArray[0].trim();
                template.detail = templateArray[1].trim();
            }
        } else {
            template.title = rawTemplate;
            template.detail = "Yes";
        }

        return template;
    }

    public List<ContactSummaryModel> generateNextContactSchedule(String edd, List<String> contactSchedule,
                                                                 Integer lastContactSequence) {
        List<ContactSummaryModel> contactDates = new ArrayList<>();
        Integer contactSequence = lastContactSequence;
        if (!TextUtils.isEmpty(edd)) {
            LocalDate localDate = new LocalDate(edd);
            LocalDate lmpDate = localDate.minusWeeks(Constants.DELIVERY_DATE_WEEKS);

            for (String contactWeeks : contactSchedule) {
                contactDates.add(new ContactSummaryModel(String.format(
                        AncApplication.getInstance().getApplicationContext().getString(R.string.contact_number),
                        contactSequence++),
                        Utils.convertDateFormat(lmpDate.plusWeeks(Integer.valueOf(contactWeeks)).toDate(),
                                Utils.CONTACT_SUMMARY_DF), lmpDate.plusWeeks(Integer.valueOf(contactWeeks)).toDate(),
                        contactWeeks));
            }
        }
        return contactDates;
    }

    /**
     * Creates and populates a constraint view to add the contacts tab view instead of using recycler views which introduce
     * lots of scroll complexities
     *
     * @param data
     * @param facts
     * @param position
     * @param context
     *
     * @return constraintLayout
     */
    @NonNull
    public ConstraintLayout createListViewItems(List<YamlConfigWrapper> data, Facts facts, int position, Context context) {
        YamlConfigItem yamlConfigItem = data.get(position).getYamlConfigItem();

        JsonFormUtils.Template template = getTemplate(yamlConfigItem.getTemplate());
        String output = "";
        if (!TextUtils.isEmpty(template.detail)) {
            output = Utils.fillTemplate(template.detail, facts);
        }

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ConstraintLayout constraintLayout =
                (ConstraintLayout) inflater.inflate(R.layout.previous_contacts_preview_row, null);
        TextView sectionDetailTitle = constraintLayout.findViewById(R.id.overview_section_details_left);
        TextView sectionDetails = constraintLayout.findViewById(R.id.overview_section_details_right);


        sectionDetailTitle.setText(template.title);
        sectionDetails.setText(output);//Perhaps refactor to use Json Form Parser Implementation

        if (AncApplication.getInstance().getAncRulesEngineHelper().getRelevance(facts, yamlConfigItem.getIsRedFont())) {
            sectionDetailTitle.setTextColor(context.getResources().getColor(R.color.overview_font_red));
            sectionDetails.setTextColor(context.getResources().getColor(R.color.overview_font_red));
        } else {
            sectionDetailTitle.setTextColor(context.getResources().getColor(R.color.overview_font_left));
            sectionDetails.setTextColor(context.getResources().getColor(R.color.overview_font_right));
        }

        sectionDetailTitle.setVisibility(View.VISIBLE);
        sectionDetails.setVisibility(View.VISIBLE);
        return constraintLayout;
    }

    public class Template {
        public String title = "";
        public String detail = "";
    }
}
