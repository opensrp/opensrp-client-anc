package org.smartregister.anc.library.provider;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import org.apache.commons.lang3.text.WordUtils;
import org.smartregister.anc.library.R;
import org.smartregister.anc.library.fragment.HomeRegisterFragment;
import org.smartregister.anc.library.util.DBConstantsUtils;
import org.smartregister.anc.library.util.Utils;
import org.smartregister.commonregistry.CommonPersonObject;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.cursoradapter.RecyclerViewProvider;
import org.smartregister.helper.ImageRenderHelper;
import org.smartregister.view.contract.SmartRegisterClient;
import org.smartregister.view.contract.SmartRegisterClients;
import org.smartregister.view.dialog.FilterOption;
import org.smartregister.view.dialog.ServiceModeOption;
import org.smartregister.view.dialog.SortOption;
import org.smartregister.view.viewholder.OnClickFormLauncher;

import java.text.MessageFormat;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by keyman on 26/06/2018.
 */

public class AdvancedSearchProvider implements RecyclerViewProvider<AdvancedSearchProvider.AdvancedSearchViewHolder> {

    private final LayoutInflater inflater;
    private Set<org.smartregister.configurableviews.model.View> visibleColumns;

    private View.OnClickListener onClickListener;
    private View.OnClickListener paginationClickListener;

    private Context context;
    private CommonRepository commonRepository;
    private ImageRenderHelper imageRenderHelper;

    public AdvancedSearchProvider(Context context, CommonRepository commonRepository, Set visibleColumns,
                                  View.OnClickListener onClickListener, View.OnClickListener paginationClickListener) {

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.visibleColumns = visibleColumns;

        this.onClickListener = onClickListener;
        this.paginationClickListener = paginationClickListener;

        this.context = context;
        this.commonRepository = commonRepository;
        this.imageRenderHelper = new ImageRenderHelper(context);
    }

