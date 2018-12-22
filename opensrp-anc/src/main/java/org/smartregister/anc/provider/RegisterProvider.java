package org.smartregister.anc.provider;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.vijay.jsonwizard.views.CustomTextView;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.smartregister.anc.R;
import org.smartregister.anc.application.AncApplication;
import org.smartregister.anc.fragment.HomeRegisterFragment;
import org.smartregister.anc.rule.AlertRule;
import org.smartregister.anc.util.Constants;
import org.smartregister.anc.util.DBConstants;
import org.smartregister.anc.util.Utils;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.cursoradapter.RecyclerViewProvider;
import org.smartregister.view.contract.SmartRegisterClient;
import org.smartregister.view.contract.SmartRegisterClients;
import org.smartregister.view.dialog.FilterOption;
import org.smartregister.view.dialog.ServiceModeOption;
import org.smartregister.view.dialog.SortOption;
import org.smartregister.view.viewholder.OnClickFormLauncher;

import java.text.MessageFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Set;

import static org.smartregister.anc.util.Utils.DB_DF;
import static org.smartregister.anc.util.Utils.getName;

/**
 * Created by keyman on 26/06/2018.
 */

public class RegisterProvider implements RecyclerViewProvider<RegisterProvider.RegisterViewHolder> {

    private static final String TAG = RegisterProvider.class.getCanonicalName();
    private final LayoutInflater inflater;
    private Set<org.smartregister.configurableviews.model.View> visibleColumns;

    private View.OnClickListener onClickListener;
    private View.OnClickListener paginationClickListener;

    private Context context;
    private CommonRepository commonRepository;

    public RegisterProvider(Context context, CommonRepository commonRepository, Set visibleColumns, View.OnClickListener onClickListener, View.OnClickListener paginationClickListener) {

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.visibleColumns = visibleColumns;

        this.onClickListener = onClickListener;
        this.paginationClickListener = paginationClickListener;

        this.context = context;
        this.commonRepository = commonRepository;
    }

    @Override
    public void getView(Cursor cursor, SmartRegisterClient client, RegisterViewHolder viewHolder) {
        CommonPersonObjectClient pc = (CommonPersonObjectClient) client;
        if (visibleColumns.isEmpty()) {
            populatePatientColumn(pc, client, viewHolder);
            populateIdentifierColumn(pc, viewHolder);
            populateLastColumn(pc, viewHolder);

            return;
        }
    }

    @Override
    public void getFooterView(RecyclerView.ViewHolder viewHolder, int currentPageCount, int totalPageCount, boolean hasNext, boolean hasPrevious) {
        FooterViewHolder footerViewHolder = (FooterViewHolder) viewHolder;
        footerViewHolder.pageInfoView.setText(
                MessageFormat.format(context.getString(R.string.str_page_info), currentPageCount,
                        totalPageCount));

        footerViewHolder.nextPageView.setVisibility(hasNext ? View.VISIBLE : View.INVISIBLE);
        footerViewHolder.previousPageView.setVisibility(hasPrevious ? View.VISIBLE : View.INVISIBLE);

        footerViewHolder.nextPageView.setOnClickListener(paginationClickListener);
        footerViewHolder.previousPageView.setOnClickListener(paginationClickListener);
    }

