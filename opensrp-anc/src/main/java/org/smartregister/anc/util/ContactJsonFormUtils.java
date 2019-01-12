package org.smartregister.anc.util;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.utils.FormUtils;
import com.vijay.jsonwizard.views.CustomTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.anc.R;
import org.smartregister.anc.application.AncApplication;
import org.smartregister.anc.contract.AncGenericDialogInterface;
import org.smartregister.anc.domain.Contact;
import org.smartregister.anc.model.PartialContact;
import org.smartregister.anc.view.AncGenericDialogPopup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContactJsonFormUtils extends FormUtils {
    private AncGenericDialogInterface genericDialogInterface;

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
        CustomTextView reasonsTextView = (CustomTextView) view.getTag(com.vijay.jsonwizard.R.id.popup_reasons_textview);
        String toolbarHeader = "";
        String container = "";
        LinearLayout rootLayout = (LinearLayout) view.getTag(R.id.main_layout);
        if (type != null && type.equals(Constants.EXPANSION_PANEL)) {
            toolbarHeader = (String) view.getTag(R.id.header);
            container = (String) view.getTag(R.id.contact_container);
        }
        String childKey;

        if (specifyContent != null) {
            AncGenericDialogPopup genericPopupDialog = new AncGenericDialogPopup();
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
            if (type != null && (type.equals(JsonFormConstants.CHECK_BOX) || type.equals(JsonFormConstants.NATIVE_RADIO_BUTTON))) {
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

    public Map<String, String> createAssignedValue(AncGenericDialogInterface genericDialogInterface, String itemKey, String optionKey,
                                                   String keyValue, String itemType, String itemText) {
        this.genericDialogInterface = genericDialogInterface;
        return addAssignedValue(itemKey, optionKey, keyValue, itemType, itemText);
    }

    @Override
    public Map<String, String> addAssignedValue(String itemKey, String optionKey, String keyValue, String itemType, String itemText) {
        Map<String, String> value = new HashMap<>();
        if (genericDialogInterface != null && !TextUtils.isEmpty(genericDialogInterface.getWidgetType()) && genericDialogInterface
                .getWidgetType().equals(Constants.EXPANSION_PANEL)) {
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

    public void changeIcon(ImageView imageView, String type, Context context) {
        if (!TextUtils.isEmpty(type)) {
            switch (type) {
                case Constants.ANC_RADIO_BUTTON_OPTION_TYPES.DONE_TODAY:
                case Constants.ANC_RADIO_BUTTON_OPTION_TEXT.DONE_TODAY:
                case "done":
                case "Done":
                    imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.done_today));
                    break;
                case Constants.ANC_RADIO_BUTTON_OPTION_TYPES.DONE_EARLIER:
                case Constants.ANC_RADIO_BUTTON_OPTION_TEXT.DONE_EARLIER:
                    imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.done_today));
                    break;
                case Constants.ANC_RADIO_BUTTON_OPTION_TYPES.ORDERED:
                case Constants.ANC_RADIO_BUTTON_OPTION_TEXT.ORDERED:
                    imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ordered));
                    break;
                case Constants.ANC_RADIO_BUTTON_OPTION_TYPES.NOT_DONE:
                case Constants.ANC_RADIO_BUTTON_OPTION_TEXT.NOT_DONE:
                    imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.not_done));
                    break;
                default:
                    imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.grey_circle));
                    break;
            }
        }
    }


    public static void persistPartial(Contact contact, String baseEntityId) {

        PartialContact partialContact = new PartialContact();
        partialContact.setBaseEntityId(baseEntityId);
        partialContact.setContactNo(contact.getContactNumber());
        partialContact.setFinalized(false);

        if (contact != null) {
            partialContact.setType(contact.getFormName());
        }
        partialContact.setFormJsonDraft(contact.getJsonForm());

        AncApplication.getInstance().getPartialContactRepository().savePartialContact(partialContact);
    }


    public static JSONObject getFormJsonCore(PartialContact partialContactRequest, JSONObject form) throws JSONException {

        JSONObject object;

        //partial contact exists?

        PartialContact partialContact = AncApplication.getInstance().getPartialContactRepository().getPartialContact(partialContactRequest);
        String formJsonString = partialContact != null && (partialContact.getFormJson() != null || partialContact.getFormJsonDraft() !=
                null) ? (partialContact.getFormJsonDraft() != null ? partialContact.getFormJsonDraft() : partialContact.getFormJson()) :
                form.toString();
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


    public static void processSpecialWidgets(JSONObject widget) throws JSONException {
        String widgetType = widget.getString(JsonFormConstants.TYPE);
        List<String> keyList = new ArrayList<>();
        List<String> valueList = new ArrayList<>();


        if (widgetType.equals(JsonFormConstants.CHECK_BOX)) {

            JSONArray jsonArray = widget.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonObject = jsonArray.getJSONObject(i);

                if (jsonObject.has(JsonFormConstants.VALUE) && !TextUtils.isEmpty(jsonObject.getString(JsonFormConstants.VALUE)) &&
                        jsonObject.getString(JsonFormConstants.VALUE).equals(Constants.BOOLEAN.TRUE)) {

                    keyList.add(jsonObject.getString(JsonFormConstants.KEY));


                    if (jsonObject.has(JsonFormConstants.SECONDARY_VALUE) && !TextUtils.isEmpty(jsonObject.getString(JsonFormConstants
                            .SECONDARY_VALUE))) {


                        valueList = getRealSecondaryValue(jsonObject);

                    } else {

                        valueList.add(jsonObject.getString(JsonFormConstants.TEXT));
                    }

                }

            }

            if (keyList.size() > 0) {

                String valueListString = valueList.toString();

                widget.put(JsonFormConstants.VALUE, keyList.toString());
                widget.put(widget.getString(JsonFormConstants.KEY) + Constants.SUFFIX.VALUE, valueListString.substring(1, valueListString
                        .length() - 1));
            }
        } else if (widgetType.equals(JsonFormConstants.NATIVE_RADIO_BUTTON) || widgetType.equals(JsonFormConstants.RADIO_BUTTON) || widgetType.equals(Constants.ANC_RADIO_BUTTON)) {
            //Value already good for radio buttons so no keylist

            JSONArray jsonArray = widget.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonObject = jsonArray.getJSONObject(i);

                if (widget.has(JsonFormConstants.VALUE) && !TextUtils.isEmpty(widget.getString(JsonFormConstants.VALUE)) && jsonObject
                        .getString(JsonFormConstants.KEY).equals(widget.getString(JsonFormConstants.VALUE))) {

                    if (jsonObject.has(JsonFormConstants.SECONDARY_VALUE) && !TextUtils.isEmpty(jsonObject.getString(JsonFormConstants
                            .SECONDARY_VALUE))) {


                        valueList = getRealSecondaryValue(jsonObject);

                    } else {

                        valueList.add(jsonObject.getString(JsonFormConstants.TEXT));
                    }

                }

            }

            if (valueList.size() > 0) {
                String valueListString = valueList.toString();
                widget.put(widget.getString(JsonFormConstants.KEY) + Constants.SUFFIX.VALUE, valueListString.substring(1, valueListString
                        .length() - 1));
            }
        }
    }


    public static List<String> getRealSecondaryValue(JSONObject jsonObject) throws JSONException {

        List<String> valueList = new ArrayList<>();

        JSONArray jsonArray2 = jsonObject.getJSONArray(JsonFormConstants.SECONDARY_VALUE);


        String resultString = "";

        for (int j = 0; j < jsonArray2.length(); j++) {

            JSONObject secValue = jsonArray2.getJSONObject(j);

            JSONArray values = secValue.getJSONArray(JsonFormConstants.VALUES);

            boolean containsOther = j == 0 && jsonArray2.length() == 2;

            int valueLength = containsOther ? values.length() - 1 : values.length();

            for (int k = 0; k < valueLength; k++) {
                String valuesString = values.getString(k);
                valuesString = valuesString.contains(":") ? valuesString.substring(valuesString.indexOf(":") + 1) : valuesString;
                valuesString = valuesString.contains(":") ? valuesString.substring(0, valuesString.indexOf(":")) : valuesString;

                resultString += valuesString;
                if (k != valueLength - 1 || containsOther) {
                    resultString += ", ";
                }

            }

        }

        valueList.add(resultString);

        return valueList;
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
    public void updateExpansionPanelRecyclerView(List<String> values, ImageView statusImageView, Context context) throws JSONException {
        JSONArray list = new JSONArray(values);
        for (int k = 0; k < list.length(); k++) {
            String[] stringValues = list.getString(k).split(":");
            if (stringValues.length >= 2) {
                String valueDisplay = list.getString(k).split(":")[1];
                if (valueDisplay.equals(Constants.ANC_RADIO_BUTTON_OPTION_TYPES.DONE_TODAY) || valueDisplay.equals(Constants
                        .ANC_RADIO_BUTTON_OPTION_TEXT.DONE_TODAY) || valueDisplay.equals(Constants.ANC_RADIO_BUTTON_OPTION_TYPES.DONE) ||
                        valueDisplay.equals(Constants.ANC_RADIO_BUTTON_OPTION_TEXT.DONE) ||
                        valueDisplay.equals(Constants.ANC_RADIO_BUTTON_OPTION_TYPES.DONE_EARLIER) || valueDisplay.equals(Constants
                        .ANC_RADIO_BUTTON_OPTION_TEXT.DONE_EARLIER) || valueDisplay.equals(Constants.ANC_RADIO_BUTTON_OPTION_TYPES
                        .ORDERED) || valueDisplay.equals(Constants.ANC_RADIO_BUTTON_OPTION_TEXT.ORDERED) || valueDisplay.equals(Constants
                        .ANC_RADIO_BUTTON_OPTION_TYPES.NOT_DONE) || valueDisplay.equals(Constants.ANC_RADIO_BUTTON_OPTION_TEXT.NOT_DONE)) {

                    changeIcon(statusImageView, valueDisplay, context);
                    break;
                }
            }
        }
    }

    public static JSONObject createSecondaryFormObject(JSONObject parentObject, JSONObject jsonSubForm, String encounterType) throws JSONException {

        Map<String, String> vMap = new HashMap<>();

        JSONObject resultJsonObject = new JSONObject();

        JSONObject stepJsonObject = new JSONObject();

        JSONArray fieldsJsonArray = jsonSubForm.getJSONArray(JsonFormConstants.CONTENT_FORM);

        if (parentObject.has(JsonFormConstants.VALUE) && !TextUtils.isEmpty(parentObject.getString(JsonFormConstants.VALUE))) {
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

        JSONObject valueObject = jsonObject;
        String key = valueObject.getString(JsonFormConstants.KEY);
        JSONArray values = valueObject.getJSONArray(JsonFormConstants.VALUES);
        for (int k = 0; k < values.length(); k++) {
            String valuesString = values.getString(k);

            vMap.put(key, valuesString.contains(":") ? valuesString.substring(0, valuesString.indexOf(":")) : valuesString);
        }
    }

}