    @Override
    public void getView(Cursor cursor, SmartRegisterClient client, AdvancedSearchViewHolder viewHolder) {
        CommonPersonObjectClient pc = (CommonPersonObjectClient) client;
        if (visibleColumns.isEmpty()) {
            populatePatientColumn(pc, client, viewHolder);
            populateIdentifierColumn(pc, viewHolder);
            populateLastColumn(pc, viewHolder);
            profileImageClick(client, viewHolder);

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
        ConfigurableViewsLibrary.getInstance().getConfigurableViewsHelper().processRegisterColumns(mapping, convertView,
        visibleColumns, R.id.register_columns);
        */
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
    public SmartRegisterClients updateClients(FilterOption filterOption, ServiceModeOption serviceModeOption,
                                              FilterOption filterOption1, SortOption sortOption) {
        return null;
    }

    @Override
    public void onServiceModeSelected(ServiceModeOption serviceModeOption) { //Implement Abstract Method
    }

    @Override
    public OnClickFormLauncher newFormLauncher(String s, String s1, String s2) {
        return null;
    }

    @Override
    public LayoutInflater inflater() {
        return inflater;
    }

    @Override
    public AdvancedSearchProvider.AdvancedSearchViewHolder createViewHolder(ViewGroup parent) {
        View view;
        view = inflater.inflate(R.layout.advanced_result_list_row, parent, false);

        /*
        ConfigurableViewsHelper helper = ConfigurableViewsLibrary.getInstance().getConfigurableViewsHelper();
        if (helper.isJsonViewsEnabled()) {

            ViewConfiguration viewConfiguration = helper.getViewConfiguration(Constants.CONFIGURATION.HOME_REGISTER_ROW);
            ViewConfiguration commonConfiguration = helper.getViewConfiguration(COMMON_REGISTER_ROW);

            if (viewConfiguration != null) {
                return helper.inflateDynamicView(viewConfiguration, commonConfiguration, view, R.id.register_columns, false);
            }
        }*/

        return new AdvancedSearchViewHolder(view);
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
                                       AdvancedSearchViewHolder viewHolder) {

        String firstName = Utils.getValue(pc.getColumnmaps(), DBConstantsUtils.KeyUtils.FIRST_NAME, true);
        String lastName = Utils.getValue(pc.getColumnmaps(), DBConstantsUtils.KeyUtils.LAST_NAME, true);
        String patientName = Utils.getName(firstName, lastName);

        fillValue(viewHolder.patientName, WordUtils.capitalize(patientName));

        String dobString = Utils.getDuration(Utils.getValue(pc.getColumnmaps(), DBConstantsUtils.KeyUtils.DOB, false));
        dobString = dobString.contains("y") ? dobString.substring(0, dobString.indexOf("y")) : dobString;
        fillValue((viewHolder.age), String.format(context.getString(R.string.age_text), dobString));

        View patient = viewHolder.patientColumn;
        attachPatientOnclickListener(patient, client);
    }

    private void populateIdentifierColumn(CommonPersonObjectClient pc, AdvancedSearchViewHolder viewHolder) {
        String ancId = Utils.getValue(pc.getColumnmaps(), DBConstantsUtils.KeyUtils.ANC_ID, false);
        fillValue(viewHolder.ancId, String.format(context.getString(R.string.anc_id_text), ancId));
    }

    private void populateLastColumn(CommonPersonObjectClient pc, AdvancedSearchViewHolder viewHolder) {

        if (commonRepository != null) {
            CommonPersonObject commonPersonObject = commonRepository.findByBaseEntityId(pc.entityId());
            if (commonPersonObject != null) {
                imageRenderHelper
                        .refreshProfileImage(pc.entityId(), viewHolder.profile, Utils.getProfileImageResourceIdentifier());

                viewHolder.sync.setVisibility(View.GONE);
                viewHolder.profile.setVisibility(View.VISIBLE);
            } else {
                viewHolder.profile.setVisibility(View.GONE);
                viewHolder.sync.setVisibility(View.VISIBLE);
                attachSyncOnclickListener(viewHolder.sync, pc);
            }
        }
    }

    private void profileImageClick(SmartRegisterClient client, AdvancedSearchViewHolder viewHolder) {
        View patientImage = viewHolder.profile;
        attachPatientOnclickListener(patientImage, client);
    }

    public static void fillValue(TextView v, String value) {
        if (v != null) v.setText(value);

    }

    private void attachPatientOnclickListener(View view, SmartRegisterClient client) {
        view.setOnClickListener(onClickListener);
        view.setTag(client);
        view.setTag(R.id.VIEW_ID, HomeRegisterFragment.CLICK_VIEW_NORMAL);
    }

    private void attachSyncOnclickListener(View view, SmartRegisterClient client) {
        view.setOnClickListener(onClickListener);
        view.setTag(client);
        view.setTag(R.id.VIEW_ID, HomeRegisterFragment.CLICK_VIEW_SYNC);
    }

    ////////////////////////////////////////////////////////////////
    // Inner classes
    ////////////////////////////////////////////////////////////////

    public class AdvancedSearchViewHolder extends RecyclerView.ViewHolder {
        public TextView patientName;
        public TextView age;
        public TextView ga;
        public TextView ancId;
        public TextView risk;

        public CircleImageView profile;
        public Button sync;

        public View patientColumn;


        public AdvancedSearchViewHolder(View itemView) {
            super(itemView);

            patientName = itemView.findViewById(R.id.patient_name);
            age = itemView.findViewById(R.id.age);
            ga = itemView.findViewById(R.id.ga);
            ancId = itemView.findViewById(R.id.anc_id);
            risk = itemView.findViewById(R.id.risk);
            profile = itemView.findViewById(R.id.profile);
            sync = itemView.findViewById(R.id.sync);

            patientColumn = itemView.findViewById(R.id.patient_column);
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
