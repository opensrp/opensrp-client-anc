package org.smartregister.anc.interactor;

import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.rules.RuleConstant;

import org.jeasy.rules.api.Facts;
import org.joda.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.anc.application.AncApplication;
import org.smartregister.anc.contract.BaseContactContract;
import org.smartregister.anc.contract.ContactContract;
import org.smartregister.anc.domain.WomanDetail;
import org.smartregister.anc.domain.YamlConfig;
import org.smartregister.anc.domain.YamlConfigItem;
import org.smartregister.anc.model.PartialContact;
import org.smartregister.anc.model.PreviousContact;
import org.smartregister.anc.repository.PartialContactRepository;
import org.smartregister.anc.repository.PatientRepository;
import org.smartregister.anc.repository.PreviousContactRepository;
import org.smartregister.anc.rule.ContactRule;
import org.smartregister.anc.util.AppExecutors;
import org.smartregister.anc.util.Constants;
import org.smartregister.anc.util.ContactJsonFormUtils;
import org.smartregister.anc.util.DBConstants;
import org.smartregister.anc.util.FilePath;
import org.smartregister.anc.util.JsonFormUtils;
import org.smartregister.anc.util.Utils;
import org.smartregister.clientandeventmodel.Event;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.smartregister.anc.util.ContactJsonFormUtils.extractItemValue;

/**
 * Created by keyman 30/07/2018.
 */
public class ContactInteractor extends BaseContactInteractor implements ContactContract.Interactor {

    public static final String TAG = ContactInteractor.class.getName();
    private Map<String, Integer> attentionFlagCountMap = new HashMap<>();
    private List<String> parsableFormsList = Arrays
            .asList(Constants.JSON_FORM.ANC_QUICK_CHECK, Constants.JSON_FORM.ANC_PROFILE,
                    Constants.JSON_FORM.ANC_SYMPTOMS_FOLLOW_UP, Constants.JSON_FORM.ANC_PHYSICAL_EXAM,
                    Constants.JSON_FORM.ANC_TEST, Constants.JSON_FORM.ANC_COUNSELLING_TREATMENT);

    @VisibleForTesting
    ContactInteractor(AppExecutors appExecutors) {
        super(appExecutors);
    }

    public ContactInteractor() {
        this(new AppExecutors());
    }

    @Override
    public void fetchWomanDetails(String baseEntityId, BaseContactContract.InteractorCallback callBack) {
        super.fetchWomanDetails(baseEntityId, callBack);
    }

