package org.smartregister.anc.library.model;

import android.text.TextUtils;

import org.smartregister.anc.library.constants.ANCJsonFormConstants;
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
import org.smartregister.anc.library.repository.PartialContactRepository;
import org.smartregister.anc.library.repository.PatientRepository;
import org.smartregister.anc.library.repository.PreviousContactRepository;
import org.smartregister.anc.library.util.ANCFormUtils;
import org.smartregister.anc.library.util.ANCJsonFormUtils;
import org.smartregister.anc.library.util.ConstantsUtils;
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

public class ContactVisit {
    private Map<String, String> details;
    private String referral;
    private String baseEntityId;
    private int nextContact;
    private String nextContactVisitDate;
    private PartialContactRepository partialContactRepository;
    private List<PartialContact> partialContactList;
    private Facts facts;
    private List<String> formSubmissionIDs;
    private WomanDetail womanDetail;
    private Map<String, Integer> attentionFlagCountMap = new HashMap<>();
    private List<String> parsableFormsList =
            Arrays.asList(ConstantsUtils.JsonFormUtils.ANC_QUICK_CHECK, ConstantsUtils.JsonFormUtils.ANC_PROFILE,
                    ConstantsUtils.JsonFormUtils.ANC_SYMPTOMS_FOLLOW_UP, ConstantsUtils.JsonFormUtils.ANC_PHYSICAL_EXAM,
                    ConstantsUtils.JsonFormUtils.ANC_TEST, ConstantsUtils.JsonFormUtils.ANC_COUNSELLING_TREATMENT);
    private Map<String, Long> currentClientTasks = new HashMap<>();
    private ANCFormUtils ancFormUtils = new ANCFormUtils();

    public ContactVisit(Map<String, String> details, String referral, String baseEntityId, int nextContact,
                        String nextContactVisitDate, PartialContactRepository partialContactRepository,
                        List<PartialContact> partialContactList) {
        this.details = details;
        this.referral = referral;
        this.baseEntityId = baseEntityId;
        this.nextContact = nextContact;
        this.nextContactVisitDate = nextContactVisitDate;
        this.partialContactRepository = partialContactRepository;
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

        getCurrentClientsTasks(baseEntityId);
        updateEventAndRequiredStepsField(baseEntityId, partialContactRepository, partialContactList, facts, formSubmissionIDs);

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
        PatientRepository.updateContactVisitDetails(womanDetail, true);
        return this;
    }