    private void populatePatientColumn(CommonPersonObjectClient pc, SmartRegisterClient client, RegisterViewHolder viewHolder) {

        String firstName = Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.FIRST_NAME, true);
        String lastName = Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.LAST_NAME, true);
        String patientName = getName(firstName, lastName);

        fillValue(viewHolder.patientName, WordUtils.capitalize(patientName));

        String dobString = Utils.getDuration(Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.DOB, false));
        dobString = dobString.contains("y") ? dobString.substring(0, dobString.indexOf("y")) : dobString;
        fillValue((viewHolder.age), String.format(context.getString(R.string.age_text), dobString));


        String edd = Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.EDD, false);

        if (StringUtils.isNotBlank(edd)) {

            fillValue((viewHolder.ga), String.format(context.getString(R.string.ga_text), Utils.getGestationAgeFromEDDate(edd)));
        } else {

            fillValue((viewHolder.ga), "");
        }

        View patient = viewHolder.patientColumn;
        attachPatientOnclickListener(patient, client);


        View dueButton = viewHolder.dueButton;
        attachDosageOnclickListener(dueButton, client);


        View riskLayout = viewHolder.risk;
        attachRiskLayoutOnclickListener(riskLayout, client);
    }


    private void populateIdentifierColumn(CommonPersonObjectClient pc, RegisterViewHolder viewHolder) {
        String ancId = Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.ANC_ID, false);
        fillValue(viewHolder.ancId, String.format(context.getString(R.string.anc_id_text), ancId));
    }


    private void populateLastColumn(CommonPersonObjectClient pc, RegisterViewHolder viewHolder) {

        if (commonRepository != null) {
            CommonPersonObject commonPersonObject = commonRepository.findByBaseEntityId(pc.entityId());
            if (commonPersonObject != null) {
                viewHolder.sync.setVisibility(View.GONE);
                viewHolder.dueButton.setVisibility(View.VISIBLE);

                String contactStatus = getColumnMapValue(pc, DBConstants.KEY.CONTACT_STATUS);

                String nextContactDate = getColumnMapValue(pc, DBConstants.KEY.NEXT_CONTACT_DATE);
                String edd = org.smartregister.util.Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.EDD, false);
                String buttonAlertStatus;
                Integer gestationAge = 0;
                if (StringUtils.isNotBlank(edd)) {
                    gestationAge = Utils.getGestationAgeFromEDDate(edd);
                    AlertRule alertRule = new AlertRule(gestationAge, nextContactDate);
                    buttonAlertStatus = StringUtils.isNotBlank(contactStatus) ? Constants.ALERT_STATUS.IN_PROGRESS : AncApplication.getInstance().getRulesEngineHelper().getButtonAlertStatus(alertRule, Constants.RULES_FILE.ALERT_RULES);
                } else {
                    buttonAlertStatus = StringUtils.isNotBlank(contactStatus) ? Constants.ALERT_STATUS.IN_PROGRESS : "DEAD";
                }

                //Set text first
                String nextContact = getColumnMapValue(pc, DBConstants.KEY.NEXT_CONTACT);

                nextContactDate = StringUtils.isNotBlank(nextContactDate) ? Utils.reverseHyphenSeperatedValues(nextContactDate, "/") : null;
                viewHolder.dueButton.setText(String.format(context.getString(R.string.contact_weeks), StringUtils.isNotBlank(nextContact) ? nextContact : "1", nextContactDate != null ? nextContactDate : Utils.convertDateFormat(Calendar.getInstance().getTime(), Utils.CONTACT_DF)));
                viewHolder.dueButton.setTag(R.id.GESTATION_AGE, gestationAge);

                buttonAlertStatus = processContactDoneToday(getColumnMapValue(pc, DBConstants.KEY.LAST_CONTACT_RECORD_DATE), buttonAlertStatus);

                processButtonAlertStatus(viewHolder, buttonAlertStatus, nextContact);

            } else {
                viewHolder.dueButton.setVisibility(View.GONE);
                viewHolder.sync.setVisibility(View.VISIBLE);

                attachSyncOnclickListener(viewHolder.sync, pc);
            }
        }
    }

    private void processButtonAlertStatus(RegisterViewHolder viewHolder, String buttonAlertStatus, String nextContact) {

        switch (buttonAlertStatus) {
            case Constants.ALERT_STATUS.IN_PROGRESS:

                viewHolder.dueButton.setBackgroundColor(context.getResources().getColor(R.color.progress_orange));
                viewHolder.dueButton.setTextColor(context.getResources().getColor(R.color.white));
                break;
            case Constants.ALERT_STATUS.DUE:

                viewHolder.dueButton.setBackground(context.getResources().getDrawable(R.drawable.contact_due));
                viewHolder.dueButton.setTextColor(context.getResources().getColor(R.color.vaccine_blue_bg_st));
                break;
            case Constants.ALERT_STATUS.OVERDUE:

                viewHolder.dueButton.setBackgroundColor(context.getResources().getColor(R.color.vaccine_red_bg_st));
                viewHolder.dueButton.setTextColor(context.getResources().getColor(R.color.white));
                break;
            case Constants.ALERT_STATUS.NOT_DUE:

                viewHolder.dueButton.setBackground(context.getResources().getDrawable(R.drawable.contact_not_due));
                break;
            case Constants.ALERT_STATUS.DELIVERY_DUE:
                viewHolder.dueButton.setBackground(context.getResources().getDrawable(R.drawable.contact_due));
                viewHolder.dueButton.setTextColor(context.getResources().getColor(R.color.vaccine_blue_bg_st));
                viewHolder.dueButton.setText(context.getString(R.string.due_delivery));
                break;
            case Constants.ALERT_STATUS.EXPIRED:
                viewHolder.dueButton.setBackgroundColor(context.getResources().getColor(R.color.vaccine_red_bg_st));
                viewHolder.dueButton.setTextColor(context.getResources().getColor(R.color.white));
                viewHolder.dueButton.setText(context.getString(R.string.due_delivery));
                break;
            case Constants.ALERT_STATUS.TODAY:
                viewHolder.dueButton.setBackground(context.getResources().getDrawable(R.drawable.contact_completed_today));
                viewHolder.dueButton.setTextColor(context.getResources().getColor(R.color.dark_grey));

                SpannableStringBuilder ssb = new SpannableStringBuilder(String.format(context.getString(R.string.contact_recorded_today), getTodayContact(nextContact)));
                ssb.setSpan(new ImageSpan(context, R.drawable.ic_checked_green, DynamicDrawableSpan.ALIGN_BASELINE), 0, 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                viewHolder.dueButton.setText(ssb, TextView.BufferType.SPANNABLE);
                viewHolder.dueButton.setPadding(2,2,2,2);

                break;
            default:
                viewHolder.dueButton.setBackground(context.getResources().getDrawable(R.drawable.contact_due));
                viewHolder.dueButton.setTextColor(context.getResources().getColor(R.color.vaccine_blue_bg_st));
                break;

        }
    }

    private void attachSyncOnclickListener(View view, SmartRegisterClient client) {
        view.setOnClickListener(onClickListener);
        view.setTag(client);
        view.setTag(R.id.VIEW_ID, HomeRegisterFragment.CLICK_VIEW_SYNC);
    }

    private void attachPatientOnclickListener(View view, SmartRegisterClient client) {
        view.setOnClickListener(onClickListener);
        view.setTag(client);
        view.setTag(R.id.VIEW_ID, HomeRegisterFragment.CLICK_VIEW_NORMAL);
    }

    private void attachRiskLayoutOnclickListener(View view, SmartRegisterClient client) {
        view.setOnClickListener(onClickListener);
        view.setTag(client);
        view.setTag(R.id.VIEW_ID, HomeRegisterFragment.CLICK_VIEW_ATTENTION_FLAG);
    }

    /*
    private void adjustLayoutParams(View view, TextView details) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        view.setLayoutParams(params);

        params = details.getLayoutParams();
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        details.setLayoutParams(params);
    }
*/

    private void attachDosageOnclickListener(View view, SmartRegisterClient client) {
        view.setOnClickListener(onClickListener);
        view.setTag(client);
        view.setTag(R.id.VIEW_ID, HomeRegisterFragment.CLICK_VIEW_DOSAGE_STATUS);
    }

    @Override
    public SmartRegisterClients updateClients(FilterOption villageFilter, ServiceModeOption serviceModeOption, FilterOption searchFilter, SortOption sortOption) {
        return null;
    }

    @Override
    public void onServiceModeSelected(ServiceModeOption serviceModeOption) {//Implement Abstract Method
    }

    @Override
    public OnClickFormLauncher newFormLauncher(String formName, String entityId, String metaData) {
        return null;
    }

    @Override
    public LayoutInflater inflater() {
        return inflater;
    }

    @Override
    public RegisterViewHolder createViewHolder(ViewGroup parent) {
        View view = inflater.inflate(R.layout.register_home_list_row, parent, false);
        return new RegisterViewHolder(view);
    }

    @Override
    public RecyclerView.ViewHolder createFooterHolder(ViewGroup parent) {
        View view = inflater.inflate(R.layout.smart_register_pagination, parent, false);
        return new FooterViewHolder(view);
    }

    @Override
    public boolean isFooterViewHolder(RecyclerView.ViewHolder viewHolder) {
        return FooterViewHolder.class.isInstance(viewHolder);
    }


    public static void fillValue(TextView v, String value) {
        if (v != null)
            v.setText(value);

    }

    ////////////////////////////////////////////////////////////////
    // Inner classes
    ////////////////////////////////////////////////////////////////

    public class RegisterViewHolder extends RecyclerView.ViewHolder {
        public TextView patientName;
        public TextView age;
        public TextView ga;
        public TextView ancId;
        public TextView risk;
        public Button dueButton;
        public Button sync;
        public View patientColumn;

        public RegisterViewHolder(View itemView) {
            super(itemView);

            patientName = itemView.findViewById(R.id.patient_name);
            age = itemView.findViewById(R.id.age);
            ga = itemView.findViewById(R.id.ga);
            ancId = itemView.findViewById(R.id.anc_id);
            risk = itemView.findViewById(R.id.risk);
            dueButton = itemView.findViewById(R.id.due_button);
            sync = itemView.findViewById(R.id.sync);

            patientColumn = itemView.findViewById(R.id.patient_column);
        }
    }

    public class FooterViewHolder extends RecyclerView.ViewHolder {
        public TextView pageInfoView;
        public Button nextPageView;
        public Button previousPageView;

        public FooterViewHolder(View view) {
            super(view);

            nextPageView = view.findViewById(org.smartregister.R.id.btn_next_page);
            previousPageView = view.findViewById(org.smartregister.R.id.btn_previous_page);
            pageInfoView = view.findViewById(org.smartregister.R.id.txt_page_info);
        }
    }

    private String getColumnMapValue(CommonPersonObjectClient pc, String key) {
        return org.smartregister.util.Utils.getValue(pc.getColumnmaps(), key, false);
    }

    private String processContactDoneToday(String lastContactDate, String alertStatus) {
        String result = alertStatus;

        if (!TextUtils.isEmpty(lastContactDate)) {

            try {
                result = DateUtils.isToday(DB_DF.parse(lastContactDate).getTime()) ? Constants.ALERT_STATUS.TODAY : alertStatus;
            } catch (ParseException e) {
                Log.e(TAG, e.getMessage());
            }
        }

        return result;
    }

    private Integer getTodayContact(String nextContact) {
        Integer todayContact = 1;
        try {
            todayContact = Integer.valueOf(nextContact) - 1;

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        return todayContact;
    }
}
