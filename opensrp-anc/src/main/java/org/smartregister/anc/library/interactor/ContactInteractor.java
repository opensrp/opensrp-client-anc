package org.smartregister.anc.library.interactor;

import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import org.jeasy.rules.api.Facts;
import org.joda.time.LocalDate;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.anc.library.AncLibrary;
import org.smartregister.anc.library.contract.BaseContactContract;
import org.smartregister.anc.library.contract.ContactContract;
import org.smartregister.anc.library.domain.WomanDetail;
import org.smartregister.anc.library.model.ContactVisit;
import org.smartregister.anc.library.model.PartialContact;
import org.smartregister.anc.library.model.PartialContacts;
import org.smartregister.anc.library.model.PreviousContact;
import org.smartregister.anc.library.repository.PartialContactRepository;
import org.smartregister.anc.library.repository.PreviousContactRepository;
import org.smartregister.anc.library.rule.ContactRule;
import org.smartregister.anc.library.util.AppExecutors;
import org.smartregister.anc.library.util.ConstantsUtils;
import org.smartregister.anc.library.util.DBConstantsUtils;
import org.smartregister.anc.library.util.JsonFormUtils;
import org.smartregister.anc.library.util.Utils;
import org.smartregister.clientandeventmodel.Event;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by keyman 30/07/2018.
 */
public class ContactInteractor extends BaseContactInteractor implements ContactContract.Interactor {

    public static final String TAG = ContactInteractor.class.getName();

    public ContactInteractor() {
        this(new AppExecutors());
    }

    @VisibleForTesting
    ContactInteractor(AppExecutors appExecutors) {
        super(appExecutors);
    }

    @Override
    public void fetchWomanDetails(String baseEntityId, BaseContactContract.InteractorCallback callBack) {
        super.fetchWomanDetails(baseEntityId, callBack);
    }

