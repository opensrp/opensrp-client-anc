package org.smartregister.anc.widget;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatRadioButton;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import com.rey.material.util.ViewUtil;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.utils.FormUtils;
import com.vijay.jsonwizard.widgets.NativeRadioButtonFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.anc.R;
import org.smartregister.anc.util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.vijay.jsonwizard.utils.FormUtils.showEditButton;

public class AncRadioButtonWidgetFactory extends NativeRadioButtonFactory {
	@Override
	protected List<View> attachJson(String stepName, Context context, JsonFormFragment formFragment, JSONObject
			jsonObject, CommonListener commonListener, boolean popup) throws JSONException {
		String widgetType = jsonObject.optString(JsonFormConstants.TYPE, "");
        JSONArray canvasIds = new JSONArray();
		List<View> views = new ArrayList<>(1);
        ImageView editButton;
		if (widgetType.equals(Constants.ANC_RADIO_BUTTON)) {
			boolean readOnly = false;
			if (jsonObject.has(JsonFormConstants.READ_ONLY)) {
				readOnly = jsonObject.getBoolean(JsonFormConstants.READ_ONLY);
			}
            LinearLayout rootLayout = (LinearLayout) LayoutInflater.from(context).inflate(getLayout(), null);
            Map<String, View> labelViews = FormUtils.createRadioButtonAndCheckBoxLabel(stepName, rootLayout, jsonObject, context, canvasIds,
                    readOnly, commonListener);
			String openMrsEntityParent = jsonObject.optString(JsonFormConstants.OPENMRS_ENTITY_PARENT);
			String openMrsEntity = jsonObject.optString(JsonFormConstants.OPENMRS_ENTITY);
			String openMrsEntityId = jsonObject.optString(JsonFormConstants.OPENMRS_ENTITY_ID);
			String relevance = jsonObject.optString(JsonFormConstants.RELEVANCE);
			LinearLayout.LayoutParams layoutParams =
					FormUtils.getLinearLayoutParams(FormUtils.MATCH_PARENT, FormUtils.WRAP_CONTENT, 1, 3, 1, 3);
			RadioGroup radioGroup = getRootLayout(context);
			radioGroup.setLayoutParams(layoutParams);
			radioGroup.setId(ViewUtil.generateViewId());
			canvasIds.put(radioGroup.getId());
			radioGroup.setTag(com.vijay.jsonwizard.R.id.canvas_ids, canvasIds.toString());
			radioGroup.setTag(com.vijay.jsonwizard.R.id.key, jsonObject.getString(JsonFormConstants.KEY));
			radioGroup.setTag(com.vijay.jsonwizard.R.id.openmrs_entity_parent, openMrsEntityParent);
			radioGroup.setTag(com.vijay.jsonwizard.R.id.openmrs_entity, openMrsEntity);
			radioGroup.setTag(com.vijay.jsonwizard.R.id.openmrs_entity_id, openMrsEntityId);
			radioGroup.setTag(com.vijay.jsonwizard.R.id.relevance, relevance);
			radioGroup.setTag(com.vijay.jsonwizard.R.id.extraPopup, popup);
			radioGroup.setTag(com.vijay.jsonwizard.R.id.type, jsonObject.getString(JsonFormConstants.TYPE));
			radioGroup.setTag(com.vijay.jsonwizard.R.id.canvas_ids, canvasIds.toString());
			radioGroup
					.setTag(com.vijay.jsonwizard.R.id.address, stepName + ":" + jsonObject.getString(JsonFormConstants
							.KEY));
			addRadioButtons(stepName, context, jsonObject, commonListener, popup, radioGroup, readOnly, canvasIds);
			rootLayout.addView(radioGroup);
			views.add(rootLayout);
            if (labelViews.size() > 0) {
                editButton = (ImageView) labelViews.get(JsonFormConstants.EDIT_BUTTON);
                if (editButton != null) {
                    showEditButton(jsonObject, radioGroup, editButton, commonListener);
                }

            }
		} else {
			return super.attachJson(stepName, context, formFragment, jsonObject, commonListener, popup);
		}
		
		return views;
	}
	
