package org.smartregister.anc.widget;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.rey.material.util.ViewUtil;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.FormWidgetFactory;
import com.vijay.jsonwizard.interfaces.JsonApi;
import com.vijay.jsonwizard.utils.FormUtils;
import com.vijay.jsonwizard.views.CustomTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.anc.R;
import org.smartregister.anc.adapter.ExpansionWidgetAdapter;
import org.smartregister.anc.event.RefreshExpansionPanelEvent;
import org.smartregister.anc.util.Constants;
import org.smartregister.anc.util.ContactJsonFormUtils;
import org.smartregister.anc.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class ExpansionWidgetFactory implements FormWidgetFactory {
    private RecordButtonClickListener recordButtonClickListener = new RecordButtonClickListener();
    private UndoButtonClickListener undoButtonClickListener = new UndoButtonClickListener();
    private ContactJsonFormUtils formUtils = new ContactJsonFormUtils();
    private Utils utils = new Utils();
    private JsonApi jsonApi;
    private String TAG = this.getClass().getSimpleName();

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JsonFormFragment jsonFormFragment,
                                       JSONObject jsonObject,
                                       CommonListener commonListener, boolean popup) throws Exception {
        return attachJson(stepName, context, jsonFormFragment, jsonObject, commonListener, popup);
    }

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JsonFormFragment jsonFormFragment,
                                       JSONObject jsonObject,
                                       CommonListener commonListener) throws Exception {
        return attachJson(stepName, context, jsonFormFragment, jsonObject, commonListener, false);
    }

    public List<View> attachJson(String stepName, Context context, JsonFormFragment jsonFormFragment, JSONObject jsonObject,
                                 CommonListener commonListener, boolean popup) throws JSONException {
        JSONArray canvasIds = new JSONArray();
        List<View> views = new ArrayList<>(1);

        String openMrsEntityParent = jsonObject.optString(JsonFormConstants.OPENMRS_ENTITY_PARENT);
        String openMrsEntity = jsonObject.optString(JsonFormConstants.OPENMRS_ENTITY);
        String openMrsEntityId = jsonObject.optString(JsonFormConstants.OPENMRS_ENTITY_ID);
        String relevance = jsonObject.optString(JsonFormConstants.RELEVANCE);
        String constraints = jsonObject.optString(JsonFormConstants.CONSTRAINTS);
        String calculation = jsonObject.optString(JsonFormConstants.CALCULATION);
        LinearLayout.LayoutParams layoutParams = FormUtils
                .getLinearLayoutParams(FormUtils.MATCH_PARENT, FormUtils.MATCH_PARENT, 1, 2, 1,
                        2);
        LinearLayout rootLayout = getRootLayout(context);
        rootLayout.setLayoutParams(layoutParams);
        rootLayout.setId(ViewUtil.generateViewId());
        canvasIds.put(rootLayout.getId());
        rootLayout.setTag(com.vijay.jsonwizard.R.id.canvas_ids, canvasIds.toString());
        rootLayout.setTag(com.vijay.jsonwizard.R.id.key, jsonObject.getString(JsonFormConstants.KEY));
        rootLayout.setTag(com.vijay.jsonwizard.R.id.openmrs_entity_parent, openMrsEntityParent);
        rootLayout.setTag(com.vijay.jsonwizard.R.id.openmrs_entity, openMrsEntity);
        rootLayout.setTag(com.vijay.jsonwizard.R.id.openmrs_entity_id, openMrsEntityId);
        rootLayout.setTag(com.vijay.jsonwizard.R.id.extraPopup, popup);
        rootLayout.setTag(com.vijay.jsonwizard.R.id.type, jsonObject.getString(JsonFormConstants.TYPE));
        rootLayout.setTag(com.vijay.jsonwizard.R.id.address, stepName + ":" + jsonObject.getString(JsonFormConstants.KEY));
        rootLayout.setTag(com.vijay.jsonwizard.R.id.canvas_ids, canvasIds.toString());

        if (!TextUtils.isEmpty(relevance) && context instanceof JsonApi) {
            rootLayout.setTag(com.vijay.jsonwizard.R.id.relevance, relevance);
            ((JsonApi) context).addSkipLogicView(rootLayout);
        }

        if (!TextUtils.isEmpty(constraints) && context instanceof JsonApi) {
            rootLayout.setTag(com.vijay.jsonwizard.R.id.constraints, constraints);
            ((JsonApi) context).addConstrainedView(rootLayout);
        }

        if (!TextUtils.isEmpty(calculation) && context instanceof JsonApi) {
            rootLayout.setTag(com.vijay.jsonwizard.R.id.calculation, calculation);
            ((JsonApi) context).addCalculationLogicView(rootLayout);
        }

        attachLayout(stepName, context, jsonFormFragment, jsonObject, commonListener, rootLayout);

        views.add(rootLayout);
        return views;
    }

    private void attachLayout(String stepName, final Context context, JsonFormFragment jsonFormFragment,
                              JSONObject jsonObject,
                              CommonListener commonListener, LinearLayout rootLayout) throws JSONException {
        String accordionText = jsonObject.optString(JsonFormConstants.TEXT, "");
        RelativeLayout expansionHeader = rootLayout.findViewById(R.id.expansionHeader);
        RelativeLayout expansion_header_layout = expansionHeader.findViewById(R.id.expansion_header_layout);
        addRecordViewTags(expansion_header_layout, jsonObject, stepName, commonListener, jsonFormFragment, context);
        expansion_header_layout.setOnClickListener(recordButtonClickListener);

        ImageView statusImage = expansion_header_layout.findViewById(R.id.statusImageView);
        addRecordViewTags(statusImage, jsonObject, stepName, commonListener, jsonFormFragment, context);
        statusImage.setOnClickListener(recordButtonClickListener);

        ImageView infoIcon = expansionHeader.findViewById(R.id.accordion_info_icon);

        CustomTextView headerText = expansion_header_layout.findViewById(R.id.topBarTextView);
        headerText.setText(accordionText);
        addRecordViewTags(headerText, jsonObject, stepName, commonListener, jsonFormFragment, context);
        headerText.setOnClickListener(recordButtonClickListener);

        displayInfoIcon(jsonObject, commonListener, infoIcon);
        changeStatusIcon(statusImage, jsonObject, context);
        attachContent(rootLayout, context, jsonObject);
        addBottomSection(stepName, context, jsonFormFragment, jsonObject, commonListener, rootLayout);
    }

    private void changeStatusIcon(ImageView imageView, JSONObject optionItem, Context context) throws JSONException {
        JSONArray value = getExpansionPanelValue(optionItem);
        for (int i = 0; i < value.length(); i++) {
            JSONObject item = value.getJSONObject(i);
            if (item.getString(JsonFormConstants.TYPE).equals(Constants.ANC_RADIO_BUTTON) || item
                    .getString(JsonFormConstants.TYPE)
                    .equals(JsonFormConstants.NATIVE_RADIO_BUTTON)) {

                JSONArray jsonArray = item.getJSONArray(JsonFormConstants.VALUES);
                for (int k = 0; k < jsonArray.length(); k++) {
                    String list = jsonArray.getString(k);
                    String[] stringValues = list.split(":");
                    if (getWidgetValueAndChangeIcon(imageView, context, list, stringValues)) break;
                }
            }
        }
    }

    private boolean getWidgetValueAndChangeIcon(ImageView imageView, Context context, String list, String[] stringValues) {
        if (stringValues.length >= 2) {
            String valueDisplay = list.split(":")[1];

            if (valueDisplay.equals(Constants.ANC_RADIO_BUTTON_OPTION_TYPES.DONE_TODAY) || valueDisplay
                    .equals(Constants
                            .ANC_RADIO_BUTTON_OPTION_TEXT.DONE_TODAY) || valueDisplay
                    .equals(Constants.ANC_RADIO_BUTTON_OPTION_TYPES
                            .DONE) || valueDisplay
                    .equals(Constants.ANC_RADIO_BUTTON_OPTION_TEXT.DONE) || valueDisplay.equals
                    (Constants.ANC_RADIO_BUTTON_OPTION_TYPES.DONE_EARLIER) || valueDisplay.equals(Constants
                    .ANC_RADIO_BUTTON_OPTION_TEXT.DONE_EARLIER) || valueDisplay.equals(Constants
                    .ANC_RADIO_BUTTON_OPTION_TYPES.ORDERED) || valueDisplay
                    .equals(Constants.ANC_RADIO_BUTTON_OPTION_TEXT
                            .ORDERED) || valueDisplay
                    .equals(Constants.ANC_RADIO_BUTTON_OPTION_TYPES.NOT_DONE) || valueDisplay.equals
                    (Constants.ANC_RADIO_BUTTON_OPTION_TEXT.NOT_DONE)) {

                formUtils.changeIcon(imageView, valueDisplay, context);
                return true;
            }
        }
        return false;
    }

    /**
     * Return the Expansion Panel value
     *
     * @param optionItem {@link JSONObject}
     * @return value {@link JSONArray}
     * @throws JSONException
     */
    private JSONArray getExpansionPanelValue(JSONObject optionItem) throws JSONException {
        JSONArray value = new JSONArray();
        if (optionItem.has(JsonFormConstants.VALUE)) {
            value = optionItem.getJSONArray(JsonFormConstants.VALUE);
        }
        return value;
    }


    private void attachContent(LinearLayout rootLayout, Context context, JSONObject jsonObject) throws JSONException {
        JSONArray values = new JSONArray();
        LinearLayout contentLayout = rootLayout.findViewById(R.id.contentLayout);
        if (jsonObject.has(JsonFormConstants.VALUE)) {
            values = jsonObject.getJSONArray(JsonFormConstants.VALUE);
            if (checkValuesContent(values)) {
                contentLayout.setVisibility(View.VISIBLE);
            }
        }
        RecyclerView contentView = contentLayout.findViewById(R.id.contentRecyclerView);
        contentView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        ExpansionWidgetAdapter adapter = new ExpansionWidgetAdapter(utils.createExpansionPanelChildren(values));
        contentView.setAdapter(adapter);
    }

    private void displayInfoIcon(JSONObject jsonObject, CommonListener commonListener, ImageView accordionInfoWidget)
            throws JSONException {
        String accordionInfoText = jsonObject.optString(Constants.ACCORDION_INFO_TEXT, null);
        String accordionInfoTitle = jsonObject.optString(Constants.ACCORDION_INFO_TITLE, null);
        String accordionKey = jsonObject.getString(JsonFormConstants.KEY);
        String accordionType = jsonObject.getString(JsonFormConstants.TYPE);
        if (accordionInfoText != null) {
            accordionInfoWidget.setVisibility(View.VISIBLE);
            accordionInfoWidget.setTag(com.vijay.jsonwizard.R.id.key, accordionKey);
            accordionInfoWidget.setTag(com.vijay.jsonwizard.R.id.type, accordionType);
            accordionInfoWidget.setTag(com.vijay.jsonwizard.R.id.label_dialog_info, accordionInfoText);
            accordionInfoWidget.setTag(com.vijay.jsonwizard.R.id.label_dialog_title, accordionInfoTitle);
            accordionInfoWidget.setOnClickListener(commonListener);
        }
    }

    private void addBottomSection(String stepName, Context context, JsonFormFragment jsonFormFragment, JSONObject jsonObject,
                                  CommonListener commonListener, LinearLayout rootLayout) throws JSONException {
        if (jsonObject.has(JsonFormConstants.VALUE)) {
            JSONArray value = jsonObject.optJSONArray(JsonFormConstants.VALUE);
            if (checkValuesContent(value)) {
                RelativeLayout relativeLayout = rootLayout.findViewById(R.id.accordion_bottom_navigation);
                relativeLayout.setVisibility(View.VISIBLE);

                Button recordButton = relativeLayout.findViewById(R.id.ok_button);
                addRecordViewTags(recordButton, jsonObject, stepName, commonListener, jsonFormFragment, context);
                recordButton.setOnClickListener(recordButtonClickListener);
                Button undoButton = relativeLayout.findViewById(R.id.undo_button);
                if (jsonObject.has(JsonFormConstants.VALUE)) {
                    undoButton.setVisibility(View.VISIBLE);
                }

                undoButton.setTag(R.id.key, jsonObject.getString(JsonFormConstants.KEY));
                undoButton.setTag(R.id.specify_context, context);
                undoButton.setTag(R.id.specify_step_name, stepName);
                undoButton.setTag(R.id.linearLayout, rootLayout);
                undoButton.setOnClickListener(undoButtonClickListener);
            }
        }
    }


    private void addRecordViewTags(View recordView, JSONObject jsonObject, String stepName, CommonListener commonListener,
                                   JsonFormFragment jsonFormFragment, Context context) throws JSONException {
        recordView.setTag(R.id.specify_content, jsonObject.optString(JsonFormConstants.CONTENT_FORM, ""));
        recordView.setTag(R.id.specify_context, context);
        recordView.setTag(R.id.specify_content_form, jsonObject.optString(JsonFormConstants.CONTENT_FORM_LOCATION, ""));
        recordView.setTag(R.id.specify_step_name, stepName);
        recordView.setTag(R.id.specify_listener, commonListener);
        recordView.setTag(R.id.specify_fragment, jsonFormFragment);
        recordView.setTag(R.id.header, jsonObject.optString(JsonFormConstants.TEXT, ""));
        if (jsonObject.getString(JsonFormConstants.TYPE) != null) {
            recordView.setTag(R.id.secondaryValues,
                    formUtils.getSecondaryValues(jsonObject, jsonObject.getString(JsonFormConstants.TYPE)));
        }
        recordView.setTag(R.id.key, jsonObject.getString(JsonFormConstants.KEY));
        recordView.setTag(R.id.type, jsonObject.getString(JsonFormConstants.TYPE));
        if (jsonObject.has(Constants.JSON_FORM_CONSTANTS.CONTACT_CONTAINER)) {
            String container = jsonObject.optString(Constants.JSON_FORM_CONSTANTS.CONTACT_CONTAINER, null);
            recordView.setTag(R.id.contact_container, container);
        }

    }

    protected LinearLayout getRootLayout(Context context) {
        return (LinearLayout) LayoutInflater.from(context).inflate(R.layout.native_expansion_panel, null);
    }

    private class RecordButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            LinearLayout linearLayout;
            if (view instanceof RelativeLayout) {
                linearLayout = (LinearLayout) view.getParent().getParent();
            } else if (view instanceof ImageView || view instanceof CustomTextView) { // This caters for the different views that can be
                // clicked to show the popup
                linearLayout = (LinearLayout) view.getParent().getParent().getParent();
            } else {
                linearLayout = (LinearLayout) view.getParent().getParent().getParent();
            }
            view.setTag(R.id.main_layout, linearLayout);
            formUtils.showGenericDialog(view);
        }
    }

    private boolean checkValuesContent(JSONArray value) throws JSONException {
        boolean showHiddenViews = true;
        if (value.length() == 1) {
            JSONObject jsonObject = value.getJSONObject(0);
            if (jsonObject.has(JsonFormConstants.TYPE) && Constants.ANC_RADIO_BUTTON
                    .equals(jsonObject.getString(JsonFormConstants.TYPE))) {
                JSONArray values = jsonObject.getJSONArray(JsonFormConstants.VALUES);
                if (values.length() == 1) {
                    String object = values.getString(0);
                    if (object.contains(Constants.ANC_RADIO_BUTTON_OPTION_TEXT.DONE_EARLIER) || object
                            .contains(Constants.ANC_RADIO_BUTTON_OPTION_TEXT.DONE_TODAY)) {
                        showHiddenViews = false;
                    }
                }
            }
        }
        return showHiddenViews;
    }

    private class UndoButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            String key = (String) view.getTag(R.id.key);
            Context context = (Context) view.getTag(R.id.specify_context);
            String stepName = (String) view.getTag(R.id.specify_step_name);
            jsonApi = (JsonApi) context;
            JSONObject mainJson = jsonApi.getmJSONObject();
            if (mainJson != null) {
                getExpansionPanel(mainJson, stepName, key, context, view);
            }
        }

        private void getExpansionPanel(JSONObject mainJson, String stepName, String parentKey, Context context, View view) {
            if (mainJson != null) {
                JSONArray itemValues = new JSONArray();
                JSONArray fields = formUtils.getFormFields(stepName, context);
                try {
                    if (fields.length() > 0) {
                        for (int i = 0; i < fields.length(); i++) {
                            JSONObject item = fields.getJSONObject(i);
                            if (item != null && item.getString(JsonFormConstants.KEY).equals(parentKey) && item
                                    .has(JsonFormConstants
                                            .VALUE)) {
                                JSONArray newValues = removeLastAddedExpansionValue(item);
                                item.put(JsonFormConstants.VALUE, newValues);
                                itemValues = newValues;
                            }
                        }
                    }
                    jsonApi.setmJSONObject(mainJson);

                    org.smartregister.anc.util.Utils.postEvent(
                            new RefreshExpansionPanelEvent(itemValues, (LinearLayout) view.getTag(R.id.linearLayout)));
                } catch (JSONException e) {
                    Log.i(TAG, Log.getStackTraceString(e));
                }
            }
        }

        private JSONArray removeLastAddedExpansionValue(JSONObject item) throws JSONException {
            JSONArray jsonArray = item.getJSONArray(JsonFormConstants.VALUE);
            JSONArray newValues = new JSONArray();
            for (int i = 0; i < jsonArray.length(); i++) {
                if (i != 0) {
                    JSONObject valueItem = jsonArray.getJSONObject(i);
                    newValues.put(valueItem);
                }
            }
            return newValues;
        }
    }
}
