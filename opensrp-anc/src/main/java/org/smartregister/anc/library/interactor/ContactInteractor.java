package org.smartregister.anc.library.interactor;

import android.support.annotation.VisibleForTesting;
import android.support.v4.util.Pair;
import android.text.TextUtils;

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
import org.smartregister.anc.library.repository.PartialContactRepositoryHelper;
import org.smartregister.anc.library.repository.PreviousContactRepositoryHelper;
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

import timber.log.Timber;

/**
 * Created by keyman 30/07/2018.
 */
public class ContactInteractor extends BaseContactInteractor implements ContactContract.Interactor {

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
                String baseEntityId = details.get(DBConstantsUtils.KeyUtils.BASE_ENTITY_ID);

                int gestationAge = getGestationAge(details);
                int nextContact;
                boolean isFirst = false;
                String nextContactVisitDate;
                if (referral == null) {
                    isFirst = TextUtils.equals("1", details.get(DBConstantsUtils.KeyUtils.NEXT_CONTACT));
                    ContactRule contactRule = new ContactRule(gestationAge, isFirst, baseEntityId);

                    List<Integer> integerList = AncLibrary.getInstance().getAncRulesEngineHelper()
                            .getContactVisitSchedule(contactRule, ConstantsUtils.RulesFileUtils.CONTACT_RULES);

                    int nextContactVisitWeeks = integerList.get(0);

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(ConstantsUtils.DetailsKeyUtils.CONTACT_SCHEDULE, integerList);
                    addThePreviousContactSchedule(baseEntityId, details, integerList);
                    AncLibrary.getInstance().getDetailsRepository()
                            .add(baseEntityId, ConstantsUtils.DetailsKeyUtils.CONTACT_SCHEDULE, jsonObject.toString(),
                                    Calendar.getInstance().getTimeInMillis());
                    //convert String to LocalDate ;
                    LocalDate localDate = new LocalDate(details.get(DBConstantsUtils.KeyUtils.EDD));
                    nextContactVisitDate =
                            localDate.minusWeeks(ConstantsUtils.DELIVERY_DATE_WEEKS).plusWeeks(nextContactVisitWeeks).toString();
                    nextContact = getNextContact(details);
                } else {
                    nextContact = Integer.parseInt(details.get(DBConstantsUtils.KeyUtils.NEXT_CONTACT));
                    nextContactVisitDate = details.get(DBConstantsUtils.KeyUtils.NEXT_CONTACT_DATE);
                }

                PartialContacts partialContacts =
                        new PartialContacts(details, referral, baseEntityId, isFirst).invoke();
                PartialContactRepositoryHelper partialContactRepositoryHelper = partialContacts.getPartialContactRepositoryHelper();
                List<PartialContact> partialContactList = partialContacts.getPartialContactList();

