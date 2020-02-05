package org.smartregister.anc.library.model;

import android.text.TextUtils;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.rules.RuleConstant;

import org.apache.commons.lang3.StringUtils;
import org.jeasy.rules.api.Facts;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.anc.library.AncLibrary;
import org.smartregister.anc.library.domain.WomanDetail;
import org.smartregister.anc.library.domain.YamlConfig;
import org.smartregister.anc.library.domain.YamlConfigItem;
import org.smartregister.anc.library.repository.PartialContactRepositoryHelper;
import org.smartregister.anc.library.repository.PatientRepositoryHelper;
import org.smartregister.anc.library.repository.PreviousContactRepositoryHelper;
import org.smartregister.anc.library.util.ConstantsUtils;
import org.smartregister.anc.library.util.ContactJsonFormUtils;
import org.smartregister.anc.library.util.DBConstantsUtils;
import org.smartregister.anc.library.util.FilePathUtils;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.util.JsonFormUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

import static org.smartregister.anc.library.util.ContactJsonFormUtils.extractItemValue;

public class ContactVisit {
    private Map<String, String> details;
    private String referral;
    private String baseEntityId;
    private int nextContact;
    private String nextContactVisitDate;
    private PartialContactRepositoryHelper partialContactRepositoryHelper;
    private List<PartialContact> partialContactList;
    private Facts facts;
    private List<String> formSubmissionIDs;
    private WomanDetail womanDetail;
    private Map<String, Integer> attentionFlagCountMap = new HashMap<>();
    private List<String> parsableFormsList =
            Arrays.asList(ConstantsUtils.JsonFormUtils.ANC_QUICK_CHECK, ConstantsUtils.JsonFormUtils.ANC_PROFILE,
                    ConstantsUtils.JsonFormUtils.ANC_SYMPTOMS_FOLLOW_UP, ConstantsUtils.JsonFormUtils.ANC_PHYSICAL_EXAM,
                    ConstantsUtils.JsonFormUtils.ANC_TEST, ConstantsUtils.JsonFormUtils.ANC_COUNSELLING_TREATMENT);
    private Map<String, Long> currentClientTasks;

    public ContactVisit(Map<String, String> details, String referral, String baseEntityId, int nextContact,
                        String nextContactVisitDate, PartialContactRepositoryHelper partialContactRepositoryHelper,
                        List<PartialContact> partialContactList) {
        this.details = details;
        this.referral = referral;
        this.baseEntityId = baseEntityId;
        this.nextContact = nextContact;
        this.nextContactVisitDate = nextContactVisitDate;
        this.partialContactRepositoryHelper = partialContactRepositoryHelper;
        this.partialContactList = partialContactList;
    }

    public Facts getFacts() {
        return facts;
    }

    public List<String> getFormSubmissionIDs() {
        return formSubmissionIDs;
    }

    public WomanDetail getWomanDetail() {
        return womanDetail;
    }

    public ContactVisit invoke() throws Exception {
        facts = new Facts();
        formSubmissionIDs = new ArrayList<>();

        updateEventAndRequiredStepsField(baseEntityId, partialContactRepositoryHelper, partialContactList, facts, formSubmissionIDs);
        getCurrentClientsTasks(baseEntityId);

        womanDetail = getWomanDetail(baseEntityId, nextContactVisitDate, nextContact);

        processAttentionFlags(womanDetail, facts);

        if (referral != null) {
            int yellowFlagCount = 0;
            int redFlagCount = 0;
            if (details.containsKey(DBConstantsUtils.KeyUtils.YELLOW_FLAG_COUNT) && details.get(DBConstantsUtils.KeyUtils.YELLOW_FLAG_COUNT) != null) {
                yellowFlagCount = Integer.valueOf(details.get(DBConstantsUtils.KeyUtils.YELLOW_FLAG_COUNT));
            }

            if (details.containsKey(DBConstantsUtils.KeyUtils.RED_FLAG_COUNT) && details.get(DBConstantsUtils.KeyUtils.RED_FLAG_COUNT) != null) {
                redFlagCount = Integer.valueOf(details.get(DBConstantsUtils.KeyUtils.RED_FLAG_COUNT));
            }

            womanDetail.setReferral(true);
            womanDetail.setRedFlagCount(redFlagCount);
            womanDetail.setYellowFlagCount(yellowFlagCount);
            womanDetail.setContactStatus(details.get(DBConstantsUtils.KeyUtils.PREVIOUS_CONTACT_STATUS));
            womanDetail.setPreviousContactStatus(details.get(DBConstantsUtils.KeyUtils.PREVIOUS_CONTACT_STATUS));
            womanDetail.setLastContactRecordDate(details.get(DBConstantsUtils.KeyUtils.LAST_CONTACT_RECORD_DATE));
        }
        PatientRepositoryHelper.updateContactVisitDetails(womanDetail, true);
        return this;
    }

