package org.smartregister.anc.interactor;

import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;
import android.util.Log;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.rules.RuleConstant;

import org.jeasy.rules.api.Facts;
import org.joda.time.LocalDate;
import org.json.JSONArray;
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

/**
 * Created by keyman 30/07/2018.
 */
public class ContactInteractor extends BaseContactInteractor implements ContactContract.Interactor {

    public static final String TAG = ContactInteractor.class.getName();
    private Map<String, Integer> attentionFlagCountMap = new HashMap<>();
    private List<String> parsableFormsList = Arrays.asList(new String[]{Constants.JSON_FORM.ANC_PROFILE, Constants.JSON_FORM.ANC_SYMPTOMS_FOLLOW_UP, Constants.JSON_FORM.ANC_PHYSICAL_EXAM});

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
    public void finalizeContactForm(final Map<String, String> details) {

        try {

            String baseEntityId = details.get(DBConstants.KEY.BASE_ENTITY_ID);

            int gestationAge = getGestationAge(details);

            boolean isFirst = details.get(DBConstants.KEY.NEXT_CONTACT) == null;
            ContactRule contactRule = new ContactRule(gestationAge, isFirst, baseEntityId);

            List<Integer> integerList = AncApplication.getInstance().getRulesEngineHelper().getContactVisitSchedule(contactRule, Constants.RULES_FILE.CONTACT_RULES);

            int nextContactVisitWeeks = integerList.get(0);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put(Constants.DETAILS_KEY.CONTACT_SHEDULE, integerList);

            //convert String to LocalDate ;
            LocalDate localDate = new LocalDate(details.get(DBConstants.KEY.EDD));
            String nextContactVisitDate = localDate.minusWeeks(Constants.DELIVERY_DATE_WEEKS).plusWeeks(nextContactVisitWeeks).toString();

            Integer nextContact = getNextContact(details);


            AncApplication.getInstance().getDetailsRepository().add(baseEntityId, Constants.DETAILS_KEY.CONTACT_SHEDULE, jsonObject.toString(), Calendar.getInstance().getTimeInMillis());


            PartialContactRepository partialContactRepository = AncApplication.getInstance().getPartialContactRepository();

            List<PartialContact> partialContactList = partialContactRepository != null ? partialContactRepository.getPartialContacts(baseEntityId, isFirst ? 1 : Integer.valueOf(details.get(DBConstants.KEY.NEXT_CONTACT))) : null;

            Facts facts = new Facts();
            List<Event> eventList = new ArrayList<>();

            if (partialContactList != null) {

                Collections.sort(partialContactList, new Comparator<PartialContact>() {
                    @Override
                    public int compare(PartialContact o1, PartialContact o2) {
                        return o1.getSortOrder().compareTo(o2.getSortOrder());
                    }
                });

                for (PartialContact partialContact : partialContactList) {


                    JSONObject formObject = JsonFormUtils.toJSONObject(partialContact.getFormJsonDraft() != null ? partialContact.getFormJsonDraft() : partialContact.getFormJson());


                    if (formObject != null) {


                        //process form details
                        if (parsableFormsList.contains(partialContact.getType())) {

                            processFormFieldKeyValues(baseEntityId, formObject);

                        }

                        //process attention flags
                        ContactJsonFormUtils.processRequiredStepsField(facts, formObject, AncApplication.getInstance().getApplicationContext());


                        //process events
                        eventList.add(ContactJsonFormUtils.processFormEvent(baseEntityId, formObject));
                    }

                    //Remove partial contact
                    partialContactRepository.deletePartialContact(partialContact.getId());
                }
            }

            WomanDetail womanDetail = new WomanDetail();
            womanDetail.setBaseEntityId(baseEntityId);
            womanDetail.setNextContact(nextContact);
            womanDetail.setNextContactDate(nextContactVisitDate);
            womanDetail.setContactStatus(Constants.ALERT_STATUS.TODAY);


            processAttentionFlags(womanDetail, facts);

            PatientRepository.updateContactVisitDetails(womanDetail, true);

            //Attention Flags
            String attentionFlagsString = new JSONObject(facts.asMap()).toString();
            AncApplication.getInstance().getDetailsRepository().add(baseEntityId, Constants.DETAILS_KEY.ATTENTION_FLAG_FACTS, attentionFlagsString, Calendar.getInstance().getTimeInMillis());

            Event event = JsonFormUtils.createContactVisitEvent(eventList, baseEntityId, details.get(DBConstants.KEY.NEXT_CONTACT));

            event.addDetails(Constants.DETAILS_KEY.ATTENTION_FLAG_FACTS, attentionFlagsString);
            
            JSONObject eventJson = new JSONObject(JsonFormUtils.gson.toJson(event));

            AncApplication.getInstance().getEcSyncHelper().addEvent(baseEntityId, eventJson);


        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    private int getGestationAge(Map<String, String> details) {
        return details.containsKey(DBConstants.KEY.EDD) && details.get(DBConstants.KEY.EDD) != null ? Utils.getGestationAgeFromEDDate(details.get(DBConstants.KEY.EDD)) : 4;
    }

    private int getNextContact(Map<String, String> details) {
        Integer nextContact = details.containsKey(DBConstants.KEY.NEXT_CONTACT) && details.get(DBConstants.KEY.NEXT_CONTACT) != null ? Integer.valueOf(details.get(DBConstants.KEY.NEXT_CONTACT)) : 0;
        nextContact += 1;
        return nextContact;
    }

    private void processAttentionFlags(WomanDetail patientDetail, Facts facts) throws IOException {

        Iterable<Object> ruleObjects = AncApplication.getInstance().readYaml(FilePath.FILE.ATTENTION_FLAGS);

        for (Object ruleObject : ruleObjects) {
            YamlConfig attentionFlagConfig = (YamlConfig) ruleObject;

            for (YamlConfigItem yamlConfigItem : attentionFlagConfig.getFields()) {

                if (AncApplication.getInstance().getRulesEngineHelper().getRelevance(facts, yamlConfigItem.getRelevance())) {

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


    private void processFormFieldKeyValues(String baseEntityId, JSONObject object) throws Exception {

        if (object != null) {
            Iterator<String> keys = object.keys();

            while (keys.hasNext()) {
                String key = keys.next();

                if (key.startsWith(RuleConstant.STEP)) {
                    JSONArray stepArray = object.getJSONObject(key).getJSONArray(JsonFormConstants.FIELDS);

                    for (int i = 0; i < stepArray.length(); i++) {

                        JSONObject fieldObject = stepArray.getJSONObject(i);

                        ContactJsonFormUtils.processSpecialWidgets(fieldObject);

                        if (fieldObject.has(JsonFormConstants.VALUE) && !TextUtils.isEmpty(fieldObject.getString(JsonFormConstants.VALUE))) {

                            PreviousContact previousContact = new PreviousContact();

                            previousContact.setKey(fieldObject.getString(JsonFormConstants.KEY));
                            previousContact.setValue(fieldObject.getString(JsonFormConstants.VALUE));
                            previousContact.setBaseEntityId(baseEntityId);
                            getPreviousContactRepository().savePreviousContact(previousContact);

                        }

                    }

                }
            }
        }

    }

    protected PreviousContactRepository getPreviousContactRepository() {
        return AncApplication.getInstance().getPreviousContactRepository();
    }
}