                ContactVisit contactVisit =
                        new ContactVisit(details, referral, baseEntityId, nextContact, nextContactVisitDate,
                                partialContactRepositoryHelper, partialContactList).invoke();
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
                                    .get(ConstantsUtils.DetailsKeyUtils.ATTENTION_FLAG_FACTS);
                }
                addAttentionFlags(baseEntityId, details, new JSONObject(facts.asMap()).toString());
                AncLibrary.getInstance().getDetailsRepository()
                        .add(baseEntityId, ConstantsUtils.DetailsKeyUtils.ATTENTION_FLAG_FACTS, attentionFlagsString,
                                Calendar.getInstance().getTimeInMillis());

                addTheContactDate(baseEntityId, details);
                updateWomanDetails(details, womanDetail);
                if (referral != null && !TextUtils.isEmpty(details.get(DBConstantsUtils.KeyUtils.EDD))) {
                    addReferralGa(baseEntityId, details);
                }

                Pair<Event, Event> eventPair = JsonFormUtils.createContactVisitEvent(formSubmissionIDs, details);
                if (eventPair != null) {
                    createEvent(baseEntityId, new JSONObject(facts.asMap()).toString(), eventPair, referral);
                    JSONObject updateClientEventJson = new JSONObject(JsonFormUtils.gson.toJson(eventPair.second));
                    AncLibrary.getInstance().getEcSyncHelper().addEvent(baseEntityId, updateClientEventJson);
                }
            } catch (Exception e) {
                Timber.e(e, "%s --> finalizeContactForm", this.getClass().getCanonicalName());
            }
        }
        return (HashMap<String, String>) details;
    }

    private void addThePreviousContactSchedule(String baseEntityId, Map<String, String> details, List<Integer> integerList) {
        PreviousContact previousContact = preLoadPreviousContact(baseEntityId, details);
        previousContact.setKey(ConstantsUtils.DetailsKeyUtils.CONTACT_SCHEDULE);
        previousContact.setValue(String.valueOf(integerList));
        AncLibrary.getInstance().getPreviousContactRepositoryHelper().savePreviousContact(previousContact);
    }

    private int getNextContact(Map<String, String> details) {
        int nextContact =
                details.containsKey(DBConstantsUtils.KeyUtils.NEXT_CONTACT) && details.get(DBConstantsUtils.KeyUtils.NEXT_CONTACT) != null ?
                        Integer.valueOf(details.get(DBConstantsUtils.KeyUtils.NEXT_CONTACT)) : 1;
        nextContact += 1;
        return nextContact;
    }

    private void addAttentionFlags(String baseEntityId, Map<String, String> details,
                                   String attentionFlagsString) {
        PreviousContact previousContact = preLoadPreviousContact(baseEntityId, details);
        previousContact.setKey(ConstantsUtils.DetailsKeyUtils.ATTENTION_FLAG_FACTS);
        previousContact.setValue(attentionFlagsString);
        AncLibrary.getInstance().getPreviousContactRepositoryHelper().savePreviousContact(previousContact);
    }

    private void addTheContactDate(String baseEntityId, Map<String, String> details) {
        PreviousContact previousContact = preLoadPreviousContact(baseEntityId, details);
        previousContact.setKey(ConstantsUtils.CONTACT_DATE);
        previousContact.setValue(Utils.getDBDateToday());
        AncLibrary.getInstance().getPreviousContactRepositoryHelper().savePreviousContact(previousContact);
    }

    private void updateWomanDetails(Map<String, String> details, WomanDetail womanDetail) {
        //update woman profile details
        if (details != null) {
            if (details.get(ConstantsUtils.REFERRAL) != null) {
                details.put(DBConstantsUtils.KeyUtils.LAST_CONTACT_RECORD_DATE, details.get(DBConstantsUtils.KeyUtils.LAST_CONTACT_RECORD_DATE));
                details.put(DBConstantsUtils.KeyUtils.YELLOW_FLAG_COUNT, details.get(DBConstantsUtils.KeyUtils.YELLOW_FLAG_COUNT));
                details.put(DBConstantsUtils.KeyUtils.RED_FLAG_COUNT, details.get(DBConstantsUtils.KeyUtils.RED_FLAG_COUNT));
            } else {
                details.put(DBConstantsUtils.KeyUtils.CONTACT_STATUS, womanDetail.getContactStatus());
                details.put(DBConstantsUtils.KeyUtils.LAST_CONTACT_RECORD_DATE, Utils.getDBDateToday());
                details.put(DBConstantsUtils.KeyUtils.YELLOW_FLAG_COUNT, womanDetail.getYellowFlagCount().toString());
                details.put(DBConstantsUtils.KeyUtils.RED_FLAG_COUNT, womanDetail.getRedFlagCount().toString());

            }
            details.put(DBConstantsUtils.KeyUtils.NEXT_CONTACT, womanDetail.getNextContact().toString());
            details.put(DBConstantsUtils.KeyUtils.NEXT_CONTACT_DATE, womanDetail.getNextContactDate());
            details.put(DBConstantsUtils.KeyUtils.PREVIOUS_CONTACT_STATUS, womanDetail.getPreviousContactStatus());
        }
    }

    private void addReferralGa(String baseEntityId, Map<String, String> details) {
        PreviousContact previousContact = preLoadPreviousContact(baseEntityId, details);
        previousContact.setKey(ConstantsUtils.GEST_AGE_OPENMRS);
        String edd = details.get(DBConstantsUtils.KeyUtils.EDD);
        previousContact.setValue(String.valueOf(Utils.getGestationAgeFromEDDate(edd)));
        AncLibrary.getInstance().getPreviousContactRepositoryHelper().savePreviousContact(previousContact);
    }

    private void createEvent(String baseEntityId, String attentionFlagsString, Pair<Event, Event> eventPair,
                             String referral)
            throws JSONException {
        Event event = eventPair.first;
        //Here we save state
        event.addDetails(ConstantsUtils.DetailsKeyUtils.ATTENTION_FLAG_FACTS, attentionFlagsString);
        String currentContactState = getCurrentContactState(baseEntityId);
        if (currentContactState != null && referral == null) {
            event.addDetails(ConstantsUtils.DetailsKeyUtils.PREVIOUS_CONTACTS, currentContactState);
        }
        JSONObject eventJson = new JSONObject(JsonFormUtils.gson.toJson(event));
        AncLibrary.getInstance().getEcSyncHelper().addEvent(baseEntityId, eventJson);
    }

    private PreviousContact preLoadPreviousContact(String baseEntityId, Map<String, String> details) {
        PreviousContact previousContact = new PreviousContact();
        previousContact.setBaseEntityId(baseEntityId);
        String contactNo = details.containsKey(ConstantsUtils.REFERRAL) ? details.get(ConstantsUtils.REFERRAL) :
                details.get(DBConstantsUtils.KeyUtils.NEXT_CONTACT);
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

    protected PreviousContactRepositoryHelper getPreviousContactRepository() {
        return AncLibrary.getInstance().getPreviousContactRepositoryHelper();
    }

    public int getGestationAge(Map<String, String> details) {
        return details.containsKey(DBConstantsUtils.KeyUtils.EDD) && details.get(DBConstantsUtils.KeyUtils.EDD) != null ? Utils
                .getGestationAgeFromEDDate(details.get(DBConstantsUtils.KeyUtils.EDD)) : 4;
    }
}
