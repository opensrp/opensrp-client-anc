package org.smartregister.anc.library.model;

import android.text.TextUtils;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.rules.RuleConstant;

import org.jeasy.rules.api.Facts;
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
import org.smartregister.anc.library.util.Constants;
import org.smartregister.anc.library.util.ContactJsonFormUtils;
import org.smartregister.anc.library.util.DBConstants;
import org.smartregister.anc.library.util.FilePath;
import org.smartregister.anc.library.util.JsonFormUtils;
import org.smartregister.clientandeventmodel.Event;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.smartregister.anc.library.util.ContactJsonFormUtils.extractItemValue;

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
            Arrays.asList(Constants.JSON_FORM.ANC_QUICK_CHECK, Constants.JSON_FORM.ANC_PROFILE,
                    Constants.JSON_FORM.ANC_SYMPTOMS_FOLLOW_UP, Constants.JSON_FORM.ANC_PHYSICAL_EXAM,
                    Constants.JSON_FORM.ANC_TEST, Constants.JSON_FORM.ANC_COUNSELLING_TREATMENT);

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

        updateEventAndRequiredStepsField(baseEntityId, partialContactRepository, partialContactList, facts,
                formSubmissionIDs);

        womanDetail = getWomanDetail(baseEntityId, nextContactVisitDate, nextContact);

        processAttentionFlags(womanDetail, facts);

        if (referral != null) {
            int yellowFlagCount = 0;
            int redFlagCount = 0;
            if (details.containsKey(DBConstants.KEY.YELLOW_FLAG_COUNT) && details.get(DBConstants.KEY.YELLOW_FLAG_COUNT) != null) {
                yellowFlagCount = Integer.valueOf(details.get(DBConstants.KEY.YELLOW_FLAG_COUNT));
            }

            if (details.containsKey(DBConstants.KEY.RED_FLAG_COUNT) && details.get(DBConstants.KEY.RED_FLAG_COUNT) != null) {
                redFlagCount = Integer.valueOf(details.get(DBConstants.KEY.RED_FLAG_COUNT));
            }

            womanDetail.setYellowFlagCount(yellowFlagCount);
            womanDetail.setRedFlagCount(redFlagCount);
            womanDetail.setContactStatus(details.get(DBConstants.KEY.CONTACT_STATUS));
            womanDetail.setReferral(true);
            womanDetail.setLastContactRecordDate(details.get(DBConstants.KEY.LAST_CONTACT_RECORD_DATE));
        }
        PatientRepository.updateContactVisitDetails(womanDetail, true);
        return this;
    }

    private void updateEventAndRequiredStepsField(String baseEntityId, PartialContactRepository partialContactRepository,
                                                  List<PartialContact> partialContactList, Facts facts,
                                                  List<String> formSubmissionIDs) throws Exception {
        if (partialContactList != null) {

            Collections.sort(partialContactList, new Comparator<PartialContact>() {
                @Override
                public int compare(PartialContact o1, PartialContact o2) {
                    return o1.getSortOrder().compareTo(o2.getSortOrder());
                }
            });

            for (PartialContact partialContact : partialContactList) {
                JSONObject formObject = JsonFormUtils.toJSONObject(
                        partialContact.getFormJsonDraft() != null ? partialContact.getFormJsonDraft() :
                                partialContact.getFormJson());

                if (formObject != null) {
                    //process form details
                    if (parsableFormsList.contains(partialContact.getType())) {
                        processFormFieldKeyValues(baseEntityId, formObject,
                                String.valueOf(partialContact.getContactNo()));
                    }

                    //process attention flags
                    ContactJsonFormUtils.processRequiredStepsField(facts, formObject);

                    //process events
                    Event event = JsonFormUtils.processContactFormEvent(formObject, baseEntityId);
                    formSubmissionIDs.add(event.getFormSubmissionId());

                    JSONObject eventJson = new JSONObject(JsonFormUtils.gson.toJson(event));
                    AncLibrary.getInstance().getEcSyncHelper().addEvent(baseEntityId, eventJson);
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
        womanDetail.setContactStatus(Constants.ALERT_STATUS.TODAY);
        return womanDetail;
    }

    private void processAttentionFlags(WomanDetail patientDetail, Facts facts) throws IOException {
        Iterable<Object> ruleObjects = AncLibrary.getInstance().readYaml(FilePath.FILE.ATTENTION_FLAGS);

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

        Integer redCount = attentionFlagCountMap.get(Constants.ATTENTION_FLAG.RED);
        Integer yellowCount = attentionFlagCountMap.get(Constants.ATTENTION_FLAG.YELLOW);
        patientDetail.setRedFlagCount(redCount != null ? redCount : 0);
        patientDetail.setYellowFlagCount(yellowCount != null ? yellowCount : 0);
    }

    private void processFormFieldKeyValues(String baseEntityId, JSONObject object, String contactNo) throws Exception {
        if (object != null) {
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

                            fieldObject.put(PreviousContactRepository.CONTACT_NO, contactNo);
                            savePreviousContactItem(baseEntityId, fieldObject);
                        }

                        if (fieldObject.has(Constants.KEY.SECONDARY_VALUES) &&
                                fieldObject.getJSONArray(Constants.KEY.SECONDARY_VALUES).length() > 0) {
                            JSONArray secondaryValues = fieldObject.getJSONArray(Constants.KEY.SECONDARY_VALUES);
                            for (int count = 0; count < secondaryValues.length(); count++) {
                                JSONObject secondaryValuesJSONObject = secondaryValues.getJSONObject(count);
                                secondaryValuesJSONObject.put(PreviousContactRepository.CONTACT_NO, contactNo);
                                savePreviousContactItem(baseEntityId, secondaryValuesJSONObject);
                            }
                        }
                    }
                }
            }
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
                itemToSave.put(PreviousContactRepository.CONTACT_NO, contactNo);
                savePreviousContactItem(baseEntityId, itemToSave);
            }
        }
    }

    private void savePreviousContactItem(String baseEntityId, JSONObject fieldObject) throws JSONException {
        PreviousContact previousContact = new PreviousContact();
        previousContact.setKey(fieldObject.getString(JsonFormConstants.KEY));
        previousContact.setValue(fieldObject.getString(JsonFormConstants.VALUE));
        previousContact.setBaseEntityId(baseEntityId);
        previousContact.setContactNo(fieldObject.getString(PreviousContactRepository.CONTACT_NO));
        getPreviousContactRepository().savePreviousContact(previousContact);
    }

    protected PreviousContactRepository getPreviousContactRepository() {
        return AncLibrary.getInstance().getPreviousContactRepository();
    }
}
