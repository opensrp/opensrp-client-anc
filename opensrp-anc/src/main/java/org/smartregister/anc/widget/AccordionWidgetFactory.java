package org.smartregister.anc.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.gson.JsonObject;
import com.rey.material.util.ViewUtil;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.FormWidgetFactory;
import com.vijay.jsonwizard.interfaces.JsonApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.anc.R;
import org.smartregister.anc.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class AccordionWidgetFactory implements FormWidgetFactory {
    private OkButtonClickListener okButtonClickListener = new OkButtonClickListener();

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JsonFormFragment jsonFormFragment, JSONObject jsonObject, CommonListener commonListener, boolean popup) throws Exception {
        return attachJson(stepName, context, jsonFormFragment, jsonObject, commonListener, popup);
    }

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JsonFormFragment jsonFormFragment, JSONObject jsonObject, CommonListener commonListener) throws Exception {
        return attachJson(stepName, context, jsonFormFragment, jsonObject, commonListener, false);
    }

    private List<View> attachJson(String stepName, Context context, JsonFormFragment jsonFormFragment, JSONObject jsonObject, CommonListener commonListener, boolean popup) throws JSONException {
        List<View> views = new ArrayList<>(1);

        String openMrsEntityParent = jsonObject.optString(JsonFormConstants.OPENMRS_ENTITY_PARENT);
        String openMrsEntity = jsonObject.optString(JsonFormConstants.OPENMRS_ENTITY);
        String openMrsEntityId = jsonObject.optString(JsonFormConstants.OPENMRS_ENTITY_ID);
        String relevance = jsonObject.optString(JsonFormConstants.RELEVANCE);
        LinearLayout rootLayout = getRootLayout(context);
        JSONArray canvasIds = new JSONArray();
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

        if (relevance != null && context instanceof JsonApi) {
            rootLayout.setTag(com.vijay.jsonwizard.R.id.relevance, relevance);
            ((JsonApi) context).addSkipLogicView(rootLayout);
        }

        attachLayout(stepName, context, jsonFormFragment, jsonObject, commonListener, popup, rootLayout, views);

        views.add(rootLayout);
        return views;
    }

    private void attachLayout(String stepName, Context context, JsonFormFragment jsonFormFragment, JSONObject jsonObject, CommonListener commonListener, boolean popup, LinearLayout rootLayout, List<View> views) throws JSONException {
        addAccordionInfo(jsonObject, commonListener, rootLayout);
        addBottomSection(stepName, context, jsonFormFragment, jsonObject, commonListener, popup, rootLayout);
    }

    private void addAccordionInfo(JSONObject jsonObject, CommonListener commonListener, LinearLayout rootLayout) throws JSONException {
        String accordionInfoText = jsonObject.optString(Constants.ACCORDION_INFO_TEXT, null);
        String accordionInfoTitle = jsonObject.optString(Constants.ACCORDION_INFO_TITLE, null);
        RelativeLayout relativeLayout = rootLayout.findViewById(R.id.accordionTopBar);

        if (accordionInfoText != null) {
            ImageView accordionInfoWidget = relativeLayout.findViewById(R.id.accordion_info_icon);
            accordionInfoWidget.setVisibility(View.VISIBLE);
            accordionInfoWidget.setTag(com.vijay.jsonwizard.R.id.key, jsonObject.getString(JsonFormConstants.KEY));
            accordionInfoWidget.setTag(com.vijay.jsonwizard.R.id.type, jsonObject.getString(JsonFormConstants.TYPE));
            accordionInfoWidget.setTag(com.vijay.jsonwizard.R.id.label_dialog_info, accordionInfoText);
            accordionInfoWidget.setTag(com.vijay.jsonwizard.R.id.label_dialog_title, accordionInfoTitle);
            accordionInfoWidget.setOnClickListener(commonListener);
        }
    }

    private void addBottomSection(String stepName, Context context, JsonFormFragment jsonFormFragment, JSONObject jsonObject, CommonListener commonListener, boolean popup, LinearLayout rootLayout) {
        Boolean displayBottomSection = jsonObject.optBoolean(Constants.DISPLAY_BOTTOM_SECTION, false);
        if (displayBottomSection) {
            RelativeLayout relativeLayout = rootLayout.findViewById(R.id.accordion_bottom_navigation);
            relativeLayout.setVisibility(View.VISIBLE);

            Button okButton = relativeLayout.findViewById(R.id.ok_button);
            okButton.setTag(R.id.accordion_jsonObject, jsonObject);
            okButton.setTag(R.id.accordion_context, context);
            okButton.setTag(R.id.accordion_listener, commonListener);
            okButton.setTag(R.id.accordion_step_name    , stepName);
            okButton.setOnClickListener(okButtonClickListener);
            Button cancelButton = relativeLayout.findViewById(R.id.cancel_button);
        }
    }

    private LinearLayout getRootLayout(Context context) {
        return (LinearLayout) LayoutInflater.from(context).inflate(
                R.layout.native_form_accordion_layout, null);
    }

    private class OkButtonClickListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            String stepName = (String) view.getTag(R.id.accordion_step_name);
            JsonObject parentJson = (JsonObject) view.getTag(R.id.accordion_jsonObject);
            Context context = (Context) view.getTag(R.id.accordion_context);
            CommonListener commonListener = (CommonListener) view.getTag(R.id.accordion_listener);
        }
    }
}
