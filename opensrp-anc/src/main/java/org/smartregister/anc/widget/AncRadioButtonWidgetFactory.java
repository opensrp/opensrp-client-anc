package org.smartregister.anc.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatRadioButton;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
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

public class AncRadioButtonWidgetFactory extends NativeRadioButtonFactory {
	@Override
	protected List<View> attachJson(String stepName, Context context, JsonFormFragment formFragment, JSONObject
			jsonObject, CommonListener commonListener, boolean popup) throws JSONException {
		String widgetType = jsonObject.optString(JsonFormConstants.TYPE, "");
		List<View> views = new ArrayList<>(1);
		if (widgetType.equals(Constants.ANC_RADIO_BUTTON)) {
			boolean readOnly = false;
			if (jsonObject.has(JsonFormConstants.READ_ONLY)) {
				readOnly = jsonObject.getBoolean(JsonFormConstants.READ_ONLY);
			}
			String openMrsEntityParent = jsonObject.optString(JsonFormConstants.OPENMRS_ENTITY_PARENT);
			String openMrsEntity = jsonObject.optString(JsonFormConstants.OPENMRS_ENTITY);
			String openMrsEntityId = jsonObject.optString(JsonFormConstants.OPENMRS_ENTITY_ID);
			String relevance = jsonObject.optString(JsonFormConstants.RELEVANCE);
			LinearLayout.LayoutParams layoutParams =
					FormUtils.getLinearLayoutParams(FormUtils.MATCH_PARENT, FormUtils.WRAP_CONTENT, 1, 2, 1, 2);
			RadioGroup rootLayout = getRootLayout(context);
			rootLayout.setLayoutParams(layoutParams);
			JSONArray canvasIds = new JSONArray();
			rootLayout.setId(ViewUtil.generateViewId());
			canvasIds.put(rootLayout.getId());
			rootLayout.setTag(com.vijay.jsonwizard.R.id.canvas_ids, canvasIds.toString());
			rootLayout.setTag(com.vijay.jsonwizard.R.id.key, jsonObject.getString(JsonFormConstants.KEY));
			rootLayout.setTag(com.vijay.jsonwizard.R.id.openmrs_entity_parent, openMrsEntityParent);
			rootLayout.setTag(com.vijay.jsonwizard.R.id.openmrs_entity, openMrsEntity);
			rootLayout.setTag(com.vijay.jsonwizard.R.id.openmrs_entity_id, openMrsEntityId);
			rootLayout.setTag(com.vijay.jsonwizard.R.id.relevance, relevance);
			rootLayout.setTag(com.vijay.jsonwizard.R.id.extraPopup, popup);
			rootLayout.setTag(com.vijay.jsonwizard.R.id.type, jsonObject.getString(JsonFormConstants.TYPE));
			rootLayout
					.setTag(com.vijay.jsonwizard.R.id.address, stepName + ":" + jsonObject.getString(JsonFormConstants
							.KEY));
			addRadioButtons(stepName, context, jsonObject, commonListener, popup, rootLayout, readOnly);
			views.add(rootLayout);
		} else {
			return super.attachJson(stepName, context, formFragment, jsonObject, commonListener, popup);
		}
		
		return views;
	}
	
	private void addRadioButtons(String stepName, Context context, JSONObject
			jsonObject, CommonListener commonListener, boolean popup, RadioGroup rootLayout, boolean readOnly)
			throws JSONException {
		JSONArray jsonArray = jsonObject.getJSONArray(JsonFormConstants.OPTIONS_FIELD_NAME);
		if (jsonArray.length() > 0) {
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject item = jsonArray.getJSONObject(i);
				String openMrsEntityParent = item.optString(JsonFormConstants.OPENMRS_ENTITY_PARENT);
				String openMrsEntity = item.optString(JsonFormConstants.OPENMRS_ENTITY);
				String openMrsEntityId = item.optString(JsonFormConstants.OPENMRS_ENTITY_ID);
				LinearLayout.LayoutParams layoutParams =
						FormUtils.getLinearLayoutParams(FormUtils.MATCH_PARENT, FormUtils.WRAP_CONTENT, 1, 2, 1, 2);
				AppCompatRadioButton radioButton = new AppCompatRadioButton(context);
				radioButton.setLayoutParams(layoutParams);
				radioButton.setId(ViewUtil.generateViewId());
				radioButton.setText(item.getString(JsonFormConstants.TEXT));
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
				if (!TextUtils.isEmpty(jsonObject.optString(JsonFormConstants.VALUE))
						&& jsonObject.optString(JsonFormConstants.VALUE).equals(item.getString(JsonFormConstants.KEY))) {
					radioButton.setChecked(true);
				}
				radioButton.setEnabled(!readOnly);
				setRadioButtonIcon(radioButton, item, context);
				rootLayout.addView(radioButton);
			}
		} else {
			Toast.makeText(context, "Please make sure you have set the radio button options", Toast.LENGTH_SHORT).show();
		}
	}
	
	private void setRadioButtonIcon(RadioButton radioButtonIcon, JSONObject optionItem, Context context)
			throws JSONException {
		if (optionItem != null && optionItem.has(JsonFormConstants.TYPE)) {
			String type = optionItem.getString(JsonFormConstants.TYPE);
			radioButtonIcon.setPadding(48, 0, 0, 0);
			switch (type) {
				case Constants
						.ANC_RADIO_BUTTON_OPTION_TYPES.DONE_TODAY:
					radioButtonIcon.setButtonDrawable(context.getResources().getDrawable(R.drawable.done_today));
					break;
				case Constants
						.ANC_RADIO_BUTTON_OPTION_TYPES.DONE_EARLIER:
					radioButtonIcon.setButtonDrawable(context.getResources().getDrawable(R.drawable.done_today));
					break;
				case Constants
						.ANC_RADIO_BUTTON_OPTION_TYPES.ORDERED:
					radioButtonIcon.setButtonDrawable(context.getResources().getDrawable(R.drawable.ic_yellow_radio_button));
					break;
				case Constants
						.ANC_RADIO_BUTTON_OPTION_TYPES.NOT_DONE:
					radioButtonIcon.setButtonDrawable(context.getResources().getDrawable(R.drawable.not_done));
					break;
				default:
					break;
			}
		}
	}
	
	private RadioGroup getRootLayout(Context context) {
		return (RadioGroup)LayoutInflater.from(context).inflate(
				R.layout.anc_radio_button, null);
	}
}