    /**StringUtils.isNotBlank(previousContactValue)
     * Returns a {@link Map} of the tasks keys and task id.  These are used to delete the tasks in case a test with the same key is completed doing the current contact.
     *
     * @param baseEntityId {@link String} Client's base entity id.
     */
    private void getCurrentClientsTasks(String baseEntityId) {
        List<Task> tasksList = AncLibrary.getInstance().getContactTasksRepository().getTasks(baseEntityId, null);
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

    private void updateEventAndRequiredStepsField(String baseEntityId, PartialContactRepository partialContactRepository,
                                                  List<PartialContact> partialContactList, Facts facts, List<String> formSubmissionIDs) throws Exception {
        if (partialContactList != null) {
            Collections.sort(partialContactList, (firstPartialContact, secondPartialContact) -> firstPartialContact.getSortOrder().compareTo(secondPartialContact.getSortOrder()));

            for (PartialContact partialContact : partialContactList) {
                JSONObject formObject = ANCJsonFormUtils.toJSONObject(
                        partialContact.getFormJsonDraft() != null ? partialContact.getFormJsonDraft() :
                                partialContact.getFormJson());

                if (formObject != null) {
                    //process form details
                    if (parsableFormsList.contains(partialContact.getType())) {
                        processFormFieldKeyValues(baseEntityId, formObject, String.valueOf(partialContact.getContactNo()));
                    }

                    //process attention flags
                    ANCFormUtils.processRequiredStepsField(facts, formObject);

                    //process events
                    Event event = ANCJsonFormUtils.processContactFormEvent(formObject, baseEntityId);
                    formSubmissionIDs.add(event.getFormSubmissionId());

                    JSONObject eventJson = new JSONObject(ANCJsonFormUtils.gson.toJson(event));
                    eventJson.put(ANCJsonFormConstants.Properties.DETAILS, JsonFormUtils.getJSONObject(formObject, ANCJsonFormConstants.Properties.DETAILS));
                    AncLibrary.getInstance().getEcSyncHelper().addEvent(baseEntityId, eventJson);

                    processTasks(formObject);
                }

                //Remove partial contact
                partialContactRepository.deletePartialContact(partialContact.getId());
            }
        }
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
                    JSONArray stepArray = object.getJSONObject(key).getJSONArray(ANCJsonFormConstants.FIELDS);

                    for (int i = 0; i < stepArray.length(); i++) {
                        JSONObject fieldObject = stepArray.getJSONObject(i);
                        ANCFormUtils.processSpecialWidgets(fieldObject);

                        if (fieldObject.getString(ANCJsonFormConstants.TYPE).equals(ANCJsonFormConstants.EXPANSION_PANEL)) {
                            saveExpansionPanelPreviousValues(baseEntityId, fieldObject, contactNo);
                            continue;
                        }

                        //Do not save empty checkbox values with nothing inside square braces ([])
                        if (fieldObject.has(ANCJsonFormConstants.VALUE) &&
                                !TextUtils.isEmpty(fieldObject.getString(ANCJsonFormConstants.VALUE)) &&
                                !isCheckboxValueEmpty(fieldObject)) {

                            fieldObject.put(PreviousContactRepository.CONTACT_NO, contactNo);
                            ancFormUtils.savePreviousContactItem(baseEntityId, fieldObject);
                        }

                        if (fieldObject.has(ConstantsUtils.KeyUtils.SECONDARY_VALUES) &&
                                fieldObject.getJSONArray(ConstantsUtils.KeyUtils.SECONDARY_VALUES).length() > 0) {
                            JSONArray secondaryValues = fieldObject.getJSONArray(ConstantsUtils.KeyUtils.SECONDARY_VALUES);
                            for (int count = 0; count < secondaryValues.length(); count++) {
                                JSONObject secondaryValuesJSONObject = secondaryValues.getJSONObject(count);
                                secondaryValuesJSONObject.put(PreviousContactRepository.CONTACT_NO, contactNo);
                                ancFormUtils.savePreviousContactItem(baseEntityId, secondaryValuesJSONObject);
                            }
                        }
                    }
                }
            }
        }

    }

    private void processTasks(JSONObject formObject) {
        try {
            String encounterType = formObject.getString(ConstantsUtils.JsonFormKeyUtils.ENCOUNTER_TYPE);
            if (formObject.has(ConstantsUtils.JsonFormKeyUtils.ENCOUNTER_TYPE) && StringUtils.isNotBlank(encounterType) && ConstantsUtils.JsonFormUtils.ANC_TEST_ENCOUNTER_TYPE.equals(encounterType)) {
                JSONObject dueStep = formObject.optJSONObject(ANCJsonFormConstants.STEP1);
                if (dueStep != null && dueStep.has(ANCJsonFormConstants.STEP_TITLE) && (ConstantsUtils.DUE.equals(dueStep.getString(ANCJsonFormConstants.STEP_TITLE)) || ConstantsUtils.DUE_BHASA.equals(dueStep.getString(ANCJsonFormConstants.STEP_TITLE)))) {
                    JSONArray stepFields = dueStep.optJSONArray(ANCJsonFormConstants.FIELDS);
                    if (stepFields != null && stepFields.length() > 0) {
                        saveOrDeleteTasks(stepFields);
                    }
                }
            }
        } catch (JSONException e) {
            Timber.e(e, " --> processTasks");
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
        if (object.has(ANCJsonFormConstants.INVISIBLE_REQUIRED_FIELDS)) {
            String key = ANCJsonFormConstants.INVISIBLE_REQUIRED_FIELDS + "_" + object.getString(ConstantsUtils.JsonFormKeyUtils.ENCOUNTER_TYPE).toLowerCase().replace(" ", "_");
            ancFormUtils.savePreviousContactItem(baseEntityId, new JSONObject().put(ANCJsonFormConstants.KEY, key)
                    .put(ANCJsonFormConstants.VALUE, object.getString(ANCJsonFormConstants.INVISIBLE_REQUIRED_FIELDS))
                    .put(PreviousContactRepository.CONTACT_NO, contactNo));
        }
    }

    private void saveExpansionPanelPreviousValues(String baseEntityId, JSONObject fieldObject, String contactNo) throws JSONException {
        if (fieldObject != null) {
            JSONArray value = fieldObject.optJSONArray(ANCJsonFormConstants.VALUE);
            if (value == null) {
                return;
            }
            for (int j = 0; j < value.length(); j++) {
                JSONObject valueItem = value.getJSONObject(j);
                ancFormUtils.saveExpansionPanelValues(baseEntityId, contactNo, valueItem);
            }
        }
    }

    private boolean isCheckboxValueEmpty(JSONObject fieldObject) throws JSONException {
        if (!fieldObject.has(ANCJsonFormConstants.VALUE)) {
            return true;
        }
        String currentValue = fieldObject.getString(ANCJsonFormConstants.VALUE);
        return TextUtils.equals(currentValue, "[]") || (currentValue.length() == 2
                && currentValue.startsWith("[") && currentValue.endsWith("]"));
    }

    private void saveOrDeleteTasks(@NotNull JSONArray stepFields) throws JSONException {
        for (int i = 0; i < stepFields.length(); i++) {
            JSONObject field = stepFields.getJSONObject(i);
            if (field != null && field.has(ANCJsonFormConstants.IS_VISIBLE) && field.getBoolean(ANCJsonFormConstants.IS_VISIBLE)) {
                JSONArray jsonArray = field.optJSONArray(ANCJsonFormConstants.VALUE);
                String key = field.optString(ANCJsonFormConstants.KEY);
                if (jsonArray == null || (jsonArray.length() == 0)) {
                    if (getCurrentClientTasks() != null && !getCurrentClientTasks().containsKey(key)) {
                        saveTasks(field);
                    }
                } else {
                    if (StringUtils.isNotBlank(key) && getCurrentClientTasks() != null) {
                        if (checkTestsStatus(jsonArray)) {
                            if (!getCurrentClientTasks().containsKey(key)) {
                                saveTasks(field);
                            }
                        } else {
                            Long tasksId = getCurrentClientTasks().get(key);
                            if (tasksId != null) {
                                AncLibrary.getInstance().getContactTasksRepository().deleteContactTask(tasksId);
                            }
                        }
                    }
                }
            }
        }
    }

    public Map<String, Long> getCurrentClientTasks() {
        return currentClientTasks;
    }

    private void saveTasks(JSONObject field) {
        if (field != null) {
            String key = field.optString(ANCJsonFormConstants.KEY);
            AncLibrary.getInstance().getContactTasksRepository().saveOrUpdateTasks(getTask(field, key));
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
                if (value != null && value.has(ANCJsonFormConstants.TYPE) && ANCJsonFormConstants.EXTENDED_RADIO_BUTTON.equals(value.getString(ANCJsonFormConstants.TYPE))) {
                    JSONArray givenValue = value.getJSONArray(ANCJsonFormConstants.VALUES);
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

    @NotNull
    private Task getTask(JSONObject field, String key) {
        Task task = new Task();
        task.setBaseEntityId(baseEntityId);
        task.setKey(key);
        task.setValue(String.valueOf(field));
        task.setUpdated(false);
        task.setComplete(ANCJsonFormUtils.checkIfTaskIsComplete(field));
        task.setCreatedAt(Calendar.getInstance().getTimeInMillis());
        return task;
    }

    public void setCurrentClientTasks(Map<String, Long> currentClientTasks) {
        this.currentClientTasks = currentClientTasks;
    }

    protected PreviousContactRepository getPreviousContactRepository() {
        return AncLibrary.getInstance().getPreviousContactRepository();
    }
}