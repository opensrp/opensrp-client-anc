package org.smartregister.anc.library.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;

import org.codehaus.jackson.JsonNode;
import org.smartregister.anc.library.constants.ANCJsonFormConstants;
import com.vijay.jsonwizard.rules.RuleConstant;

import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;
import org.jeasy.rules.api.Facts;
import org.jetbrains.annotations.NotNull;
import org.joda.time.LocalDate;
import org.joda.time.Weeks;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.CoreLibrary;
import org.smartregister.anc.library.AncLibrary;
import org.smartregister.anc.library.R;
import org.smartregister.anc.library.activity.BaseHomeRegisterActivity;
import org.smartregister.anc.library.activity.ContactJsonFormActivity;
import org.smartregister.anc.library.activity.ContactSummaryFinishActivity;
import org.smartregister.anc.library.constants.AncAppPropertyConstants;
import org.smartregister.anc.library.domain.ButtonAlertStatus;
import org.smartregister.anc.library.domain.Contact;
import org.smartregister.anc.library.domain.YamlConfig;
import org.smartregister.anc.library.domain.YamlConfigItem;
import org.smartregister.anc.library.event.BaseEvent;
import org.smartregister.anc.library.model.ContactModel;
import org.smartregister.anc.library.model.PartialContact;
import org.smartregister.anc.library.model.Task;
import org.smartregister.anc.library.repository.ContactTasksRepository;
import org.smartregister.anc.library.repository.PatientRepository;
import org.smartregister.anc.library.rule.AlertRule;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.util.JsonFormUtils;
import org.smartregister.view.activity.DrishtiApplication;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

/**
 * Created by ndegwamartin on 14/03/2018.
 */

public class Utils extends org.smartregister.util.Utils {
    public static final SimpleDateFormat DB_DF = new SimpleDateFormat(ConstantsUtils.SQLITE_DATE_TIME_FORMAT);
    public static final SimpleDateFormat CONTACT_DF = new SimpleDateFormat(ConstantsUtils.CONTACT_DATE_FORMAT);
    public static final SimpleDateFormat CONTACT_SUMMARY_DF = new SimpleDateFormat(ConstantsUtils.CONTACT_SUMMARY_DATE_FORMAT);
    public static final ArrayList<String> ALLOWED_LEVELS;
    public static final String DEFAULT_LOCATION_LEVEL = "Health Facility";
    public static final String FACILITY = "Facility";
    public static final String HOME_ADDRESS = "Home Address";
    private static final DateTimeFormatter SQLITE_DATE_DF = DateTimeFormat.forPattern(ConstantsUtils.SQLITE_DATE_TIME_FORMAT);
    private static final String OTHER_SUFFIX = ", other]";

    static {
        ALLOWED_LEVELS = new ArrayList<>();
        ALLOWED_LEVELS.add(DEFAULT_LOCATION_LEVEL);
        ALLOWED_LEVELS.add(FACILITY);
    }

    public static void saveLanguage(String language) {
        Utils.getAllSharedPreferences().saveLanguagePreference(language);
    }

    public static void postEvent(BaseEvent event) {
        EventBus.getDefault().post(event);
    }

    public static void postStickyEvent(
            BaseEvent event) {//Each Sticky event must be manually cleaned by calling Utils.removeStickyEvent
        // after
        // handling
        EventBus.getDefault().postSticky(event);
    }

    public static void removeStickyEvent(BaseEvent event) {
        EventBus.getDefault().removeStickyEvent(event);

    }

    public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (value.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    public static void writePrefString(Context context, final String key, final String value) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static int convertDpToPx(Context context, int dp) {
        Resources r = context.getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
        return Math.round(px);
    }

    public static boolean isEmptyMap(Map map) {
        return map == null || map.isEmpty();
    }

    public static boolean isEmptyCollection(Collection collection) {
        return collection == null || collection.isEmpty();
    }

    public static String getTodaysDate() {
        return convertDateFormat(Calendar.getInstance().getTime(), DB_DF);
    }

    public static String convertDateFormat(Date date, SimpleDateFormat formatter) {

        return formatter.format(date);
    }

    @Nullable
    public static int getAttributeDrawableResource(Context context, int attributeId) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(attributeId, typedValue, true);
        return typedValue.resourceId;
    }

    public static int getProfileImageResourceIdentifier() {
        return R.drawable.avatar_woman;
    }

    public static List<String> getListFromString(String stringArray) {
        List<String> stringList = new ArrayList<>();
        if (!StringUtils.isEmpty(stringArray)) {
            stringList = new ArrayList<>(
                    Arrays.asList(stringArray.substring(1, stringArray.length() - 1).replaceAll("\"", "").split(", ")));
        }
        return stringList;
    }

