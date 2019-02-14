package org.smartregister.anc.util;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.common.collect.ImmutableMap;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.rules.RuleConstant;
import com.vijay.jsonwizard.utils.FormUtils;
import com.vijay.jsonwizard.views.CustomTextView;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.jeasy.rules.api.Facts;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.anc.R;
import org.smartregister.anc.application.AncApplication;
import org.smartregister.anc.contract.AncGenericDialogInterface;
import org.smartregister.anc.domain.Contact;
import org.smartregister.anc.model.PartialContact;
import org.smartregister.anc.view.AncGenericPopupDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ContactJsonFormUtils extends FormUtils {
    private AncGenericDialogInterface genericDialogInterface;
    public static final String TAG = ContactJsonFormUtils.class.getCanonicalName();

    @Override
    public void showGenericDialog(View view) {
        Context context = (Context) view.getTag(com.vijay.jsonwizard.R.id.specify_context);
        String specifyContent = (String) view.getTag(com.vijay.jsonwizard.R.id.specify_content);
        String specifyContentForm = (String) view.getTag(com.vijay.jsonwizard.R.id.specify_content_form);
        String stepName = (String) view.getTag(com.vijay.jsonwizard.R.id.specify_step_name);
        CommonListener listener = (CommonListener) view.getTag(com.vijay.jsonwizard.R.id.specify_listener);
        JsonFormFragment formFragment = (JsonFormFragment) view.getTag(com.vijay.jsonwizard.R.id.specify_fragment);
        JSONArray jsonArray = (JSONArray) view.getTag(com.vijay.jsonwizard.R.id.secondaryValues);
        String parentKey = (String) view.getTag(com.vijay.jsonwizard.R.id.key);
        String type = (String) view.getTag(com.vijay.jsonwizard.R.id.type);
        CustomTextView customTextView = (CustomTextView) view.getTag(com.vijay.jsonwizard.R.id.specify_textview);
        CustomTextView reasonsTextView = (CustomTextView) view.getTag(com.vijay.jsonwizard.R.id.specify_reasons_textview);
        String toolbarHeader = "";
        String container = "";
        LinearLayout rootLayout = (LinearLayout) view.getTag(R.id.main_layout);
        if (type != null && type.equals(Constants.EXPANSION_PANEL)) {
            toolbarHeader = (String) view.getTag(R.id.header);
            container = (String) view.getTag(R.id.contact_container);
        }
        String childKey;

        if (specifyContent != null) {
            AncGenericPopupDialog genericPopupDialog = new AncGenericPopupDialog();
            genericPopupDialog.setCommonListener(listener);
            genericPopupDialog.setFormFragment(formFragment);
            genericPopupDialog.setFormIdentity(specifyContent);
            genericPopupDialog.setFormLocation(specifyContentForm);
            genericPopupDialog.setStepName(stepName);
            genericPopupDialog.setSecondaryValues(jsonArray);
            genericPopupDialog.setParentKey(parentKey);
            genericPopupDialog.setLinearLayout(rootLayout);
            if (type != null && type.equals(Constants.EXPANSION_PANEL)) {
                genericPopupDialog.setHeader(toolbarHeader);
                genericPopupDialog.setContainer(container);
            }
            genericPopupDialog.setWidgetType(type);
            if (customTextView != null && reasonsTextView != null) {
                genericPopupDialog.setCustomTextView(customTextView);
                genericPopupDialog.setPopupReasonsTextView(reasonsTextView);
            }
            if (type != null && (type.equals(JsonFormConstants.CHECK_BOX) || type
                    .equals(JsonFormConstants.NATIVE_RADIO_BUTTON))) {
                childKey = (String) view.getTag(com.vijay.jsonwizard.R.id.childKey);
                genericPopupDialog.setChildKey(childKey);
            }

            Activity activity = (Activity) context;
            FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
            Fragment prev = activity.getFragmentManager().findFragmentByTag("GenericPopup");
            if (prev != null) {
                ft.remove(prev);
            }

            ft.addToBackStack(null);
            genericPopupDialog.show(ft, "GenericPopup");
        } else {
            Toast.makeText(context, "Please specify the sub form to display ", Toast.LENGTH_LONG).show();
        }
    }

    public Map<String, String> createAssignedValue(AncGenericDialogInterface genericDialogInterface, String itemKey,
                                                   String optionKey,
                                                   String keyValue, String itemType, String itemText) {
        this.genericDialogInterface = genericDialogInterface;
        return addAssignedValue(itemKey, optionKey, keyValue, itemType, itemText);
    }

    @Override
    public Map<String, String> addAssignedValue(String itemKey, String optionKey, String keyValue, String itemType,
                                                String itemText) {
        Map<String, String> value = new HashMap<>();
        if (genericDialogInterface != null && !TextUtils
                .isEmpty(genericDialogInterface.getWidgetType()) && genericDialogInterface.getWidgetType()
                .equals(Constants.EXPANSION_PANEL)) {
            String[] labels = itemType.split(";");
            String type = "";
            if (labels.length >= 1) {
                type = labels[0];
            }
            if (!TextUtils.isEmpty(type)) {
                switch (type) {
                    case JsonFormConstants.CHECK_BOX:
                        value.put(itemKey, optionKey + ":" + itemText + ":" + keyValue + ";" + itemType);
                        break;
                    case JsonFormConstants.NATIVE_RADIO_BUTTON:
                        value.put(itemKey, keyValue + ":" + itemText + ";" + itemType);
                        break;
                    case Constants.ANC_RADIO_BUTTON:
                        value.put(itemKey, keyValue + ":" + itemText + ";" + itemType);
                        break;
                    default:
                        value.put(itemKey, keyValue + ";" + itemType);
                        break;
                }
            }
        } else {
            return super.addAssignedValue(itemKey, optionKey, keyValue, itemType, itemText);
        }
        return value;
    }

    /**
     * Changes the Expansion panel status icon after selection
     *
     * @param imageView {@link ImageView}
     * @param type      {@link String}
     * @param context   {@link Context}
     * @author dubdabasoduba
     */
    public void changeIcon(ImageView imageView, String type, Context context) {
        if (!TextUtils.isEmpty(type)) {
            switch (type) {
                case Constants.ANC_RADIO_BUTTON_OPTION_TYPES.DONE_TODAY:
                case Constants.ANC_RADIO_BUTTON_OPTION_TEXT.DONE_TODAY:
                case Constants.ANC_RADIO_BUTTON_OPTION_TYPES.DONE:
                case Constants.ANC_RADIO_BUTTON_OPTION_TEXT.DONE:
                    imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_done_256));
                    break;
                case Constants.ANC_RADIO_BUTTON_OPTION_TYPES.DONE_EARLIER:
                case Constants.ANC_RADIO_BUTTON_OPTION_TEXT.DONE_EARLIER:
                    imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_done_256));
                    break;
                case Constants.ANC_RADIO_BUTTON_OPTION_TYPES.ORDERED:
                case Constants.ANC_RADIO_BUTTON_OPTION_TEXT.ORDERED:
                    imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_ordered_256));
                    break;
                case Constants.ANC_RADIO_BUTTON_OPTION_TYPES.NOT_DONE:
                case Constants.ANC_RADIO_BUTTON_OPTION_TEXT.NOT_DONE:
                    imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_not_done_256));
                    break;
                default:
                    imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_task_256));
                    break;
            }
        }
    }

    public static void persistPartial(String baseEntityId, Contact contact) {
        PartialContact partialContact = new PartialContact();
        partialContact.setBaseEntityId(baseEntityId);
        partialContact.setContactNo(contact.getContactNumber());
        partialContact.setFinalized(false);
        partialContact.setType(contact.getFormName());

        partialContact.setFormJsonDraft(contact.getJsonForm());
        AncApplication.getInstance().getPartialContactRepository().savePartialContact(partialContact);
    }


    public static JSONObject getFormJsonCore(PartialContact partialContactRequest, JSONObject form) throws JSONException {
        JSONObject object;
        //partial contact exists?

        PartialContact partialContact = AncApplication.getInstance().getPartialContactRepository()
                .getPartialContact(partialContactRequest);
        String formJsonString = partialContact != null && (partialContact.getFormJson() != null || partialContact
                .getFormJsonDraft() != null) ?
                (partialContact.getFormJsonDraft() != null ? partialContact.getFormJsonDraft() : partialContact
                        .getFormJson()) : form.toString();
        object = new JSONObject(formJsonString);

        JSONObject globals = null;
        if (form.has(JsonFormConstants.JSON_FORM_KEY.GLOBAL)) {
            globals = form.getJSONObject(JsonFormConstants.JSON_FORM_KEY.GLOBAL);
        }

        if (globals != null) {
            object.put(JsonFormConstants.JSON_FORM_KEY.GLOBAL, globals);
        }

        return object;
    }


    public static void processSpecialWidgets(JSONObject widget) throws Exception {
        String widgetType = widget.getString(JsonFormConstants.TYPE);
        List<String> keyList = new ArrayList<>();
        List<String> valueList = new ArrayList<>();

        if (widgetType.equals(JsonFormConstants.CHECK_BOX)) {
            JSONArray jsonArray = widget.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (jsonObject.has(JsonFormConstants.VALUE) && !TextUtils
                        .isEmpty(jsonObject.getString(JsonFormConstants.VALUE)) && jsonObject
                        .getString(JsonFormConstants.VALUE).equals(Constants.BOOLEAN.TRUE)) {
                    keyList.add(jsonObject.getString(JsonFormConstants.KEY));
                    if (jsonObject.has(JsonFormConstants.SECONDARY_VALUE) && !TextUtils
                            .isEmpty(jsonObject.getString(JsonFormConstants.SECONDARY_VALUE))) {
                        getRealSecondaryValue(jsonObject);
                    } else {
                        valueList.add(jsonObject.getString(JsonFormConstants.TEXT));
                    }
                }
            }

            if (keyList.size() > 0) {
                widget.put(JsonFormConstants.VALUE, keyList);
                widget.put(ContactJsonFormUtils.getSecondaryKey(widget),
                        ContactJsonFormUtils.getListValuesAsString(valueList));
            }

        } else if (widgetType.equals(JsonFormConstants.NATIVE_RADIO_BUTTON) || widgetType
                .equals(JsonFormConstants.RADIO_BUTTON) || widgetType.equals(Constants.ANC_RADIO_BUTTON)) {
            //Value already good for radio buttons so no keylist
            JSONArray jsonArray = widget.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonObject = jsonArray.getJSONObject(i);

                if (widget.has(JsonFormConstants.VALUE) && !TextUtils
                        .isEmpty(widget.getString(JsonFormConstants.VALUE)) && jsonObject
                        .getString(JsonFormConstants.KEY).equals(widget.getString(JsonFormConstants.VALUE))) {

                    if (jsonObject.has(JsonFormConstants.SECONDARY_VALUE) && !TextUtils
                            .isEmpty(jsonObject.getString(JsonFormConstants.SECONDARY_VALUE))) {

                        jsonObject.put(Constants.KEY.PARENT_SECONDARY_KEY, ContactJsonFormUtils.getSecondaryKey(widget));
                        getRealSecondaryValue(jsonObject);

                        if (jsonObject.has(Constants.KEY.SECONDARY_VALUES)) {
                            widget.put(Constants.KEY.SECONDARY_VALUES,
                                    jsonObject.getJSONArray(Constants.KEY.SECONDARY_VALUES));
                        }

                        break;

                    } else {
                        valueList.add(jsonObject.getString(JsonFormConstants.TEXT));
                    }

                }

            }

            if (valueList.size() > 0) {
                widget.put(ContactJsonFormUtils.getSecondaryKey(widget),
                        ContactJsonFormUtils.getListValuesAsString(valueList));
            }
        }
    }


    public static void getRealSecondaryValue(JSONObject jsonObject) throws Exception {

        JSONArray jsonArray2 = jsonObject.getJSONArray(JsonFormConstants.SECONDARY_VALUE);

        jsonObject.put(Constants.KEY.SECONDARY_VALUES, new JSONArray());

        String keystone = jsonObject.has(Constants.KEY.PARENT_SECONDARY_KEY) ? jsonObject
                .getString(Constants.KEY.PARENT_SECONDARY_KEY) : ContactJsonFormUtils.getSecondaryKey(jsonObject);
        jsonObject.getJSONArray(Constants.KEY.SECONDARY_VALUES).put(new JSONObject(ImmutableMap
                .of(JsonFormConstants.KEY, keystone, JsonFormConstants.VALUE,
                        jsonObject.getString(JsonFormConstants.TEXT))));


        for (int j = 0; j < jsonArray2.length(); j++) {

            JSONObject secValue = jsonArray2.getJSONObject(j);

            JSONArray values = secValue.getJSONArray(JsonFormConstants.VALUES);

            int valueLength = values.length();

            List<String> keyList = new ArrayList<>();
            List<String> valueList = new ArrayList<>();


            for (int k = 0; k < valueLength; k++) {
                String valuesString = values.getString(k);
                String keyString = "";
                if (TextUtils.isEmpty(keyString) && valuesString.contains(":")) {
                    keyString = valuesString.substring(0, valuesString.indexOf(":"));
                    keyList.add(keyString);
                }
                valuesString = valuesString.contains(":") ? valuesString
                        .substring(valuesString.indexOf(":") + 1) : valuesString;
                valuesString = valuesString.contains(":") ? valuesString
                        .substring(0, valuesString.indexOf(":")) : valuesString;

                valueList.add(valuesString);


            }
            if (keyList.size() > 0 && keyList.get(0).equals("other")) {
                Collections.reverse(keyList);
                Collections.reverse(valueList);
            }


            JSONObject secValueJsonObject = new JSONObject(ImmutableMap
                    .of(JsonFormConstants.KEY, ContactJsonFormUtils.getSecondaryKey(secValue), JsonFormConstants.VALUE,
                            ContactJsonFormUtils.getListValuesAsString(valueList)));
            jsonObject.getJSONArray(Constants.KEY.SECONDARY_VALUES).put(secValueJsonObject);

            secValue.put(JsonFormConstants.VALUE, keyList.size() > 0 ? keyList : valueList);
            jsonObject.getJSONArray(Constants.KEY.SECONDARY_VALUES).put(secValue);

        }

    }

    /**
     * This updates the expansion panel child values affect the done is selected from the pop up. It also updates the expansion panel status
     * image. It changes it to green when done, yellow when ordered, grey when not done
     *
     * @param values          {@link List<String>}
     * @param statusImageView {@link ImageView}
     * @throws JSONException
     * @author dubdabasoduba
     */
    public void updateExpansionPanelRecyclerView(List<String> values, ImageView statusImageView, Context context)
            throws JSONException {
        JSONArray list = new JSONArray(values);
        for (int k = 0; k < list.length(); k++) {
            String[] stringValues = list.getString(k).split(":");
            if (stringValues.length >= 2) {
                String valueDisplay = list.getString(k).split(":")[1];
                if (valueDisplay.equals(Constants.ANC_RADIO_BUTTON_OPTION_TYPES.DONE_TODAY) || valueDisplay
                        .equals(Constants.ANC_RADIO_BUTTON_OPTION_TEXT.DONE_TODAY) || valueDisplay
                        .equals(Constants.ANC_RADIO_BUTTON_OPTION_TYPES.DONE) || valueDisplay
                        .equals(Constants.ANC_RADIO_BUTTON_OPTION_TEXT.DONE) || valueDisplay
                        .equals(Constants.ANC_RADIO_BUTTON_OPTION_TYPES.DONE_EARLIER) || valueDisplay
                        .equals(Constants.ANC_RADIO_BUTTON_OPTION_TEXT.DONE_EARLIER) || valueDisplay
                        .equals(Constants.ANC_RADIO_BUTTON_OPTION_TYPES.ORDERED) || valueDisplay
                        .equals(Constants.ANC_RADIO_BUTTON_OPTION_TEXT.ORDERED) || valueDisplay
                        .equals(Constants.ANC_RADIO_BUTTON_OPTION_TYPES.NOT_DONE) || valueDisplay
                        .equals(Constants.ANC_RADIO_BUTTON_OPTION_TEXT.NOT_DONE)) {

                    changeIcon(statusImageView, valueDisplay, context);
                    break;
                }
            }
        }
    }

    public static JSONObject createSecondaryFormObject(JSONObject parentObject, JSONObject jsonSubForm, String encounterType)
            throws JSONException {
        Map<String, String> vMap = new HashMap<>();
        JSONObject resultJsonObject = new JSONObject();
        JSONObject stepJsonObject = new JSONObject();
        JSONArray fieldsJsonArray = jsonSubForm.getJSONArray(JsonFormConstants.CONTENT_FORM);

        if (parentObject.has(JsonFormConstants.VALUE) && !TextUtils
                .isEmpty(parentObject.getString(JsonFormConstants.VALUE))) {
            if (parentObject.get(JsonFormConstants.VALUE) instanceof JSONArray) {
                JSONArray jsonArray = parentObject.getJSONArray(JsonFormConstants.VALUE);
                for (int j = 0; j < jsonArray.length(); j++) {
                    populateValueMap(vMap, jsonArray.getJSONObject(j));
                }

            } else {
                populateValueMap(vMap, parentObject.getJSONObject(JsonFormConstants.VALUE));
            }

            for (int l = 0; l < fieldsJsonArray.length(); l++) {
                String value = vMap.get(fieldsJsonArray.getJSONObject(l).getString(JsonFormConstants.KEY));
                if (!TextUtils.isEmpty(value)) {
                    fieldsJsonArray.getJSONObject(l).put(JsonFormConstants.VALUE, value);
                }
            }

        }

        stepJsonObject.put(JsonFormConstants.FIELDS, fieldsJsonArray);
        resultJsonObject.put(JsonFormConstants.FIRST_STEP_NAME, stepJsonObject);
        resultJsonObject.put(Constants.JSON_FORM_KEY.ENCOUNTER_TYPE, encounterType);

        return resultJsonObject;

    }

    private static void populateValueMap(Map<String, String> vMap, JSONObject jsonObject) throws JSONException {
        String key = jsonObject.getString(JsonFormConstants.KEY);
        JSONArray values = jsonObject.getJSONArray(JsonFormConstants.VALUES);
        for (int k = 0; k < values.length(); k++) {
            String valuesString = values.getString(k);
            vMap.put(key, valuesString.contains(":") ? valuesString.substring(0, valuesString.indexOf(":")) : valuesString);
        }
    }

    public static void processRequiredStepsField(Facts facts, JSONObject object, Context context) throws Exception {
        if (object != null) {

            Iterator<String> keys = object.keys();

            while (keys.hasNext()) {
                String key = keys.next();

                if (key.startsWith(RuleConstant.STEP)) {
                    JSONArray stepArray = object.getJSONObject(key).getJSONArray(JsonFormConstants.FIELDS);

                    for (int i = 0; i < stepArray.length(); i++) {

                        JSONObject fieldObject = stepArray.getJSONObject(i);
                        ContactJsonFormUtils.processSpecialWidgets(fieldObject);

                        String fieldKey = getKey(fieldObject);

                        if (fieldKey != null && fieldObject.has(JsonFormConstants.VALUE)) {

                            facts.put(fieldKey, fieldObject.getString(JsonFormConstants.VALUE)); //Normal Value
                            ContactJsonFormUtils.processAbnormalValues(facts, fieldObject);

                            String secKey = ContactJsonFormUtils.getSecondaryKey(fieldObject);
                            if (fieldObject.has(secKey)) {

                                facts.put(secKey, fieldObject.getString(secKey)); //Normal value secondary key

                            }

                            if (fieldObject.has(Constants.KEY.SECONDARY_VALUES)) {

                                fieldObject.put(Constants.KEY.SECONDARY_VALUES,
                                        sortSecondaryValues(fieldObject));//sort and reset

                                JSONArray secondaryValues = fieldObject.getJSONArray(Constants.KEY.SECONDARY_VALUES);

                                for (int j = 0; j < secondaryValues.length(); j++) {
                                    JSONObject jsonObject = secondaryValues.getJSONObject(j);
                                    ContactJsonFormUtils.processAbnormalValues(facts, jsonObject);//secondary values
                                }
                            }

                            //Other field for check boxes
                            if (fieldObject.has(JsonFormConstants.VALUE) && !TextUtils
                                    .isEmpty(fieldObject.getString(JsonFormConstants.VALUE)) && fieldObject
                                    .getString(Constants.KEY.KEY).endsWith(Constants.SUFFIX.OTHER) && facts
                                    .get(fieldObject.getString(Constants.KEY.KEY)
                                            .replace(Constants.SUFFIX.OTHER, Constants.SUFFIX.VALUE)) != null) {

                                facts.put(getSecondaryKey(fieldObject), fieldObject.getString(JsonFormConstants.VALUE));
                                ContactJsonFormUtils.processAbnormalValues(facts, fieldObject);

                            }
                        }

                        if (fieldObject.has(JsonFormConstants.CONTENT_FORM)) {
                            try {

                                JSONObject subFormJson =
                                        com.vijay.jsonwizard.utils.FormUtils
                                                .getSubFormJson(fieldObject.getString(JsonFormConstants.CONTENT_FORM),
                                                        fieldObject
                                                                .has(JsonFormConstants.CONTENT_FORM_LOCATION) ? fieldObject
                                                                .getString(JsonFormConstants.CONTENT_FORM_LOCATION) : "",
                                                        context);
                                processRequiredStepsField(facts,
                                        ContactJsonFormUtils.createSecondaryFormObject(fieldObject, subFormJson,
                                                object.getString(Constants.JSON_FORM_KEY.ENCOUNTER_TYPE)), context);

                            } catch (Exception e) {
                                Log.e(TAG, e.getMessage(), e);
                            }
                        }


                    }

                }
            }
        }
    }

    public static JSONArray sortSecondaryValues(JSONObject fieldObject) throws JSONException {
        JSONObject otherValue = null;
        JSONArray newJsonArray = new JSONArray();

        JSONArray secondaryValues = fieldObject.getJSONArray(Constants.KEY.SECONDARY_VALUES);

        for (int j = 0; j < secondaryValues.length(); j++) {
            JSONObject jsonObject = secondaryValues.getJSONObject(j);

            if (jsonObject.getString(JsonFormConstants.KEY).endsWith(Constants.SUFFIX.OTHER)) {
                otherValue = jsonObject;
            } else {
                newJsonArray.put(jsonObject);
            }
        }

        if (otherValue != null) {
            newJsonArray.put(otherValue);
        }

        return newJsonArray;
    }

    public static void processAbnormalValues(Facts facts, JSONObject jsonObject) throws Exception {

        String fieldKey = getKey(jsonObject);
        Object fieldValue = getValue(jsonObject);
        String fieldKeySecondary = fieldKey.contains(Constants.SUFFIX.OTHER) ? fieldKey
                .substring(0, fieldKey.indexOf(Constants.SUFFIX.OTHER)) + Constants.SUFFIX.VALUE : "";
        String fieldKeyOtherValue = fieldKey + Constants.SUFFIX.VALUE;

        if (fieldKey.endsWith(Constants.SUFFIX.OTHER) && !fieldKeySecondary.isEmpty() && facts
                .get(fieldKeySecondary) != null && facts.get(fieldKeyOtherValue) != null) {

            List<String> tempList = new ArrayList<>(
                    Arrays.asList(facts.get(fieldKeySecondary).toString().split("\\s*,\\s*")));
            tempList.remove(tempList.size() - 1);
            tempList.add(StringUtils.capitalize(facts.get(fieldKeyOtherValue).toString()));
            facts.put(fieldKeySecondary, ContactJsonFormUtils.getListValuesAsString(tempList));

           /* if (fieldKey.endsWith(Constants.SUFFIX.ABNORMAL_OTHER)) {
                String valueKey = fieldKeySecondary.replace(Constants.SUFFIX.ABNORMAL, "");
                String prefix = facts.get(valueKey);
                facts.put(valueKey, prefix + " - " + facts.get(fieldKeySecondary));
            }*/

        } else {
            facts.put(fieldKey, fieldValue);
        }

    }

    public static String getKey(JSONObject jsonObject) throws Exception {

        return jsonObject.has(JsonFormConstants.KEY) ? jsonObject.getString(JsonFormConstants.KEY) : null;
    }

    public static String getValue(JSONObject jsonObject) throws Exception {

        return jsonObject.has(JsonFormConstants.VALUE) ? jsonObject.getString(JsonFormConstants.VALUE) : null;
    }

    public static String getSecondaryKey(JSONObject jsonObject) throws Exception {

        return getKey(jsonObject) + Constants.SUFFIX.VALUE;

    }

    /**
     * @return comma separated string of list values
     */
    public static String getListValuesAsString(List<String> list) {
        return list.toString().substring(1, list.toString().length() - 1);
    }

    public static String cleanValue(String raw) {
        return raw.charAt(0) == '[' ? raw.substring(1, raw.length() - 1) : raw;
    }

    public static String keyToValueConverter(String keys) {
        if (!TextUtils.isEmpty(keys) && keys.charAt(0) == '[') {
            return WordUtils.capitalize(cleanValue(keys)).replaceAll("_", " ");
        } else {
            return keys;
        }
    }

}