    @Override
    public HashMap<String, String> finalizeContactForm(final Map<String, String> details) {
        if (details != null) {
            try {
                String referral = details.get(ConstantsUtils.REFERRAL);
                String baseEntityId = details.get(DBConstantsUtils.KEY_UTILS.BASE_ENTITY_ID);

                int gestationAge = getGestationAge(details);
                int nextContact;
                boolean isFirst = false;
                String nextContactVisitDate;
                if (referral == null) {
                    isFirst = TextUtils.equals("1", details.get(DBConstantsUtils.KEY_UTILS.NEXT_CONTACT));
                    ContactRule contactRule = new ContactRule(gestationAge, isFirst, baseEntityId);

                    List<Integer> integerList = AncLibrary.getInstance().getAncRulesEngineHelper()
                            .getContactVisitSchedule(contactRule, ConstantsUtils.RULES_FILE_UTILS.CONTACT_RULES);

                    int nextContactVisitWeeks = integerList.get(0);

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(ConstantsUtils.DETAILS_KEY_UTILS.CONTACT_SCHEDULE, integerList);
                    addThePreviousContactSchedule(baseEntityId, details, integerList);
                    AncLibrary.getInstance().getDetailsRepository()
                            .add(baseEntityId, ConstantsUtils.DETAILS_KEY_UTILS.CONTACT_SCHEDULE, jsonObject.toString(),
                                    Calendar.getInstance().getTimeInMillis());
                    //convert String to LocalDate ;
                    LocalDate localDate = new LocalDate(details.get(DBConstantsUtils.KEY_UTILS.EDD));
                    nextContactVisitDate =
                            localDate.minusWeeks(ConstantsUtils.DELIVERY_DATE_WEEKS).plusWeeks(nextContactVisitWeeks).toString();
                    nextContact = getNextContact(details);
                } else {
                    nextContact = Integer.parseInt(details.get(DBConstantsUtils.KEY_UTILS.NEXT_CONTACT));
                    nextContactVisitDate = details.get(DBConstantsUtils.KEY_UTILS.NEXT_CONTACT_DATE);
                }


                PartialContacts partialContacts =
                        new PartialContacts(details, referral, baseEntityId, isFirst).invoke();
                PartialContactRepository partialContactRepository = partialContacts.getPartialContactRepository();
                List<PartialContact> partialContactList = partialContacts.getPartialContactList();

                ContactVisit contactVisit =
                        new ContactVisit(details, referral, baseEntityId, nextContact, nextContactVisitDate,
                                partialContactRepository, partialContactList).invoke();
                Facts facts = contactVisit.getFacts();
                List<String> formSubmissionIDs = contactVisit.getFormSubmissionIDs();
                WomanDetail womanDetail = contactVisit.getWomanDetail();

                //Attention Flags
                String attentionFlagsString;
                if (referral == null) {
                    attentionFlagsString = new JSONObject(facts.asMap()).toString();
                } else {
                    attentionFlagsString =
                            AncLibrary.getInstance().getDetailsRepository().getAllDetailsForClient(baseEntityId)
                                    .get(ConstantsUtils.DETAILS_KEY_UTILS.ATTENTION_FLAG_FACTS);
                }
                addAttentionFlags(baseEntityId, details, new JSONObject(facts.asMap()).toString());
                AncLibrary.getInstance().getDetailsRepository()
                        .add(baseEntityId, ConstantsUtils.DETAILS_KEY_UTILS.ATTENTION_FLAG_FACTS, attentionFlagsString,
                                Calendar.getInstance().getTimeInMillis());

                addTheContactDate(baseEntityId, details);
                updateWomanDetails(details, womanDetail);
                if (referral != null && !TextUtils.isEmpty(details.get(DBConstantsUtils.KEY_UTILS.EDD))) {
                    addReferralGa(baseEntityId, details);
                }

                Pair<Event, Event> eventPair = JsonFormUtils.createContactVisitEvent(formSubmissionIDs, details);
                if (eventPair != null) {
                    createEvent(baseEntityId, new JSONObject(facts.asMap()).toString(), eventPair, referral);
                    JSONObject updateClientEventJson = new JSONObject(JsonFormUtils.gson.toJson(eventPair.second));
                    AncLibrary.getInstance().getEcSyncHelper().addEvent(baseEntityId, updateClientEventJson);
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
        return (HashMap<String, String>) details;
    }

    private void addThePreviousContactSchedule(String baseEntityId, Map<String, String> details, List<Integer> integerList) {
        PreviousContact previousContact = preLoadPreviousContact(baseEntityId, details);
        previousContact.setKey(ConstantsUtils.DETAILS_KEY_UTILS.CONTACT_SCHEDULE);
        previousContact.setValue(String.valueOf(integerList));
        AncLibrary.getInstance().getPreviousContactRepository().savePreviousContact(previousContact);
    }

    private int getNextContact(Map<String, String> details) {
        Integer nextContact =
                details.containsKey(DBConstantsUtils.KEY_UTILS.NEXT_CONTACT) && details.get(DBConstantsUtils.KEY_UTILS.NEXT_CONTACT) != null ?
                        Integer.valueOf(details.get(DBConstantsUtils.KEY_UTILS.NEXT_CONTACT)) : 1;
        nextContact += 1;
        return nextContact;
    }

    private void addAttentionFlags(String baseEntityId, Map<String, String> details,
                                   String attentionFlagsString) {
        PreviousContact previousContact = preLoadPreviousContact(baseEntityId, details);
        previousContact.setKey(ConstantsUtils.DETAILS_KEY_UTILS.ATTENTION_FLAG_FACTS);
        previousContact.setValue(attentionFlagsString);
        AncLibrary.getInstance().getPreviousContactRepository().savePreviousContact(previousContact);
    }

    private void addTheContactDate(String baseEntityId, Map<String, String> details) {
        PreviousContact previousContact = preLoadPreviousContact(baseEntityId, details);
        previousContact.setKey(ConstantsUtils.CONTACT_DATE);
        previousContact.setValue(Utils.getDBDateToday());
        AncLibrary.getInstance().getPreviousContactRepository().savePreviousContact(previousContact);
    }

    private void updateWomanDetails(Map<String, String> details, WomanDetail womanDetail) {
        //update woman profile details
        if (details != null && details.get(ConstantsUtils.REFERRAL) != null) {
            details.put(DBConstantsUtils.KEY_UTILS.LAST_CONTACT_RECORD_DATE, details.get(DBConstantsUtils.KEY_UTILS.LAST_CONTACT_RECORD_DATE));
            details.put(DBConstantsUtils.KEY_UTILS.YELLOW_FLAG_COUNT, details.get(DBConstantsUtils.KEY_UTILS.YELLOW_FLAG_COUNT));
            details.put(DBConstantsUtils.KEY_UTILS.RED_FLAG_COUNT, details.get(DBConstantsUtils.KEY_UTILS.RED_FLAG_COUNT));
        } else {
            details.put(DBConstantsUtils.KEY_UTILS.CONTACT_STATUS, womanDetail.getContactStatus());
            details.put(DBConstantsUtils.KEY_UTILS.LAST_CONTACT_RECORD_DATE, Utils.getDBDateToday());
            details.put(DBConstantsUtils.KEY_UTILS.YELLOW_FLAG_COUNT, womanDetail.getYellowFlagCount().toString());
            details.put(DBConstantsUtils.KEY_UTILS.RED_FLAG_COUNT, womanDetail.getRedFlagCount().toString());

        }
        details.put(DBConstantsUtils.KEY_UTILS.CONTACT_STATUS, womanDetail.getContactStatus());
        details.put(DBConstantsUtils.KEY_UTILS.NEXT_CONTACT, womanDetail.getNextContact().toString());
        details.put(DBConstantsUtils.KEY_UTILS.NEXT_CONTACT_DATE, womanDetail.getNextContactDate());
    }

    private void addReferralGa(String baseEntityId, Map<String, String> details) {
        PreviousContact previousContact = preLoadPreviousContact(baseEntityId, details);
        previousContact.setKey(ConstantsUtils.GEST_AGE_OPENMRS);
        String edd = details.get(DBConstantsUtils.KEY_UTILS.EDD);
        previousContact.setValue(String.valueOf(Utils.getGestationAgeFromEDDate(edd)));
        AncLibrary.getInstance().getPreviousContactRepository().savePreviousContact(previousContact);
    }

    private void createEvent(String baseEntityId, String attentionFlagsString, Pair<Event, Event> eventPair,
                             String referral)
            throws JSONException {
        Event event = eventPair.first;
        //Here we save state
        event.addDetails(ConstantsUtils.DETAILS_KEY_UTILS.ATTENTION_FLAG_FACTS, attentionFlagsString);
        String currentContactState = getCurrentContactState(baseEntityId);
        if (currentContactState != null && referral == null) {
            event.addDetails(ConstantsUtils.DETAILS_KEY_UTILS.PREVIOUS_CONTACTS, currentContactState);
        }
        JSONObject eventJson = new JSONObject(JsonFormUtils.gson.toJson(event));
        AncLibrary.getInstance().getEcSyncHelper().addEvent(baseEntityId, eventJson);
    }

    private PreviousContact preLoadPreviousContact(String baseEntityId, Map<String, String> details) {
        PreviousContact previousContact = new PreviousContact();
        previousContact.setBaseEntityId(baseEntityId);
        String contactNo = details.containsKey(ConstantsUtils.REFERRAL) ? details.get(ConstantsUtils.REFERRAL) :
                details.get(DBConstantsUtils.KEY_UTILS.NEXT_CONTACT);
        previousContact.setContactNo(contactNo);
        return previousContact;
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

    protected PreviousContactRepository getPreviousContactRepository() {
        return AncLibrary.getInstance().getPreviousContactRepository();
    }

    public int getGestationAge(Map<String, String> details) {
        return details.containsKey(DBConstantsUtils.KEY_UTILS.EDD) && details.get(DBConstantsUtils.KEY_UTILS.EDD) != null ? Utils
                .getGestationAgeFromEDDate(details.get(DBConstantsUtils.KEY_UTILS.EDD)) : 4;
    }
}