    /**
     * Check for the quick check form then finds whether it still has pending required fields, If it has pending fields if so
     * it redirects to the quick check page. If not pending required fields then it redirects to the main contact page
     *
     * @param baseEntityId       {@link String}
     * @param personObjectClient {@link CommonPersonObjectClient}
     * @param context            {@link Context}
     * @author martinndegwa
     */
    public static void proceedToContact(String baseEntityId, HashMap<String, String> personObjectClient, Context context) {
        try {

            Intent intent = new Intent(context.getApplicationContext(), ContactJsonFormActivity.class);
            Contact quickCheck = new Contact();
            quickCheck.setName(context.getResources().getString(R.string.quick_check));
            quickCheck.setFormName(ConstantsUtils.JsonFormUtils.ANC_QUICK_CHECK);
            quickCheck.setContactNumber(Integer.valueOf(personObjectClient.get(DBConstantsUtils.KeyUtils.NEXT_CONTACT)));
            quickCheck.setBackground(R.drawable.quick_check_bg);
            quickCheck.setActionBarBackground(R.color.quick_check_red);
            quickCheck.setBackIcon(R.drawable.ic_clear);
            quickCheck.setWizard(false);
            quickCheck.setHideSaveLabel(true);


            //partial contact exists?
            PartialContact partialContactRequest = new PartialContact();
            partialContactRequest.setBaseEntityId(baseEntityId);
            partialContactRequest.setContactNo(quickCheck.getContactNumber());
            partialContactRequest.setType(quickCheck.getFormName());

            String locationId = AncLibrary.getInstance().getContext().allSharedPreferences()
                    .getPreference(AllConstants.CURRENT_LOCATION_ID);

            ContactModel baseContactModel = new ContactModel();
            JSONObject form = baseContactModel.getFormAsJson(quickCheck.getFormName(), baseEntityId, locationId);

            if (ConstantsUtils.DueCheckStrategy.CHECK_FOR_FIRST_CONTACT.equals(Utils.getDueCheckStrategy())) {
                JSONObject globals = new JSONObject();
                globals.put(ConstantsUtils.IS_FIRST_CONTACT, PatientRepository.isFirstVisit(personObjectClient.get(DBConstantsUtils.KeyUtils.BASE_ENTITY_ID)));
                form.put(ConstantsUtils.GLOBAL, globals);
            }

            String processedForm = ANCFormUtils.getFormJsonCore(partialContactRequest, form).toString();

            if (hasPendingRequiredFields(new JSONObject(processedForm))) {
                intent.putExtra(ConstantsUtils.JsonFormExtraUtils.JSON, processedForm);
                intent.putExtra(ANCJsonFormConstants.JSON_FORM_KEY.FORM, quickCheck);
                intent.putExtra(ConstantsUtils.IntentKeyUtils.BASE_ENTITY_ID, partialContactRequest.getBaseEntityId());
                intent.putExtra(ConstantsUtils.IntentKeyUtils.CLIENT_MAP, personObjectClient);
                intent.putExtra(ConstantsUtils.IntentKeyUtils.FORM_NAME, partialContactRequest.getType());
                intent.putExtra(ConstantsUtils.IntentKeyUtils.CONTACT_NO, partialContactRequest.getContactNo());
                intent.putExtra(ANCJsonFormConstants.PERFORM_FORM_TRANSLATION, true);
                Activity activity = (Activity) context;
                activity.startActivityForResult(intent, ANCJsonFormUtils.REQUEST_CODE_GET_JSON);
            } else {
                intent = new Intent(context, AncLibrary.getInstance().getActivityConfiguration().getMainContactActivityClass());
                intent.putExtra(ConstantsUtils.IntentKeyUtils.BASE_ENTITY_ID, baseEntityId);
                intent.putExtra(ConstantsUtils.IntentKeyUtils.CLIENT_MAP, personObjectClient);
                intent.putExtra(ConstantsUtils.IntentKeyUtils.FORM_NAME, partialContactRequest.getType());
                intent.putExtra(ConstantsUtils.IntentKeyUtils.CONTACT_NO,
                        Integer.valueOf(personObjectClient.get(DBConstantsUtils.KeyUtils.NEXT_CONTACT)));
                intent.putExtra(ANCJsonFormConstants.PERFORM_FORM_TRANSLATION, true);
                context.startActivity(intent);
            }


        } catch (Exception e) {
            Timber.e(e, " --> proceedToContact");
            Utils.showToast(context,
                    "Error proceeding to contact for client " + personObjectClient.get(DBConstantsUtils.KeyUtils.FIRST_NAME));
        }
    }

