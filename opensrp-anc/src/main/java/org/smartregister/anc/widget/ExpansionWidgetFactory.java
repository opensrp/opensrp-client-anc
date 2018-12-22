package org.smartregister.anc.widget;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.github.florent37.expansionpanel.ExpansionHeader;
import com.github.florent37.expansionpanel.ExpansionLayout;
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
                                       JSONObject jsonObject, CommonListener commonListener, boolean popup) throws Exception {
        return attachJson(stepName, context, jsonFormFragment, jsonObject, commonListener, popup);
    }

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JsonFormFragment jsonFormFragment,
                                       JSONObject jsonObject, CommonListener commonListener) throws Exception {
        return attachJson(stepName, context, jsonFormFragment, jsonObject, commonListener, false);
    }

    public List<View> attachJson(String stepName, Context context, JsonFormFragment jsonFormFragment, JSONObject
            jsonObject, CommonListener commonListener, boolean popup) throws JSONException {
        JSONArray canvasIds = new JSONArray();
        List<View> views = new ArrayList<>(1);

        String openMrsEntityParent = jsonObject.optString(JsonFormConstants.OPENMRS_ENTITY_PARENT);
        String openMrsEntity = jsonObject.optString(JsonFormConstants.OPENMRS_ENTITY);
        String openMrsEntityId = jsonObject.optString(JsonFormConstants.OPENMRS_ENTITY_ID);
        String relevance = jsonObject.optString(JsonFormConstants.RELEVANCE);
        LinearLayout.LayoutParams layoutParams =
                FormUtils.getLinearLayoutParams(FormUtils.MATCH_PARENT, FormUtils.MATCH_PARENT, 1, 2, 1, 2);
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

        if (relevance != null && context instanceof JsonApi) {
            rootLayout.setTag(com.vijay.jsonwizard.R.id.relevance, relevance);
            ((JsonApi) context).addSkipLogicView(rootLayout);
        }

        attachLayout(stepName, context, jsonFormFragment, jsonObject, commonListener, rootLayout);

        views.add(rootLayout);
        return views;
    }

    private void attachLayout(String stepName, final Context context, JsonFormFragment jsonFormFragment,
                              JSONObject jsonObject, CommonListener commonListener, LinearLayout rootLayout)
            throws JSONException {
        String accordionText = jsonObject.optString(JsonFormConstants.TEXT, "");
        ExpansionHeader expansionHeader = rootLayout.findViewById(R.id.expansionHeader);
        ImageView statusImage = expansionHeader.findViewById(R.id.statusImageView);
        ImageView infoIcon = expansionHeader.findViewById(R.id.accordion_info_icon);
        CustomTextView headerText = expansionHeader.findViewById(R.id.topBarTextView);

        headerText.setText(accordionText);
        displayInfoIcon(jsonObject, commonListener, infoIcon);
        changeStatusIcon(statusImage, jsonObject, context);
        attachContent(rootLayout, context, jsonObject);
        addBottomSection(stepName, context, jsonFormFragment, jsonObject, commonListener, rootLayout);
    }

    private void changeStatusIcon(ImageView imageView, JSONObject optionItem, Context context)
            throws JSONException {
        JSONArray value = new JSONArray();
        if (optionItem.has(JsonFormConstants.VALUE)) {
            value = optionItem.getJSONArray(JsonFormConstants.VALUE);
        }

        for (int i = 0; i < value.length(); i++) {
            JSONObject item = value.getJSONObject(i);
            if (item.getString(JsonFormConstants.TYPE).equals(Constants.ANC_RADIO_BUTTON)) {
                JSONArray jsonArray = item.getJSONArray(JsonFormConstants.VALUES);
                for (int k = 0; k < jsonArray.length(); k++) {
                    String valueDisplay = jsonArray.getString(k);
                    formUtils.changeIcon(imageView, valueDisplay, context);
                }

            }
        }
    }


    private void attachContent(LinearLayout rootLayout, Context context, JSONObject jsonObject) throws JSONException {
        JSONArray values = new JSONArray();
        if (jsonObject.has(JsonFormConstants.VALUE)) {
            values = jsonObject.getJSONArray(JsonFormConstants.VALUE);
        }
        ExpansionLayout expansionLayout = rootLayout.findViewById(R.id.expansionLayout);
        LinearLayout contentLayout = expansionLayout.findViewById(R.id.contentLayout);
        RecyclerView contentView = contentLayout.findViewById(R.id.contentRecyclerView);
        contentView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        ExpansionWidgetAdapter adapter = new ExpansionWidgetAdapter(utils.createExpansionPanelChildren(values));
        contentView.setAdapter(adapter);
    }

    private void displayInfoIcon(JSONObject jsonObject, CommonListener commonListener, ImageView accordionInfoWidget) throws JSONException {
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

    private void addBottomSection(String stepName, Context context, JsonFormFragment jsonFormFragment, JSONObject
            jsonObject, CommonListener commonListener, LinearLayout rootLayout) throws JSONException {
        Boolean displayBottomSection = jsonObject.optBoolean(Constants.DISPLAY_BOTTOM_SECTION, false);
        if (displayBottomSection) {
            RelativeLayout relativeLayout = rootLayout.findViewById(R.id.accordion_bottom_navigation);
            relativeLayout.setVisibility(View.VISIBLE);

            Button recordButton = relativeLayout.findViewById(R.id.ok_button);
            recordButton = addOkButtonTags(recordButton, jsonObject, stepName, commonListener, jsonFormFragment, context);
            recordButton.setOnClickListener(recordButtonClickListener);
            Button undoButton = relativeLayout.findViewById(R.id.undo_button);
            undoButton.setTag(R.id.key, jsonObject.getString(JsonFormConstants.KEY));
            undoButton.setTag(R.id.specify_context, context);
            undoButton.setTag(R.id.specify_step_name, stepName);
            undoButton.setOnClickListener(undoButtonClickListener);
        }
    }

    private Button addOkButtonTags(Button okButton, JSONObject jsonObject, String stepName, CommonListener commonListener,
                                   JsonFormFragment jsonFormFragment, Context context) throws JSONException {
        okButton.setTag(R.id.specify_content, jsonObject.optString(JsonFormConstants.CONTENT_FORM, ""));
        okButton.setTag(R.id.specify_context, context);
        okButton.setTag(R.id.specify_content_form, jsonObject.optString(JsonFormConstants.CONTENT_FORM_LOCATION, ""));
        okButton.setTag(R.id.specify_step_name, stepName);
        okButton.setTag(R.id.specify_listener, commonListener);
        okButton.setTag(R.id.specify_fragment, jsonFormFragment);
        okButton.setTag(R.id.header, jsonObject.optString(JsonFormConstants.TEXT, ""));
        okButton.setTag(R.id.secondaryValues,
                formUtils.getSecondaryValues(jsonObject, jsonObject.getString(JsonFormConstants.TYPE)));
        okButton.setTag(R.id.key, jsonObject.getString(JsonFormConstants.KEY));
        okButton.setTag(R.id.type, jsonObject.getString(JsonFormConstants.TYPE));

        return okButton;
    }

    private LinearLayout getRootLayout(Context context) {
        return (LinearLayout) LayoutInflater.from(context).inflate(
                R.layout.native_expansion_panel, null);
    }

    private class RecordButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            LinearLayout linearLayout = (LinearLayout) view.getParent().getParent().getParent().getParent();
            view.setTag(R.id.main_layout, linearLayout);
            formUtils.showGenericDialog(view);
        }
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
                getExpansionPanel(mainJson, stepName, key);
            }
        }

        private void getExpansionPanel(JSONObject mainJson, String stepName, String parentKey) {
            if (mainJson != null) {
                JSONObject parentJson = jsonApi.getStep(stepName);
                JSONArray fields = new JSONArray();
                try {
                    if (parentJson.has(JsonFormConstants.SECTIONS) && parentJson.get(JsonFormConstants.SECTIONS) instanceof JSONArray) {
                        JSONArray sections = parentJson.getJSONArray(JsonFormConstants.SECTIONS);
                        for (int i = 0; i < sections.length(); i++) {
                            JSONObject sectionJson = sections.getJSONObject(i);
                            if (sectionJson.has(JsonFormConstants.FIELDS)) {
                                fields = formUtils.concatArray(fields, sectionJson.getJSONArray(JsonFormConstants.FIELDS));
                            }
                        }
                    } else if (parentJson.has(JsonFormConstants.FIELDS) && parentJson.get(JsonFormConstants.FIELDS) instanceof JSONArray) {
                        fields = parentJson.getJSONArray(JsonFormConstants.FIELDS);

                    }

                    if (fields.length() > 0) {
                        for (int i = 0; i < fields.length(); i++) {
                            JSONObject item = fields.getJSONObject(i);
                            if (item != null && item.getString(JsonFormConstants.KEY).equals(parentKey) && item.has(JsonFormConstants.VALUE)) {
                                removeLastAddedExpansionValue(item);
                            }
                        }
                    }
                    jsonApi.setmJSONObject(mainJson);
                } catch (JSONException e) {
                    Log.i(TAG, Log.getStackTraceString(e));
                }
            }
        }

        private void removeLastAddedExpansionValue(JSONObject item) throws JSONException {
            JSONArray jsonArray = item.getJSONArray(JsonFormConstants.VALUE);
            JSONArray newValues = new JSONArray();
            for (int i = 0; i < jsonArray.length(); i++) {
                if (i != 0) {
                    JSONObject valueItem = jsonArray.getJSONObject(i);
                    newValues.put(valueItem);
                }
            }
            item.put(JsonFormConstants.VALUE, newValues);
        }
    }
}
