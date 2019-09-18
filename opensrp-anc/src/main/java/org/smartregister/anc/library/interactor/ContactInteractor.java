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
import org.smartregister.anc.library.model.PartialContacts;
import org.smartregister.anc.library.model.PreviousContact;
import org.smartregister.anc.library.model.ContactVisit;
import org.smartregister.anc.library.model.PartialContact;
import org.smartregister.anc.library.repository.PartialContactRepository;
import org.smartregister.anc.library.repository.PreviousContactRepository;
import org.smartregister.anc.library.rule.ContactRule;
import org.smartregister.anc.library.util.AppExecutors;
import org.smartregister.anc.library.util.Constants;
import org.smartregister.anc.library.util.DBConstants;
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
                    isFirst = TextUtils.equals("1", details.get(DBConstants.KEY.NEXT_CONTACT));
                    ContactRule contactRule = new ContactRule(gestationAge, isFirst, baseEntityId);

                    List<Integer> integerList = AncLibrary.getInstance().getAncRulesEngineHelper()
                            .getContactVisitSchedule(contactRule, Constants.RULES_FILE.CONTACT_RULES);

                    int nextContactVisitWeeks = integerList.get(0);

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(Constants.DETAILS_KEY.CONTACT_SCHEDULE, integerList);
                    addThePreviousContactSchedule(baseEntityId, details, integerList);
                    AncLibrary.getInstance().getDetailsRepository()
                            .add(baseEntityId, Constants.DETAILS_KEY.CONTACT_SCHEDULE, jsonObject.toString(),
                                    Calendar.getInstance().getTimeInMillis());
                    //convert String to LocalDate ;
                    LocalDate localDate = new LocalDate(details.get(DBConstants.KEY.EDD));
                    nextContactVisitDate =
                            localDate.minusWeeks(Constants.DELIVERY_DATE_WEEKS).plusWeeks(nextContactVisitWeeks).toString();
                    nextContact = getNextContact(details);
                } else {
                    nextContact = Integer.parseInt(details.get(DBConstants.KEY.NEXT_CONTACT));
                    nextContactVisitDate = details.get(DBConstants.KEY.NEXT_CONTACT_DATE);
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
                                    .get(Constants.DETAILS_KEY.ATTENTION_FLAG_FACTS);
                }
                addAttentionFlags(baseEntityId, details, new JSONObject(facts.asMap()).toString());
                AncLibrary.getInstance().getDetailsRepository()
                        .add(baseEntityId, Constants.DETAILS_KEY.ATTENTION_FLAG_FACTS, attentionFlagsString,
                                Calendar.getInstance().getTimeInMillis());

                addTheContactDate(baseEntityId, details);
                updateWomanDetails(details, womanDetail);
                if (referral != null && !TextUtils.isEmpty(details.get(DBConstants.KEY.EDD))) {
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

    private void addTheContactDate(String baseEntityId, Map<String, String> details) {
        PreviousContact previousContact = preLoadPreviousContact(baseEntityId, details);
        previousContact.setKey(Constants.CONTACT_DATE);
        previousContact.setValue(Utils.getDBDateToday());
        AncLibrary.getInstance().getPreviousContactRepository().savePreviousContact(previousContact);
    }

    private void addReferralGa(String baseEntityId, Map<String, String> details) {
        PreviousContact previousContact = preLoadPreviousContact(baseEntityId, details);
        previousContact.setKey(Constants.GEST_AGE_OPENMRS);
        String edd = details.get(DBConstants.KEY.EDD);
        previousContact.setValue(String.valueOf(Utils.getGestationAgeFromEDDate(edd)));
        AncLibrary.getInstance().getPreviousContactRepository().savePreviousContact(previousContact);
    }

    private void addThePreviousContactSchedule(String baseEntityId, Map<String, String> details, List<Integer> integerList) {
        PreviousContact previousContact = preLoadPreviousContact(baseEntityId, details);
        previousContact.setKey(Constants.DETAILS_KEY.CONTACT_SCHEDULE);
        previousContact.setValue(String.valueOf(integerList));
        AncLibrary.getInstance().getPreviousContactRepository().savePreviousContact(previousContact);
    }

    private void addAttentionFlags(String baseEntityId, Map<String, String> details,
                                   String attentionFlagsString) {
        PreviousContact previousContact = preLoadPreviousContact(baseEntityId, details);
        previousContact.setKey(Constants.DETAILS_KEY.ATTENTION_FLAG_FACTS);
        previousContact.setValue(attentionFlagsString);
        AncLibrary.getInstance().getPreviousContactRepository().savePreviousContact(previousContact);
    }

    private PreviousContact preLoadPreviousContact(String baseEntityId, Map<String, String> details) {
        PreviousContact previousContact = new PreviousContact();
        previousContact.setBaseEntityId(baseEntityId);
        String contactNo = details.containsKey(Constants.REFERRAL) ? details.get(Constants.REFERRAL) :
                details.get(DBConstants.KEY.NEXT_CONTACT);
        previousContact.setContactNo(contactNo);
        return previousContact;
    }

    private void createEvent(String baseEntityId, String attentionFlagsString, Pair<Event, Event> eventPair,
                             String referral)
    throws JSONException {
        Event event = eventPair.first;
        //Here we save state
        event.addDetails(Constants.DETAILS_KEY.ATTENTION_FLAG_FACTS, attentionFlagsString);
        String currentContactState = getCurrentContactState(baseEntityId);
        if (currentContactState != null && referral == null) {
            event.addDetails(Constants.DETAILS_KEY.PREVIOUS_CONTACTS, currentContactState);
        }
        JSONObject eventJson = new JSONObject(JsonFormUtils.gson.toJson(event));
        AncLibrary.getInstance().getEcSyncHelper().addEvent(baseEntityId, eventJson);
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

    public int getGestationAge(Map<String, String> details) {
        return details.containsKey(DBConstants.KEY.EDD) && details.get(DBConstants.KEY.EDD) != null ? Utils
                .getGestationAgeFromEDDate(details.get(DBConstants.KEY.EDD)) : 4;
    }

    private int getNextContact(Map<String, String> details) {
        Integer nextContact =
                details.containsKey(DBConstants.KEY.NEXT_CONTACT) && details.get(DBConstants.KEY.NEXT_CONTACT) != null ?
                        Integer.valueOf(details.get(DBConstants.KEY.NEXT_CONTACT)) : 1;
        nextContact += 1;
        return nextContact;
    }


    protected PreviousContactRepository getPreviousContactRepository() {
        return AncLibrary.getInstance().getPreviousContactRepository();
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
}
