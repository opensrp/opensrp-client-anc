package org.smartregister.anc.library.util;

import android.text.TextUtils;

import com.google.common.collect.ImmutableMap;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.domain.ExpansionPanelItemModel;
import com.vijay.jsonwizard.rules.RuleConstant;
import com.vijay.jsonwizard.utils.FormUtils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.jeasy.rules.api.Facts;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.anc.library.AncLibrary;
import org.smartregister.anc.library.domain.Contact;
import org.smartregister.anc.library.model.PartialContact;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class ContactJsonFormUtils extends FormUtils {

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

    public static String extractItemValue(JSONObject valueItem, JSONArray valueItemJSONArray) throws JSONException {
        String result;
        switch (valueItem.getString(JsonFormConstants.TYPE)) {
            case JsonFormConstants.EXTENDED_RADIO_BUTTON:
            case JsonFormConstants.NATIVE_RADIO_BUTTON:
                result = valueItemJSONArray.getString(0).split(":")[0];
                break;
            case JsonFormConstants.CHECK_BOX:
                result = formatCheckboxValues(new StringBuilder("["), valueItemJSONArray, 0) + "]";
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
        AncLibrary.getInstance().getPartialContactRepositoryHelper().savePartialContact(partialContact);
    }

    public static JSONObject getFormJsonCore(PartialContact partialContactRequest, JSONObject form) throws JSONException {
        //partial contact exists?

        PartialContact partialContact = AncLibrary.getInstance().getPartialContactRepositoryHelper()
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

    private static boolean isValidPartialForm(PartialContact partialContact) {
        return partialContact != null && (partialContact.getFormJson() != null || partialContact.getFormJsonDraft() != null);
    }

    private static String getPartialContactForm(PartialContact partialContact) {
        return partialContact.getFormJsonDraft() != null ? partialContact.getFormJsonDraft() : partialContact.getFormJson();
    }

    public static void processSpecialWidgets(JSONObject widget) throws Exception {
        String widgetType = widget.getString(JsonFormConstants.TYPE);
        List<String> keyList = new ArrayList<>();
        List<String> valueList = new ArrayList<>();

        if (widgetType.equals(JsonFormConstants.CHECK_BOX)) {
            processCheckBoxSpecialWidget(widget, keyList, valueList);

        } else if (widgetType.equals(JsonFormConstants.NATIVE_RADIO_BUTTON) ||
                widgetType.equals(JsonFormConstants.RADIO_BUTTON) || widgetType.equals(ConstantsUtils.EXTENDED_RADIO_BUTTON)) {
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

                    jsonObject.put(ConstantsUtils.KeyUtils.PARENT_SECONDARY_KEY, ContactJsonFormUtils.getSecondaryKey(widget));
                    getRealSecondaryValue(jsonObject);

                    if (jsonObject.has(ConstantsUtils.KeyUtils.SECONDARY_VALUES)) {
                        widget.put(ConstantsUtils.KeyUtils.SECONDARY_VALUES, jsonObject.getJSONArray(ConstantsUtils.KeyUtils.SECONDARY_VALUES));
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
            if (jsonObject.has(JsonFormConstants.VALUE) && jsonObject.getBoolean(JsonFormConstants.VALUE)) {
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

    public static void getRealSecondaryValue(JSONObject itemField) throws Exception {
        JSONArray secondaryValues = itemField.getJSONArray(JsonFormConstants.SECONDARY_VALUE);
        itemField.put(ConstantsUtils.KeyUtils.SECONDARY_VALUES, new JSONArray());

        String keystone = itemField.has(ConstantsUtils.KeyUtils.PARENT_SECONDARY_KEY) ?
                itemField.getString(ConstantsUtils.KeyUtils.PARENT_SECONDARY_KEY) : ContactJsonFormUtils.getSecondaryKey(itemField);
        itemField.getJSONArray(ConstantsUtils.KeyUtils.SECONDARY_VALUES).put(new JSONObject(ImmutableMap.of(JsonFormConstants.KEY, keystone, JsonFormConstants.VALUE, itemField.getString(JsonFormConstants.TEXT))));

        setSecondaryValues(itemField, secondaryValues);
    }

    private static void setSecondaryValues(JSONObject itemField, JSONArray secondaryValues) throws JSONException {
        for (int j = 0; j < secondaryValues.length(); j++) {
            JSONObject secValue = secondaryValues.getJSONObject(j);

            if (secValue.length() > 0) {
                JSONArray values = new JSONArray();
                if (secValue.has(JsonFormConstants.VALUES)) {
                    values = secValue.getJSONArray(JsonFormConstants.VALUES);
                }

                int valueLength = values.length();

                List<String> keyList = new ArrayList<>();
                List<String> valueList = new ArrayList<>();

                getSecondaryValueKeyPair(values, valueLength, keyList, valueList);
                setItemSecondaryValues(itemField, secValue, keyList, valueList);
            }
        }
    }

    private static void setItemSecondaryValues(JSONObject itemField, JSONObject secValue, List<String> keyList, List<String> valueList) throws JSONException {
        JSONObject secValueJsonObject = new JSONObject(ImmutableMap
                .of(JsonFormConstants.KEY, ContactJsonFormUtils.getSecondaryKey(secValue), JsonFormConstants.VALUE,
                        ContactJsonFormUtils.getListValuesAsString(valueList)));
        itemField.getJSONArray(ConstantsUtils.KeyUtils.SECONDARY_VALUES).put(secValueJsonObject);

        secValue.put(JsonFormConstants.VALUE, keyList.size() > 0 ? keyList : valueList);
        itemField.getJSONArray(ConstantsUtils.KeyUtils.SECONDARY_VALUES).put(secValue);
    }

    private static void getSecondaryValueKeyPair(JSONArray values, int valueLength, List<String> keyList, List<String> valueList) throws JSONException {
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
        resultJsonObject.put(ConstantsUtils.JsonFormKeyUtils.ENCOUNTER_TYPE, encounterType);

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

    public static void processRequiredStepsField(Facts facts, JSONObject object) throws Exception {
        if (object != null) {
            Iterator<String> keys = object.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                if (key.startsWith(RuleConstant.STEP)) {
                    JSONArray stepArray = object.getJSONObject(key).getJSONArray(JsonFormConstants.FIELDS);

                    for (int i = 0; i < stepArray.length(); i++) {
                        JSONObject fieldObject = stepArray.getJSONObject(i);
                        ContactJsonFormUtils.processSpecialWidgets(fieldObject);

                        String fieldKey = getObjectKey(fieldObject);
                        //Do not add to facts values from expansion panels since they are processed separately
                        if (fieldKey != null && fieldObject.has(JsonFormConstants.VALUE) && fieldObject.has(JsonFormConstants.TYPE)
                                && !JsonFormConstants.EXPANSION_PANEL.equals(fieldObject.getString(JsonFormConstants.TYPE))) {

                            facts.put(fieldKey, fieldObject.getString(JsonFormConstants.VALUE));
                            ContactJsonFormUtils.processAbnormalValues(facts, fieldObject);
                            String secKey = ContactJsonFormUtils.getSecondaryKey(fieldObject);

                            if (fieldObject.has(secKey)) {
                                facts.put(secKey, fieldObject.getString(secKey)); //Normal value secondary key
                            }

                            processRequiredStepsFieldsSecondaryValues(facts, fieldObject);
                            processOtherCheckBoxField(facts, fieldObject);
                        }

                        if (fieldObject.has(JsonFormConstants.CONTENT_FORM)) {
                            processRequiredStepsExpansionPanelValues(facts, fieldObject);
                        }
                    }
                }
            }
        }
    }

    private static void processOtherCheckBoxField(Facts facts, JSONObject fieldObject) throws Exception {
        //Other field for check boxes
        if (fieldObject.has(JsonFormConstants.VALUE) && !TextUtils.isEmpty(fieldObject.getString(JsonFormConstants.VALUE)) &&
                fieldObject.getString(ConstantsUtils.KeyUtils.KEY).endsWith(ConstantsUtils.SuffixUtils.OTHER) && facts.get(
                fieldObject.getString(ConstantsUtils.KeyUtils.KEY).replace(ConstantsUtils.SuffixUtils.OTHER, ConstantsUtils.SuffixUtils.VALUE)) != null) {

            facts.put(getSecondaryKey(fieldObject), fieldObject.getString(JsonFormConstants.VALUE));
            ContactJsonFormUtils.processAbnormalValues(facts, fieldObject);
            // in complex expression of other where more than one other option is defined e.g. surgeries for profile has 2 items
            //To specify other for: gynecology surgery and the normal other fields with edit text
        } else if (fieldObject.has(ConstantsUtils.OTHER_FOR) && !TextUtils.isEmpty(fieldObject.getString(ConstantsUtils.OTHER_FOR))) {
            JSONObject otherFor = fieldObject.getJSONObject(ConstantsUtils.OTHER_FOR);
            String parentKey = otherFor.getString(JsonFormConstants.PARENT_KEY) + ConstantsUtils.SuffixUtils.VALUE;
            String parentLabel = otherFor.getString(JsonFormConstants.LABEL);
            String factValue = facts.get(parentKey);
            String newValue = factValue.replace(parentLabel, fieldObject.getString(JsonFormConstants.VALUE));
            facts.put(parentKey, newValue);
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
        if (fieldObject.has(ConstantsUtils.KeyUtils.SECONDARY_VALUES)) {
            fieldObject.put(ConstantsUtils.KeyUtils.SECONDARY_VALUES, sortSecondaryValues(fieldObject));//sort and reset

            JSONArray secondaryValues = fieldObject.getJSONArray(ConstantsUtils.KeyUtils.SECONDARY_VALUES);

            for (int j = 0; j < secondaryValues.length(); j++) {
                JSONObject jsonObject = secondaryValues.getJSONObject(j);
                ContactJsonFormUtils.processAbnormalValues(facts, jsonObject);
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
                JsonFormConstants.EXPANSION_PANEL.equals(fieldObject.getString(JsonFormConstants.TYPE)) &&
                fieldObject.has(JsonFormConstants.VALUE)) {
            JSONArray expansionPanelValue = fieldObject.getJSONArray(JsonFormConstants.VALUE);

            for (int j = 0; j < expansionPanelValue.length(); j++) {
                JSONObject jsonObject = expansionPanelValue.getJSONObject(j);
                ExpansionPanelItemModel expansionPanelItem = getExpansionPanelItem(
                        jsonObject.getString(JsonFormConstants.KEY), expansionPanelValue);

                if (jsonObject.has(JsonFormConstants.TYPE) && (JsonFormConstants.CHECK_BOX.equals(jsonObject.getString(JsonFormConstants.TYPE))
                        || JsonFormConstants.NATIVE_RADIO_BUTTON.equals(jsonObject.getString(JsonFormConstants.TYPE)) ||
                        JsonFormConstants.EXTENDED_RADIO_BUTTON.equals(jsonObject.getString(JsonFormConstants.TYPE)))) {

                    facts.put(expansionPanelItem.getKey(), expansionPanelItem.getSelectedKeys());
                    facts.put(expansionPanelItem.getKey() + ConstantsUtils.SuffixUtils.VALUE, expansionPanelItem.getSelectedValues());
                } else {
                    processExpansionPanelAbnormalValues(facts, expansionPanelItem);
                    facts.put(expansionPanelItem.getKey(), expansionPanelItem.getSelectedKeys());
                }
            }
        }
    }

    /***
     * Method that replaces abnormal values other
     * @param facts Map containing facts
     * @param expansionPanelItem expansionPanel with values
     */
    private static void processExpansionPanelAbnormalValues(Facts facts, ExpansionPanelItemModel expansionPanelItem) {
        if (expansionPanelItem.getKey().endsWith(ConstantsUtils.SuffixUtils.OTHER)) {
            String parentKey = expansionPanelItem.getKey().replace(ConstantsUtils.SuffixUtils.OTHER, "") + ConstantsUtils.SuffixUtils.VALUE;
            String parentsValue = facts.get(parentKey);
            if (parentsValue != null) {
                int startPos = StringUtils.indexOf(parentsValue.toLowerCase(), ConstantsUtils.OTHER);
                int endPos = StringUtils.indexOf(parentsValue.toLowerCase(), ",", startPos);

                String newValue = parentsValue.replace(StringUtils.substring(parentsValue,
                        startPos, endPos != -1 ? endPos : parentsValue.length()), expansionPanelItem.getSelectedValues());
                facts.put(parentKey, newValue);
            }
        }
    }

    public static JSONArray sortSecondaryValues(JSONObject fieldObject) throws JSONException {
        JSONObject otherValue = null;
        JSONArray newJsonArray = new JSONArray();

        JSONArray secondaryValues = fieldObject.getJSONArray(ConstantsUtils.KeyUtils.SECONDARY_VALUES);

        for (int j = 0; j < secondaryValues.length(); j++) {
            JSONObject jsonObject = secondaryValues.getJSONObject(j);

            if (jsonObject.has(JsonFormConstants.KEY) && jsonObject.getString(JsonFormConstants.KEY)
                    .endsWith(ConstantsUtils.SuffixUtils.OTHER)) {
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

    private static void processAbnormalValues(Facts facts, JSONObject jsonObject) throws Exception {

        //Expansion panel widgets have "values" attribute with no "value" do not process them
        //We will handle the processing somewhere else.
        if (jsonObject.has(JsonFormConstants.VALUES) && !jsonObject.has(JsonFormConstants.VALUE)) {
            return;
        }

        String fieldKey = getObjectKey(jsonObject);
        Object fieldValue = getObjectValue(jsonObject);
        String fieldKeySecondary = fieldKey.contains(ConstantsUtils.SuffixUtils.OTHER) ?
                fieldKey.substring(0, fieldKey.indexOf(ConstantsUtils.SuffixUtils.OTHER)) + ConstantsUtils.SuffixUtils.VALUE : "";
        String fieldKeyOtherValue = fieldKey + ConstantsUtils.SuffixUtils.VALUE;

        if (fieldKey.endsWith(ConstantsUtils.SuffixUtils.OTHER) && !fieldKeySecondary.isEmpty() &&
                facts.get(fieldKeySecondary) != null && facts.get(fieldKeyOtherValue) != null) {

            List<String> tempList =
                    new ArrayList<>(Arrays.asList(facts.get(fieldKeySecondary).toString().split("\\s*,\\s*")));
            tempList.remove(tempList.size() - 1);
            tempList.add(StringUtils.capitalize(facts.get(fieldKeyOtherValue).toString()));
            facts.put(fieldKeySecondary, ContactJsonFormUtils.getListValuesAsString(tempList));

        } else {
            facts.put(fieldKey, fieldValue);
        }

    }

    public static String getSecondaryKey(JSONObject jsonObject) throws JSONException {

        return getObjectKey(jsonObject) + ConstantsUtils.SuffixUtils.VALUE;

    }

    /**
     * @return comma separated string of list values
     */
    public static String getListValuesAsString(List<String> list) {
        return list != null ? list.toString().substring(1, list.toString().length() - 1) : "";
    }

    public static String keyToValueConverter(String keys) {
        if (keys != null) {
            String cleanKey = WordUtils.capitalizeFully(cleanValue(keys), ',');
            if (!TextUtils.isEmpty(keys)) {
                return cleanKey.replaceAll("_", " ");
            } else {
                return cleanKey;
            }
        } else {
            return "";
        }
    }

    public static String cleanValue(String raw) {
        if (raw.length() > 0 && raw.charAt(0) == '[') {
            return raw.substring(1, raw.length() - 1);
        } else {
            return raw;
        }
    }

    /**
     * Filters checkbox values based on specified list
     *
     * @param mainJsonObject Main json object with all fields
     * @throws JSONException Capture Json Form errors
     */
    public static void processCheckboxFilteredItems(JSONObject mainJsonObject) throws JSONException {

        if (!mainJsonObject.has(ConstantsUtils.FILTERED_ITEMS) || mainJsonObject.getJSONArray(ConstantsUtils.FILTERED_ITEMS).length() < 1) {
            return;
        }

        JSONArray filteredItems = mainJsonObject.getJSONArray(ConstantsUtils.FILTERED_ITEMS);
        for (int index = 0; index < filteredItems.length(); index++) {
            String step = filteredItems.getString(index).split("_")[0];
            String key = removeKeyPrefix(filteredItems.getString(index), step);
            JSONObject checkBoxField = FormUtils.getFieldJSONObject(FormUtils.fields(mainJsonObject, step), key);
            if (!mainJsonObject.has(ConstantsUtils.GLOBAL) || checkBoxField == null || !checkBoxField.getString(JsonFormConstants.TYPE).equals(JsonFormConstants.CHECK_BOX)) {
                return;
            }
            if (!checkBoxField.optBoolean(ConstantsUtils.IS_FILTERED, false)) {
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

                if (checkBoxField.has(ConstantsUtils.FILTER_OPTIONS_SOURCE)) {
                    if (getFilteredItemsWithSource(mainJsonObject, checkBoxField, newOptionsList, optionsMap))
                        return;
                } else {
                    if (getFilteredItemsWithoutFilteredSource(mainJsonObject, checkBoxField, newOptionsList, optionsMap))
                        return;
                }
                checkBoxField.put(JsonFormConstants.OPTIONS_FIELD_NAME, new JSONArray(newOptionsList));
                checkBoxField.put(ConstantsUtils.IS_FILTERED, true);
            }
        }
    }

    private static boolean getFilteredItemsWithSource(JSONObject mainJsonObject, JSONObject checkBoxField, ArrayList<JSONObject> newOptionsList, Map<String, JSONObject> optionsMap) throws JSONException {
        String filterOptionsSource = checkBoxField.getString(ConstantsUtils.FILTER_OPTIONS_SOURCE);
        if (!filterOptionsSource.startsWith("global_")) {
            return true;
        }
        String globalKey = removeKeyPrefix(filterOptionsSource, ConstantsUtils.GLOBAL);
        String itemsToFilter = mainJsonObject.getJSONObject(ConstantsUtils.GLOBAL).getString(globalKey);

        if (TextUtils.isEmpty(itemsToFilter)) {
            return true;
        }
        //Remove square braces and split the filterOptions to array of strings
        String[] filteredKeys = itemsToFilter.substring(1, itemsToFilter.length() - 1).split(", ");

        for (String filteredKey : filteredKeys) {
            if (!TextUtils.equals("none", filteredKey)) {
                newOptionsList.add(optionsMap.get(filteredKey));
            }
        }
        return false;
    }

    private static boolean getFilteredItemsWithoutFilteredSource(JSONObject mainJsonObject, JSONObject checkBoxField, ArrayList<JSONObject> newOptionsList, Map<String, JSONObject> optionsMap) throws JSONException {
        if (checkBoxField.has(ConstantsUtils.FILTER_OPTIONS)) {
            JSONArray filterOptions = checkBoxField.getJSONArray(ConstantsUtils.FILTER_OPTIONS);
            if (filterOptions.length() > 0) {
                for (int count = 0; count < filterOptions.length(); count++) {
                    JSONObject filterOption = filterOptions.getJSONObject(count);
                    if (!filterOption.has(JsonFormConstants.KEY) && !filterOption.has(JsonFormConstants.VALUE)) {
                        Timber.e("JsonObject for filter options must contain a key value pair with an optional options attribute");
                        return true;
                    }
                    String keyGlobal = removeKeyPrefix(getObjectKey(filterOption), ConstantsUtils.GLOBAL);
                    String itemValue = getObjectValue(filterOption);
                    String globalValue = mainJsonObject.getJSONObject(ConstantsUtils.GLOBAL).getString(keyGlobal);

                    if (compareItemAndValueGlobal(itemValue, globalValue)) {
                        JSONArray optionsToFilter = filterOption.optJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME);
                        if (optionsToFilter == null) {
                            String itemKey = removeKeyPrefix(keyGlobal, ConstantsUtils.PREVIOUS);
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
        return false;
    }

    public static String removeKeyPrefix(String widgetKey, String prefix) {
        return widgetKey.replace(prefix + "_", "");
    }

    public static String getObjectKey(JSONObject jsonObject) throws JSONException {
        return jsonObject.has(JsonFormConstants.KEY) ? jsonObject.getString(JsonFormConstants.KEY) : null;
    }

    public static String getObjectValue(JSONObject jsonObject) throws JSONException {
        return jsonObject.has(JsonFormConstants.VALUE) ? jsonObject.getString(JsonFormConstants.VALUE) : null;
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
}
