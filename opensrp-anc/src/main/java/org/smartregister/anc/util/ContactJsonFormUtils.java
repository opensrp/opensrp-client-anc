package org.smartregister.anc.util;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
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
    public static final String TAG = ContactJsonFormUtils.class.getCanonicalName();
    private AncGenericDialogInterface genericDialogInterface;

    public static String obtainValue(String key, JSONArray value) throws JSONException {
        String result = "";
        for (int j = 0; j < value.length(); j++) {
            JSONObject valueItem = value.getJSONObject(j);
            if (valueItem.getString(JsonFormConstants.KEY).equals(key)) {
                JSONArray valueItemJSONArray = valueItem.getJSONArray(JsonFormConstants.VALUES);
                result = extractItemValue(valueItem, valueItemJSONArray);
                break;
            }
        }
        return result;
    }

    public static String removeKeyPrefix(String widgetKey, String prefix) {
        return widgetKey.replace(prefix + "_", "");
    }

    public static String extractItemValue(JSONObject valueItem, JSONArray valueItemJSONArray) throws JSONException {
        String result;
        switch (valueItem.getString(JsonFormConstants.TYPE)) {
            case JsonFormConstants.ANC_RADIO_BUTTON:
            case JsonFormConstants.NATIVE_RADIO_BUTTON:
                result = valueItemJSONArray.getString(0).split(":")[0];
                break;
            case JsonFormConstants.CHECK_BOX:
                StringBuilder sb = new StringBuilder("[");
                for (int index = 0; index < valueItemJSONArray.length(); index++) {
                    sb.append(valueItemJSONArray.getString(index).split(":")[0]);
                    sb.append(", ");
                }
                result = sb.toString().replaceAll(", $", "") + "]";
                break;
            default:
                result = valueItemJSONArray.getString(0);
                break;
        }
        return result;
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
        //partial contact exists?

        PartialContact partialContact = AncApplication.getInstance().getPartialContactRepository()
                .getPartialContact(partialContactRequest);

        String formJsonString = isValidPartialForm(partialContact) ? getPartialContactForm(partialContact) : form.toString();
        JSONObject object = new JSONObject(formJsonString);

        JSONObject globals = null;
        if (form.has(JsonFormConstants.JSON_FORM_KEY.GLOBAL)) {
            globals = form.getJSONObject(JsonFormConstants.JSON_FORM_KEY.GLOBAL);
        }

        if (globals != null) {
            object.put(JsonFormConstants.JSON_FORM_KEY.GLOBAL, globals);
        }

        return object;
    }

    private static String getPartialContactForm(PartialContact partialContact) {
        return partialContact.getFormJsonDraft() != null ? partialContact.getFormJsonDraft() : partialContact.getFormJson();
    }

    private static boolean isValidPartialForm(PartialContact partialContact) {
        return partialContact != null && (partialContact.getFormJson() != null || partialContact.getFormJsonDraft() != null);
    }

    public static void processSpecialWidgets(JSONObject widget) throws Exception {
        String widgetType = widget.getString(JsonFormConstants.TYPE);
        List<String> keyList = new ArrayList<>();
        List<String> valueList = new ArrayList<>();

        if (widgetType.equals(JsonFormConstants.CHECK_BOX)) {
            processCheckBoxSpecialWidget(widget, keyList, valueList);

        } else if (widgetType.equals(JsonFormConstants.NATIVE_RADIO_BUTTON) ||
                widgetType.equals(JsonFormConstants.RADIO_BUTTON) || widgetType.equals(Constants.ANC_RADIO_BUTTON)) {
            processRadioButtonsSpecialWidget(widget, valueList);
        }
    }

    private static void processRadioButtonsSpecialWidget(JSONObject widget, List<String> valueList) throws Exception {
        //Value already good for radio buttons so no keylist
        JSONArray jsonArray = widget.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME);

        for (int i = 0; i < jsonArray.length(); i++) {

            JSONObject jsonObject = jsonArray.getJSONObject(i);

            if (widget.has(JsonFormConstants.VALUE) && !TextUtils.isEmpty(widget.getString(JsonFormConstants.VALUE)) &&
                    jsonObject.getString(JsonFormConstants.KEY).equals(widget.getString(JsonFormConstants.VALUE))) {

                if (jsonObject.has(JsonFormConstants.SECONDARY_VALUE) &&
                        !TextUtils.isEmpty(jsonObject.getString(JsonFormConstants.SECONDARY_VALUE))) {

                    jsonObject.put(Constants.KEY.PARENT_SECONDARY_KEY, ContactJsonFormUtils.getSecondaryKey(widget));
                    getRealSecondaryValue(jsonObject);

                    if (jsonObject.has(Constants.KEY.SECONDARY_VALUES)) {
                        widget.put(Constants.KEY.SECONDARY_VALUES, jsonObject.getJSONArray(Constants.KEY.SECONDARY_VALUES));
                    }

                    break;

                } else {
                    valueList.add(jsonObject.getString(JsonFormConstants.TEXT));
                }

            }

        }

        if (valueList.size() > 0) {
            widget.put(ContactJsonFormUtils.getSecondaryKey(widget), ContactJsonFormUtils.getListValuesAsString(valueList));
        }
    }

    private static void processCheckBoxSpecialWidget(JSONObject widget, List<String> keyList, List<String> valueList)
            throws Exception {
        //Clear previous selected values from the widget first
        if (widget.has(JsonFormConstants.VALUE)) {
            widget.remove(JsonFormConstants.VALUE);
        }
        if (widget.has(ContactJsonFormUtils.getSecondaryKey(widget))) {
            widget.remove(ContactJsonFormUtils.getSecondaryKey(widget));
        }
        JSONArray jsonArray = widget.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            if (jsonObject.has(JsonFormConstants.VALUE) &&
                    !TextUtils.isEmpty(jsonObject.getString(JsonFormConstants.VALUE)) &&
                    jsonObject.getString(JsonFormConstants.VALUE).equals(Constants.BOOLEAN.TRUE)) {
                keyList.add(jsonObject.getString(JsonFormConstants.KEY));
                if (jsonObject.has(JsonFormConstants.SECONDARY_VALUE) &&
                        jsonObject.getJSONArray(JsonFormConstants.SECONDARY_VALUE).length() > 0) {
                    getRealSecondaryValue(jsonObject);
                } else {
                    valueList.add(jsonObject.getString(JsonFormConstants.TEXT));
                }
            }
        }

        if (keyList.size() > 0) {
            widget.put(JsonFormConstants.VALUE, keyList);
            widget.put(ContactJsonFormUtils.getSecondaryKey(widget), ContactJsonFormUtils.getListValuesAsString(valueList));
        }
    }

    public static void getRealSecondaryValue(JSONObject jsonObject) throws Exception {

        JSONArray jsonArray2 = jsonObject.getJSONArray(JsonFormConstants.SECONDARY_VALUE);

        jsonObject.put(Constants.KEY.SECONDARY_VALUES, new JSONArray());

        String keystone = jsonObject.has(Constants.KEY.PARENT_SECONDARY_KEY) ?
                jsonObject.getString(Constants.KEY.PARENT_SECONDARY_KEY) : ContactJsonFormUtils.getSecondaryKey(jsonObject);
        jsonObject.getJSONArray(Constants.KEY.SECONDARY_VALUES).put(new JSONObject(ImmutableMap
                .of(JsonFormConstants.KEY, keystone, JsonFormConstants.VALUE,
                        jsonObject.getString(JsonFormConstants.TEXT))));


        for (int j = 0; j < jsonArray2.length(); j++) {
            JSONObject secValue = jsonArray2.getJSONObject(j);

            if (secValue.length() > 0) {
                JSONArray values = new JSONArray();
                if (secValue.has(JsonFormConstants.VALUES)) {
                    values = secValue.getJSONArray(JsonFormConstants.VALUES);
                }

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
                    valuesString =
                            valuesString.contains(":") ? valuesString
                                    .substring(valuesString.indexOf(":") + 1) : valuesString;
                    valuesString =
                            valuesString.contains(":") ? valuesString.substring(0, valuesString.indexOf(":")) : valuesString;

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
    }

    public static JSONObject createSecondaryFormObject(JSONObject parentObject, JSONObject jsonSubForm, String encounterType)
            throws JSONException {
        Map<String, String> vMap = new HashMap<>();
        JSONObject resultJsonObject = new JSONObject();
        JSONObject stepJsonObject = new JSONObject();
        JSONArray fieldsJsonArray = jsonSubForm.getJSONArray(JsonFormConstants.CONTENT_FORM);

        if (parentObject.has(JsonFormConstants.VALUE) &&
                !TextUtils.isEmpty(parentObject.getString(JsonFormConstants.VALUE))) {
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

                            processRequiredStepsFieldsSecondaryValues(facts, fieldObject);
                            processRequiredStepsExpansionPanelValues(facts, fieldObject);
                            processOtherCheckBoxField(facts, fieldObject);

                        }

                        if (fieldObject.has(JsonFormConstants.CONTENT_FORM)) {
                            try {

                                JSONObject subFormJson = FormUtils
                                        .getSubFormJson(fieldObject.getString(JsonFormConstants.CONTENT_FORM),
                                                fieldObject.has(JsonFormConstants.CONTENT_FORM_LOCATION) ?
                                                        fieldObject.getString(JsonFormConstants.CONTENT_FORM_LOCATION) : "",
                                                context);
                                processRequiredStepsField(facts, ContactJsonFormUtils
                                        .createSecondaryFormObject(fieldObject, subFormJson,
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

    private static void processOtherCheckBoxField(Facts facts, JSONObject fieldObject) throws Exception {
        //Other field for check boxes
        if (fieldObject.has(JsonFormConstants.VALUE) && !TextUtils.isEmpty(fieldObject.getString(JsonFormConstants.VALUE)) &&
                fieldObject.getString(Constants.KEY.KEY).endsWith(Constants.SUFFIX.OTHER) && facts.get(
                fieldObject.getString(Constants.KEY.KEY).replace(Constants.SUFFIX.OTHER, Constants.SUFFIX.VALUE)) != null) {

            facts.put(getSecondaryKey(fieldObject), fieldObject.getString(JsonFormConstants.VALUE));
            ContactJsonFormUtils.processAbnormalValues(facts, fieldObject);

        }
    }

    /**
     * Processes the number of Required fields for the specific field with secondary values
     *
     * @param facts       {@link Facts}
     * @param fieldObject {@link JSONObject}
     * @throws Exception {@link JSONException}
     */
    private static void processRequiredStepsFieldsSecondaryValues(Facts facts, JSONObject fieldObject) throws Exception {
        if (fieldObject.has(Constants.KEY.SECONDARY_VALUES)) {
            fieldObject.put(Constants.KEY.SECONDARY_VALUES, sortSecondaryValues(fieldObject));//sort and reset

            JSONArray secondaryValues = fieldObject.getJSONArray(Constants.KEY.SECONDARY_VALUES);

            for (int j = 0; j < secondaryValues.length(); j++) {
                JSONObject jsonObject = secondaryValues.getJSONObject(j);
                ContactJsonFormUtils.processAbnormalValues(facts, jsonObject);//secondary values
            }
        }
    }

    /**
     * Processes the number of Required fields for the specific field with secondary values
     *
     * @param facts       {@link Facts}
     * @param fieldObject {@link JSONObject}
     * @throws Exception {@link JSONException}
     */
    private static void processRequiredStepsExpansionPanelValues(Facts facts, JSONObject fieldObject) throws Exception {
        if (fieldObject.has(JsonFormConstants.TYPE) &&
                JsonFormConstants.EXPANSION_PANEL.equals(fieldObject.getString(JsonFormConstants.TYPE))) {
            JSONArray expansionPanelValue = fieldObject.getJSONArray(JsonFormConstants.VALUE);

            for (int j = 0; j < expansionPanelValue.length(); j++) {
                JSONObject jsonObject = expansionPanelValue.getJSONObject(j);
                ContactJsonFormUtils.processAbnormalValues(facts, jsonObject);//secondary values
            }
        }
    }

    public static JSONArray sortSecondaryValues(JSONObject fieldObject) throws JSONException {
        JSONObject otherValue = null;
        JSONArray newJsonArray = new JSONArray();

        JSONArray secondaryValues = fieldObject.getJSONArray(Constants.KEY.SECONDARY_VALUES);

        for (int j = 0; j < secondaryValues.length(); j++) {
            JSONObject jsonObject = secondaryValues.getJSONObject(j);

            if (jsonObject.has(JsonFormConstants.KEY) && jsonObject.getString(JsonFormConstants.KEY)
                    .endsWith(Constants.SUFFIX.OTHER)) {
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
        String fieldKeySecondary = fieldKey.contains(Constants.SUFFIX.OTHER) ?
                fieldKey.substring(0, fieldKey.indexOf(Constants.SUFFIX.OTHER)) + Constants.SUFFIX.VALUE : "";
        String fieldKeyOtherValue = fieldKey + Constants.SUFFIX.VALUE;

        if (fieldKey.endsWith(Constants.SUFFIX.OTHER) && !fieldKeySecondary.isEmpty() &&
                facts.get(fieldKeySecondary) != null && facts.get(fieldKeyOtherValue) != null) {

            List<String> tempList =
                    new ArrayList<>(Arrays.asList(facts.get(fieldKeySecondary).toString().split("\\s*,\\s*")));
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


    public static String getKey(JSONObject jsonObject) throws JSONException {
        return jsonObject.has(JsonFormConstants.KEY) ? jsonObject.getString(JsonFormConstants.KEY) : null;
    }

    public static String getValue(JSONObject jsonObject) throws JSONException {

        return jsonObject.has(JsonFormConstants.VALUE) ? jsonObject.getString(JsonFormConstants.VALUE) : null;
    }

    public static String getSecondaryKey(JSONObject jsonObject) throws JSONException {

        return getKey(jsonObject) + Constants.SUFFIX.VALUE;

    }

    /**
     * @return comma separated string of list values
     */
    public static String getListValuesAsString(List<String> list) {
        return list != null ? list.toString().substring(1, list.toString().length() - 1) : "";
    }

    public static String cleanValue(String raw) {
        if (raw.length() > 0 && raw.charAt(0) == '[') {
            return raw.substring(1, raw.length() - 1);
        } else {
            return raw;
        }
    }

    public static String keyToValueConverter(String keys) {
        if (keys != null) {
            String cleanKey = WordUtils.capitalizeFully(cleanValue(keys));
            if (!TextUtils.isEmpty(keys)) {
                return cleanKey.replaceAll("_", " ");
            } else {
                return cleanKey;
            }
        } else {
            return "";
        }
    }

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
            genericPopupDialog.setContext(context);
            if (type != null && type.equals(Constants.EXPANSION_PANEL)) {
                genericPopupDialog.setHeader(toolbarHeader);
                genericPopupDialog.setContainer(container);
            }
            genericPopupDialog.setWidgetType(type);
            if (customTextView != null && reasonsTextView != null) {
                genericPopupDialog.setCustomTextView(customTextView);
                genericPopupDialog.setPopupReasonsTextView(reasonsTextView);
            }
            if (type != null &&
                    (type.equals(JsonFormConstants.CHECK_BOX) || type.equals(JsonFormConstants.NATIVE_RADIO_BUTTON))) {
                childKey = (String) view.getTag(com.vijay.jsonwizard.R.id.childKey);
                genericPopupDialog.setChildKey(childKey);
            }

            Activity activity = (Activity) context;
            FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
            Fragment prev = activity.getFragmentManager().findFragmentByTag("ANCGenericPopup");
            if (prev != null) {
                ft.remove(prev);
            }

            ft.addToBackStack(null);
            genericPopupDialog.show(ft, "ANCGenericPopup");
        } else {
            Toast.makeText(context, "Please specify the sub form to display ", Toast.LENGTH_LONG).show();
        }
    }

    public Map<String, String> createAssignedValue(AncGenericDialogInterface genericDialogInterface, String itemKey,
                                                   String optionKey, String keyValue, String itemType, String itemText) {
        this.genericDialogInterface = genericDialogInterface;
        return addAssignedValue(itemKey, optionKey, keyValue, itemType, itemText);
    }

    @Override
    public Map<String, String> addAssignedValue(String itemKey, String optionKey, String keyValue, String itemType,
                                                String itemText) {
        Map<String, String> value = new HashMap<>();
        if (genericDialogInterface != null && !TextUtils.isEmpty(genericDialogInterface.getWidgetType()) &&
                genericDialogInterface.getWidgetType().equals(Constants.EXPANSION_PANEL)) {
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

    /**
     * This updates the expansion panel child values affect the done is selected from the pop up. It also updates the
     * expansion panel status image. It changes it to green when done, yellow when ordered, grey when not done
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
                if (valueDisplay.equals(Constants.ANC_RADIO_BUTTON_OPTION_TYPES.DONE_TODAY) ||
                        valueDisplay.equals(Constants.ANC_RADIO_BUTTON_OPTION_TEXT.DONE_TODAY) ||
                        valueDisplay.equals(Constants.ANC_RADIO_BUTTON_OPTION_TYPES.DONE) ||
                        valueDisplay.equals(Constants.ANC_RADIO_BUTTON_OPTION_TEXT.DONE) ||
                        valueDisplay.equals(Constants.ANC_RADIO_BUTTON_OPTION_TYPES.DONE_EARLIER) ||
                        valueDisplay.equals(Constants.ANC_RADIO_BUTTON_OPTION_TEXT.DONE_EARLIER) ||
                        valueDisplay.equals(Constants.ANC_RADIO_BUTTON_OPTION_TYPES.ORDERED) ||
                        valueDisplay.equals(Constants.ANC_RADIO_BUTTON_OPTION_TEXT.ORDERED) ||
                        valueDisplay.equals(Constants.ANC_RADIO_BUTTON_OPTION_TYPES.NOT_DONE) ||
                        valueDisplay.equals(Constants.ANC_RADIO_BUTTON_OPTION_TEXT.NOT_DONE)) {

                    changeIcon(statusImageView, valueDisplay, context);
                    break;
                }
            }
        }
    }

    public Facts getCheckBoxResults(JSONObject jsonObject) throws JSONException {
        Facts result = new Facts();
        JSONArray options = jsonObject.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME);
        for (int j = 0; j < options.length(); j++) {
            if (options.getJSONObject(j).has(JsonFormConstants.VALUE)) {
                if (jsonObject.has(RuleConstant.IS_RULE_CHECK) && jsonObject.getBoolean(RuleConstant.IS_RULE_CHECK)) {
                    if (Boolean.valueOf(options.getJSONObject(j)
                            .getString(JsonFormConstants.VALUE))) {//Rules engine use only true values
                        result.put(options.getJSONObject(j).getString(JsonFormConstants.KEY),
                                options.getJSONObject(j).getString(JsonFormConstants.VALUE));
                    }
                } else {
                    result.put(options.getJSONObject(j).getString(JsonFormConstants.KEY),
                            options.getJSONObject(j).getString(JsonFormConstants.VALUE));
                }
            }

            //Backward compatibility Fix
            if (jsonObject.has(RuleConstant.IS_RULE_CHECK) && !jsonObject.getBoolean(RuleConstant.IS_RULE_CHECK)) {
                if (options.getJSONObject(j).has(JsonFormConstants.VALUE)) {
                    result.put(JsonFormConstants.VALUE, options.getJSONObject(j).getString(JsonFormConstants.VALUE));
                } else {
                    result.put(JsonFormConstants.VALUE, Constants.FALSE);
                }
            }
        }
        return result;
    }

    /**
     * Filters checkbox values based on specified list
     *
     * @param mainJsonObject Main json object with all fields
     * @throws JSONException Capture Json Form errors
     */
    public static void processCheckboxFilteredItems(JSONObject mainJsonObject) throws JSONException {

        if (!mainJsonObject.has(Constants.FILTERED_ITEMS) || mainJsonObject.getJSONArray(Constants.FILTERED_ITEMS).length() < 1) {
            return;
        }

        JSONArray filteredItems = mainJsonObject.getJSONArray(Constants.FILTERED_ITEMS);
        for (int index = 0; index < filteredItems.length(); index++) {
            String step = filteredItems.getString(index).split("_")[0];
            String key = removeKeyPrefix(filteredItems.getString(index), step);
            JSONObject checkBoxField = FormUtils.getFieldJSONObject(FormUtils.fields(mainJsonObject, step), key);
            if (!mainJsonObject.has(Constants.GLOBAL) || checkBoxField == null || !checkBoxField.getString(JsonFormConstants.TYPE).equals(JsonFormConstants.CHECK_BOX)) {
                return;
            }
            if (!checkBoxField.optBoolean(Constants.IS_FILTERED, false)) {
                ArrayList<JSONObject> newOptionsList = new ArrayList<>();
                Map<String, JSONObject> optionsMap = new HashMap<>();
                JSONArray checkboxOptions = checkBoxField.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME);

                for (int i = 0; i < checkboxOptions.length(); i++) {
                    JSONObject item = checkboxOptions.getJSONObject(i);
                    optionsMap.put(item.getString(JsonFormConstants.KEY), item);
                }
                //Treat none option as special.
                if (optionsMap.containsKey("none")) {
                    newOptionsList.add(optionsMap.get("none"));
                }

                if (checkBoxField.has(Constants.FILTER_OPTIONS_SOURCE)) {
                    String filterOptionsSource = checkBoxField.getString(Constants.FILTER_OPTIONS_SOURCE);
                    if (!filterOptionsSource.startsWith("global_")) {
                        return;
                    }
                    String globalKey = removeKeyPrefix(filterOptionsSource, Constants.GLOBAL);
                    String itemsToFilter = mainJsonObject.getJSONObject(Constants.GLOBAL).getString(globalKey);

                    if (TextUtils.isEmpty(itemsToFilter)) {
                        return;
                    }
                    //Remove square braces and split the filterOptions to array of strings
                    String[] filteredKeys = itemsToFilter.substring(1, itemsToFilter.length() - 1).split(", ");

                    for (String filteredKey : filteredKeys) {
                        if (!TextUtils.equals("none",filteredKey)) {
                            newOptionsList.add(optionsMap.get(filteredKey));
                        }
                    }
                } else {
                    if (checkBoxField.has(Constants.FILTER_OPTIONS)) {
                        JSONArray filterOptions = checkBoxField.getJSONArray(Constants.FILTER_OPTIONS);
                        if (filterOptions.length() > 0) {
                            for (int count = 0; count < filterOptions.length(); count++) {
                                JSONObject filterOption = filterOptions.getJSONObject(count);
                                if (!filterOption.has(JsonFormConstants.KEY) && !filterOption.has(JsonFormConstants.VALUE)) {
                                    Log.e(TAG, "JsonObject for filter options must contain a key value pair with an optional options attribute");
                                    return;
                                }
                                String keyGlobal = removeKeyPrefix(getKey(filterOption), Constants.GLOBAL);
                                String itemValue = getValue(filterOption);
                                String globalValue = mainJsonObject.getJSONObject(Constants.GLOBAL).getString(keyGlobal);

                                if (compareItemAndValueGlobal(itemValue, globalValue)) {
                                    JSONArray optionsToFilter = filterOption.optJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME);
                                    if (optionsToFilter == null) {
                                        String itemKey = removeKeyPrefix(keyGlobal, Constants.PREVIOUS);
                                        newOptionsList.add(optionsMap.get(itemKey));
                                    } else {
                                        for (int itemIndex = 0; itemIndex < optionsToFilter.length(); itemIndex++) {
                                            newOptionsList.add(optionsMap.get(optionsToFilter.getString(itemIndex)));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                checkBoxField.put(JsonFormConstants.OPTIONS_FIELD_NAME, new JSONArray(newOptionsList));
                checkBoxField.put(Constants.IS_FILTERED, true);
            }
        }
    }

    private static boolean compareItemAndValueGlobal(String itemValue, String globalValue) {
        if (!TextUtils.isEmpty(itemValue) && !TextUtils.isEmpty(globalValue)) {
            List<String> globalValuesList = new ArrayList<>();
            if (globalValue.startsWith("[")) {
                String[] globalValuesArray = globalValue.substring(1, globalValue.length() - 1).split(", ");
                globalValuesList.addAll(Arrays.asList(globalValuesArray));
            } else {
                globalValuesList.add(globalValue);
            }

            if (itemValue.startsWith("[")) {
                String[] itemValueArray = itemValue.substring(1, itemValue.length() - 1).split(", ");
                for (String item : itemValueArray) {
                    if (globalValuesList.contains(item)) {
                        return true;
                    }
                }
            } else if (itemValue.startsWith("!")) {
                return !globalValuesList.contains(itemValue.substring(1));
            } else {
                return TextUtils.equals(itemValue.trim(), globalValue.trim());
            }
        }
        return false;
    }

    public void addValuesDisplay(List<String> expansionWidgetValues, LinearLayout contentView, Context context) {
        if (expansionWidgetValues.size() > 0) {
            if (contentView.getChildCount() > 0) {
                contentView.removeAllViews();
            }
            for (int i = 0; i < expansionWidgetValues.size(); i++) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                LinearLayout valuesLayout = (LinearLayout) inflater.inflate(R.layout.native_expansion_panel_list_item, null);
                CustomTextView listHeader = valuesLayout.findViewById(R.id.item_header);
                CustomTextView listValue = valuesLayout.findViewById(R.id.item_value);
                listValue.setTextColor(context.getResources().getColor(R.color.text_color_primary));
                String[] valueObject = expansionWidgetValues.get(i).split(":");
                if (valueObject.length >= 2 && !Constants.ANC_RADIO_BUTTON_OPTION_TEXT.DONE_EARLIER.equals(valueObject[1]) &&
                        !Constants.ANC_RADIO_BUTTON_OPTION_TEXT.DONE_TODAY.equals(valueObject[1])) {
                    listHeader.setText(valueObject[0]);
                    listValue.setText(valueObject[1]);
                }

                contentView.addView(valuesLayout);
            }
        }
    }
}
