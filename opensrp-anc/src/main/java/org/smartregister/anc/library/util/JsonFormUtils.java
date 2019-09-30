package org.smartregister.anc.library.util;

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
import org.jetbrains.annotations.NotNull;
import org.joda.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.anc.library.AncLibrary;
import org.smartregister.anc.library.BuildConfig;
import org.smartregister.anc.library.R;
import org.smartregister.anc.library.activity.EditJsonFormActivity;
import org.smartregister.anc.library.domain.YamlConfigItem;
import org.smartregister.anc.library.domain.YamlConfigWrapper;
import org.smartregister.anc.library.model.ContactSummaryModel;
import org.smartregister.anc.library.repository.PatientRepositoryHelper;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import timber.log.Timber;

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

    public static boolean isFieldRequired(JSONObject fieldObject) throws JSONException {
        boolean isValueRequired = false;
        if (fieldObject.has(JsonFormConstants.V_REQUIRED)) {
            JSONObject valueRequired = fieldObject.getJSONObject(JsonFormConstants.V_REQUIRED);
            String value = valueRequired.getString(JsonFormConstants.VALUE);
            isValueRequired = Boolean.parseBoolean(value);
        }
        //Don't check required for hidden, toaster notes, spacer and label widgets
        return (!fieldObject.getString(JsonFormConstants.TYPE).equals(JsonFormConstants.LABEL) &&
                !fieldObject.getString(JsonFormConstants.TYPE).equals(JsonFormConstants.SPACER) &&
                !fieldObject.getString(JsonFormConstants.TYPE).equals(JsonFormConstants.TOASTER_NOTES) &&
                !fieldObject.getString(JsonFormConstants.TYPE).equals(JsonFormConstants.HIDDEN)) &&
                isValueRequired;
    }

    public static JSONObject getFormAsJson(JSONObject form, String formName, String id, String currentLocationId)
            throws Exception {
        if (form == null) {
            return null;
        }

        String entityId = id;
        form.getJSONObject(METADATA).put(org.smartregister.anc.library.util.JsonFormUtils.ENCOUNTER_LOCATION, currentLocationId);

        if (ConstantsUtils.JsonFormUtils.ANC_REGISTER.equals(formName)) {
            if (StringUtils.isNotBlank(entityId)) {
                entityId = entityId.replace("-", "");
            }

            // Inject opensrp id into the form
            JSONArray field = org.smartregister.anc.library.util.JsonFormUtils.fields(form);
            JSONObject ancId = getFieldJSONObject(field, ConstantsUtils.JsonFormKeyUtils.ANC_ID);
            if (ancId != null) {
                ancId.remove(org.smartregister.anc.library.util.JsonFormUtils.VALUE);
                ancId.put(org.smartregister.anc.library.util.JsonFormUtils.VALUE, entityId);
            }

        } else if (ConstantsUtils.JsonFormUtils.ANC_CLOSE.equals(formName)) {
            if (StringUtils.isNotBlank(entityId)) {
                // Inject entity id into the remove form
                form.remove(org.smartregister.anc.library.util.JsonFormUtils.ENTITY_ID);
                form.put(org.smartregister.anc.library.util.JsonFormUtils.ENTITY_ID, entityId);
            }
        } else {
            Log.w(TAG, "Unsupported form requested for launch " + formName);
        }
        Log.d(TAG, "form is " + form.toString());
        return form;
    }

    public static JSONObject getFieldJSONObject(JSONArray jsonArray, String key) {
        if (jsonArray == null || jsonArray.length() == 0) {
            return null;
        }

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = org.smartregister.anc.library.util.JsonFormUtils.getJSONObject(jsonArray, i);
            String keyVal = org.smartregister.anc.library.util.JsonFormUtils.getString(jsonObject, org.smartregister.anc.library.util.JsonFormUtils.KEY);
            if (keyVal != null && keyVal.equals(key)) {
                return jsonObject;
            }
        }
        return null;
    }

    public static Pair<Client, Event> processRegistrationForm(AllSharedPreferences allSharedPreferences, String jsonString) {
        try {
            Triple<Boolean, JSONObject, JSONArray> registrationFormParams = validateParameters(jsonString);

            if (!registrationFormParams.getLeft()) {
                return null;
            }

            JSONObject jsonForm = registrationFormParams.getMiddle();
            JSONArray fields = registrationFormParams.getRight();

            String entityId = getEntityId(jsonForm);
            String encounterType = org.smartregister.anc.library.util.JsonFormUtils.getString(jsonForm, ENCOUNTER_TYPE);
            JSONObject metadata = org.smartregister.anc.library.util.JsonFormUtils.getJSONObject(jsonForm, METADATA);

            // String lastLocationName = null;
            // String lastLocationId = null;
            // TODO Replace values for location questions with their corresponding location IDs


            addLastInteractedWith(fields);
            getDobStrings(fields);
            initializeFirstContactValues(fields);
            FormTag formTag = getFormTag(allSharedPreferences);


            Client baseClient = org.smartregister.util.JsonFormUtils.createBaseClient(fields, formTag, entityId);
            Event baseEvent = org.smartregister.util.JsonFormUtils
                    .createEvent(fields, metadata, formTag, entityId, encounterType, DBConstantsUtils.WOMAN_TABLE_NAME);

            tagSyncMetadata(allSharedPreferences, baseEvent);// tag docs

            return Pair.create(baseClient, baseEvent);
        } catch (Exception e) {
            Timber.e(e, "JsonFormUtils --> processRegistrationForm");
            return null;
        }
    }

    @NotNull
    private static String getEntityId(JSONObject jsonForm) {
        String entityId = JsonFormUtils.getString(jsonForm, JsonFormUtils.ENTITY_ID);
        if (StringUtils.isBlank(entityId)) {
            entityId = JsonFormUtils.generateRandomUUIDString();
        }
        return entityId;
    }

    private static void getDobStrings(JSONArray fields) throws JSONException {
        JSONObject dobUnknownObject = getFieldJSONObject(fields, DBConstantsUtils.KeyUtils.DOB_UNKNOWN);
        JSONArray options = JsonFormUtils.getJSONArray(dobUnknownObject, ConstantsUtils.JsonFormKeyUtils.OPTIONS);
        JSONObject option = JsonFormUtils.getJSONObject(options, 0);
        String dobUnKnownString = option != null ? option.getString(JsonFormUtils.VALUE) : null;

        if (StringUtils.isNotBlank(dobUnKnownString)) {
            dobUnknownObject.put(JsonFormUtils.VALUE, Boolean.valueOf(dobUnKnownString) ? 1 : 0);
        }
    }

    private static void addLastInteractedWith(JSONArray fields) throws JSONException {
        JSONObject lastInteractedWith = new JSONObject();
        lastInteractedWith.put(ConstantsUtils.KeyUtils.KEY, DBConstantsUtils.KeyUtils.LAST_INTERACTED_WITH);
        lastInteractedWith.put(ConstantsUtils.KeyUtils.VALUE, Calendar.getInstance().getTimeInMillis());
        fields.put(lastInteractedWith);
    }

    private static void initializeFirstContactValues(JSONArray fields) throws JSONException {
        //initialize first contact values
        JSONObject nextContactJSONObject = getFieldJSONObject(fields, DBConstantsUtils.KeyUtils.NEXT_CONTACT);
        if (nextContactJSONObject.has(JsonFormConstants.VALUE) &&
                "".equals(nextContactJSONObject.getString(JsonFormConstants.VALUE))) {
            nextContactJSONObject.put(JsonFormUtils.VALUE, 1);
        }

        JSONObject nextContactDateJSONObject = getFieldJSONObject(fields, DBConstantsUtils.KeyUtils.NEXT_CONTACT_DATE);
        if (nextContactDateJSONObject.has(JsonFormConstants.VALUE) &&
                "".equals(nextContactDateJSONObject.getString(JsonFormConstants.VALUE))) {
            nextContactDateJSONObject.put(JsonFormUtils.VALUE, Utils.convertDateFormat(Calendar.getInstance().getTime(), Utils.DB_DF));
        }
    }

    @NotNull
    private static FormTag getFormTag(AllSharedPreferences allSharedPreferences) {
        FormTag formTag = new FormTag();
        formTag.providerId = allSharedPreferences.fetchRegisteredANM();
        formTag.appVersion = BuildConfig.VERSION_CODE;
        formTag.databaseVersion = AncLibrary.getInstance().getDatabaseVersion();
        return formTag;
    }

    public static Triple<Boolean, JSONObject, JSONArray> validateParameters(String jsonString) {

        JSONObject jsonForm = org.smartregister.anc.library.util.JsonFormUtils.toJSONObject(jsonString);
        JSONArray fields = org.smartregister.anc.library.util.JsonFormUtils.fields(jsonForm);

        return Triple.of(jsonForm != null && fields != null, jsonForm, fields);
    }

    private static void tagSyncMetadata(AllSharedPreferences allSharedPreferences, Event event) {
        String providerId = allSharedPreferences.fetchRegisteredANM();
        event.setProviderId(providerId);
        event.setLocationId(allSharedPreferences.fetchDefaultLocalityId(providerId));
        event.setChildLocationId(LocationHelper.getInstance().getChildLocationId());
        event.setTeam(allSharedPreferences.fetchDefaultTeam(providerId));
        event.setTeamId(allSharedPreferences.fetchDefaultTeamId(providerId));
        //event.setVersion(BuildConfig.EVENT_VERSION);
        event.setClientApplicationVersion(BuildConfig.VERSION_CODE);
        event.setClientDatabaseVersion(AncLibrary.getInstance().getDatabaseVersion());
    }

    public static void mergeAndSaveClient(Client baseClient) throws Exception {
        JSONObject updatedClientJson = new JSONObject(org.smartregister.util.JsonFormUtils.gson.toJson(baseClient));

        JSONObject originalClientJsonObject =
                AncLibrary.getInstance().getEcSyncHelper().getClient(baseClient.getBaseEntityId());

        JSONObject mergedJson = org.smartregister.util.JsonFormUtils.merge(originalClientJsonObject, updatedClientJson);

        //TODO Save edit log ?

        AncLibrary.getInstance().getEcSyncHelper().addClient(baseClient.getBaseEntityId(), mergedJson);
    }

    public static void saveImage(String providerId, String entityId, String imageLocation) {
        if (StringUtils.isBlank(imageLocation)) {
            return;
        }

        File file = FileUtil.createFileFromPath(imageLocation);

        if (!file.exists()) {
            return;
        }

        Bitmap compressedBitmap = AncLibrary.getInstance().getCompressor().compressToBitmap(file);

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
                profileImage.setFilecategory(ConstantsUtils.FileCategoryUtils.PROFILE_PIC);
                profileImage.setSyncStatus(ImageRepository.TYPE_Unsynced);
                ImageRepository imageRepo = AncLibrary.getInstance().getContext().imageRepository();
                imageRepo.add(profileImage);
            }

        } catch (FileNotFoundException e) {
            Timber.e("Failed to save static image to disk");
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    Timber.e("Failed to close static images output stream after attempting to write image");
                }
            }
        }
    }

    public static String getString(String jsonString, String field) {
        return org.smartregister.anc.library.util.JsonFormUtils.getString(org.smartregister.anc.library.util.JsonFormUtils.toJSONObject(jsonString), field);
    }

    public static String getFieldValue(String jsonString, String key) {
        JSONObject jsonForm = org.smartregister.anc.library.util.JsonFormUtils.toJSONObject(jsonString);
        if (jsonForm == null) {
            return null;
        }

        JSONArray fields = org.smartregister.anc.library.util.JsonFormUtils.fields(jsonForm);
        if (fields == null) {
            return null;
        }

        return org.smartregister.anc.library.util.JsonFormUtils.getFieldValue(fields, key);

    }

    public static String getAutoPopulatedJsonEditRegisterFormString(Context context, Map<String, String> womanClient) {
        try {
            JSONObject form = FormUtils.getInstance(context).getFormJson(ConstantsUtils.JsonFormUtils.ANC_REGISTER);
            LocationPickerView lpv = createLocationPickerView(context);
            if (lpv != null) {
                lpv.init();
            }
            org.smartregister.anc.library.util.JsonFormUtils.addWomanRegisterHierarchyQuestions(form);
            Timber.d("Form is %s", form.toString());
            if (form != null) {
                form.put(org.smartregister.anc.library.util.JsonFormUtils.ENTITY_ID, womanClient.get(DBConstantsUtils.KeyUtils.BASE_ENTITY_ID));
                form.put(org.smartregister.anc.library.util.JsonFormUtils.ENCOUNTER_TYPE, ConstantsUtils.EventTypeUtils.UPDATE_REGISTRATION);

                JSONObject metadata = form.getJSONObject(org.smartregister.anc.library.util.JsonFormUtils.METADATA);
                String lastLocationId =
                        lpv != null ? LocationHelper.getInstance().getOpenMrsLocationId(lpv.getSelectedItem()) : "";

                metadata.put(org.smartregister.anc.library.util.JsonFormUtils.ENCOUNTER_LOCATION, lastLocationId);

                form.put(ConstantsUtils.CURRENT_OPENSRP_ID, womanClient.get(DBConstantsUtils.KeyUtils.ANC_ID).replace("-", ""));

                //inject opensrp id into the form
                JSONObject stepOne = form.getJSONObject(org.smartregister.anc.library.util.JsonFormUtils.STEP1);
                JSONArray jsonArray = stepOne.getJSONArray(org.smartregister.anc.library.util.JsonFormUtils.FIELDS);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    processPopulatableFields(womanClient, jsonObject);

                }

                return form.toString();
            }
        } catch (Exception e) {
            Timber.e(e, "JsonFormUtils --> getAutoPopulatedJsonEditRegisterFormString");
        }

        return "";
    }

    private static LocationPickerView createLocationPickerView(Context context) {
        try {
            return new LocationPickerView(context);
        } catch (Exception e) {
            Timber.e(e, "JsonFormUtils --> createLocationPickerView");
            return null;
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
                if (questions.getJSONObject(i).getString(ConstantsUtils.KeyUtils.KEY).equalsIgnoreCase(DBConstantsUtils.KeyUtils.HOME_ADDRESS)) {
                    if (StringUtils.isNotBlank(upToFacilitiesString)) {
                        questions.getJSONObject(i).put(ConstantsUtils.KeyUtils.TREE, new JSONArray(upToFacilitiesString));
                    }
                    if (StringUtils.isNotBlank(defaultFacilityString)) {
                        questions.getJSONObject(i).put(ConstantsUtils.KeyUtils.DEFAULT, defaultFacilityString);
                    }
                }
            }

        } catch (JSONException e) {
            Timber.e(e, "JsonFormUtils --> addWomanRegisterHierarchyQuestions");
        }
    }

    protected static void processPopulatableFields(Map<String, String> womanClient, JSONObject jsonObject)
            throws JSONException {

        if (jsonObject.getString(org.smartregister.anc.library.util.JsonFormUtils.KEY).equalsIgnoreCase(ConstantsUtils.JsonFormKeyUtils.DOB_ENTERED)) {
            getDobUsingEdd(womanClient, jsonObject, DBConstantsUtils.KeyUtils.DOB);

        } else if (jsonObject.getString(org.smartregister.anc.library.util.JsonFormUtils.KEY).equalsIgnoreCase(DBConstantsUtils.KeyUtils.HOME_ADDRESS)) {
            String homeAddress = womanClient.get(DBConstantsUtils.KeyUtils.HOME_ADDRESS);
            jsonObject.put(org.smartregister.anc.library.util.JsonFormUtils.VALUE, homeAddress);

        } else if (jsonObject.getString(org.smartregister.anc.library.util.JsonFormUtils.KEY).equalsIgnoreCase(ConstantsUtils.WOM_IMAGE)) {
            getPhotoFieldValue(womanClient, jsonObject);
        } else if (jsonObject.getString(org.smartregister.anc.library.util.JsonFormUtils.KEY).equalsIgnoreCase(DBConstantsUtils.KeyUtils.DOB_UNKNOWN)) {
            jsonObject.put(org.smartregister.anc.library.util.JsonFormUtils.READ_ONLY, false);
            JSONObject optionsObject = jsonObject.getJSONArray(ConstantsUtils.JsonFormKeyUtils.OPTIONS).getJSONObject(0);
            optionsObject.put(org.smartregister.anc.library.util.JsonFormUtils.VALUE, womanClient.get(DBConstantsUtils.KeyUtils.DOB_UNKNOWN));

        } else if (jsonObject.getString(org.smartregister.anc.library.util.JsonFormUtils.KEY).equalsIgnoreCase(ConstantsUtils.KeyUtils.AGE_ENTERED)) {
            jsonObject.put(org.smartregister.anc.library.util.JsonFormUtils.READ_ONLY, false);
            if (StringUtils.isNotBlank(womanClient.get(DBConstantsUtils.KeyUtils.DOB))) {
                jsonObject.put(org.smartregister.anc.library.util.JsonFormUtils.VALUE, Utils.getAgeFromDate(womanClient.get(DBConstantsUtils.KeyUtils.DOB)));
            }

        } else if (jsonObject.getString(org.smartregister.anc.library.util.JsonFormUtils.KEY).equalsIgnoreCase(DBConstantsUtils.KeyUtils.EDD)) {
            formatEdd(womanClient, jsonObject, DBConstantsUtils.KeyUtils.EDD);

        } else if (jsonObject.getString(org.smartregister.anc.library.util.JsonFormUtils.KEY).equalsIgnoreCase(ConstantsUtils.JsonFormKeyUtils.ANC_ID)) {
            jsonObject.put(org.smartregister.anc.library.util.JsonFormUtils.VALUE, womanClient.get(DBConstantsUtils.KeyUtils.ANC_ID).replace("-", ""));

        } else if (womanClient.containsKey(jsonObject.getString(org.smartregister.anc.library.util.JsonFormUtils.KEY))) {
            jsonObject.put(org.smartregister.anc.library.util.JsonFormUtils.READ_ONLY, false);
            jsonObject.put(org.smartregister.anc.library.util.JsonFormUtils.VALUE, womanClient.get(jsonObject.getString(org.smartregister.anc.library.util.JsonFormUtils.KEY)));
        } else {
            Timber.e("ERROR:: Unprocessed Form Object Key %s", jsonObject.getString(JsonFormUtils.KEY));
        }
    }

    private static void getDobUsingEdd(Map<String, String> womanClient, JSONObject jsonObject, String birthDate)
            throws JSONException {
        String dobString = womanClient.get(birthDate);
        if (StringUtils.isNotBlank(dobString)) {
            Date dob = Utils.dobStringToDate(dobString);
            if (dob != null) {
                jsonObject.put(org.smartregister.anc.library.util.JsonFormUtils.VALUE, DATE_FORMAT.format(dob));
            }
        }
    }

    private static void getPhotoFieldValue(Map<String, String> womanClient, JSONObject jsonObject) throws JSONException {
        Photo photo = ImageUtils.profilePhotoByClientID(womanClient.get(DBConstantsUtils.KeyUtils.BASE_ENTITY_ID),
                Utils.getProfileImageResourceIdentifier());

        if (photo != null && StringUtils.isNotBlank(photo.getFilePath())) {
            jsonObject.put(org.smartregister.anc.library.util.JsonFormUtils.VALUE, photo.getFilePath());

        }
    }

    private static void formatEdd(Map<String, String> womanClient, JSONObject jsonObject, String eddDate)
            throws JSONException {
        String eddString = womanClient.get(eddDate);
        if (StringUtils.isNotBlank(eddString)) {
            Date edd = Utils.dobStringToDate(eddString);
            if (edd != null) {
                jsonObject.put(org.smartregister.anc.library.util.JsonFormUtils.VALUE, EDD_DATE_FORMAT.format(edd));
            }
        }
    }

    public static void startFormForEdit(Activity context, int jsonFormActivityRequestCode, String metaData) {
        Intent intent = new Intent(context, EditJsonFormActivity.class);
        intent.putExtra(ConstantsUtils.IntentKeyUtils.JSON, metaData);
        Timber.d("form is %s", metaData);
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

            String encounterType = org.smartregister.anc.library.util.JsonFormUtils.getString(jsonForm, ENCOUNTER_TYPE);
            JSONObject metadata = org.smartregister.anc.library.util.JsonFormUtils.getJSONObject(jsonForm, METADATA);

            String encounterLocation = null;

            try {
                encounterLocation = metadata.getString(ConstantsUtils.JsonFormKeyUtils.ENCOUNTER_LOCATION);
            } catch (JSONException e) {
                Timber.e(e, "JsonFormUtils --> saveRemovedFromANCRegister --> getEncounterLocation");
            }

            Date encounterDate = new Date();
            String entityId = org.smartregister.anc.library.util.JsonFormUtils.getString(jsonForm, org.smartregister.anc.library.util.JsonFormUtils.ENTITY_ID);

            Event event = (Event) new Event().withBaseEntityId(entityId) //should be different for main and subform
                    .withEventDate(encounterDate).withEventType(encounterType).withLocationId(encounterLocation)
                    .withProviderId(providerId).withEntityType(DBConstantsUtils.WOMAN_TABLE_NAME)
                    .withFormSubmissionId(org.smartregister.anc.library.util.JsonFormUtils.generateRandomUUIDString()).withDateCreated(new Date());
            tagSyncMetadata(allSharedPreferences, event);

            for (int i = 0; i < fields.length(); i++) {
                JSONObject jsonObject = org.smartregister.anc.library.util.JsonFormUtils.getJSONObject(fields, i);

                String value = org.smartregister.anc.library.util.JsonFormUtils.getString(jsonObject, org.smartregister.anc.library.util.JsonFormUtils.VALUE);
                if (StringUtils.isNotBlank(value)) {
                    org.smartregister.anc.library.util.JsonFormUtils.addObservation(event, jsonObject);
                    if (jsonObject.get(org.smartregister.anc.library.util.JsonFormUtils.KEY).equals(ConstantsUtils.JsonFormKeyUtils.ANC_CLOSE_REASON)) {
                        isDeath = "Woman Died".equalsIgnoreCase(value);
                    }
                }
            }

            if (metadata != null) {
                Iterator<?> keys = metadata.keys();

                while (keys.hasNext()) {
                    String key = (String) keys.next();
                    JSONObject jsonObject = org.smartregister.anc.library.util.JsonFormUtils.getJSONObject(metadata, key);
                    String value = org.smartregister.anc.library.util.JsonFormUtils.getString(jsonObject, org.smartregister.anc.library.util.JsonFormUtils.VALUE);
                    if (StringUtils.isNotBlank(value)) {
                        String entityVal = org.smartregister.anc.library.util.JsonFormUtils.getString(jsonObject, org.smartregister.anc.library.util.JsonFormUtils.OPENMRS_ENTITY);
                        if (entityVal != null) {
                            if (entityVal.equals(org.smartregister.anc.library.util.JsonFormUtils.CONCEPT)) {
                                org.smartregister.anc.library.util.JsonFormUtils.addToJSONObject(jsonObject, org.smartregister.anc.library.util.JsonFormUtils.KEY, key);
                                org.smartregister.anc.library.util.JsonFormUtils.addObservation(event, jsonObject);

                            } else if (entityVal.equals(org.smartregister.anc.library.util.JsonFormUtils.ENCOUNTER)) {
                                String entityIdVal = org.smartregister.anc.library.util.JsonFormUtils.getString(jsonObject, org.smartregister.anc.library.util.JsonFormUtils.OPENMRS_ENTITY_ID);
                                if (entityIdVal.equals(FormEntityConstants.Encounter.encounter_date.name())) {
                                    Date eDate = org.smartregister.anc.library.util.JsonFormUtils.formatDate(value, false);
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
                            .withEventDate(encounterDate).withEventType(ConstantsUtils.EventTypeUtils.UPDATE_REGISTRATION)
                            .withLocationId(encounterLocation).withProviderId(providerId)
                            .withEntityType(DBConstantsUtils.WOMAN_TABLE_NAME).withFormSubmissionId(org.smartregister.anc.library.util.JsonFormUtils.generateRandomUUIDString())
                            .withDateCreated(new Date());
            tagSyncMetadata(allSharedPreferences, updateChildDetailsEvent);

            return Triple.of(isDeath, event, updateChildDetailsEvent);

        } catch (Exception e) {
            Timber.e(e, "JsonFormUtils --> saveRemovedFromANCRegister");
        }
        return null;
    }

    public static void launchANCCloseForm(Activity activity) {
        try {
            Intent intent = new Intent(activity, JsonFormActivity.class);
            JSONObject form = FormUtils.getInstance(activity).getFormJson(ConstantsUtils.JsonFormUtils.ANC_CLOSE);
            if (form != null) {
                form.put(ConstantsUtils.JsonFormKeyUtils.ENTITY_ID,
                        activity.getIntent().getStringExtra(ConstantsUtils.IntentKeyUtils.BASE_ENTITY_ID));
                intent.putExtra(ConstantsUtils.IntentKeyUtils.JSON, form.toString());
                activity.startActivityForResult(intent, org.smartregister.anc.library.util.JsonFormUtils.REQUEST_CODE_GET_JSON);
            }
        } catch (Exception e) {
            Timber.e(e, "JsonFormUtils --> launchANCCloseForm");
        }
    }

    public static void launchSiteCharacteristicsForm(Activity activity) {
        try {
            Intent intent = new Intent(activity, JsonFormActivity.class);
            JSONObject form = FormUtils.getInstance(activity).getFormJson(ConstantsUtils.JsonFormUtils.ANC_SITE_CHARACTERISTICS);
            if (form != null) {
                form.put(ConstantsUtils.JsonFormKeyUtils.ENTITY_ID,
                        activity.getIntent().getStringExtra(ConstantsUtils.IntentKeyUtils.BASE_ENTITY_ID));
                intent.putExtra(ConstantsUtils.IntentKeyUtils.JSON, form.toString());
                activity.startActivityForResult(intent, org.smartregister.anc.library.util.JsonFormUtils.REQUEST_CODE_GET_JSON);
            }
        } catch (Exception e) {
            Timber.e(e, "JsonFormUtils --> launchSiteCharacteristicsForm");
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
                    registrationFormParams.getMiddle().getJSONObject(org.smartregister.anc.library.util.JsonFormUtils.STEP1).getJSONArray(org.smartregister.anc.library.util.JsonFormUtils.FIELDS);

            for (int i = 0; i < fields.length(); i++) {
                if (!"label".equals(fields.getJSONObject(i).getString(ConstantsUtils.KeyUtils.TYPE))) {
                    settings.put(fields.getJSONObject(i).getString(ConstantsUtils.KeyUtils.KEY),
                            StringUtils.isBlank(fields.getJSONObject(i).getString(ConstantsUtils.KeyUtils.VALUE)) ? "0" :
                                    fields.getJSONObject(i).getString(ConstantsUtils.KeyUtils.VALUE));
                }

            }

            return settings;
        } catch (Exception e) {
            Timber.e(e, "JsonFormUtils --> processSiteCharacteristics");
            return null;
        }
    }

    public static String getAutoPopulatedSiteCharacteristicsEditFormString(Context context,
                                                                           Map<String, String> characteristics) {
        try {
            JSONObject form = FormUtils.getInstance(context).getFormJson(ConstantsUtils.JsonFormUtils.ANC_SITE_CHARACTERISTICS);
            Timber.d("Form is " + form.toString());
            if (form != null) {
                form.put(org.smartregister.anc.library.util.JsonFormUtils.ENCOUNTER_TYPE, ConstantsUtils.EventTypeUtils.SITE_CHARACTERISTICS);

                JSONObject stepOne = form.getJSONObject(org.smartregister.anc.library.util.JsonFormUtils.STEP1);
                JSONArray jsonArray = stepOne.getJSONArray(org.smartregister.anc.library.util.JsonFormUtils.FIELDS);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (characteristics.containsKey(jsonObject.getString(org.smartregister.anc.library.util.JsonFormUtils.KEY))) {
                        jsonObject.put(org.smartregister.anc.library.util.JsonFormUtils.READ_ONLY, false);
                        jsonObject.put(org.smartregister.anc.library.util.JsonFormUtils.VALUE,
                                "true".equals(characteristics.get(jsonObject.getString(org.smartregister.anc.library.util.JsonFormUtils.KEY))) ? "1" : "0");
                    }

                }

                return form.toString();
            }
        } catch (Exception e) {
            Timber.e(e, "JsonFormUtils --> getAutoPopulatedSiteCharacteristicsEditFormString");
        }

        return "";
    }

    public static Pair<Event, Event> createContactVisitEvent(List<String> formSubmissionIDs,
                                                             Map<String, String> womanDetails) {
        if (formSubmissionIDs.size() < 1 && womanDetails.get(ConstantsUtils.REFERRAL) == null) {
            return null;
        }

        try {

            String contactNo = womanDetails.get(DBConstantsUtils.KeyUtils.NEXT_CONTACT);
            String contactStartDate = womanDetails.get(DBConstantsUtils.KeyUtils.VISIT_START_DATE);
            String baseEntityId = womanDetails.get(DBConstantsUtils.KeyUtils.BASE_ENTITY_ID);

            Event contactVisitEvent = (Event) new Event().withBaseEntityId(baseEntityId).withEventDate(new Date())
                    .withEventType(ConstantsUtils.EventTypeUtils.CONTACT_VISIT).withEntityType(DBConstantsUtils.CONTACT_ENTITY_TYPE)
                    .withFormSubmissionId(org.smartregister.anc.library.util.JsonFormUtils.generateRandomUUIDString())
                    .withDateCreated(getContactStartDate(contactStartDate));

            String currentContactNo;
            if (womanDetails.get(ConstantsUtils.REFERRAL) == null) {
                currentContactNo = ConstantsUtils.CONTACT + " " + contactNo;
            } else {
                currentContactNo = ConstantsUtils.CONTACT + " " + womanDetails.get(ConstantsUtils.REFERRAL);
            }
            contactVisitEvent.addDetails(ConstantsUtils.CONTACT, currentContactNo);
            contactVisitEvent.addDetails(ConstantsUtils.FORM_SUBMISSION_IDS, formSubmissionIDs.toString());

            tagSyncMetadata(AncLibrary.getInstance().getContext().userService().getAllSharedPreferences(),
                    contactVisitEvent);

            PatientRepositoryHelper.updateContactVisitStartDate(baseEntityId, null);//reset contact visit date


            //Update client
            EventClientRepository db = AncLibrary.getInstance().getEventClientRepository();
            JSONObject clientForm = db.getClientByBaseEntityId(baseEntityId);

            JSONObject attributes = clientForm.getJSONObject(ConstantsUtils.JsonFormKeyUtils.ATTRIBUTES);
            attributes.put(DBConstantsUtils.KeyUtils.NEXT_CONTACT, contactNo);
            attributes.put(DBConstantsUtils.KeyUtils.NEXT_CONTACT_DATE, womanDetails.get(DBConstantsUtils.KeyUtils.NEXT_CONTACT_DATE));
            attributes.put(DBConstantsUtils.KeyUtils.LAST_CONTACT_RECORD_DATE,
                    womanDetails.get(DBConstantsUtils.KeyUtils.LAST_CONTACT_RECORD_DATE));
            attributes.put(DBConstantsUtils.KeyUtils.CONTACT_STATUS, womanDetails.get(DBConstantsUtils.KeyUtils.CONTACT_STATUS));
            attributes.put(DBConstantsUtils.KeyUtils.YELLOW_FLAG_COUNT, womanDetails.get(DBConstantsUtils.KeyUtils.YELLOW_FLAG_COUNT));
            attributes.put(DBConstantsUtils.KeyUtils.RED_FLAG_COUNT, womanDetails.get(DBConstantsUtils.KeyUtils.RED_FLAG_COUNT));
            attributes.put(DBConstantsUtils.KeyUtils.EDD, womanDetails.get(DBConstantsUtils.KeyUtils.EDD));
            clientForm.put(ConstantsUtils.JsonFormKeyUtils.ATTRIBUTES, attributes);

            FormTag formTag = getFormTag(AncLibrary.getInstance().getContext().allSharedPreferences());
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

    private static Date getContactStartDate(String contactStartDate) {
        try {
            return new LocalDate(contactStartDate).toDate();
        } catch (Exception e) {
            return new LocalDate().toDate();
        }
    }

    protected static Event createUpdateClientDetailsEvent(String baseEntityId) {

        Event updateChildDetailsEvent = (Event) new Event().withBaseEntityId(baseEntityId).withEventDate(new Date())
                .withEventType(ConstantsUtils.EventTypeUtils.UPDATE_REGISTRATION).withEntityType(DBConstantsUtils.WOMAN_TABLE_NAME)
                .withFormSubmissionId(org.smartregister.anc.library.util.JsonFormUtils.generateRandomUUIDString()).withDateCreated(new Date());

        org.smartregister.anc.library.util.JsonFormUtils
                .tagSyncMetadata(AncLibrary.getInstance().getContext().allSharedPreferences(), updateChildDetailsEvent);

        return updateChildDetailsEvent;
    }

    public static Event processContactFormEvent(JSONObject jsonForm, String baseEntityId) {
        AllSharedPreferences allSharedPreferences = AncLibrary.getInstance().getContext().allSharedPreferences();

        JSONArray fields = null;
        try {
            fields = org.smartregister.anc.library.util.JsonFormUtils.getMultiStepFormFields(jsonForm);
        } catch (JsonFormMissingStepCountException e) {
            e.printStackTrace();
        }

        String entityId = org.smartregister.anc.library.util.JsonFormUtils.getString(jsonForm, org.smartregister.anc.library.util.JsonFormUtils.ENTITY_ID);
        if (StringUtils.isBlank(entityId)) {
            entityId = baseEntityId;
        }

        String encounterType = org.smartregister.anc.library.util.JsonFormUtils.getString(jsonForm, ENCOUNTER_TYPE);
        JSONObject metadata = org.smartregister.anc.library.util.JsonFormUtils.getJSONObject(jsonForm, METADATA);

        FormTag formTag = getFormTag(allSharedPreferences);
        Event baseEvent = org.smartregister.util.JsonFormUtils
                .createEvent(fields, metadata, formTag, entityId, encounterType, DBConstantsUtils.WOMAN_TABLE_NAME);

        tagSyncMetadata(allSharedPreferences, baseEvent);// tag docs

        return baseEvent;
    }

    public static JSONObject readJsonFromAsset(Context context, String filePath) throws Exception {
        InputStream inputStream = context.getAssets().open(filePath + ".json");
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        String jsonString;
        StringBuilder stringBuilder = new StringBuilder();
        while ((jsonString = reader.readLine()) != null) {
            stringBuilder.append(jsonString);
        }
        inputStream.close();
        return new JSONObject(stringBuilder.toString());
    }

    public List<ContactSummaryModel> generateNextContactSchedule(String edd, List<String> contactSchedule,
                                                                 Integer lastContactSequence) {
        List<ContactSummaryModel> contactDates = new ArrayList<>();
        Integer contactSequence = lastContactSequence;
        if (!TextUtils.isEmpty(edd)) {
            LocalDate localDate = new LocalDate(edd);
            LocalDate lmpDate = localDate.minusWeeks(ConstantsUtils.DELIVERY_DATE_WEEKS);

            for (String contactWeeks : contactSchedule) {
                contactDates.add(new ContactSummaryModel(String.format(
                        AncLibrary.getInstance().getApplicationContext().getString(R.string.contact_number),
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

        if (AncLibrary.getInstance().getAncRulesEngineHelper().getRelevance(facts, yamlConfigItem.getIsRedFont())) {
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

    public class Template {
        public String title = "";
        public String detail = "";
    }
}
