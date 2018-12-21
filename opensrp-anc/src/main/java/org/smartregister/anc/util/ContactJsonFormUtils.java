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
import org.smartregister.anc.R;
import org.smartregister.anc.contract.AncGenericDialogInterface;
import org.smartregister.anc.view.AncGenericDialogPopup;

import java.util.HashMap;
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
        String toolbarHeader = "";
        LinearLayout rootLayout = (LinearLayout) view.getTag(R.id.main_layout);
        if (type != null && type.equals(Constants.EXPANSION_PANEL)) {
            toolbarHeader = (String) view.getTag(R.id.header);
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
            }
            genericPopupDialog.setWidgetType(type);
            if (customTextView != null) {
                genericPopupDialog.setCustomTextView(customTextView);
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

    public Map<String, String> createAssignedValue(AncGenericDialogInterface genericDialogInterface, String itemKey, String optionKey, String keyValue, String itemType,
                                                   String itemText) {
        this.genericDialogInterface = genericDialogInterface;
        return addAssignedValue(itemKey, optionKey, keyValue, itemType, itemText);
    }

    @Override
    public Map<String, String> addAssignedValue(String itemKey, String optionKey, String keyValue, String itemType,
                                                String itemText) {
        Map<String, String> value = new HashMap<>();
        if (genericDialogInterface != null && !TextUtils.isEmpty(genericDialogInterface.getWidgetType()) && genericDialogInterface.getWidgetType().equals(Constants.EXPANSION_PANEL)) {
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
            if (type.contains(Constants.ANC_RADIO_BUTTON_OPTION_TYPES.DONE_TODAY) || type.contains(Constants.ANC_RADIO_BUTTON_OPTION_TEXT.DONE_TODAY)) {
                imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.done_today));
            } else if (type.contains(Constants
                    .ANC_RADIO_BUTTON_OPTION_TYPES.DONE_EARLIER) || type.contains(Constants.ANC_RADIO_BUTTON_OPTION_TEXT.DONE_EARLIER)) {
                imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.done_today));
            } else if (type.contains(Constants
                    .ANC_RADIO_BUTTON_OPTION_TYPES.ORDERED) || type.contains(Constants.ANC_RADIO_BUTTON_OPTION_TEXT.ORDERED)) {
                imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.ordered));
            } else if (type.contains(Constants
                    .ANC_RADIO_BUTTON_OPTION_TYPES.NOT_DONE) || type.contains(Constants.ANC_RADIO_BUTTON_OPTION_TEXT.NOT_DONE)) {
                imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.not_done));
            }
        }
    }

}