    @Override
    public HashMap<String, String> finalizeContactForm(final Map<String, String> details) {

        if (details != null) {
            try {

                String referral = details.get(Constants.REFERRAL);
                String baseEntityId = details.get(DBConstants.KEY.BASE_ENTITY_ID);

                int gestationAge = getGestationAge(details);
                int nextContact;
                boolean isFirst = false;
                String nextContactVisitDate;
                if (referral == null) {
                    isFirst = details.get(DBConstants.KEY.NEXT_CONTACT) == null;
                    ContactRule contactRule = new ContactRule(gestationAge, isFirst, baseEntityId);

                    List<Integer> integerList = AncApplication.getInstance().getAncRulesEngineHelper()
                            .getContactVisitSchedule(contactRule, Constants.RULES_FILE.CONTACT_RULES);

                    int nextContactVisitWeeks = integerList.get(0);

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(Constants.DETAILS_KEY.CONTACT_SHEDULE, integerList);
                    addThePreviousContactSchedule(baseEntityId, details, integerList);
                    AncApplication.getInstance().getDetailsRepository()
                            .add(baseEntityId, Constants.DETAILS_KEY.CONTACT_SHEDULE, jsonObject.toString(),
                                    Calendar.getInstance().getTimeInMillis());
                    //convert String to LocalDate ;
                    LocalDate localDate = new LocalDate(details.get(DBConstants.KEY.EDD));
                    nextContactVisitDate = localDate.minusWeeks(Constants.DELIVERY_DATE_WEEKS)
                            .plusWeeks(nextContactVisitWeeks)
                            .toString();
                    nextContact = getNextContact(details);
                } else {
                    nextContact = Integer.parseInt(details.get(DBConstants.KEY.NEXT_CONTACT));
                    nextContactVisitDate = details.get(DBConstants.KEY.NEXT_CONTACT_DATE);
                }


                GetPartialContacts getPartialContacts = new GetPartialContacts(details, referral, baseEntityId, isFirst)
                        .invoke();
                PartialContactRepository partialContactRepository = getPartialContacts.getPartialContactRepository();
                List<PartialContact> partialContactList = getPartialContacts.getPartialContactList();

                UpdateContactVisit updateContactVisit = new UpdateContactVisit(details, referral, baseEntityId, nextContact,
                        nextContactVisitDate, partialContactRepository,
                        partialContactList).invoke();
                Facts facts = updateContactVisit.getFacts();
                List<String> formSubmissionIDs = updateContactVisit.getFormSubmissionIDs();
                WomanDetail womanDetail = updateContactVisit.getWomanDetail();

                //Attention Flags
                String attentionFlagsString;
                if (referral == null) {
                    attentionFlagsString = new JSONObject(facts.asMap()).toString();
                } else {
                    attentionFlagsString =
                            AncApplication.getInstance().getDetailsRepository().getAllDetailsForClient(baseEntityId)
                                    .get(Constants.DETAILS_KEY.ATTENTION_FLAG_FACTS);
                }
                addThePreviousAttentionFlags(baseEntityId, details, attentionFlagsString);
                AncApplication.getInstance().getDetailsRepository()
                        .add(baseEntityId, Constants.DETAILS_KEY.ATTENTION_FLAG_FACTS, attentionFlagsString,
                                Calendar.getInstance().getTimeInMillis());
                updateWomanDetails(details, womanDetail);


                Pair<Event, Event> eventPair = JsonFormUtils.createContactVisitEvent(formSubmissionIDs, details);

                if (eventPair != null) {
                    createEvent(baseEntityId, attentionFlagsString, eventPair);
                    JSONObject updateClientEventJson = new JSONObject(JsonFormUtils.gson.toJson(eventPair.second));
                    AncApplication.getInstance().getEcSyncHelper().addEvent(baseEntityId, updateClientEventJson);
                }

            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
        return (HashMap<String, String>) details;
    }

    private void addThePreviousContactSchedule(String baseEntityId, Map<String, String> details, List<Integer> integerList) {
        PreviousContact previousContact = new PreviousContact();
        previousContact.setBaseEntityId(baseEntityId);
        String contactNo = details.containsKey(Constants.REFERRAL) ? details.get(Constants.REFERRAL) : details
                .get(DBConstants.KEY.NEXT_CONTACT);
        previousContact.setContactNo(contactNo);
        previousContact.setKey(Constants.DETAILS_KEY.CONTACT_SHEDULE);
        previousContact.setValue(String.valueOf(integerList));
        AncApplication.getInstance().getPreviousContactRepository().savePreviousContact(previousContact);
    }

    private void addThePreviousAttentionFlags(String baseEntityId, Map<String, String> details,
                                              String attentionFlagsString) {
        PreviousContact previousContact = new PreviousContact();
        previousContact.setBaseEntityId(baseEntityId);
        String contactNo = details.containsKey(Constants.REFERRAL) ? details.get(Constants.REFERRAL) : details
                .get(DBConstants.KEY.NEXT_CONTACT);
        previousContact.setContactNo(contactNo);
        previousContact.setKey(Constants.DETAILS_KEY.ATTENTION_FLAG_FACTS);
        previousContact.setValue(attentionFlagsString);
        AncApplication.getInstance().getPreviousContactRepository().savePreviousContact(previousContact);
    }

    private void createEvent(String baseEntityId, String attentionFlagsString, Pair<Event, Event> eventPair)
            throws JSONException {
        Event event = eventPair.first;
        //Here we save state
        event.addDetails(Constants.DETAILS_KEY.ATTENTION_FLAG_FACTS, attentionFlagsString);
        String currentContactState = getCurrentContactState(baseEntityId);
        if (currentContactState != null) {
            event.addDetails(Constants.DETAILS_KEY.PREVIOUS_CONTACTS, currentContactState);
        }
        JSONObject eventJson = new JSONObject(JsonFormUtils.gson.toJson(event));
        AncApplication.getInstance().getEcSyncHelper().addEvent(baseEntityId, eventJson);
    }

    private void updateWomanDetails(Map<String, String> details, WomanDetail womanDetail) {
        //update woman profile details
        if (details != null && details.get(Constants.REFERRAL) != null) {
            details.put(DBConstants.KEY.LAST_CONTACT_RECORD_DATE, details.get(DBConstants.KEY.LAST_CONTACT_RECORD_DATE));
            details.put(DBConstants.KEY.YELLOW_FLAG_COUNT, details.get(DBConstants.KEY.YELLOW_FLAG_COUNT));
            details.put(DBConstants.KEY.RED_FLAG_COUNT, details.get(DBConstants.KEY.RED_FLAG_COUNT));
        } else {
            details.put(DBConstants.KEY.CONTACT_STATUS, womanDetail.getContactStatus());
            details.put(DBConstants.KEY.LAST_CONTACT_RECORD_DATE, Utils.getDBDateToday());
            details.put(DBConstants.KEY.YELLOW_FLAG_COUNT, womanDetail.getYellowFlagCount().toString());
            details.put(DBConstants.KEY.RED_FLAG_COUNT, womanDetail.getRedFlagCount().toString());

        }
        details.put(DBConstants.KEY.CONTACT_STATUS, womanDetail.getContactStatus());
        details.put(DBConstants.KEY.NEXT_CONTACT, womanDetail.getNextContact().toString());
        details.put(DBConstants.KEY.NEXT_CONTACT_DATE, womanDetail.getNextContactDate());
    }

    private int getGestationAge(Map<String, String> details) {
        return details.containsKey(DBConstants.KEY.EDD) && details.get(DBConstants.KEY.EDD) != null ? Utils
                .getGestationAgeFromEDDate(details.get(DBConstants.KEY.EDD)) : 4;
    }

    private int getNextContact(Map<String, String> details) {
        Integer nextContact = details.containsKey(DBConstants.KEY.NEXT_CONTACT) && details
                .get(DBConstants.KEY.NEXT_CONTACT) != null ? Integer.valueOf(details.get(DBConstants.KEY.NEXT_CONTACT)) : 1;
        nextContact += 1;
        return nextContact;
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

                        if (fieldObject.has(JsonFormConstants.VALUE) && !TextUtils
                                .isEmpty(fieldObject.getString(JsonFormConstants.VALUE))) {

                            fieldObject.put(PreviousContactRepository.CONTACT_NO, contactNo);
                            savePreviousContactItem(baseEntityId, fieldObject);

                        }

                    }

                }
            }
        }

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
        return AncApplication.getInstance().getPreviousContactRepository();
    }

    private String getCurrentContactState(String baseEntityId) throws JSONException {
        List<PreviousContact> previousContactList = getPreviousContactRepository().getPreviousContacts(baseEntityId, null);
        JSONObject stateObject = null;
        if (previousContactList != null) {
            stateObject = new JSONObject();

            for (PreviousContact previousContact : previousContactList) {
                stateObject.put(previousContact.getKey(), previousContact.getValue());

            }

        }

        return stateObject != null ? stateObject.toString() : null;
    }

    private class GetPartialContacts {
        private Map<String, String> details;
        private String referral;
        private String baseEntityId;
        private boolean isFirst;
        private PartialContactRepository partialContactRepository;
        private List<PartialContact> partialContactList;

        public GetPartialContacts(Map<String, String> details, String referral, String baseEntityId, boolean isFirst) {
            this.details = details;
            this.referral = referral;
            this.baseEntityId = baseEntityId;
            this.isFirst = isFirst;
        }

        public PartialContactRepository getPartialContactRepository() {
            return partialContactRepository;
        }

        public List<PartialContact> getPartialContactList() {
            return partialContactList;
        }

        public GetPartialContacts invoke() {
            partialContactRepository = AncApplication.getInstance()
                    .getPartialContactRepository();

            if (partialContactRepository != null) {
                if (isFirst) {
                    partialContactList = partialContactRepository.getPartialContacts(baseEntityId, 1);
                } else {
                    if (referral != null) {
                        partialContactList = partialContactRepository.getPartialContacts(baseEntityId,
                                Integer.valueOf(details.get(Constants.REFERRAL)));
                    } else {
                        partialContactList = partialContactRepository.getPartialContacts(baseEntityId,
                                Integer.valueOf(details.get(DBConstants.KEY.NEXT_CONTACT)));
                    }
                }
            } else {
                partialContactList = null;
            }
            return this;
        }
    }

    private class UpdateContactVisit {
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

        public UpdateContactVisit(Map<String, String> details, String referral, String baseEntityId, int nextContact,
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

        public UpdateContactVisit invoke() throws Exception {
            facts = new Facts();
            formSubmissionIDs = new ArrayList<>();

            updateEventAndRequiredStepsField(baseEntityId, partialContactRepository, partialContactList, facts,
                    formSubmissionIDs);

            womanDetail = getWomanDetail(baseEntityId, nextContactVisitDate, nextContact);

            processAttentionFlags(womanDetail, facts);

            if (referral != null) {
                womanDetail.setYellowFlagCount(Integer.valueOf(details.get(DBConstants.KEY.YELLOW_FLAG_COUNT)));
                womanDetail.setRedFlagCount(Integer.valueOf(details.get(DBConstants.KEY.RED_FLAG_COUNT)));
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
                            partialContact.getFormJsonDraft() != null ? partialContact.getFormJsonDraft() : partialContact
                                    .getFormJson());

                    if (formObject != null) {
                        //process form details
                        if (parsableFormsList.contains(partialContact.getType())) {
                            processFormFieldKeyValues(baseEntityId, formObject,
                                    String.valueOf(partialContact.getContactNo()));
                        }

                        //process attention flags
                        ContactJsonFormUtils.processRequiredStepsField(facts, formObject, AncApplication.getInstance().getApplicationContext());

                        //process events
                        Event event = JsonFormUtils.processContactFormEvent(formObject, baseEntityId);
                        formSubmissionIDs.add(event.getFormSubmissionId());

                        JSONObject eventJson = new JSONObject(JsonFormUtils.gson.toJson(event));
                        AncApplication.getInstance().getEcSyncHelper().addEvent(baseEntityId, eventJson);

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

            Iterable<Object> ruleObjects = AncApplication.getInstance().readYaml(FilePath.FILE.ATTENTION_FLAGS);

            for (Object ruleObject : ruleObjects) {
                YamlConfig attentionFlagConfig = (YamlConfig) ruleObject;

                for (YamlConfigItem yamlConfigItem : attentionFlagConfig.getFields()) {

                    if (AncApplication.getInstance().getAncRulesEngineHelper()
                            .getRelevance(facts, yamlConfigItem.getRelevance())) {

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
    }
}
