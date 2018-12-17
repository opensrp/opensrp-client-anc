package org.smartregister.anc.util;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.utils.FormUtils;
import com.vijay.jsonwizard.views.CustomTextView;
import org.json.JSONArray;
import org.smartregister.anc.view.AncGenericDialogPopup;

import java.util.HashMap;
import java.util.Map;

public class ContactJsonFormUtils extends FormUtils {
	private AncGenericDialogPopup genericPopupDialog = AncGenericDialogPopup.getInstance();
	
	@Override
	public void showGenericDialog(View view) {
		Context context = (Context)view.getTag(com.vijay.jsonwizard.R.id.specify_context);
		String specifyContent = (String)view.getTag(com.vijay.jsonwizard.R.id.specify_content);
		String specifyContentForm = (String)view.getTag(com.vijay.jsonwizard.R.id.specify_content_form);
		String stepName = (String)view.getTag(com.vijay.jsonwizard.R.id.specify_step_name);
		CommonListener listener = (CommonListener)view.getTag(com.vijay.jsonwizard.R.id.specify_listener);
		JsonFormFragment formFragment = (JsonFormFragment)view.getTag(com.vijay.jsonwizard.R.id.specify_fragment);
		JSONArray jsonArray = (JSONArray)view.getTag(com.vijay.jsonwizard.R.id.secondaryValues);
		String parentKey = (String)view.getTag(com.vijay.jsonwizard.R.id.key);
		String type = (String)view.getTag(com.vijay.jsonwizard.R.id.type);
		CustomTextView customTextView = (CustomTextView)view.getTag(com.vijay.jsonwizard.R.id.specify_textview);
		String childKey;
		
		if (specifyContent != null) {
			genericPopupDialog.setContext(context);
			genericPopupDialog.setCommonListener(listener);
			genericPopupDialog.setFormFragment(formFragment);
			genericPopupDialog.setFormIdentity(specifyContent);
			genericPopupDialog.setFormLocation(specifyContentForm);
			genericPopupDialog.setStepName(stepName);
			genericPopupDialog.setSecondaryValues(jsonArray);
			genericPopupDialog.setParentKey(parentKey);
			genericPopupDialog.setWidgetType(type);
			if (customTextView != null) {
				genericPopupDialog.setCustomTextView(customTextView);
			}
			if (type.equals(JsonFormConstants.CHECK_BOX) || type.equals(JsonFormConstants.NATIVE_RADIO_BUTTON)) {
				childKey = (String)view.getTag(com.vijay.jsonwizard.R.id.childKey);
				genericPopupDialog.setChildKey(childKey);
			}
			
			Activity activity = (Activity)context;
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
	
	@Override
	public Map<String, String> addAssignedValue(String itemKey, String optionKey, String keyValue, String itemType,
			String itemText) {
		Map<String, String> value = new HashMap<>();
		if (genericPopupDialog.getWidgetType().equals(JsonFormConstants.NATIVE_ACCORDION)) {
			String[] labels = itemType.split(";");
			String type = "";
			if (labels.length > 1) {
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
	
}
