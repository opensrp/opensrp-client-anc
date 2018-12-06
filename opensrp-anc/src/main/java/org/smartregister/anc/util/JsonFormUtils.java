package org.smartregister.anc.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.Pair;

import com.google.common.reflect.TypeToken;
import com.vijay.jsonwizard.activities.JsonFormActivity;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.anc.BuildConfig;
import org.smartregister.anc.activity.EditJsonFormActivity;
import org.smartregister.anc.application.AncApplication;
import org.smartregister.anc.domain.QuickCheck;
import org.smartregister.anc.helper.ECSyncHelper;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.clientandeventmodel.FormEntityConstants;
import org.smartregister.clientandeventmodel.Obs;
import org.smartregister.configurableviews.model.Field;
import org.smartregister.domain.Photo;
import org.smartregister.domain.ProfileImage;
import org.smartregister.domain.form.FormLocation;
import org.smartregister.domain.tag.FormTag;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.ImageRepository;
import org.smartregister.util.AssetHandler;
import org.smartregister.util.FormUtils;
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
import java.util.Set;
import java.util.UUID;

/**
 * Created by keyman on 27/06/2018.
 */
public class JsonFormUtils extends org.smartregister.util.JsonFormUtils {
    private static final String TAG = JsonFormUtils.class.getCanonicalName();

    public static final String METADATA = "metadata";
    public static final String ENCOUNTER_TYPE = "encounter_type";
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");

    public static final String CURRENT_OPENSRP_ID = "current_opensrp_id";
    public static final String ANC_ID = "ANC_ID";
    public static final String READ_ONLY = "read_only";

    private static final String FORM_SUBMISSION_FIELD = "formsubmissionField";
    private static final String TEXT_DATA_TYPE = "text";
    private static final String SELECT_ONE_DATA_TYPE = "select one";
    private static final String SELECT_MULTIPLE_DATA_TYPE = "select multiple";

    public static final int REQUEST_CODE_GET_JSON = 3432;

