package org.smartregister.anc.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.rey.material.util.ViewUtil;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.FormWidgetFactory;
import com.vijay.jsonwizard.interfaces.JsonApi;
import com.vijay.jsonwizard.utils.FormUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.anc.R;
import org.smartregister.anc.adapter.ExpandableListAdapter;
import org.smartregister.anc.util.Constants;
import org.smartregister.anc.util.ContactJsonFormUtils;
import org.smartregister.anc.view.ScrollDisabledExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AccordionWidgetFactory implements FormWidgetFactory {
	private RecordButtonClickListener recordButtonClickListener = new RecordButtonClickListener();
	private UndoButtonClickListener undoButtonClickListener = new UndoButtonClickListener();
	private ContactJsonFormUtils formUtils = new ContactJsonFormUtils();
	private JSONArray accordionValues = new JSONArray();
	private HashMap<String, List<String>> expandableListDetail = new HashMap<>();
	
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
	
	private List<View> attachJson(String stepName, Context context, JsonFormFragment jsonFormFragment, JSONObject
			jsonObject,
			CommonListener commonListener, boolean popup) throws JSONException {
		List<View> views = new ArrayList<>(1);
		
		String openMrsEntityParent = jsonObject.optString(JsonFormConstants.OPENMRS_ENTITY_PARENT);
		String openMrsEntity = jsonObject.optString(JsonFormConstants.OPENMRS_ENTITY);
		String openMrsEntityId = jsonObject.optString(JsonFormConstants.OPENMRS_ENTITY_ID);
		String relevance = jsonObject.optString(JsonFormConstants.RELEVANCE);
		LinearLayout.LayoutParams layoutParams =
				FormUtils.getLinearLayoutParams(FormUtils.MATCH_PARENT, FormUtils.MATCH_PARENT, 1, 2, 1, 2);
		LinearLayout rootLayout = getRootLayout(context);
		rootLayout.setLayoutParams(layoutParams);
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
			((JsonApi)context).addSkipLogicView(rootLayout);
		}
		
		attachLayout(stepName, context, jsonFormFragment, jsonObject, commonListener, popup, rootLayout);
		
		views.add(rootLayout);
		return views;
	}
	
	private void attachLayout(String stepName, final Context context, JsonFormFragment jsonFormFragment,
			JSONObject jsonObject, CommonListener commonListener, boolean popup, LinearLayout rootLayout)
			throws JSONException {
		String accordionInfoText = jsonObject.optString(Constants.ACCORDION_INFO_TEXT, null);
		String accordionInfoTitle = jsonObject.optString(Constants.ACCORDION_INFO_TITLE, null);
		String accordionText = jsonObject.optString(JsonFormConstants.TEXT, "");
		if (jsonObject.has(JsonFormConstants.VALUE)) {
			setAccordionValues(jsonObject.getJSONArray(JsonFormConstants.VALUE));
		}
		ScrollDisabledExpandableListView expandableListView = rootLayout.findViewById(R.id.expandable_list_view);
		expandableListDetail = addExpandableListDetails(accordionText, accordionValues);
		List<String> expandableListTitle = new ArrayList<>(expandableListDetail.keySet());
		ExpandableListAdapter expandableListAdapter =
				new ExpandableListAdapter(context, expandableListTitle, expandableListDetail);
		expandableListView.setTag(R.id.accordion_info_text, accordionInfoText);
		expandableListView.setTag(R.id.accordion_info_title, accordionInfoTitle);
		expandableListView.setTag(com.vijay.jsonwizard.R.id.key, jsonObject.getString(JsonFormConstants.KEY));
		expandableListView.setTag(com.vijay.jsonwizard.R.id.type, jsonObject.getString(JsonFormConstants.TYPE));
		expandableListView.setTag(com.vijay.jsonwizard.R.id.specify_listener, commonListener);
		expandableListView.setAdapter(expandableListAdapter);
		expandableListAdapter.notifyDataSetChanged();
		addBottomSection(stepName, context, jsonFormFragment, jsonObject, commonListener, popup, rootLayout);
	}
	
	private HashMap<String, List<String>> addExpandableListDetails(String text, JSONArray jsonArray) throws JSONException {
		HashMap<String, List<String>> details = new HashMap<>();
		details.put(text, addExpandableChildren(jsonArray));
		return details;
	}
	
	private String getStringValue(JSONObject jsonObject) throws JSONException {
		StringBuilder value = new StringBuilder();
		if (jsonObject != null) {
			JSONArray jsonArray = jsonObject.getJSONArray(JsonFormConstants.VALUES);
			for (int i = 0; i < jsonArray.length(); i++) {
				String stringValue = jsonArray.getString(i);
				value.append(getValueFromSecondaryValues(stringValue));
				value.append(", ");
			}
		}
		
		return value.toString().replaceAll(", $", "");
	}
	
	private String getValueFromSecondaryValues(String itemString) {
		String newString;
		String[] strings = itemString.split(":");
		if (strings.length > 1) {
			newString = strings[1];
		} else {
			newString = strings[0];
		}
		
		return newString;
	}
	
	private List<String> addExpandableChildren(JSONArray jsonArray) throws JSONException {
		List<String> stringList = new ArrayList<>();
		String label;
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			if (jsonObject.has(JsonFormConstants.VALUES) && jsonObject.has(JsonFormConstants.LABEL)) {
				label = jsonObject.getString(JsonFormConstants.LABEL);
				stringList.add(label + ":" + getStringValue(jsonObject));
			}
		}
		
		return stringList;
	}
	
	private void addBottomSection(String stepName, Context context, JsonFormFragment jsonFormFragment, JSONObject
			jsonObject,
			CommonListener commonListener, boolean popup, LinearLayout rootLayout) throws JSONException {
		Boolean displayBottomSection = jsonObject.optBoolean(Constants.DISPLAY_BOTTOM_SECTION, false);
		if (displayBottomSection) {
			RelativeLayout relativeLayout = rootLayout.findViewById(R.id.accordion_bottom_navigation);
			relativeLayout.setVisibility(View.VISIBLE);
			
			Button recordButton = relativeLayout.findViewById(R.id.ok_button);
			recordButton = addOkButtonTags(recordButton, jsonObject, stepName, commonListener, jsonFormFragment, context);
			recordButton.setOnClickListener(recordButtonClickListener);
			Button undoButton = relativeLayout.findViewById(R.id.undo_button);
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
		okButton.setTag(R.id.secondaryValues,
				formUtils.getSecondaryValues(jsonObject, jsonObject.getString(JsonFormConstants.TYPE)));
		okButton.setTag(R.id.key, jsonObject.getString(JsonFormConstants.KEY));
		okButton.setTag(R.id.type, jsonObject.getString(JsonFormConstants.TYPE));
		
		return okButton;
	}
	
	private LinearLayout getRootLayout(Context context) {
		return (LinearLayout)LayoutInflater.from(context).inflate(
				R.layout.native_expandable_list_view, null);
	}
	
	public void setAccordionValues(JSONArray accordionValues) {
		this.accordionValues = accordionValues;
	}
	
	private class RecordButtonClickListener implements View.OnClickListener {
		@Override
		public void onClick(View view) {
			formUtils.showGenericDialog(view);
		}
	}
	
	private class UndoButtonClickListener implements View.OnClickListener {
		
		@Override
		public void onClick(View view) {
			expandableListDetail.remove(expandableListDetail.size() -1);
		}
	}
}
