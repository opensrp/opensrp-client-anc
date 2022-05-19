package org.smartregister.anc.library.provider;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.vijay.jsonwizard.views.CustomTextView;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;
import org.smartregister.anc.library.R;
import org.smartregister.anc.library.domain.ButtonAlertStatus;
import org.smartregister.anc.library.fragment.HomeRegisterFragment;
import org.smartregister.anc.library.util.DBConstantsUtils;
import org.smartregister.anc.library.util.Utils;
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
import java.util.Set;

/**
 * Created by keyman on 26/06/2018.
 */

public class RegisterProvider implements RecyclerViewProvider<RegisterProvider.RegisterViewHolder> {
    private final LayoutInflater inflater;
    private Set<org.smartregister.configurableviews.model.View> visibleColumns;

    private View.OnClickListener onClickListener;
    private View.OnClickListener paginationClickListener;

    private Context context;
    private CommonRepository commonRepository;

    public RegisterProvider(Context context, CommonRepository commonRepository, Set visibleColumns,
                            View.OnClickListener onClickListener, View.OnClickListener paginationClickListener) {

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
        }
    }

    @Override
    public void getFooterView(RecyclerView.ViewHolder viewHolder, int currentPageCount, int totalPageCount, boolean hasNext,
                              boolean hasPrevious) {
        FooterViewHolder footerViewHolder = (FooterViewHolder) viewHolder;
        footerViewHolder.pageInfoView
                .setText(MessageFormat.format(context.getString(R.string.str_page_info), currentPageCount, totalPageCount));

        footerViewHolder.nextPageView.setVisibility(hasNext ? View.VISIBLE : View.INVISIBLE);
        footerViewHolder.previousPageView.setVisibility(hasPrevious ? View.VISIBLE : View.INVISIBLE);

        footerViewHolder.nextPageView.setOnClickListener(paginationClickListener);
        footerViewHolder.previousPageView.setOnClickListener(paginationClickListener);
    }

    @Override
    public SmartRegisterClients updateClients(FilterOption villageFilter, ServiceModeOption serviceModeOption,
                                              FilterOption searchFilter, SortOption sortOption) {
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
        return viewHolder instanceof FooterViewHolder;
    }

    private void populatePatientColumn(CommonPersonObjectClient pc, SmartRegisterClient client,
                                       RegisterViewHolder viewHolder) {

        String firstName = Utils.getValue(pc.getColumnmaps(), DBConstantsUtils.KeyUtils.FIRST_NAME, true);
        String lastName = Utils.getValue(pc.getColumnmaps(), DBConstantsUtils.KeyUtils.LAST_NAME, true);
        String patientName = Utils.getName(firstName, lastName);

        fillValue(viewHolder.patientName, WordUtils.capitalize(patientName));

        String dobString = Utils.getDuration(Utils.getValue(pc.getColumnmaps(), DBConstantsUtils.KeyUtils.DOB, false));
        dobString = dobString.contains("y") ? dobString.substring(0, dobString.indexOf("y")) : dobString;
        fillValue((viewHolder.age), String.format(context.getString(R.string.age_text), dobString));


        String edd = Utils.getValue(pc.getColumnmaps(), DBConstantsUtils.KeyUtils.EDD, false);

        if (StringUtils.isNotBlank(edd)) {
            if(Utils.getGestationAgeFromEDDate(edd) > 40)
            {
                fillValue(viewHolder.ga, "");
            }
            else {
                fillValue((viewHolder.ga),
                        String.format(context.getString(R.string.ga_text), Utils.getGestationAgeFromEDDate(edd)));
                viewHolder.period.setVisibility(View.VISIBLE);
            }
        } else {

            fillValue((viewHolder.ga), "");
        }

        View patient = viewHolder.patientColumn;
        attachPatientOnclickListener(patient, client);


        View dueButton = viewHolder.dueButton;
        attachAlertButtonOnclickListener(dueButton, client);


        String redFlagCountRaw = Utils.getValue(pc.getColumnmaps(), DBConstantsUtils.KeyUtils.RED_FLAG_COUNT, false);
        String yellowFlagCountRaw = Utils.getValue(pc.getColumnmaps(), DBConstantsUtils.KeyUtils.YELLOW_FLAG_COUNT, false);

        int redFlagCount = !TextUtils.isEmpty(redFlagCountRaw) ? Integer.valueOf(redFlagCountRaw) : 0;
        int yellowFlagCount = !TextUtils.isEmpty(yellowFlagCountRaw) ? Integer.valueOf(yellowFlagCountRaw) : 0;
        int totalFlagCount = yellowFlagCount + redFlagCount;

        TextView riskLayout = viewHolder.risk;

        if (totalFlagCount > 0) {
            riskLayout.setCompoundDrawablesWithIntrinsicBounds(
                    redFlagCount > 0 ? R.drawable.ic_red_flag : R.drawable.ic_yellow_flag, 0, 0, 0);
            riskLayout.setText(String.valueOf(totalFlagCount));
            riskLayout.setVisibility(View.VISIBLE);

            attachRiskLayoutOnclickListener(riskLayout, client);
        } else {
            riskLayout.setVisibility(View.GONE);
        }
    }

    private void populateIdentifierColumn(CommonPersonObjectClient pc, RegisterViewHolder viewHolder) {
        String ancId = Utils.getValue(pc.getColumnmaps(), DBConstantsUtils.KeyUtils.ANC_ID, false);
        fillValue(viewHolder.ancId, String.format(context.getString(R.string.anc_id_text), ancId));
    }

    private void populateLastColumn(CommonPersonObjectClient pc, RegisterViewHolder viewHolder) {
        if (commonRepository != null) {
            CommonPersonObject commonPersonObject = commonRepository.findByBaseEntityId(pc.entityId());
            if (commonPersonObject != null) {
                viewHolder.sync.setVisibility(View.GONE);
                ButtonAlertStatus buttonAlertStatus =
                        Utils.getButtonAlertStatus(pc.getColumnmaps(), context, false);
                Utils.processButtonAlertStatus(context, viewHolder.dueButton, viewHolder.contactDoneTodayButton,
                        buttonAlertStatus);

            } else {
                viewHolder.dueButton.setVisibility(View.GONE);
                viewHolder.sync.setVisibility(View.VISIBLE);

                attachSyncOnclickListener(viewHolder.sync, pc);
            }
        }
    }

    public static void fillValue(TextView v, String value) {
        if (v != null) v.setText(value);

    }

    private void attachPatientOnclickListener(View view, SmartRegisterClient client) {
        view.setOnClickListener(onClickListener);
        view.setTag(client);
        view.setTag(R.id.VIEW_ID, HomeRegisterFragment.CLICK_VIEW_NORMAL);
    }

    private void attachAlertButtonOnclickListener(View view, SmartRegisterClient client) {
        view.setOnClickListener(onClickListener);
        view.setTag(client);
        view.setTag(R.id.VIEW_ID, HomeRegisterFragment.CLICK_VIEW_ALERT_STATUS);
    }

    private void attachRiskLayoutOnclickListener(View view, SmartRegisterClient client) {
        view.setOnClickListener(onClickListener);
        view.setTag(client);
        view.setTag(R.id.VIEW_ID, HomeRegisterFragment.CLICK_VIEW_ATTENTION_FLAG);
    }

    private void attachSyncOnclickListener(View view, SmartRegisterClient client) {
        view.setOnClickListener(onClickListener);
        view.setTag(client);
        view.setTag(R.id.VIEW_ID, HomeRegisterFragment.CLICK_VIEW_SYNC);
    }

    ////////////////////////////////////////////////////////////////
    // Inner classes
    ////////////////////////////////////////////////////////////////

    public class RegisterViewHolder extends RecyclerView.ViewHolder {
        private TextView patientName;
        private TextView age;
        private TextView period;
        private TextView ga;
        private TextView ancId;
        private TextView risk;
        private Button dueButton;
        private Button sync;
        private View patientColumn;
        private CustomTextView contactDoneTodayButton;

        public RegisterViewHolder(View itemView) {
            super(itemView);
            patientName = itemView.findViewById(R.id.patient_name);
            age = itemView.findViewById(R.id.age);
            ga = itemView.findViewById(R.id.ga);
            period = itemView.findViewById(R.id.period);
            ancId = itemView.findViewById(R.id.anc_id);
            risk = itemView.findViewById(R.id.risk);
            dueButton = itemView.findViewById(R.id.due_button);
            sync = itemView.findViewById(R.id.sync);
            patientColumn = itemView.findViewById(R.id.patient_column);
            contactDoneTodayButton = itemView.findViewById(R.id.contact_today_text);
        }
    }

    public class FooterViewHolder extends RecyclerView.ViewHolder {
        private TextView pageInfoView;
        private Button nextPageView;
        private Button previousPageView;

        public FooterViewHolder(View view) {
            super(view);

            nextPageView = view.findViewById(org.smartregister.R.id.btn_next_page);
            previousPageView = view.findViewById(org.smartregister.R.id.btn_previous_page);
            pageInfoView = view.findViewById(org.smartregister.R.id.txt_page_info);
        }
    }
}
