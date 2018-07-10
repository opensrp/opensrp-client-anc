package org.smartregister.anc.provider;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.apache.commons.lang3.text.WordUtils;
import org.smartregister.anc.R;
import org.smartregister.anc.fragment.BaseRegisterFragment;
import org.smartregister.anc.util.Constants;
import org.smartregister.anc.util.DBConstants;
import org.smartregister.anc.util.Utils;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.configurableviews.ConfigurableViewsLibrary;
import org.smartregister.configurableviews.helper.ConfigurableViewsHelper;
import org.smartregister.cursoradapter.RecyclerViewProvider;
import org.smartregister.view.contract.SmartRegisterClient;
import org.smartregister.view.contract.SmartRegisterClients;
import org.smartregister.view.dialog.FilterOption;
import org.smartregister.view.dialog.ServiceModeOption;
import org.smartregister.view.dialog.SortOption;
import org.smartregister.view.viewholder.OnClickFormLauncher;

import java.util.Set;

import static org.smartregister.util.Utils.getName;

/**
 * Created by keyman on 26/06/2018.
 */

public class RegisterProvider implements RecyclerViewProvider<RegisterProvider.RegisterViewHolder> {
    private final LayoutInflater inflater;
    private Set<org.smartregister.configurableviews.model.View> visibleColumns;
    private View.OnClickListener onClickListener;
    private Context context;


    public RegisterProvider(Context context, Set visibleColumns, View.OnClickListener onClickListener) {

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.visibleColumns = visibleColumns;
        this.onClickListener = onClickListener;
        this.context = context;
    }

    @Override
    public void getView(Cursor cursor, SmartRegisterClient client, RegisterViewHolder convertView) {
        CommonPersonObjectClient pc = (CommonPersonObjectClient) client;
        if (visibleColumns.isEmpty()) {
            populatePatientColumn(pc, client, convertView);
            populateIdentifierColumn(pc, convertView);
            populateDoseColumn(pc, convertView);

            return;
        }

       /* for (org.smartregister.configurableviews.model.View columnView : visibleColumns) {
            switch (columnView.getIdentifier()) {
                case ID:
                    populatePatientColumn(pc, client, convertView);
                    break;
                case NAME:
                    populateIdentifierColumn(pc, convertView);
                    break;
                case DOSE:
                    populateDoseColumn(pc, convertView);
                    break;
                default:
            }
        }

        Map<String, Integer> mapping = new HashMap();
        mapping.put(ID, R.id.patient_column);
        mapping.put(DOSE, R.id.identifier_column);
        mapping.put(NAME, R.id.dose_column);
        ConfigurableViewsLibrary.getInstance().getConfigurableViewsHelper().processRegisterColumns(mapping, convertView, visibleColumns, R.id.register_columns);
        */
    }

    private void populatePatientColumn(CommonPersonObjectClient pc, SmartRegisterClient client, RegisterViewHolder viewHolder) {

        String firstName = org.smartregister.util.Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.FIRST_NAME, true);
        String lastName = org.smartregister.util.Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.LAST_NAME, true);
        String patientName = getName(firstName, lastName);

        fillValue(viewHolder.patientName, WordUtils.capitalize(patientName));

        String dobString = Utils.getDuration(org.smartregister.util.Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.DOB, false));
        dobString = dobString.contains("y") ? dobString.substring(0, dobString.indexOf("y")) : dobString;
        fillValue((viewHolder.age), String.format(context.getString(R.string.age_text), dobString));

        View patient = viewHolder.patientColumn;
        attachPatientOnclickListener(patient, client);
    }


    private void populateIdentifierColumn(CommonPersonObjectClient pc, RegisterViewHolder viewHolder) {
        String ancId = org.smartregister.util.Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.ANC_ID, false);
        fillValue(viewHolder.ancId, String.format(context.getString(R.string.anc_id_text), ancId));
    }


    private void populateDoseColumn(CommonPersonObjectClient pc, RegisterViewHolder viewHolder) {


        /*DoseStatus doseStatus = Utils.getCurrentDoseStatus(pc);

        Button patient = (Button) view.findViewById(R.id.dose_button);

        LinearLayout completeView = (LinearLayout) view.findViewById(R.id.completedView);

        if (StringUtils.isNotBlank(doseStatus.getDateDoseTwoGiven())) {
            patient.setVisibility(View.GONE);
            completeView.setVisibility(View.VISIBLE);
        } else {

            patient.setVisibility(View.VISIBLE);
            completeView.setVisibility(View.GONE);
            patient.setText(getDoseButtonText(doseStatus));
            patient.setBackground(Utils.getDoseButtonBackground(context, Utils.getRegisterViewButtonStatus(doseStatus)));
            patient.setTextColor(Utils.getDoseButtonTextColor(context, Utils.getRegisterViewButtonStatus(doseStatus)));
            attachDosageOnclickListener(patient, pc);
        } */
    }


    private void adjustLayoutParams(View view, TextView details) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        view.setLayoutParams(params);

        params = details.getLayoutParams();
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        details.setLayoutParams(params);
    }

    private void attachPatientOnclickListener(View view, SmartRegisterClient client) {
        view.setOnClickListener(onClickListener);
        view.setTag(client);
        view.setTag(R.id.VIEW_ID, BaseRegisterFragment.CLICK_VIEW_NORMAL);
    }

    private void attachDosageOnclickListener(View view, SmartRegisterClient client) {
        view.setOnClickListener(onClickListener);
        view.setTag(client);
        view.setTag(R.id.VIEW_ID, BaseRegisterFragment.CLICK_VIEW_DOSAGE_STATUS);
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
    public RegisterViewHolder createViewHolder() {
        View view;
        view = inflater.inflate(R.layout.register_home_list_row, null);
        ConfigurableViewsHelper helper = ConfigurableViewsLibrary.getInstance().getConfigurableViewsHelper();
        if (helper.isJsonViewsEnabled()) {
            /*
            ViewConfiguration viewConfiguration = helper.getViewConfiguration(Constants.CONFIGURATION.HOME_REGISTER_ROW);
            ViewConfiguration commonConfiguration = helper.getViewConfiguration(COMMON_REGISTER_ROW);

            if (viewConfiguration != null) {
                return helper.inflateDynamicView(viewConfiguration, commonConfiguration, view, R.id.register_columns, false);
            }*/
        }

        return new RegisterViewHolder(view);
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
        public View patientColumn;

        public RegisterViewHolder(View itemView) {
            super(itemView);

            patientName = itemView.findViewById(R.id.patient_name);
            age = itemView.findViewById(R.id.age);
            ga = itemView.findViewById(R.id.ga);
            ancId = itemView.findViewById(R.id.anc_id);
            risk = itemView.findViewById(R.id.risk);
            dueButton = itemView.findViewById(R.id.due_button);

            patientColumn = itemView.findViewById(R.id.patient_column);
        }
    }

}