    public static JSONObject getFormAsJson(JSONObject form,
                                           String formName, String id,
                                           String currentLocationId) throws Exception {
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

    protected static Triple<Boolean, JSONObject, JSONArray> validateParameters(String jsonString) {

        JSONObject jsonForm = toJSONObject(jsonString);
        JSONArray fields = fields(jsonForm);

        Triple<Boolean, JSONObject, JSONArray> registrationFormParams = Triple.of(jsonForm != null && fields != null, jsonForm, fields);
        return registrationFormParams;
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
            if (StringUtils.isNotBlank(dobUnKnownString) && Boolean.valueOf(dobUnKnownString)) {

                String ageString = getFieldValue(fields, DBConstants.KEY.AGE);
                if (StringUtils.isNotBlank(ageString) && NumberUtils.isNumber(ageString)) {
                    int age = Integer.valueOf(ageString);
                    JSONObject dobJSONObject = getFieldJSONObject(fields, DBConstants.KEY.DOB);
                    dobJSONObject.put(VALUE, Utils.getDob(age));

                    //Mark the birth date as an approximation
                    JSONObject isBirthdateApproximate = new JSONObject();
                    isBirthdateApproximate.put(Constants.KEY.KEY, FormEntityConstants.Person.birthdate_estimated);
                    isBirthdateApproximate.put(Constants.KEY.VALUE, Constants.BOOLEAN_INT.TRUE);
                    isBirthdateApproximate.put(Constants.OPENMRS.ENTITY, Constants.ENTITY.PERSON);//Required for value to be processed
                    isBirthdateApproximate.put(Constants.OPENMRS.ENTITY_ID, FormEntityConstants.Person.birthdate_estimated);
                    fields.put(isBirthdateApproximate);

                }
            }

            //initialize first contact values
            JSONObject nextContactJSONObject = getFieldJSONObject(fields, DBConstants.KEY.NEXT_CONTACT);
            nextContactJSONObject.put(VALUE, 1);

            JSONObject nextContactDateJSONObject = getFieldJSONObject(fields, DBConstants.KEY.NEXT_CONTACT_DATE);
            nextContactDateJSONObject.put(VALUE, Utils.convertDateFormat(Calendar.getInstance().getTime(), Utils.DB_DF));


//Temporary process EDD
            if (getFieldJSONObject(fields, DBConstants.KEY.EDD) != null) {

                String edd = getFieldJSONObject(fields, DBConstants.KEY.EDD).get("value").toString();
                edd = Utils.reverseHyphenSeperatedValues(edd, "-");
                JSONObject dobJSONObject = getFieldJSONObject(fields, DBConstants.KEY.EDD);
                dobJSONObject.put(VALUE, edd);

            }

            FormTag formTag = new FormTag();
            formTag.providerId = allSharedPreferences.fetchRegisteredANM();
            formTag.appVersion = BuildConfig.VERSION_CODE;
            formTag.databaseVersion = BuildConfig.DATABASE_VERSION;


            Client baseClient = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag, entityId);
            Event baseEvent = org.smartregister.util.JsonFormUtils.createEvent(fields, metadata, formTag, entityId, encounterType, DBConstants.WOMAN_TABLE_NAME);

            JsonFormUtils.tagSyncMetadata(allSharedPreferences, baseEvent);// tag docs

            return Pair.create(baseClient, baseEvent);
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
            return null;
        }
    }

    public static void mergeAndSaveClient(ECSyncHelper ecUpdater, Client baseClient) throws Exception {
        JSONObject updatedClientJson = new JSONObject(org.smartregister.util.JsonFormUtils.gson.toJson(baseClient));

        JSONObject originalClientJsonObject = ecUpdater.getClient(baseClient.getBaseEntityId());

        JSONObject mergedJson = org.smartregister.util.JsonFormUtils.merge(originalClientJsonObject, updatedClientJson);

        //TODO Save edit log ?

        ecUpdater.addClient(baseClient.getBaseEntityId(), mergedJson);
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
                    throw new IllegalArgumentException("Failed to save static image, could not retrieve image compression format from name "
                            + absoluteFileName);
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
                String lastLocationId = lpv != null ? LocationHelper.getInstance().getOpenMrsLocationId(lpv.getSelectedItem()) : "";

                metadata.put(JsonFormUtils.ENCOUNTER_LOCATION, lastLocationId);

                form.put(JsonFormUtils.CURRENT_OPENSRP_ID, womanClient.get(DBConstants.KEY.ANC_ID).replace("-", ""));

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

    protected static void processPopulatableFields(Map<String, String> womanClient, JSONObject jsonObject) throws JSONException {


        if (jsonObject.getString(JsonFormUtils.KEY).equalsIgnoreCase(DBConstants.KEY.DOB)) {

            String dobString = womanClient.get(DBConstants.KEY.DOB);
            if (StringUtils.isNotBlank(dobString)) {
                Date dob = Utils.dobStringToDate(dobString);
                if (dob != null) {
                    jsonObject.put(JsonFormUtils.VALUE, DATE_FORMAT.format(dob));
                }
            }

        } else if (jsonObject.getString(JsonFormUtils.KEY).equalsIgnoreCase(DBConstants.KEY.HOME_ADDRESS)) {

            String homeAddress = womanClient.get(DBConstants.KEY.HOME_ADDRESS);
            jsonObject.put(JsonFormUtils.VALUE, homeAddress);

        } else if (jsonObject.getString(JsonFormUtils.KEY).equalsIgnoreCase(Constants.KEY.PHOTO)) {

            Photo photo = ImageUtils.profilePhotoByClientID(womanClient.get(DBConstants.KEY.BASE_ENTITY_ID));

            if (StringUtils.isNotBlank(photo.getFilePath())) {

                jsonObject.put(JsonFormUtils.VALUE, photo.getFilePath());

            }
        } else if (jsonObject.getString(JsonFormUtils.KEY).equalsIgnoreCase(DBConstants.KEY.DOB_UNKNOWN)) {

            jsonObject.put(JsonFormUtils.READ_ONLY, false);
            JSONObject optionsObject = jsonObject.getJSONArray(Constants.JSON_FORM_KEY.OPTIONS).getJSONObject(0);
            optionsObject.put(JsonFormUtils.VALUE, womanClient.get(DBConstants.KEY.DOB_UNKNOWN));

        } else if (jsonObject.getString(JsonFormUtils.KEY).equalsIgnoreCase(DBConstants.KEY.AGE)) {

            jsonObject.put(JsonFormUtils.READ_ONLY, false);
            if (StringUtils.isNotBlank(womanClient.get(DBConstants.KEY.DOB))) {
                jsonObject.put(JsonFormUtils.VALUE, Utils.getAgeFromDate(womanClient.get(DBConstants.KEY.DOB)));
            }

        } else if (jsonObject.getString(JsonFormUtils.KEY).equalsIgnoreCase(DBConstants.KEY.EDD)) {

            String dobString = womanClient.get(DBConstants.KEY.EDD);
            if (StringUtils.isNotBlank(dobString)) {
                Date dob = Utils.dobStringToDate(dobString);
                if (dob != null) {
                    jsonObject.put(JsonFormUtils.VALUE, DATE_FORMAT.format(dob));
                }
            }

        } else if (jsonObject.getString(JsonFormUtils.KEY).equalsIgnoreCase(DBConstants.KEY.ANC_ID)) {

            jsonObject.put(JsonFormUtils.VALUE, womanClient.get(DBConstants.KEY.ANC_ID).replace("-", ""));

        } else if (womanClient.containsKey(jsonObject.getString(JsonFormUtils.KEY))) {

            jsonObject.put(JsonFormUtils.READ_ONLY, false);
            jsonObject.put(JsonFormUtils.VALUE, womanClient.get(jsonObject.getString(JsonFormUtils.KEY)));
        } else {
            Log.e(TAG, "ERROR:: Unprocessed Form Object Key " + jsonObject.getString(JsonFormUtils.KEY));
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
            List<FormLocation> upToFacilities = LocationHelper.getInstance().generateLocationHierarchyTree(false, healthFacilities);

            String defaultFacilityString = AssetHandler.javaToJsonString(defaultFacility,
                    new TypeToken<List<String>>() {
                    }.getType());

            String upToFacilitiesString = AssetHandler.javaToJsonString(upToFacilities,
                    new TypeToken<List<FormLocation>>() {
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

    public static Triple<Boolean, Event, Event> saveRemovedFromANCRegister(
            AllSharedPreferences allSharedPreferences,
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

            Event event = (Event) new Event()
                    .withBaseEntityId(entityId) //should be different for main and subform
                    .withEventDate(encounterDate)
                    .withEventType(encounterType)
                    .withLocationId(encounterLocation)
                    .withProviderId(providerId)
                    .withEntityType(DBConstants.WOMAN_TABLE_NAME)
                    .withFormSubmissionId(generateRandomUUIDString())
                    .withDateCreated(new Date());
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
            Event updateChildDetailsEvent = (Event) new Event()
                    .withBaseEntityId(entityId) //should be different for main and subform
                    .withEventDate(encounterDate)
                    .withEventType(Constants.EventType.UPDATE_REGISTRATION)
                    .withLocationId(encounterLocation)
                    .withProviderId(providerId)
                    .withEntityType(DBConstants.WOMAN_TABLE_NAME)
                    .withFormSubmissionId(generateRandomUUIDString())
                    .withDateCreated(new Date());
            JsonFormUtils.tagSyncMetadata(allSharedPreferences, updateChildDetailsEvent);

            return Triple.of(isDeath, event, updateChildDetailsEvent);

        } catch (Exception e) {
            Log.e(TAG, "", e);
        }
        return null;
    }

    public static Event createQuickCheckEvent(AllSharedPreferences allSharedPreferences, QuickCheck quickCheck, String baseEntityId) {

        try {

            Field selectedReason = quickCheck.getSelectedReason();
            Set<Field> selectedComplaints = quickCheck.getSpecificComplaints();
            Set<Field> selectedDangerSigns = quickCheck.getSelectedDangerSigns();
            String specify = quickCheck.getOtherSpecify();


            Event event = (Event) new Event()
                    .withBaseEntityId(baseEntityId)
                    .withEventDate(new Date())
                    .withEventType(Constants.EventType.QUICK_CHECK)
                    .withEntityType(DBConstants.WOMAN_TABLE_NAME)
                    .withFormSubmissionId(JsonFormUtils.generateRandomUUIDString())
                    .withDateCreated(new Date());

            if (selectedReason != null) {
                event.addObs(createObs("contact_reason", SELECT_ONE_DATA_TYPE, selectedReason.getDisplayName()));
            }

            if (selectedComplaints != null && !selectedComplaints.isEmpty()) {
                event.addObs(createObs("specific_complaint", SELECT_MULTIPLE_DATA_TYPE, selectedComplaints));
            }

            if (StringUtils.isNotBlank(specify)) {
                event.addObs(createObs("specific_complaint_other", TEXT_DATA_TYPE, specify));
            }

            if (selectedDangerSigns != null && !selectedDangerSigns.isEmpty()) {
                event.addObs(createObs("danger_signs", SELECT_MULTIPLE_DATA_TYPE, selectedDangerSigns));
            }

            if (quickCheck.getHasDangerSigns()) {
                String value = quickCheck.getProceedRefer() ? quickCheck.getProceedToContact() : quickCheck.getReferAndCloseContact();
                event.addObs(createObs("danger_signs_proceed", SELECT_ONE_DATA_TYPE, value));

                if (!quickCheck.getProceedRefer()) {
                    value = quickCheck.getTreat() ? quickCheck.getYes() : quickCheck.getNo();
                    event.addObs(createObs("danger_signs_treat", SELECT_ONE_DATA_TYPE, value));
                }
            }

            JsonFormUtils.tagSyncMetadata(allSharedPreferences, event);

            return event;

        } catch (
                Exception e)

        {
            Log.e(TAG, Log.getStackTraceString(e));
            return null;
        }

    }

    private static Obs createObs(String formSubmissionField, String dataType, Set<Field> fieldList) {
        List<Object> vall = new ArrayList<>();
        for (Field field : fieldList) {
            vall.add(field.getDisplayName());
        }
        return new Obs(FORM_SUBMISSION_FIELD, dataType, formSubmissionField,
                "", vall, new ArrayList<>(), null, formSubmissionField);
    }

    private static Obs createObs(String formSubmissionField, String dataType, String value) {
        List<Object> vall = new ArrayList<>();
        vall.add(value);
        return new Obs(FORM_SUBMISSION_FIELD, dataType, formSubmissionField,
                "", vall, new ArrayList<>(), null, formSubmissionField);
    }

    private static Event tagSyncMetadata(AllSharedPreferences allSharedPreferences, Event event) {
        String providerId = allSharedPreferences.fetchRegisteredANM();
        event.setProviderId(providerId);
        event.setLocationId(allSharedPreferences.fetchDefaultLocalityId(providerId));
        event.setTeam(allSharedPreferences.fetchDefaultTeam(providerId));
        event.setTeamId(allSharedPreferences.fetchDefaultTeamId(providerId));
        return event;
    }

    public static void launchANCCloseForm(Activity activity) {
        try {
            Intent intent = new Intent(activity, JsonFormActivity.class);

            JSONObject form = FormUtils.getInstance(activity).getFormJson(Constants.JSON_FORM.ANC_CLOSE);
            if (form != null) {
                form.put(Constants.JSON_FORM_KEY.ENTITY_ID, activity.getIntent().getStringExtra(Constants.INTENT_KEY.BASE_ENTITY_ID));
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
                form.put(Constants.JSON_FORM_KEY.ENTITY_ID, activity.getIntent().getStringExtra(Constants.INTENT_KEY.BASE_ENTITY_ID));
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
            JSONArray fields = registrationFormParams.getMiddle().getJSONObject(JsonFormUtils.STEP1).getJSONArray(JsonFormUtils.FIELDS);

            for (int i = 0; i < fields.length(); i++) {
                if (!"label".equals(fields.getJSONObject(i).getString(Constants.KEY.TYPE))) {
                    settings.put(fields.getJSONObject(i).getString(Constants.KEY.KEY), StringUtils.isBlank(fields.getJSONObject(i).getString(Constants.KEY.VALUE)) ? "0" : fields.getJSONObject(i).getString(Constants.KEY.VALUE));
                }

            }

            return settings;
        } catch (Exception e) {

            Log.e(TAG, e.getMessage());
            return null;

        }
    }

    public static String getAutoPopulatedSiteCharacteristicsEditFormString(Context context, Map<String, String> characteristics) {
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
                        jsonObject.put(JsonFormUtils.VALUE, "true".equals(characteristics.get(jsonObject.getString(JsonFormUtils.KEY))) ? "1" : "0");
                    }

                }

                return form.toString();
            }
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

        return "";
    }
}
