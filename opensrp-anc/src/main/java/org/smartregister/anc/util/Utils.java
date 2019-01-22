package org.smartregister.anc.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.rules.RuleConstant;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;
import org.jeasy.rules.api.Facts;
import org.joda.time.LocalDate;
import org.joda.time.Weeks;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.anc.BuildConfig;
import org.smartregister.anc.R;
import org.smartregister.anc.activity.BaseContactActivity;
import org.smartregister.anc.activity.ContactJsonFormActivity;
import org.smartregister.anc.activity.ContactSummaryFinishActivity;
import org.smartregister.anc.activity.MainContactActivity;
import org.smartregister.anc.application.AncApplication;
import org.smartregister.anc.domain.Contact;
import org.smartregister.anc.event.BaseEvent;
import org.smartregister.anc.model.BaseContactModel;
import org.smartregister.anc.model.ContactModel;
import org.smartregister.anc.model.PartialContact;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.view.activity.BaseRegisterActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by ndegwamartin on 14/03/2018.
 */

public class Utils extends org.smartregister.util.Utils {

    public static final SimpleDateFormat DB_DF = new SimpleDateFormat(Constants.SQLITE_DATE_TIME_FORMAT);
    public static final SimpleDateFormat CONTACT_DF = new SimpleDateFormat(Constants.CONTACT_DATE_FORMAT);
    public static final SimpleDateFormat CONTACT_SUMMARY_DF = new SimpleDateFormat(Constants.CONTACT_SUMMARY_DATE_FORMAT);
    private static final DateTimeFormatter SQLITE_DATE_DF = DateTimeFormat.forPattern(Constants.SQLITE_DATE_TIME_FORMAT);

    public static final ArrayList<String> ALLOWED_LEVELS;
    public static final String DEFAULT_LOCATION_LEVEL = "Health Facility";
    public static final String FACILITY = "Facility";
    public static final String HOME_ADDRESS = "Home Address";
    private static final String TAG = "Anc Utils";

    static {
        ALLOWED_LEVELS = new ArrayList<>();
        ALLOWED_LEVELS.add(DEFAULT_LOCATION_LEVEL);
        ALLOWED_LEVELS.add(FACILITY);
    }

    public static void saveLanguage(String language) {
        getAllSharedPreferences().saveLanguagePreference(language);
        setLocale(new Locale(language));
    }


    public static String getLanguage() {
        return getAllSharedPreferences().fetchLanguagePreference();
    }

    public static void setLocale(Locale locale) {
        Resources resources = AncApplication.getInstance().getApplicationContext().getResources();
        Configuration configuration = resources.getConfiguration();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLocale(locale);
            AncApplication.getInstance().getApplicationContext().createConfigurationContext(configuration);
        } else {
            configuration.locale = locale;
            resources.updateConfiguration(configuration, displayMetrics);
        }
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

    public static String convertDateFormat(Date date, SimpleDateFormat formatter) {

        return formatter.format(date);
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

    @Nullable
    public static int getAttributeDrawableResource(Context context, int attributeId) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(attributeId, typedValue, true);
        return typedValue.resourceId;
    }

    public static int getGestationAgeFromEDDate(String expectedDeliveryDate) {

        try {
            LocalDate date = SQLITE_DATE_DF.withOffsetParsed().parseLocalDate(expectedDeliveryDate);

            LocalDate lmpDate = date.minusWeeks(Constants.DELIVERY_DATE_WEEKS);

            Weeks weeks = Weeks.weeksBetween(lmpDate, LocalDate.now());
            return weeks.getWeeks();
        } catch (IllegalArgumentException e) {
            Log.e(TAG, e.getMessage(), e);
            return 0;
        }


    }

    public static int getProfileImageResourceIdentifier() {
        return R.drawable.avatar_woman;
    }

    public static String getBuildDate(Boolean isShortMonth) {
        String simpleDateFormat;
        if (isShortMonth) {
            simpleDateFormat =
                    new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(new Date(BuildConfig.BUILD_TIMESTAMP));
        } else {
            simpleDateFormat =
                    new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(new Date(BuildConfig.BUILD_TIMESTAMP));
        }
        return simpleDateFormat;
    }

    public static String reverseHyphenSeperatedValues(String rawString, String outputSeperator) {
        String resultString = rawString;
        String[] tokenArray = resultString.split("-");
        ArrayUtils.reverse(tokenArray);
        resultString = StringUtils.join(tokenArray, outputSeperator);

        return resultString;
    }

    public static List<String> getListFromString(String stringArray) {
        return new ArrayList<>(
                Arrays.asList(stringArray.substring(1, stringArray.length() - 1).replaceAll("\"", "").split(", ")));
    }


    public List<String> createExpansionPanelChildren(JSONArray jsonArray) throws JSONException {
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
        String[] strings = itemString.split(":");
        return strings.length > 1 ? strings[1] : strings[0];
    }

