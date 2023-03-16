package org.smartregister.anc.library.configuration;


import androidx.annotation.NonNull;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.utils.FormUtils;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.anc.library.AncLibrary;
import org.smartregister.anc.library.interactor.ClientTransferProcessor;
import org.smartregister.anc.library.model.PreviousContact;
import org.smartregister.anc.library.util.ANCJsonFormUtils;
import org.smartregister.anc.library.util.ConstantsUtils;
import org.smartregister.anc.library.util.Utils;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.domain.tag.FormTag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

import static org.smartregister.anc.library.util.ANCJsonFormUtils.METADATA;
import static org.smartregister.util.JsonFormUtils.ENTITY_ID;

public class AncMaternityTransferProcessor implements ClientTransferProcessor {

    private String baseEntityId = null;

    @Override
    public void startTransferProcessing(@NonNull JSONObject closeForm) throws Exception {
        setBaseEntityId(closeForm.optString(ENTITY_ID));
        String closeReason = FormUtils.getFieldFromForm(closeForm, ConstantsUtils.JsonFormKeyUtils.ANC_CLOSE_REASON).optString(JsonFormConstants.VALUE);

        List<String> formSubmissionIds = new ArrayList<>();
        //create transfer event
        if (closeReason.equalsIgnoreCase("in_labour")) {
            Event transferEvent = createAncMaternityTransferEvent(closeForm);
            saveEvent(transferEvent, transferEvent.getBaseEntityId());
            formSubmissionIds.add(transferEvent.getFormSubmissionId());
        }

        //create anc close event
        Event closeEvent = createAncCloseEvent(closeForm);
        if (closeReason.equalsIgnoreCase("woman_died")) {
            closeEvent.setEventType("Death");
        }
        saveEvent(closeEvent, getBaseEntityId());
        formSubmissionIds.add(closeEvent.getFormSubmissionId());

        long lastSyncTimeStamp = Utils.getAllSharedPreferences().fetchLastUpdatedAtDate(0);
        Date lastSyncDate = new Date(lastSyncTimeStamp);
        AncLibrary.getInstance().getClientProcessorForJava()
                .processClient(
                        AncLibrary.getInstance()
                                .getEcSyncHelper()
                                .getEvents(formSubmissionIds));

        Utils.getAllSharedPreferences().saveLastUpdatedAtDate(lastSyncDate.getTime());
    }

    private void saveEvent(@NonNull Event event, @NonNull String baseEntityId) throws JSONException {
        JSONObject eventJson = new JSONObject(ANCJsonFormUtils.gson.toJson(event));
        AncLibrary.getInstance().getEcSyncHelper().addEvent(baseEntityId, eventJson);
    }

    protected Event createAncMaternityTransferEvent(@NonNull JSONObject closeForm) {
        JSONObject jsonFormObject = populateTransferForm(closeForm);
        FormTag formTag = ANCJsonFormUtils.getFormTag(Utils.getAllSharedPreferences());
        Event event = ANCJsonFormUtils.createEvent(FormUtils.getMultiStepFormFields(jsonFormObject),
                jsonFormObject.optJSONObject(METADATA), formTag, getBaseEntityId(), jsonFormObject.optString(JsonFormConstants.ENCOUNTER_TYPE), "");
        ANCJsonFormUtils.tagSyncMetadata(Utils.getAllSharedPreferences(), event);
        return event;
    }

    protected Event createAncCloseEvent(@NonNull JSONObject closeFormObject) {
        FormTag formTag = ANCJsonFormUtils.getFormTag(Utils.getAllSharedPreferences());
        Event event = ANCJsonFormUtils.createEvent(FormUtils.getMultiStepFormFields(closeFormObject),
                closeFormObject.optJSONObject(METADATA), formTag, closeFormObject.optString(ENTITY_ID), closeFormObject.optString(JsonFormConstants.ENCOUNTER_TYPE), "");
        ANCJsonFormUtils.tagSyncMetadata(Utils.getAllSharedPreferences(), event);
        return event;
    }


    @Override
    public String transferForm() {
        return "";
    }

    @Override
    public Map<String, String> columnMap() {
        return new HashMap<>();
    }

    @Override
    public Map<String, String> details() {
        String[] keyStrings = previousContactKeys();
        HashMap<String, String> transferDetails = new HashMap<>();
        List<PreviousContact> previousContacts = AncLibrary.getInstance()
                .getPreviousContactRepository()
                .getPreviousContacts(getBaseEntityId(), keyStrings);
        for (PreviousContact previousContact : previousContacts) {
            transferDetails.put(previousContact.getKey(), previousContact.getValue());
        }
        return transferDetails;
    }

    @NotNull
    protected String[] previousContactKeys() {
        return new String[]{"gravida", "prev_preg_comps",
                "prev_preg_comps_other", "miscarriages_abortions", "occupation", "occupation_other",
                "religion_other", "religion", "marital_status", "educ_level"};
    }

    @Override
    public JSONObject populateTransferForm(@NonNull JSONObject closeForm) {
        JSONObject jsonForm = null;
        try {
            if (StringUtils.isNotBlank(transferForm())) {
                jsonForm = Utils.getFormUtils().getFormJson(transferForm());
                if (jsonForm != null) {
                    Map<String, String> clientDetails = details();
                    Map<String, String> columnMap = columnMap();
                    JSONArray fields = jsonForm.optJSONObject(JsonFormConstants.STEP1)
                            .optJSONArray(JsonFormConstants.FIELDS);

                    for (int i = 0; i < fields.length(); i++) {
                        JSONObject object = fields.optJSONObject(i);
                        String key = object.optString(JsonFormConstants.KEY);

                        if (clientDetails.get(key) != null) {
                            updateValue(object, clientDetails.get(key));
                        }

                        if (columnMap.get(key) != null) {
                            object.put(JsonFormConstants.KEY, columnMap.get(key));
                        }
                    }

                    JSONArray closeFormFields = FormUtils.getMultiStepFormFields(closeForm);
                    List<String> closeFormFieldsList = getCloseFormFieldsList();
                    for (int i = 0; i < closeFormFields.length() && !closeFormFieldsList.isEmpty(); i++) {
                        JSONObject object = closeFormFields.optJSONObject(i);
                        String key = object.optString(JsonFormConstants.KEY);
                        if (closeFormFieldsList.contains(key)) {
                            fields.put(object);
                            closeFormFieldsList.remove(key);
                        }
                    }
                }
            }
        } catch (JSONException e) {
            Timber.e(e);
        }
        return jsonForm;
    }

    protected void updateValue(@NonNull JSONObject object, @NonNull String value) {
        Timber.i("key %s", object.optString(JsonFormConstants.KEY));
        try {
            object.put(JsonFormConstants.VALUE, value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected List<String> getCloseFormFieldsList() {
        return new ArrayList<>(Arrays.asList("onset_labour_date", "onset_labour_time"));
    }

    public String getBaseEntityId() {
        return baseEntityId;
    }

    public void setBaseEntityId(String baseEntityId) {
        this.baseEntityId = baseEntityId;
    }
}