    /**
     * Checks the pending required fields on the json forms and returns true|false
     *
     * @param object {@link JSONObject}
     * @return true|false {@link Boolean}
     * @throws Exception
     * @author martinndegwa
     */
    public static boolean hasPendingRequiredFields(JSONObject object) throws Exception {
        if (object != null) {
            Iterator<String> keys = object.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                if (key.startsWith(RuleConstant.STEP)) {
                    JSONArray stepArray = object.getJSONObject(key).getJSONArray(ANCJsonFormConstants.FIELDS);

                    for (int i = 0; i < stepArray.length(); i++) {
                        JSONObject fieldObject = stepArray.getJSONObject(i);
                        ANCFormUtils.processSpecialWidgets(fieldObject);

                        boolean isRequiredField = ANCJsonFormUtils.isFieldRequired(fieldObject);
                        //Do not check for required for fields that are invisible
                        if (fieldObject.has(ANCJsonFormConstants.IS_VISIBLE) && !fieldObject.getBoolean(ANCJsonFormConstants.IS_VISIBLE)) {
                            isRequiredField = false;
                        }

                        if (isRequiredField && ((fieldObject.has(ANCJsonFormConstants.VALUE) && TextUtils.isEmpty(
                                fieldObject.getString(ANCJsonFormConstants.VALUE))) || !fieldObject.has(ANCJsonFormConstants.VALUE))) {
                            //TO DO Remove/ Alter logical condition
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * This finalizes the form and redirects you to the contact summary page for more confirmation of the data added
     *
     * @param context {@link Activity}
     * @author martinndegwa
     */
    public static void finalizeForm(Activity context, HashMap<String, String> womanDetails, boolean isRefferal) {
        try {

            Intent contactSummaryFinishIntent = new Intent(context, ContactSummaryFinishActivity.class);
            contactSummaryFinishIntent
                    .putExtra(ConstantsUtils.IntentKeyUtils.BASE_ENTITY_ID, womanDetails.get(DBConstantsUtils.KeyUtils.BASE_ENTITY_ID));
            contactSummaryFinishIntent.putExtra(ConstantsUtils.IntentKeyUtils.CLIENT_MAP, womanDetails);
            contactSummaryFinishIntent.putExtra(ConstantsUtils.IntentKeyUtils.CONTACT_NO,
                    Integer.valueOf(womanDetails.get(DBConstantsUtils.KeyUtils.NEXT_CONTACT)));
            if (isRefferal) {
                int contactNo = Integer.parseInt(womanDetails.get(DBConstantsUtils.KeyUtils.NEXT_CONTACT));
                if (contactNo < 0) {
                    contactSummaryFinishIntent.putExtra(ConstantsUtils.IntentKeyUtils.CONTACT_NO, Integer.valueOf(contactNo));
                } else {
                    contactSummaryFinishIntent.putExtra(ConstantsUtils.IntentKeyUtils.CONTACT_NO, Integer.valueOf("-" + contactNo));
                }
            } else {
                contactSummaryFinishIntent.putExtra(ConstantsUtils.IntentKeyUtils.CONTACT_NO,
                        Integer.valueOf(womanDetails.get(DBConstantsUtils.KeyUtils.NEXT_CONTACT)));
            }
            context.startActivity(contactSummaryFinishIntent);
        } catch (Exception e) {
            Timber.e(e);
        }

    }

    public static String fillTemplate(String stringValue, Facts facts) {
        String stringValueResult = stringValue;
        while (stringValueResult.contains("{")) {
            String key = stringValueResult.substring(stringValueResult.indexOf("{") + 1, stringValueResult.indexOf("}"));
            String value = processValue(key, facts);
            stringValueResult = stringValueResult.replace("{" + key + "}", value).replaceAll(", $", "").trim();
        }
        //Remove unnecessary commas by cleaning the returned string
        return cleanValueResult(stringValueResult);
    }

    private static String processValue(String key, Facts facts) {
        String value = "";
        if (facts.get(key) instanceof String) {
            value = facts.get(key);
            if ((key.equals(ConstantsUtils.PrescriptionUtils.NAUSEA_PHARMA) || key.equals(ConstantsUtils.PrescriptionUtils.ANTACID) || key.equals(ConstantsUtils.PrescriptionUtils.PENICILLIN) || key.equals(ConstantsUtils.PrescriptionUtils.ANTIBIOTIC) || key.equals(ConstantsUtils.PrescriptionUtils.IFA_MEDICATION) || key.equals(ConstantsUtils.PrescriptionUtils.VITA)
                    || key.equals(ConstantsUtils.PrescriptionUtils.MAG_CALC) || key.equals(ConstantsUtils.PrescriptionUtils.ALBEN_MEBEN) || key.equals(ConstantsUtils.PrescriptionUtils.PREP) || key.equals(ConstantsUtils.PrescriptionUtils.SP) || key.equals(ConstantsUtils.PrescriptionUtils.IFA) || key.equals(ConstantsUtils.PrescriptionUtils.ASPIRIN) || key.equals(ConstantsUtils.PrescriptionUtils.CALCIUM)) && (value != null && value.equals("0")))
                return ANCFormUtils.keyToValueConverter("");

            if (value != null && value.endsWith(OTHER_SUFFIX)) {
                Object otherValue = value.endsWith(OTHER_SUFFIX) ? facts.get(key + ConstantsUtils.SuffixUtils.OTHER) : "";
                value = otherValue != null ?
                        value.substring(0, value.lastIndexOf(",")) + ", " + otherValue.toString() + "]" :
                        value.substring(0, value.lastIndexOf(",")) + "]";

            }
        }

        // Cannot get value of the key
        else {

            // Try using attention flag to retrieve value
            if (facts.get("attention_flag_facts") instanceof String) {

                try {
                    String attentionFlag = facts.get("attention_flag_facts");
                    ObjectMapper mapper = new ObjectMapper();
                    Map<String,String> map = mapper.readValue(attentionFlag, Map.class);

                    if (map.get(key) instanceof String) {
                        return map.get(key);
                    }

                } catch (JsonProcessingException e) {

                }

            }

            // All means above are failed
            return key;

        }

        return ANCFormUtils.keyToValueConverter(value);
    }

    private static String cleanValueResult(String result) {
        List<String> nonEmptyItems = new ArrayList<>();
        for (String item : result.split(",")) {
            if (item.length() > 0 && StringUtils.isNotBlank(item)) {
                nonEmptyItems.add(item);
            }
        }
        //Get the first item that usually  has a colon and remove it form list, if list has one item append separator
        String itemLabel = "";
        if (!nonEmptyItems.isEmpty() && nonEmptyItems.get(0).contains(":")) {
            String[] separatedLabel = nonEmptyItems.get(0).split(":");
            itemLabel = separatedLabel[0];
            if (separatedLabel.length > 1) {
                nonEmptyItems.set(0, nonEmptyItems.get(0).split(":")[1]);
                if (StringUtils.isBlank(nonEmptyItems.get(0)))
                    nonEmptyItems.remove(0);
            }//replace with extracted value
        }
        if (!itemLabel.equals(StringUtils.join(nonEmptyItems.toArray(), ",").replace(":", "")))
            return itemLabel + (!TextUtils.isEmpty(itemLabel) ? ": " : "") + StringUtils.join(nonEmptyItems.toArray(), ",");
        else
            return itemLabel + ":";
    }

    public static void navigateToHomeRegister(Context context, boolean isRemote, Class<? extends BaseHomeRegisterActivity> homeRegisterActivityClass) {
        Intent intent = new Intent(context, homeRegisterActivityClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(ConstantsUtils.IntentKeyUtils.IS_REMOTE_LOGIN, isRemote);
        context.startActivity(intent);
    }

    public static void navigateToProfile(Context context, HashMap<String, String> patient) {
        Intent intent = new Intent(context, AncLibrary.getInstance().getActivityConfiguration().getProfileActivityClass());
        intent.putExtra(ConstantsUtils.IntentKeyUtils.BASE_ENTITY_ID, patient.get(DBConstantsUtils.KeyUtils.ID_LOWER_CASE));
        intent.putExtra(ConstantsUtils.IntentKeyUtils.CLIENT_MAP, patient);
        context.startActivity(intent);
    }

    public static String getColumnMapValue(CommonPersonObjectClient pc, String key) {
        return org.smartregister.util.Utils.getValue(pc.getColumnmaps(), key, false);
    }

    public static String getDBDateToday() {
        return (new LocalDate()).toString(SQLITE_DATE_DF);
    }

    public static ButtonAlertStatus getButtonAlertStatus(Map<String, String> details, Context context, boolean isProfile) {
        String contactStatus = details.get(DBConstantsUtils.KeyUtils.CONTACT_STATUS);

        String nextContactDate = details.get(DBConstantsUtils.KeyUtils.NEXT_CONTACT_DATE);
        String edd = details.get(DBConstantsUtils.KeyUtils.EDD);
        String alertStatus;
        Integer gestationAge = 0;
        if (StringUtils.isNotBlank(edd)) {
            gestationAge = Utils.getGestationAgeFromEDDate(edd);
            AlertRule alertRule = new AlertRule(gestationAge, nextContactDate);
            alertStatus =
                    StringUtils.isNotBlank(contactStatus) && ConstantsUtils.AlertStatusUtils.ACTIVE.equals(contactStatus) ?
                            ConstantsUtils.AlertStatusUtils.IN_PROGRESS : AncLibrary.getInstance().getAncRulesEngineHelper()
                            .getButtonAlertStatus(alertRule, ConstantsUtils.RulesFileUtils.ALERT_RULES);
        } else {
            alertStatus = StringUtils.isNotBlank(contactStatus) ? ConstantsUtils.AlertStatusUtils.IN_PROGRESS : "DEAD";
        }

        ButtonAlertStatus buttonAlertStatus = new ButtonAlertStatus();

        //Set text first
        String nextContactRaw = details.get(DBConstantsUtils.KeyUtils.NEXT_CONTACT);
        Integer nextContact = StringUtils.isNotBlank(nextContactRaw) ? Integer.parseInt(nextContactRaw) : 1;

        nextContactDate =
                StringUtils.isNotBlank(nextContactDate) ? Utils.reverseHyphenSeperatedValues(nextContactDate, "/") : null;

        buttonAlertStatus.buttonText = String.format(getDisplayTemplate(context, alertStatus, isProfile), nextContact, (nextContactDate != null ? nextContactDate :
                Utils.convertDateFormat(Calendar.getInstance().getTime(), Utils.CONTACT_DF)));

        alertStatus =
                Utils.processContactDoneToday(details.get(DBConstantsUtils.KeyUtils.LAST_CONTACT_RECORD_DATE), alertStatus);

        buttonAlertStatus.buttonAlertStatus = alertStatus;
        buttonAlertStatus.gestationAge = gestationAge;
        buttonAlertStatus.nextContact = nextContact;
        buttonAlertStatus.nextContactDate = nextContactDate;

        return buttonAlertStatus;
    }

    public static int getGestationAgeFromEDDate(String expectedDeliveryDate) {
        try {
            if (!"0".equals(expectedDeliveryDate) && expectedDeliveryDate.length() > 0) {
                LocalDate date = SQLITE_DATE_DF.withOffsetParsed().parseLocalDate(expectedDeliveryDate);
                LocalDate lmpDate = date.minusWeeks(ConstantsUtils.DELIVERY_DATE_WEEKS);
                Weeks weeks = Weeks.weeksBetween(lmpDate, LocalDate.now());
                return weeks.getWeeks();
            } else {
                return 0;
            }
        } catch (IllegalArgumentException e) {
            Timber.e(e, " --> getGestationAgeFromEDDate");
            return 0;
        }
    }

    public static String reverseHyphenSeperatedValues(String rawString, String outputSeparator) {
        if (StringUtils.isNotBlank(rawString)) {
            String resultString = rawString;
            String[] tokenArray = resultString.trim().split("-");
            ArrayUtils.reverse(tokenArray);
            resultString = StringUtils.join(tokenArray, outputSeparator);
            return resultString;
        }
        return "";
    }

    private static String getDisplayTemplate(Context context, String alertStatus, boolean isProfile) {
        String displayTemplate;
        if (StringUtils.isNotBlank(alertStatus) && !isProfile) {
            switch (alertStatus) {
                case ConstantsUtils.AlertStatusUtils.IN_PROGRESS:
                    displayTemplate = context.getString(R.string.contact_in_progress);
                    break;
                case ConstantsUtils.AlertStatusUtils.NOT_DUE:
                    displayTemplate = context.getString(R.string.contact_number_due);
                    break;
                default:
                    displayTemplate = context.getString(R.string.contact_weeks);
                    break;
            }
        } else {
            switch (alertStatus) {
                case ConstantsUtils.AlertStatusUtils.IN_PROGRESS:
                    displayTemplate = context.getString(R.string.contact_in_progress_no_break);
                    break;
                case ConstantsUtils.AlertStatusUtils.NOT_DUE:
                    displayTemplate = context.getString(R.string.contact_number_due_no_break);
                    break;
                default:
                    displayTemplate = context.getString(R.string.contact_weeks_no_break);
                    break;
            }
        }
        return displayTemplate;
    }

    public static String processContactDoneToday(String lastContactDate, String alertStatus) {
        String result = alertStatus;

        if (!TextUtils.isEmpty(lastContactDate)) {
            try {
                result = DateUtils.isToday(DB_DF.parse(lastContactDate).getTime()) ? ConstantsUtils.AlertStatusUtils.TODAY : alertStatus;
            } catch (ParseException e) {
                Timber.e(e, " --> processContactDoneToday");
            }
        }

        return result;
    }

    public static void processButtonAlertStatus(Context context, Button dueButton, ButtonAlertStatus buttonAlertStatus) {
        Utils.processButtonAlertStatus(context, dueButton, null, buttonAlertStatus);
    }

    public static void processButtonAlertStatus(Context context, Button dueButton, TextView contactTextView,
                                                ButtonAlertStatus buttonAlertStatus) {
        if (dueButton != null) {
            dueButton.setVisibility(View.VISIBLE);
            dueButton.setText(buttonAlertStatus.buttonText);
            dueButton.setTag(R.id.GESTATION_AGE, buttonAlertStatus.gestationAge);

            if (buttonAlertStatus.buttonAlertStatus != null) {
                switch (buttonAlertStatus.buttonAlertStatus) {
                    case ConstantsUtils.AlertStatusUtils.IN_PROGRESS:
                        dueButton.setBackground(context.getResources().getDrawable(R.drawable.contact_progress));
                        dueButton.setTextColor(context.getResources().getColor(R.color.black));
                        break;
                    case ConstantsUtils.AlertStatusUtils.DUE:
                        dueButton.setBackground(context.getResources().getDrawable(R.drawable.contact_due));
                        dueButton.setTextColor(context.getResources().getColor(R.color.white));
                        break;
                    case ConstantsUtils.AlertStatusUtils.OVERDUE:
                        dueButton.setBackground(context.getResources().getDrawable(R.drawable.contact_overdue));
                        dueButton.setTextColor(context.getResources().getColor(R.color.white));
                        break;
                    case ConstantsUtils.AlertStatusUtils.NOT_DUE:
                        dueButton.setBackground(context.getResources().getDrawable(R.drawable.contact_not_due));
                        dueButton.setTextColor(context.getResources().getColor(R.color.dark_grey));
                        break;
                    case ConstantsUtils.AlertStatusUtils.DELIVERY_DUE:
                        dueButton.setBackground(context.getResources().getDrawable(R.drawable.contact_due));
                        dueButton.setTextColor(context.getResources().getColor(R.color.white));
                        dueButton.setText(context.getString(R.string.contact_due_delivery));
                        break;
                    case ConstantsUtils.AlertStatusUtils.EXPIRED:
                        dueButton.setBackgroundColor(context.getResources().getColor(R.color.vaccine_red_bg_st));
                        dueButton.setTextColor(context.getResources().getColor(R.color.white));
                        dueButton.setText(context.getString(R.string.contact_due_delivery));
                        break;
                    case ConstantsUtils.AlertStatusUtils.TODAY:
                        if (contactTextView != null) {
                            contactTextView.setText(String.format(context.getString(R.string.contact_recorded_today),
                                    Utils.getTodayContact(String.valueOf(buttonAlertStatus.nextContact))));
                            contactTextView.setPadding(2, 2, 2, 2);
                        }
                        dueButton.setBackground(context.getResources().getDrawable(R.drawable.contact_disabled));
                        dueButton.setBackground(context.getResources().getDrawable(R.drawable.contact_disabled));
                        dueButton.setTextColor(context.getResources().getColor(R.color.dark_grey));
                        dueButton.setText(String.format(context.getString(R.string.contact_recorded_today_no_break),
                                Utils.getTodayContact(String.valueOf(buttonAlertStatus.nextContact))));
                        break;
                    default:
                        dueButton.setBackground(context.getResources().getDrawable(R.drawable.contact_due));
                        dueButton.setTextColor(context.getResources().getColor(R.color.white));
                        break;
                }

                if (contactTextView != null) {
                    contactTextView.setVisibility(View.GONE);
                    if (ConstantsUtils.AlertStatusUtils.TODAY.equals(buttonAlertStatus.buttonAlertStatus)) {
                        dueButton.setVisibility(View.GONE);
                        contactTextView.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    }

    public static Integer getTodayContact(String nextContact) {
        int todayContact = 1;
        try {
            todayContact = Integer.valueOf(nextContact) - 1;
        } catch (NumberFormatException nfe) {
            Timber.e(nfe, " --> getTodayContact");
        } catch (Exception e) {
            Timber.e(e, " --> getTodayContact");
        }

        return todayContact;
    }

    /***
     * Save to shared preference
     * @param sharedPref name of shared preference file
     * @param key key to persist
     * @param value value to persist
     */
    public static void saveToSharedPreference(String sharedPref, String key, String value) {
        SharedPreferences.Editor editor = DrishtiApplication.getInstance().getSharedPreferences(
                sharedPref, Context.MODE_PRIVATE).edit();
        editor.putString(key, value).apply();
    }

    /***
     * Save to shared preference
     * @param sharedPref name of shared preference file
     * @param key key used to retrieve the value
     */
    public static String readFromSharedPreference(String sharedPref, String key) {
        SharedPreferences sharedPreferences = DrishtiApplication.getInstance().getSharedPreferences(
                sharedPref, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, null);
    }

    /**
     * Checks if a table exists on the table. An {@link Exception} is expected to be thrown by the sqlite
     * database in case of anything weird such as the query being wrongly executed. This method is used
     * to determine critical operations such as migrations that if not done will case data corruption.
     * It is therefore safe to let the app crash instead of handling the error. So that the developer/user
     * can fix the issue before continuing with any other operations.
     *
     * @param sqliteDatabase
     * @param tableName
     * @return
     */
    public static boolean isTableExists(@NonNull SQLiteDatabase sqliteDatabase, @NonNull String tableName) {
        Cursor cursor = sqliteDatabase.rawQuery(
                "SELECT name FROM sqlite_master WHERE type='table' AND name='" + tableName + "'",
                null
        );

        int nameColumnIndex = cursor.getColumnIndexOrThrow("name");
        while (cursor.moveToNext()) {
            String name = cursor.getString(nameColumnIndex);

            if (name.equals(tableName)) {
                if (cursor != null) {
                    cursor.close();
                }

                return true;
            }
        }

        if (cursor != null) {
            cursor.close();
        }

        return false;
    }

    public static Boolean enableLanguageSwitching() {
        return AncLibrary.getInstance().getProperties().getPropertyBoolean(AncAppPropertyConstants.KeyUtils.LANGUAGE_SWITCHING_ENABLED);
    }

    public static String getDueCheckStrategy() {
        return getProperties(AncLibrary.getInstance().getApplicationContext()).getProperty(ConstantsUtils.Properties.DUE_CHECK_STRATEGY, "");
    }

    /***
     * Creates a map for repeating group fields and values
     * @param fields {@link JSONArray}
     * @param fieldName {@link String}
     * @return {@link HashMap}
     * @throws JSONException
     */
    public static HashMap<String, HashMap<String, String>> buildRepeatingGroupValues(@NonNull JSONArray fields, @NonNull String fieldName) throws JSONException {
        ArrayList<String> keysArrayList = new ArrayList<>();
        JSONObject jsonObject = JsonFormUtils.getFieldJSONObject(fields, fieldName);
        HashMap<String, HashMap<String, String>> repeatingGroupMap = new LinkedHashMap<>();
        if (jsonObject != null) {
            JSONArray jsonArray = jsonObject.optJSONArray(ANCJsonFormConstants.VALUE);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject valueField = jsonArray.optJSONObject(i);
                String fieldKey = valueField.optString(ANCJsonFormConstants.KEY);
                keysArrayList.add(fieldKey);
            }

            for (int k = 0; k < fields.length(); k++) {
                JSONObject valueField = fields.optJSONObject(k);
                String fieldKey = valueField.optString(ANCJsonFormConstants.KEY);
                String fieldValue = valueField.optString(ANCJsonFormConstants.VALUE);

                if (fieldKey.contains("_")) {
                    fieldKey = fieldKey.substring(0, fieldKey.lastIndexOf("_"));
                    if (keysArrayList.contains(fieldKey) && StringUtils.isNotBlank(fieldValue)) {
                        String fieldKeyId = valueField.optString(ANCJsonFormConstants.KEY).substring(fieldKey.length() + 1);
                        valueField.put(ANCJsonFormConstants.KEY, fieldKey);
                        HashMap<String, String> hashMap = repeatingGroupMap.get(fieldKeyId) == null ? new HashMap<>() : repeatingGroupMap.get(fieldKeyId);
                        hashMap.put(fieldKey, fieldValue);
                        repeatingGroupMap.put(fieldKeyId, hashMap);
                    }
                }
            }
        }
        return repeatingGroupMap;
    }

    /***
     * Creates contact visit event after each visit
     * @param formSubmissionIDs {@link List}
     * @param womanDetails {@link Map}
     * @param openTasks {@link String}
     * @return {@link Event}
     */
    public static Event createContactVisitEvent(@NonNull List<String> formSubmissionIDs,
                                                @NonNull Map<String, String> womanDetails,
                                                @Nullable String openTasks) {

        try {
            String contactNo = womanDetails.get(DBConstantsUtils.KeyUtils.NEXT_CONTACT);
            String contactStartDate = womanDetails.get(DBConstantsUtils.KeyUtils.VISIT_START_DATE);
            String baseEntityId = womanDetails.get(DBConstantsUtils.KeyUtils.BASE_ENTITY_ID);

            Event contactVisitEvent = (Event) new Event().withBaseEntityId(baseEntityId).withEventDate(new Date())
                    .withEventType(ConstantsUtils.EventTypeUtils.CONTACT_VISIT).withEntityType(DBConstantsUtils.CONTACT_ENTITY_TYPE)
                    .withFormSubmissionId(ANCJsonFormUtils.generateRandomUUIDString())
                    .withDateCreated(ANCJsonFormUtils.getContactStartDate(contactStartDate));

            String currentContactNo;

            if (womanDetails.get(ConstantsUtils.REFERRAL) == null) {
                currentContactNo = ConstantsUtils.CONTACT + " " + contactNo;
            } else {
                currentContactNo = ConstantsUtils.CONTACT + " " + womanDetails.get(ConstantsUtils.REFERRAL);
            }

            contactVisitEvent.addDetails(ConstantsUtils.CONTACT, currentContactNo);
            contactVisitEvent.addDetails(ConstantsUtils.FORM_SUBMISSION_IDS, formSubmissionIDs.toString());
            contactVisitEvent.addDetails(ConstantsUtils.OPEN_TEST_TASKS, openTasks);

            ANCJsonFormUtils.tagSyncMetadata(getAllSharedPreferences(), contactVisitEvent);

            PatientRepository.updateContactVisitStartDate(baseEntityId, null);//reset contact visit date

            return contactVisitEvent;

        } catch (NullPointerException e) {
            Timber.e(e, " --> createContactVisitEvent");
            return null;
        }

    }

    /***
     * Creates partial previous visit events for clients from the registration form
     * @param strGroup {@link String}
     * @param baseEntityId {@link String}
     * @throws JSONException
     */
    public static void createPreviousVisitFromGroup(@NonNull String strGroup, @NonNull String baseEntityId) throws JSONException {
        JSONObject jsonObject = new JSONObject(strGroup);
        Iterator<String> repeatingGroupKeys = jsonObject.keys();
        List<String> currentFormSubmissionIds = new ArrayList<>();

        int count = 0;

        while (repeatingGroupKeys.hasNext()) {

            ++count;

            JSONObject jsonSingleVisitObject = jsonObject.optJSONObject(repeatingGroupKeys.next());

            String contactDate = jsonSingleVisitObject.optString(ConstantsUtils.JsonFormKeyUtils.VISIT_DATE);

            Facts entries = new Facts();

            entries.put(ConstantsUtils.CONTACT_DATE, contactDate);

            HashMap<String, String> womanDetails = new HashMap<>();

            womanDetails.put(DBConstantsUtils.KeyUtils.NEXT_CONTACT, String.valueOf(count + 1));

            womanDetails.put(DBConstantsUtils.KeyUtils.BASE_ENTITY_ID, baseEntityId);

            Event contactVisitEvent = Utils.createContactVisitEvent(new ArrayList<>(), womanDetails, null);

            if (contactVisitEvent != null) {
                JSONObject factsJsonObject = new JSONObject(ANCJsonFormUtils.gson.toJson(entries.asMap()));
                Event event = Utils.addContactVisitDetails("", contactVisitEvent, null, factsJsonObject.toString());
                JSONObject eventJson = new JSONObject(ANCJsonFormUtils.gson.toJson(event));
                AncLibrary.getInstance().getEcSyncHelper().addEvent(baseEntityId, eventJson);
                currentFormSubmissionIds.add(event.getFormSubmissionId());
            }
        }

        long lastSyncTimeStamp = getAllSharedPreferences().fetchLastUpdatedAtDate(0);
        Date lastSyncDate = new Date(lastSyncTimeStamp);
        try {
            AncLibrary.getInstance().getClientProcessorForJava().processClient(AncLibrary.getInstance().getEcSyncHelper().getEvents(currentFormSubmissionIds));
            getAllSharedPreferences().saveLastUpdatedAtDate(lastSyncDate.getTime());
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    public static Event addContactVisitDetails(String attentionFlagsString, Event event,
                                               String referral, String currentContactState) {
        event.addDetails(ConstantsUtils.DetailsKeyUtils.ATTENTION_FLAG_FACTS, attentionFlagsString);
        if (currentContactState != null && referral == null) {
            event.addDetails(ConstantsUtils.DetailsKeyUtils.PREVIOUS_CONTACTS, currentContactState);
        }
        return event;
    }

    /**
     * Loads yaml files that contain rules for the profile displays
     *
     * @param filename {@link String}
     * @return
     * @throws IOException
     */
    public Iterable<Object> loadRulesFiles(String filename) throws IOException {
        return AncLibrary.getInstance().readYaml(filename);
    }

    /**
     * Creates the {@link Task} partial contact form.  This is done any time we update tasks.
     *
     * @param baseEntityId {@link String} - The patient base entity id
     * @param context      {@link Context} - application context
     * @param contactNo    {@link Integer} - the contact that the partial contact belongs in.
     * @param doneTasks    {@link List<Task>} - A list of all the done/completed tasks.
     */
    public void createTasksPartialContainer(String baseEntityId, Context context, int contactNo, List<Task> doneTasks) {
        try {
            if (contactNo > 0 && doneTasks != null && doneTasks.size() > 0) {
                JSONArray fields = createFieldsArray(doneTasks);

                ANCFormUtils ANCFormUtils = new ANCFormUtils();
                JSONObject jsonForm = ANCFormUtils.loadTasksForm(context);
                if (jsonForm != null) {
                    ANCFormUtils.updateFormFields(jsonForm, fields);
                }

                createAndPersistPartialContact(baseEntityId, contactNo, jsonForm);
            }
        } catch (JSONException e) {
            Timber.e(e, " --> createTasksPartialContainer");
        }
    }

    @NotNull
    private JSONArray createFieldsArray(List<Task> doneTasks) throws JSONException {
        JSONArray fields = new JSONArray();
        for (Task task : doneTasks) {
            JSONObject field = new JSONObject(task.getValue());
            fields.put(field);
        }
        return fields;
    }

    private void createAndPersistPartialContact(String baseEntityId, int contactNo, JSONObject jsonForm) {
        Contact contact = new Contact();
        contact.setJsonForm(String.valueOf(jsonForm));
        contact.setContactNumber(contactNo);
        contact.setFormName(ConstantsUtils.JsonFormUtils.ANC_TEST_TASKS);

        ANCFormUtils.persistPartial(baseEntityId, contact);
    }

    /**
     * Returns the Contact Tasks Repository {@link ContactTasksRepository}
     *
     * @return contactTasksRepository
     */
    public ContactTasksRepository getContactTasksRepositoryHelper() {
        return AncLibrary.getInstance().getContactTasksRepository();
    }

    @Nullable
    public String getManifestVersion(Context context) {
        if (StringUtils.isNotBlank(CoreLibrary.getInstance().context().allSharedPreferences().fetchManifestVersion())) {
            return context.getString(R.string.form_manifest_version, CoreLibrary.getInstance().context().allSharedPreferences().fetchManifestVersion());
        } else {
            return null;
        }
    }

    public void createSavePdf(Context context, List<YamlConfig> yamlConfigList, Facts facts) throws FileNotFoundException {

        String FILENAME = context.getResources().getString(R.string.contact_summary_data_file);
        String filePath = getAppPath(context) + FILENAME;

        if ((new File(filePath)).exists()) {
            (new File(filePath)).delete();
        }
        FileOutputStream fOut = new FileOutputStream(filePath);
        PdfWriter pdfWriter = new PdfWriter(fOut);
        PdfDocument pdfDocument = new PdfDocument(pdfWriter);
        Document layoutDocument = new Document(pdfDocument);


        addTitle(layoutDocument, context.getResources().getString(R.string.contact_summary_data, getTodaysDate()));


        for (YamlConfig yamlConfig : yamlConfigList) {
            addEmptyLine(layoutDocument, 1);
            addSubHeading(layoutDocument, processUnderscores(yamlConfig.getGroup()));

            List<YamlConfigItem> fields = yamlConfig.getFields();
            StringBuilder outputBuilder = new StringBuilder();
            for (YamlConfigItem yamlConfigItem : fields) {
                if (yamlConfigItem.isMultiWidget() != null && yamlConfigItem.isMultiWidget()) {
                    prefillInjectableFacts(facts, yamlConfigItem.getTemplate());
                }
                if (AncLibrary.getInstance().getAncRulesEngineHelper().getRelevance(facts, yamlConfigItem.getRelevance())) {
                    outputBuilder.append(Utils.fillTemplate(yamlConfigItem.getTemplate(), facts)).append("\n\n");
                }
            }
            String output = outputBuilder.toString();

            addParagraph(layoutDocument, HorizontalAlignment.LEFT, output);
        }


        layoutDocument.close();
        Toast.makeText(context, context.getResources().getString(R.string.pdf_saved_successfully) + filePath, Toast.LENGTH_SHORT).show();
    }

    private String processUnderscores(String string) {
        return string.replace("_", " ").toUpperCase();
    }

    private void prefillInjectableFacts(Facts facts, String template) {
        String[] relevanceToken = template.split(",");
        String key;
        for (String token : relevanceToken) {
            if (token.contains("{") && token.contains("}")) {
                key = token.substring(token.indexOf('{') + 1, token.indexOf('}'));
                if (facts.get(key) == null) {
                    facts.put(key, "");
                }
            }
        }
    }

    private void addParagraph(Document layoutDocument, HorizontalAlignment horizontalAlignment, String headerDetails) {
        layoutDocument.add(new Paragraph(headerDetails).setHorizontalAlignment(horizontalAlignment));
    }

    private final String getAppPath(Context context) {
        File dir = new File(Environment.getExternalStorageDirectory() + File.separator + context.getResources().getString(R.string.app_name) + File.separator);
        if (!dir.exists()) {
            dir.mkdir();
        }

        return dir.getPath() + File.separator;
    }

    private final void addTitle(Document layoutDocument, String text) {
        layoutDocument.add((new Paragraph(text)).setBold().setUnderline().setTextAlignment(TextAlignment.CENTER));
    }

    private final void addEmptyLine(Document layoutDocument, int number) {
        int i = 0;

        for (int j = number; i < j; ++i) {
            layoutDocument.add(new Paragraph(" "));
        }

    }

    private final void addSubHeading(Document layoutDocument, String text) {
        layoutDocument.add((new Paragraph(text)).setBold().setHorizontalAlignment(HorizontalAlignment.LEFT));
    }
}
