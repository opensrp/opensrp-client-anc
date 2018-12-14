package org.smartregister.anc.activity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import com.vijay.jsonwizard.activities.JsonFormActivity;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.anc.R;
import org.smartregister.anc.application.AncApplication;
import org.smartregister.anc.domain.Contact;
import org.smartregister.anc.fragment.ContactJsonFormFragment;
import org.smartregister.anc.model.PartialContact;
import org.smartregister.anc.util.Constants;
import org.smartregister.anc.util.ContactJsonFormUtils;
import org.smartregister.anc.view.AncGenericDialogPopup;

import java.io.Serializable;

/**
 * Created by ndegwamartin on 30/06/2018.
 */
public class ContactJsonFormActivity extends JsonFormActivity {
	
	private static final String CONTACT_STATE = "contactState";
	private Contact contact;
	private ProgressDialog progressDialog;
	private AncGenericDialogPopup genericPopupDialog = AncGenericDialogPopup.getInstance();
	private ContactJsonFormUtils formUtils = new ContactJsonFormUtils();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState == null) {
			this.contact = extractContact(getIntent().getSerializableExtra(Constants.JSON_FORM_EXTRA.CONTACT));
		} else {
			this.contact = extractContact(savedInstanceState.getSerializable(CONTACT_STATE));
		}
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(CONTACT_STATE, contact);
	}
	
	@Override
	public void initializeFormFragment() {
		initializeFormFragmentCore();
	}
	
	@Override
	public void writeValue(String stepName, String key, String value, String openMrsEntityParent, String openMrsEntity,
			String openMrsEntityId, boolean popup) throws JSONException {
		callSuperWriteValue(stepName, key, value, openMrsEntityParent, openMrsEntity, openMrsEntityId, popup);
	}
	
	@Override
	public void onFormFinish() {
		callSuperFinish();
	}
	
	protected void callSuperFinish() {
		super.onFormFinish();
	}
	
	protected void callSuperWriteValue(String stepName, String key, String value, String openMrsEntityParent,
			String openMrsEntity, String openMrsEntityId, Boolean popup) throws JSONException {
		super.writeValue(stepName, key, value, openMrsEntityParent, openMrsEntity, openMrsEntityId, popup);
	}
	
	protected void initializeFormFragmentCore() {
		ContactJsonFormFragment contactJsonFormFragment =
				ContactJsonFormFragment.getFormFragment(JsonFormConstants.FIRST_STEP_NAME);
		getSupportFragmentManager().beginTransaction()
				.add(com.vijay.jsonwizard.R.id.container, contactJsonFormFragment).commit();
	}
	
	private Contact extractContact(Serializable serializable) {
		if (serializable != null && serializable instanceof Contact) {
			return (Contact)serializable;
		}
		return null;
	}
	
	public Contact getContact() {
		return contact;
	}
	
	@Override
	public void onBackPressed() {
		
		new AsyncTask<Void, Void, Void>() {
			
			@Override
			protected void onPreExecute() {
				
				showProgressDialog("Saving contact progress...");
			}
			
			@Override
			protected Void doInBackground(Void... nada) {
				
				persistPartial();
				
				return null;
				
			}
			
			@Override
			protected void onPostExecute(Void result) {
				hideProgressDialog();
				ContactJsonFormActivity.this.finish();
				
			}
		}.execute();
		
	}
	
	public void showProgressDialog(String titleIdentifier) {
		
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setCancelable(false);
			progressDialog.setTitle(titleIdentifier);
			progressDialog.setMessage(getString(R.string.please_wait_message));
		}
		
		if (!isFinishing())
			progressDialog.show();
	}
	
	public void hideProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}
	
	private void persistPartial() {
		String baseEntityId = getIntent().getStringExtra(Constants.INTENT_KEY.BASE_ENTITY_ID);
		
		PartialContact partialContact = new PartialContact();
		partialContact.setBaseEntityId(baseEntityId);
		partialContact.setContactNo(1);//Hardcoded to remove
		partialContact.setFinalized(false);
		
		partialContact.setType(getContact().getFormName());
		partialContact.setFormJsonDraft(currentJsonState());
		
		AncApplication.getInstance().getPartialContactRepository().savePartialContact(partialContact);
	}
	
	@Override
	protected JSONArray fetchFields(JSONObject parentJson, Boolean popup) {
		JSONArray fields = new JSONArray();
		if (genericPopupDialog.getWidgetType() != null && genericPopupDialog.getWidgetType()
				.equals(JsonFormConstants.NATIVE_ACCORDION)) {
			try {
				if (parentJson.has(JsonFormConstants.SECTIONS) && parentJson
						.get(JsonFormConstants.SECTIONS) instanceof JSONArray) {
					JSONArray sections = parentJson.getJSONArray(JsonFormConstants.SECTIONS);
					for (int i = 0; i < sections.length(); i++) {
						JSONObject sectionJson = sections.getJSONObject(i);
						if (sectionJson.has(JsonFormConstants.FIELDS)) {
							if (popup) {
								JSONArray jsonArray = sectionJson.getJSONArray(JsonFormConstants.FIELDS);
								for (int k = 0; k < jsonArray.length(); k++) {
									JSONObject item = jsonArray.getJSONObject(k);
									if (item.getString(JsonFormConstants.KEY).equals(genericPopupDialog.getParentKey())) {
										fields = formUtils.concatArray(fields, specifyFields(item));
									}
								}
							} else {
								fields = formUtils.concatArray(fields, sectionJson.getJSONArray(JsonFormConstants.FIELDS));
							}
						}
					}
				} else if (parentJson.has(JsonFormConstants.FIELDS) && parentJson
						.get(JsonFormConstants.FIELDS) instanceof JSONArray) {
					if (popup) {
						JSONArray jsonArray = parentJson.getJSONArray(JsonFormConstants.FIELDS);
						for (int k = 0; k < jsonArray.length(); k++) {
							JSONObject item = jsonArray.getJSONObject(k);
							if (item.getString(JsonFormConstants.KEY).equals(genericPopupDialog.getParentKey())) {
								fields = specifyFields(item);
							}
						}
					} else {
						fields = parentJson.getJSONArray(JsonFormConstants.FIELDS);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			return super.fetchFields(parentJson, popup);
		}
		
		return fields;
	}
	
	@Override
	protected JSONArray specifyFields(JSONObject parentJson) {
		JSONArray fields = new JSONArray();
		if (genericPopupDialog.getWidgetType() != null && genericPopupDialog.getWidgetType()
				.equals(JsonFormConstants.NATIVE_ACCORDION)) {
			try {
				if (parentJson.has(JsonFormConstants.CONTENT_FORM)) {
					if (getExtraFieldsWithValues() != null) {
						fields = getExtraFieldsWithValues();
					} else {
						fields = getSubFormFields(parentJson.get(JsonFormConstants.CONTENT_FORM).toString(),
								parentJson.get(JsonFormConstants.CONTENT_FORM_LOCATION).toString(), fields);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			return super.specifyFields(parentJson);
		}
		return fields;
	}
	
	@Override
	protected void widgetsWriteValue(String stepName, String key, String value, String openMrsEntityParent,
			String openMrsEntity, String openMrsEntityId, boolean popup) throws JSONException {
		synchronized (getmJSONObject()) {
			JSONObject jsonObject = getmJSONObject().getJSONObject(stepName);
			JSONArray fields = fetchFields(jsonObject, popup);
			for (int i = 0; i < fields.length(); i++) {
				JSONObject item = fields.getJSONObject(i);
				String keyAtIndex = item.getString(JsonFormConstants.KEY);
				String itemType = "";
				if (popup) {
					itemType = item.getString(JsonFormConstants.TYPE);
					String widgetLabel = getWidgetLabel(item);
					if (!TextUtils.isEmpty(widgetLabel)) {
						itemType = itemType + ";" + widgetLabel;
					}
				}
				if (key.equals(keyAtIndex)) {
					if (item.has(JsonFormConstants.TEXT)) {
						item.put(JsonFormConstants.TEXT, value);
					} else {
						if (popup) {
							String itemText = "";
							String type = item.getString(JsonFormConstants.TYPE);
							if (type.equals(JsonFormConstants.NATIVE_RADIO_BUTTON)) {
								itemText = formUtils.getRadioButtonText(item, value);
							}
							
							genericPopupDialog
									.addSelectedValues(
											formUtils.addAssignedValue(keyAtIndex, "", value, itemType, itemText));
							setExtraFieldsWithValues(fields);
						}
						item.put(JsonFormConstants.VALUE, value);
					}
					item.put(JsonFormConstants.OPENMRS_ENTITY_PARENT, openMrsEntityParent);
					item.put(JsonFormConstants.OPENMRS_ENTITY, openMrsEntity);
					item.put(JsonFormConstants.OPENMRS_ENTITY_ID, openMrsEntityId);
					refreshCalculationLogic(key, null, popup);
					refreshSkipLogic(key, null, popup);
					refreshConstraints(key, null);
					refreshMediaLogic(key, value);
					return;
				}
			}
		}
	}
	
	@Override
	protected void checkBoxWriteValue(String stepName, String parentKey, String childObjectKey, String childKey,
			String value, boolean popup) throws JSONException {
		synchronized (getmJSONObject()) {
			JSONObject jsonObject = getmJSONObject().getJSONObject(stepName);
			JSONArray fields = fetchFields(jsonObject, popup);
			for (int i = 0; i < fields.length(); i++) {
				JSONObject item = fields.getJSONObject(i);
				String keyAtIndex = item.getString(JsonFormConstants.KEY);
				StringBuilder itemType = new StringBuilder();
				
				if (popup) {
					itemType = new StringBuilder(item.getString(JsonFormConstants.TYPE));
					String widgetLabel = getWidgetLabel(item);
					if (!TextUtils.isEmpty(widgetLabel)) {
						itemType.append(";").append(widgetLabel);
					}
				}
				if (parentKey.equals(keyAtIndex)) {
					JSONArray jsonArray = item.getJSONArray(childObjectKey);
					for (int j = 0; j < jsonArray.length(); j++) {
						JSONObject innerItem = jsonArray.getJSONObject(j);
						String anotherKeyAtIndex = innerItem.getString(JsonFormConstants.KEY);
						String itemText = "";
						String type = item.getString(JsonFormConstants.TYPE);
						if (type.equals(JsonFormConstants.CHECK_BOX)) {
							itemText = innerItem.getString(JsonFormConstants.TEXT);
						}
						
						if (childKey.equals(anotherKeyAtIndex)) {
							innerItem.put(JsonFormConstants.VALUE, value);
							if (popup) {
								genericPopupDialog.addSelectedValues(
										formUtils.addAssignedValue(keyAtIndex, childKey, value, itemType.toString(),
												itemText));
								setExtraFieldsWithValues(fields);
							}
							refreshCalculationLogic(parentKey, childKey, popup);
							refreshSkipLogic(parentKey, childKey, popup);
							refreshConstraints(parentKey, childKey);
							return;
						}
					}
				}
			}
		}
	}
	
	private String getWidgetLabel(JSONObject jsonObject) throws JSONException {
		String label = "";
		String widgetType = jsonObject.getString(JsonFormConstants.TYPE);
		if (!TextUtils.isEmpty(widgetType)) {
			switch (widgetType) {
				case JsonFormConstants.NATIVE_RADIO_BUTTON:
					label = jsonObject.getString(JsonFormConstants.LABEL);
					break;
				case JsonFormConstants.CHECK_BOX:
					label = jsonObject.getString(JsonFormConstants.LABEL);
					break;
				case JsonFormConstants.EDIT_TEXT:
					label = jsonObject.getString(JsonFormConstants.HINT);
					break;
				default:
					break;
			}
		}
		return label;
	}
}