	private void addRadioButtons(String stepName, Context context, JSONObject
			jsonObject, CommonListener commonListener, boolean popup, RadioGroup rootLayout, boolean readOnly, JSONArray canvasIds)
			throws JSONException {
		JSONArray jsonArray = jsonObject.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME);
		String optionTextSize = String.valueOf(context.getResources().getDimension(com.vijay.jsonwizard.R.dimen.options_default_text_size));
        String optionTextColor = JsonFormConstants.DEFAULT_TEXT_COLOR;
		if (jsonArray.length() > 0) {
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject item = jsonArray.getJSONObject(i);
                if (item.has(JsonFormConstants.TEXT_SIZE)) {
                    optionTextSize = item.getString(JsonFormConstants.TEXT_SIZE);
                } if (item.has(JsonFormConstants.TEXT_COLOR)) {
                    optionTextColor = item.getString(JsonFormConstants.TEXT_COLOR);
                }
				String openMrsEntityParent = item.optString(JsonFormConstants.OPENMRS_ENTITY_PARENT);
				String openMrsEntity = item.optString(JsonFormConstants.OPENMRS_ENTITY);
				String openMrsEntityId = item.optString(JsonFormConstants.OPENMRS_ENTITY_ID);
				LinearLayout.LayoutParams layoutParams =
						FormUtils.getLinearLayoutParams(FormUtils.MATCH_PARENT, FormUtils.WRAP_CONTENT, 1, 3, 1, 3);
				layoutParams.setMargins(0,5,0,5);
				AppCompatRadioButton radioButton = new AppCompatRadioButton(context);
				radioButton.setLayoutParams(layoutParams);
				radioButton.setId(ViewUtil.generateViewId());
				radioButton.setText(item.getString(JsonFormConstants.TEXT));
				radioButton.setTextColor(ContextCompat.getColorStateList(context, R.color.radio_color_selector));
				radioButton.setTextSize(FormUtils.getValueFromSpOrDpOrPx(optionTextSize, context));
				radioButton.setTag(com.vijay.jsonwizard.R.id.key, jsonObject.getString(JsonFormConstants.KEY));
				radioButton.setTag(com.vijay.jsonwizard.R.id.openmrs_entity_parent, openMrsEntityParent);
				radioButton.setTag(com.vijay.jsonwizard.R.id.openmrs_entity, openMrsEntity);
				radioButton.setTag(com.vijay.jsonwizard.R.id.openmrs_entity_id, openMrsEntityId);
				radioButton.setTag(com.vijay.jsonwizard.R.id.type, jsonObject.getString(JsonFormConstants.TYPE));
				radioButton.setTag(com.vijay.jsonwizard.R.id.childKey, item.getString(JsonFormConstants.KEY));
				radioButton.setTag(com.vijay.jsonwizard.R.id.address,
						stepName + ":" + jsonObject.getString(JsonFormConstants.KEY));
				radioButton.setTag(com.vijay.jsonwizard.R.id.extraPopup, popup);
				radioButton.setOnCheckedChangeListener(commonListener);
				radioButton.setTag(com.vijay.jsonwizard.R.id.canvas_ids, canvasIds.toString());
				if (!TextUtils.isEmpty(jsonObject.optString(JsonFormConstants.VALUE))
						&& jsonObject.optString(JsonFormConstants.VALUE).equals(item.getString(JsonFormConstants.KEY))) {
					radioButton.setChecked(true);
				}
				radioButton.setEnabled(!readOnly);
				//setRadioButtonIcon(radioButton, item, context);
				rootLayout.addView(radioButton);
			}
		} else {
			Toast.makeText(context, "Please make sure you have set the radio button options", Toast.LENGTH_SHORT).show();
		}
	}
	
	private RadioGroup getRootLayout(Context context) {
		return (RadioGroup)LayoutInflater.from(context).inflate(
				R.layout.anc_radio_button, null);
	}
}