    /**
     * Returns a {@link Map} of the tasks keys and task id.  These are used to delete the tasks in case a test with the same key is completed doing the current contact.
     *
     * @param baseEntityId {@link String} Client's base entity id.
     */
    private void getCurrentClientsTasks(String baseEntityId) {
        List<Task> tasksList = AncLibrary.getInstance().getContactTasksRepositoryHelper().getTasks(baseEntityId, null);
        if (tasksList != null && tasksList.size() > 0) {
            Map<String, Long> tasksMap = new HashMap<>();
            for (int i = 0; i < tasksList.size(); i++) {
                Task task = tasksList.get(i);
                if (task != null) {
                    tasksMap.put(task.getKey(), task.getId());
                }
            }
            setCurrentClientTasks(tasksMap);
        }
    }

    private void updateEventAndRequiredStepsField(String baseEntityId, PartialContactRepositoryHelper partialContactRepositoryHelper,
                                                  List<PartialContact> partialContactList, Facts facts, List<String> formSubmissionIDs) throws Exception {
        if (partialContactList != null) {
            Collections.sort(partialContactList, (firstPartialContact, secondPartialContact) -> firstPartialContact.getSortOrder().compareTo(secondPartialContact.getSortOrder()));

            for (PartialContact partialContact : partialContactList) {
                JSONObject formObject = org.smartregister.anc.library.util.JsonFormUtils.toJSONObject(
                        partialContact.getFormJsonDraft() != null ? partialContact.getFormJsonDraft() :
                                partialContact.getFormJson());

                if (formObject != null) {
                    //process form details
                    if (parsableFormsList.contains(partialContact.getType())) {
                        processFormFieldKeyValues(baseEntityId, formObject, String.valueOf(partialContact.getContactNo()));
                    }

                    //process attention flags
                    ContactJsonFormUtils.processRequiredStepsField(facts, formObject);

                    //process events
                    Event event = org.smartregister.anc.library.util.JsonFormUtils.processContactFormEvent(formObject, baseEntityId);
                    formSubmissionIDs.add(event.getFormSubmissionId());

                    JSONObject eventJson = new JSONObject(org.smartregister.anc.library.util.JsonFormUtils.gson.toJson(event));
                    eventJson.put(JsonFormConstants.Properties.DETAILS, JsonFormUtils.getJSONObject(formObject, JsonFormConstants.Properties.DETAILS));
                    AncLibrary.getInstance().getEcSyncHelper().addEvent(baseEntityId, eventJson);

                    processTasks(formObject);
                }

                //Remove partial contact
                partialContactRepositoryHelper.deletePartialContact(partialContact.getId());
            }
        }
    }

