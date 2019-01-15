package org.smartregister.anc.interactor;

import android.support.annotation.VisibleForTesting;
import android.util.Log;

import org.jeasy.rules.api.Facts;
import org.joda.time.LocalDate;
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
import org.smartregister.anc.util.Utils;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by keyman 30/07/2018.
 */
public class ContactInteractor extends BaseContactInteractor implements ContactContract.Interactor {

    public static final String TAG = ContactInteractor.class.getName();
    private Map<String, Integer> attentionFlagCountMap = new HashMap<>();

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
    public void finalizeContactForm(Map<String, String> details) {
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

            PreviousContactRepository previousContactRepository = AncApplication.getInstance().getPreviousContactRepository();

            PartialContactRepository partialContactRepository = AncApplication.getInstance().getPartialContactRepository();

            List<PartialContact> partialContactList = partialContactRepository.getPartialContacts(baseEntityId, isFirst ? 1 : Integer.valueOf(details.get(DBConstants.KEY.NEXT_CONTACT)));

            Facts facts = new Facts();

            if (partialContactList != null) {

                for (PartialContact partialContact : partialContactList) {
                    PreviousContact previousContact = new PreviousContact();
                    previousContact.setContactNo(partialContact.getContactNo());
                    previousContact.setBaseEntityId(partialContact.getBaseEntityId());
                    previousContact.setFormJson(partialContact.getFormJsonDraft() != null ? partialContact.getFormJsonDraft() : partialContact.getFormJson());
                    previousContact.setType(partialContact.getType());


                    //Save previous contact repository
                    previousContactRepository.savePreviousContact(previousContact);

                    if (previousContact.getFormJson() != null) {
                        //process attention flags
                        ContactJsonFormUtils.processRequiredStepsField(facts, new JSONObject(previousContact.getFormJson()), AncApplication.getInstance().getApplicationContext());
                    }

                    //Remove partial contact
                    partialContactRepository.deletePartialContact(partialContact.getId());
                }
            }


            WomanDetail womanDetail = new WomanDetail();
            womanDetail.setBaseEntityId(baseEntityId);
            womanDetail.setNextContact(nextContact);
            womanDetail.setNextContactDate(nextContactVisitDate);


            processAttentionFlags(womanDetail, facts);

            PatientRepository.updateContactVisitDetails(womanDetail);

            //Attention Flags


            AncApplication.getInstance().getDetailsRepository().add(baseEntityId, Constants.DETAILS_KEY.ATTENTION_FLAG_FACTS, new JSONObject(facts.asMap()).toString(), Calendar.getInstance().getTimeInMillis());


        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
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

        patientDetail.setRedFlagCount(attentionFlagCountMap.get(Constants.ATTENTION_FLAGS.RED));
        patientDetail.setYellowFlagCount(attentionFlagCountMap.get(Constants.ATTENTION_FLAGS.YELLOW));
    }
}