    /**
     * Check for the quick check form then finds whether it still has pending required fields, If it has pending fields if so
     * it redirects to the quick check page. If not pending required fields then it redirects to the main contact page
     *
     * @param baseEntityId       {@link String}
     * @param personObjectClient {@link CommonPersonObjectClient}
     * @param context            {@link Context}
     *
     * @author martinndegwa
     */
    public void proceedToContact(String baseEntityId, CommonPersonObjectClient personObjectClient, Context context) {

        try {

            Intent intent = new Intent(context.getApplicationContext(), ContactJsonFormActivity.class);

            Contact quickCheck = new Contact();
            quickCheck.setName(context.getResources().getString(R.string.quick_check));
            quickCheck.setFormName(Constants.JSON_FORM.ANC_QUICK_CHECK);
            quickCheck.setContactNumber(Integer.valueOf(personObjectClient.getDetails().get(DBConstants.KEY.NEXT_CONTACT)));
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
            BaseContactModel baseContactModel = new ContactModel();

            String locationId = AncApplication.getInstance().getContext().allSharedPreferences()
                    .getPreference(AllConstants.CURRENT_LOCATION_ID);

            JSONObject form =
                    ((ContactModel) baseContactModel).getFormAsJson(quickCheck.getFormName(), baseEntityId, locationId);

            String processedForm = ContactJsonFormUtils.getFormJsonCore(partialContactRequest, form).toString();

            if (hasPendingRequiredFields(new JSONObject(processedForm))) {
                intent.putExtra(Constants.JSON_FORM_EXTRA.JSON, processedForm);
                intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, quickCheck);
                intent.putExtra(Constants.INTENT_KEY.BASE_ENTITY_ID, partialContactRequest.getBaseEntityId());
                intent.putExtra(Constants.INTENT_KEY.CLIENT, personObjectClient);
                intent.putExtra(Constants.INTENT_KEY.FORM_NAME, partialContactRequest.getType());
                intent.putExtra(Constants.INTENT_KEY.CONTACT_NO, partialContactRequest.getContactNo());
                ((BaseRegisterActivity) context).startActivityForResult(intent, JsonFormUtils.REQUEST_CODE_GET_JSON);
            } else {
                intent = new Intent(context, MainContactActivity.class);
                intent.putExtra(Constants.INTENT_KEY.BASE_ENTITY_ID, baseEntityId);
                intent.putExtra(Constants.INTENT_KEY.CLIENT, personObjectClient);
                intent.putExtra(Constants.INTENT_KEY.FORM_NAME, partialContactRequest.getType());
                intent.putExtra(Constants.INTENT_KEY.CONTACT_NO,
                        Integer.valueOf(personObjectClient.getDetails().get(DBConstants.KEY.NEXT_CONTACT)));
                context.startActivity(intent);
            }


        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            Utils.showToast(context, "Error proceeding to contact for client " +
                    personObjectClient.getColumnmaps().get(DBConstants.KEY.FIRST_NAME));
        }
    }

    /**
     * Checks the pending required fields on the json forms and returns true|false
     *
     * @param object {@link JSONObject}
     *
     * @return true|false {@link Boolean}
     * @throws Exception
     * @author martinndegwa
     */
    private boolean hasPendingRequiredFields(JSONObject object) throws Exception {
        if (object != null) {
            Iterator<String> keys = object.keys();

            while (keys.hasNext()) {
                String key = keys.next();

                if (key.startsWith(RuleConstant.STEP)) {
                    JSONArray stepArray = object.getJSONObject(key).getJSONArray(JsonFormConstants.FIELDS);

                    for (int i = 0; i < stepArray.length(); i++) {
                        JSONObject fieldObject = stepArray.getJSONObject(i);
                        ContactJsonFormUtils.processSpecialWidgets(fieldObject);

                        boolean isRequiredField =
                                !fieldObject.getString(JsonFormConstants.TYPE).equals(JsonFormConstants.LABEL) &&
                                        fieldObject.has(JsonFormConstants.V_REQUIRED);

                        if (isRequiredField && fieldObject.has(JsonFormConstants.VALUE) && !TextUtils.isEmpty(
                                fieldObject.getString(JsonFormConstants.VALUE))) {//TO DO Remove/ Alter logical condition

                            return false;


                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * This finalizes the form and redirects you to the contact summary page for more confirmation of the data added
     *
     * @param context {@link Activity}
     *
     * @author martinndegwa
     */
    public static void finalizeForm(Activity context) {
        try {
            CommonPersonObjectClient pc =
                    (CommonPersonObjectClient) context.getIntent().getExtras().get(Constants.INTENT_KEY.CLIENT);

            Intent contactSummaryFinishIntent = new Intent(context, ContactSummaryFinishActivity.class);
            contactSummaryFinishIntent.putExtra(Constants.INTENT_KEY.BASE_ENTITY_ID, pc.getCaseId());
            contactSummaryFinishIntent.putExtra(Constants.INTENT_KEY.CLIENT, pc);
            contactSummaryFinishIntent.putExtra(Constants.INTENT_KEY.CONTACT_NO,
                    Integer.valueOf(pc.getDetails().get(DBConstants.KEY.NEXT_CONTACT)));
            context.startActivity(contactSummaryFinishIntent);
        } catch (Exception e) {
            Log.e(BaseContactActivity.class.getCanonicalName(), e.getMessage());
        }

    }

    public static String fillTemplate(String stringValue, Facts facts) {
        String stringValueResult = stringValue;
        while (stringValueResult.contains("{")) {
            String key = stringValueResult.substring(stringValueResult.indexOf("{") + 1, stringValueResult.indexOf("}"));
            String value = facts.get(key);
            stringValueResult = stringValueResult.replace("{" + key + "}", value != null ? value : "");
        }

        return stringValueResult;
    }
}