    private void processTasks(JSONObject formObject) {
        try {
            String encounterType = formObject.getString(ConstantsUtils.JsonFormKeyUtils.ENCOUNTER_TYPE);
            if (formObject.has(ConstantsUtils.JsonFormKeyUtils.ENCOUNTER_TYPE) && StringUtils.isNotBlank(encounterType) && ConstantsUtils.JsonFormUtils.ANC_TEST_ENCOUNTER_TYPE.equals(encounterType)) {
                JSONObject dueStep = formObject.optJSONObject(JsonFormConstants.STEP1);
                if (dueStep != null && dueStep.has(JsonFormConstants.STEP_TITLE) && ConstantsUtils.DUE.equals(dueStep.getString(JsonFormConstants.STEP_TITLE))) {
                    JSONArray stepFields = dueStep.optJSONArray(JsonFormConstants.FIELDS);
                    if (stepFields != null && stepFields.length() > 0) {
                        for (int i = 0; i < stepFields.length(); i++) {
                            JSONObject field = stepFields.getJSONObject(i);
                            if (field != null && field.has(JsonFormConstants.IS_VISIBLE) && field.getBoolean(JsonFormConstants.IS_VISIBLE)) {
                                JSONArray jsonArray = field.optJSONArray(JsonFormConstants.VALUE);
                                if (jsonArray == null || (jsonArray.length() == 0)) {
                                    saveTasks(field);
                                } else {
                                    String key = field.optString(JsonFormConstants.KEY);
                                    if (StringUtils.isNotBlank(key)) {
                                        if (checkTestsStatus(jsonArray)) {
                                            if (!getCurrentClientTasks().containsKey(key)) {
                                                saveTasks(field);
                                            }
                                        } else {
                                            Long tasksId = getCurrentClientTasks().get(key);
                                            if (tasksId != null) {
                                                AncLibrary.getInstance().getContactTasksRepositoryHelper().deleteContactTask(tasksId);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (JSONException e) {
            Timber.e(e, " --> processTasks");
        }
    }

    /**
     * Checks where a test qualifies to be a tasks.  This happens in case a test is marked as ordered or not done;
     *
     * @param valueArray {@link JSONArray} the expansion panel values
     * @return isTasks {@link Boolean} true/false if true then it means the test qualifies to be a task.
     */
    private boolean checkTestsStatus(JSONArray valueArray) {
        boolean isTask = false;
        try {
            for (int i = 0; i < valueArray.length(); i++) {
                JSONObject value = valueArray.getJSONObject(i);
                if (value != null && value.has(JsonFormConstants.TYPE) && JsonFormConstants.EXTENDED_RADIO_BUTTON.equals(value.getString(JsonFormConstants.TYPE))) {
                    JSONArray givenValue = value.getJSONArray(JsonFormConstants.VALUES);
                    if (givenValue.length() > 0) {
                        String firstValue = givenValue.optString(0);
                        if (StringUtils.isNotBlank(firstValue) && (firstValue.contains(ConstantsUtils.AncRadioButtonOptionTypesUtils.ORDERED) || firstValue.contains(ConstantsUtils.AncRadioButtonOptionTypesUtils.NOT_DONE))) {
                            isTask = true;
                        }
                    }
                    break;
                }
            }
        } catch (JSONException e) {
            Timber.e(e, " --> checkTestsStatus");
        }
        return isTask;
    }

    private void saveTasks(JSONObject field) {
        if (field != null) {
            String key = field.optString(JsonFormConstants.KEY);
            AncLibrary.getInstance().getContactTasksRepositoryHelper().saveOrUpdateTasks(getTask(field, key));
        }
    }

    @NotNull
    private Task getTask(JSONObject field, String key) {
        Task task = new Task();
        task.setContactNo(getCurrentContact());
        task.setBaseEntityId(baseEntityId);
        task.setKey(key);
        task.setValue(String.valueOf(field));
        task.setUpdated(false);
        task.setCreatedAt(Calendar.getInstance().getTimeInMillis());
        return task;
    }

    private String getCurrentContact() {
        String contact = "0";
        if (StringUtils.isNotBlank(String.valueOf(nextContact))) {
            contact = nextContact == 1 ? String.valueOf(nextContact) : String.valueOf(nextContact - 1);
        }
        return contact;
    }

    private WomanDetail getWomanDetail(String baseEntityId, String nextContactVisitDate, Integer nextContact) {
        WomanDetail womanDetail = new WomanDetail();
        womanDetail.setBaseEntityId(baseEntityId);
        womanDetail.setNextContact(nextContact);
        womanDetail.setNextContactDate(nextContactVisitDate);
        womanDetail.setContactStatus(ConstantsUtils.AlertStatusUtils.TODAY);
        womanDetail.setPreviousContactStatus(ConstantsUtils.AlertStatusUtils.TODAY);
        return womanDetail;
    }

    private void processAttentionFlags(WomanDetail patientDetail, Facts facts) throws IOException {
        Iterable<Object> ruleObjects = AncLibrary.getInstance().readYaml(FilePathUtils.FileUtils.ATTENTION_FLAGS);

        for (Object ruleObject : ruleObjects) {
            YamlConfig attentionFlagConfig = (YamlConfig) ruleObject;

            for (YamlConfigItem yamlConfigItem : attentionFlagConfig.getFields()) {

                if (AncLibrary.getInstance().getAncRulesEngineHelper().getRelevance(facts, yamlConfigItem.getRelevance())) {
                    Integer requiredFieldCount = attentionFlagCountMap.get(attentionFlagConfig.getGroup());
                    requiredFieldCount = requiredFieldCount == null ? 1 : ++requiredFieldCount;
                    attentionFlagCountMap.put(attentionFlagConfig.getGroup(), requiredFieldCount);

                }
            }
        }

        Integer redCount = attentionFlagCountMap.get(ConstantsUtils.AttentionFlagUtils.RED);
        Integer yellowCount = attentionFlagCountMap.get(ConstantsUtils.AttentionFlagUtils.YELLOW);
        patientDetail.setRedFlagCount(redCount != null ? redCount : 0);
        patientDetail.setYellowFlagCount(yellowCount != null ? yellowCount : 0);
    }

    private void processFormFieldKeyValues(String baseEntityId, JSONObject object, String contactNo) throws Exception {
        if (object != null) {
            persistRequiredInvisibleFields(baseEntityId, contactNo, object);
            Iterator<String> keys = object.keys();

            while (keys.hasNext()) {
                String key = keys.next();

                if (key.startsWith(RuleConstant.STEP)) {
                    JSONArray stepArray = object.getJSONObject(key).getJSONArray(JsonFormConstants.FIELDS);

                    for (int i = 0; i < stepArray.length(); i++) {
                        JSONObject fieldObject = stepArray.getJSONObject(i);
                        ContactJsonFormUtils.processSpecialWidgets(fieldObject);

                        if (fieldObject.getString(JsonFormConstants.TYPE).equals(JsonFormConstants.EXPANSION_PANEL)) {
                            saveExpansionPanelPreviousValues(baseEntityId, fieldObject, contactNo);
                            continue;
                        }

                        //Do not save empty checkbox values with nothing inside square braces ([])
                        if (fieldObject.has(JsonFormConstants.VALUE) &&
                                !TextUtils.isEmpty(fieldObject.getString(JsonFormConstants.VALUE)) &&
                                !isCheckboxValueEmpty(fieldObject)) {

                            fieldObject.put(PreviousContactRepositoryHelper.CONTACT_NO, contactNo);
                            savePreviousContactItem(baseEntityId, fieldObject);
                        }

                        if (fieldObject.has(ConstantsUtils.KeyUtils.SECONDARY_VALUES) &&
                                fieldObject.getJSONArray(ConstantsUtils.KeyUtils.SECONDARY_VALUES).length() > 0) {
                            JSONArray secondaryValues = fieldObject.getJSONArray(ConstantsUtils.KeyUtils.SECONDARY_VALUES);
                            for (int count = 0; count < secondaryValues.length(); count++) {
                                JSONObject secondaryValuesJSONObject = secondaryValues.getJSONObject(count);
                                secondaryValuesJSONObject.put(PreviousContactRepositoryHelper.CONTACT_NO, contactNo);
                                savePreviousContactItem(baseEntityId, secondaryValuesJSONObject);
                            }
                        }
                    }
                }
            }
        }

    }

    /***
     * Method that persist previous invisible required fields
     * @param baseEntityId unique Id for the woman
     * @param contactNo the contact number
     * @param object main form json object
     * @throws JSONException exception thrown
     */
    private void persistRequiredInvisibleFields(String baseEntityId, String contactNo, JSONObject object) throws JSONException {
        if (object.has(JsonFormConstants.INVISIBLE_REQUIRED_FIELDS)) {
            String key = JsonFormConstants.INVISIBLE_REQUIRED_FIELDS + "_" +
                    object.getString(ConstantsUtils.JsonFormKeyUtils.ENCOUNTER_TYPE)
                            .toLowerCase().replace(" ", "_");
            savePreviousContactItem(baseEntityId, new JSONObject()
                    .put(JsonFormConstants.KEY, key)
                    .put(JsonFormConstants.VALUE, object.getString(JsonFormConstants.INVISIBLE_REQUIRED_FIELDS))
                    .put(PreviousContactRepositoryHelper.CONTACT_NO, contactNo));

        }
    }

    private boolean isCheckboxValueEmpty(JSONObject fieldObject) throws JSONException {
        if (!fieldObject.has(JsonFormConstants.VALUE)) {
            return true;
        }
        String currentValue = fieldObject.getString(JsonFormConstants.VALUE);
        return TextUtils.equals(currentValue, "[]") || (currentValue.length() == 2
                && currentValue.startsWith("[") && currentValue.endsWith("]"));
    }

    private void saveExpansionPanelPreviousValues(String baseEntityId, JSONObject fieldObject, String contactNo)
            throws JSONException {
        if (fieldObject != null) {
            JSONArray value = fieldObject.optJSONArray(JsonFormConstants.VALUE);
            if (value == null) {
                return;
            }
            for (int j = 0; j < value.length(); j++) {
                JSONObject valueItem = value.getJSONObject(j);
                JSONArray valueItemJSONArray = valueItem.getJSONArray(JsonFormConstants.VALUES);
                String result = extractItemValue(valueItem, valueItemJSONArray);
                // do not save empty checkbox values ([])
                if (result.startsWith("[") && result.endsWith("]") && result.length() == 2 ||
                        TextUtils.equals("[]", result)) {
                    return;
                }
                JSONObject itemToSave = new JSONObject();
                itemToSave.put(JsonFormConstants.KEY, valueItem.getString(JsonFormConstants.KEY));
                itemToSave.put(JsonFormConstants.VALUE, result);
                itemToSave.put(PreviousContactRepositoryHelper.CONTACT_NO, contactNo);
                savePreviousContactItem(baseEntityId, itemToSave);
            }
        }
    }

    private void savePreviousContactItem(String baseEntityId, JSONObject fieldObject) throws JSONException {
        PreviousContact previousContact = new PreviousContact();
        previousContact.setKey(fieldObject.getString(JsonFormConstants.KEY));
        previousContact.setValue(fieldObject.getString(JsonFormConstants.VALUE));
        previousContact.setBaseEntityId(baseEntityId);
        previousContact.setContactNo(fieldObject.getString(PreviousContactRepositoryHelper.CONTACT_NO));
        getPreviousContactRepository().savePreviousContact(previousContact);
    }

    protected PreviousContactRepositoryHelper getPreviousContactRepository() {
        return AncLibrary.getInstance().getPreviousContactRepositoryHelper();
    }

    public Map<String, Long> getCurrentClientTasks() {
        return currentClientTasks;
    }

    public void setCurrentClientTasks(Map<String, Long> currentClientTasks) {
        this.currentClientTasks = currentClientTasks;
    }
}
