package org.smartregister.anc.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.rules.RuleConstant;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.anc.R;
import org.smartregister.anc.activity.MainContactActivity;
import org.smartregister.anc.activity.ContactJsonFormActivity;
import org.smartregister.anc.activity.HomeRegisterActivity;
import org.smartregister.anc.activity.ProfileActivity;
import org.smartregister.anc.application.AncApplication;
import org.smartregister.anc.contract.RegisterFragmentContract;
import org.smartregister.anc.cursor.AdvancedMatrixCursor;
import org.smartregister.anc.domain.AttentionFlag;
import org.smartregister.anc.domain.Contact;
import org.smartregister.anc.event.SyncEvent;
import org.smartregister.anc.helper.DBQueryHelper;
import org.smartregister.anc.model.BaseContactModel;
import org.smartregister.anc.model.ContactModel;
import org.smartregister.anc.model.PartialContact;
import org.smartregister.anc.presenter.RegisterFragmentPresenter;
import org.smartregister.anc.provider.RegisterProvider;
import org.smartregister.anc.util.Constants;
import org.smartregister.anc.util.ContactJsonFormUtils;
import org.smartregister.anc.util.DBConstants;
import org.smartregister.anc.util.JsonFormUtils;
import org.smartregister.anc.util.Utils;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.configurableviews.model.Field;
import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter;
import org.smartregister.domain.FetchStatus;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;
import org.smartregister.view.activity.BaseRegisterActivity;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Created by keyman on 26/06/2018.
 */

public class HomeRegisterFragment extends BaseRegisterFragment implements RegisterFragmentContract.View, SyncStatusBroadcastReceiver
        .SyncStatusListener {

    private static final String TAG = HomeRegisterFragment.class.getCanonicalName();

    public static final String CLICK_VIEW_NORMAL = "click_view_normal";
    public static final String CLICK_VIEW_DOSAGE_STATUS = "click_view_dosage_status";
    public static final String CLICK_VIEW_SYNC = "click_view_sync";
    public static final String CLICK_VIEW_ATTENTION_FLAG = "click_view_attention_flag";

    @Override
    protected void initializePresenter() {
        if (getActivity() == null) {
            return;
        }

        String viewConfigurationIdentifier = ((BaseRegisterActivity) getActivity()).getViewIdentifiers().get(0);
        presenter = new RegisterFragmentPresenter(this, viewConfigurationIdentifier);
    }

    @Override
    protected String getMainCondition() {
        return DBQueryHelper.getHomePatientRegisterCondition();
    }

    @Override
    protected String getDefaultSortQuery() {
        return DBConstants.KEY.LAST_INTERACTED_WITH + " DESC";
    }

    @Override
    public void setupViews(View view) {
        super.setupViews(view);
        View filterText = view.findViewById(R.id.filter_text_view);
        if (filterText != null) {
            filterText.setOnClickListener(registerActionHandler);
        }

        // Due Button
        View contactButton = view.findViewById(R.id.due_button);
        if (contactButton != null) {
            contactButton.setOnClickListener(registerActionHandler);
        }

        //Risk view
        View attentionFlag = view.findViewById(R.id.risk_layout);
        if (attentionFlag != null) {
            attentionFlag.setOnClickListener(registerActionHandler);
        }
    }

    @Override
    protected void onViewClicked(View view) {


        if (getActivity() == null) {
            return;
        }

        final HomeRegisterActivity homeRegisterActivity = (HomeRegisterActivity) getActivity();

        if (view.getTag() != null && view.getTag(R.id.VIEW_ID) == CLICK_VIEW_NORMAL) {
            goToPatientDetailActivity((CommonPersonObjectClient) view.getTag(), false);
        } else if (view.getTag() != null && view.getTag(R.id.VIEW_ID) == CLICK_VIEW_DOSAGE_STATUS) {
            if (Integer.valueOf(view.getTag(R.id.GESTATION_AGE).toString()) >= Constants.DELIVERY_DATE_WEEKS) {
                homeRegisterActivity.showRecordBirthPopUp((CommonPersonObjectClient) view.getTag());
            } else {
                CommonPersonObjectClient pc = (CommonPersonObjectClient) view.getTag();
                String baseEntityId = org.smartregister.util.Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.BASE_ENTITY_ID, false);

                if (StringUtils.isNotBlank(baseEntityId)) {
                    proceedToContact(baseEntityId, pc);
                }
            }

        } else if (view.getTag() != null && view.getTag(R.id.VIEW_ID) == CLICK_VIEW_ATTENTION_FLAG) {
            //Temporary for testing UI , To remove for real dynamic data
            List<AttentionFlag> dummyAttentionFlags = Arrays.asList(new AttentionFlag[]{new AttentionFlag("Red Flag 1", true), new
                    AttentionFlag("Red Flag 2", true), new AttentionFlag("Yellow Flag 1", false), new AttentionFlag("Yellow Flag 2",
                    false)});
            homeRegisterActivity.showAttentionFlagsDialog(dummyAttentionFlags);
        } /*else if (view.getTag() != null && view.getTag(R.id.VIEW_ID) == CLICK_VIEW_SYNC) { // Need to implement move to catchment
                // TODO Move to catchment
            }*/ else if (view.getId() == R.id.filter_text_view) {
            homeRegisterActivity.switchToFragment(BaseRegisterActivity.SORT_FILTER_POSITION);
        }
    }


    @SuppressLint("NewApi")
    @Override
    public void showNotFoundPopup(String whoAncId) {
        NoMatchDialogFragment.launchDialog((BaseRegisterActivity) Objects.requireNonNull(getActivity()), DIALOG_TAG, whoAncId);
    }

    @Override
    public void setUniqueID(String qrCode) {
        BaseRegisterActivity baseRegisterActivity = (BaseRegisterActivity) getActivity();
        android.support.v4.app.Fragment currentFragment = baseRegisterActivity.findFragmentByPosition(BaseRegisterActivity
                .ADVANCED_SEARCH_POSITION);
        ((AdvancedSearchFragment) currentFragment).getAncId().setText(qrCode);
    }

    @Override
    public void initializeAdapter(Set<org.smartregister.configurableviews.model.View> visibleColumns) {
        RegisterProvider registerProvider = new RegisterProvider(getActivity(), commonRepository(), visibleColumns,
                registerActionHandler, paginationViewHandler);
        clientAdapter = new RecyclerViewPaginatedAdapter(null, registerProvider, context().commonrepository(this.tablename));
        clientAdapter.setCurrentlimit(20);
        clientsView.setAdapter(clientAdapter);
    }

    private void goToPatientDetailActivity(CommonPersonObjectClient patient, boolean launchDialog) {
        if (launchDialog) {
            Log.i(HomeRegisterFragment.TAG, patient.name);
        }

        Intent intent = new Intent(getActivity(), ProfileActivity.class);
        intent.putExtra(Constants.INTENT_KEY.BASE_ENTITY_ID, patient.getCaseId());
        startActivity(intent);
    }

    @Override
    protected void startRegistration() {
        ((HomeRegisterActivity) getActivity()).startFormActivity(Constants.JSON_FORM.ANC_REGISTER, null, null);
    }


    public void updateSortAndFilter(List<Field> filterList, Field sortField) {
        ((RegisterFragmentPresenter) presenter).updateSortAndFilter(filterList, sortField);
    }

    @Override
    public void onSyncInProgress(FetchStatus fetchStatus) {
        Utils.postEvent(new SyncEvent(fetchStatus));
    }

    @Override
    public void recalculatePagination(AdvancedMatrixCursor matrixCursor) {
        clientAdapter.setTotalcount(matrixCursor.getCount());
        Log.v("total count here", "" + clientAdapter.getTotalcount());
        clientAdapter.setCurrentlimit(20);
        if (clientAdapter.getTotalcount() > 0) {
            clientAdapter.setCurrentlimit(clientAdapter.getTotalcount());
        }
        clientAdapter.setCurrentoffset(0);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        final AdvancedMatrixCursor matrixCursor = ((RegisterFragmentPresenter) presenter).getMatrixCursor();
        if (!globalQrSearch || matrixCursor == null) {
            return super.onCreateLoader(id, args);
        } else {
            globalQrSearch = false;
            switch (id) {
                case LOADER_ID:
                    // Returns a new CursorLoader
                    return new CursorLoader(getActivity()) {
                        @Override
                        public Cursor loadInBackground() {
                            return matrixCursor;
                        }
                    };
                default:
                    // An invalid id was passed in
                    return null;
            }
        }
    }

    public void proceedToContact(String baseEntityId, CommonPersonObjectClient personObjectClient) {
        try {

            Intent intent = new Intent(getActivity(), ContactJsonFormActivity.class);

            Contact quickCheck = new Contact();
            quickCheck.setName(getString(R.string.quick_check));
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

            String locationId = AncApplication.getInstance().getContext().allSharedPreferences().getPreference(AllConstants.CURRENT_LOCATION_ID);

            JSONObject form = ((ContactModel) baseContactModel).getFormAsJson(quickCheck.getFormName(), baseEntityId, locationId);

            String processedForm = ContactJsonFormUtils.getFormJsonCore(partialContactRequest, form).toString();

            if (hasPendingRequiredFields(new JSONObject(processedForm))) {
                intent.putExtra(Constants.JSON_FORM_EXTRA.JSON, processedForm);
                intent.putExtra(JsonFormConstants.JSON_FORM_KEY.FORM, quickCheck);
                intent.putExtra(Constants.INTENT_KEY.BASE_ENTITY_ID, partialContactRequest.getBaseEntityId());
                intent.putExtra(Constants.INTENT_KEY.FORM_NAME, partialContactRequest.getType());
                intent.putExtra(Constants.INTENT_KEY.CONTACT_NO, partialContactRequest.getContactNo());
                startActivityForResult(intent, JsonFormUtils.REQUEST_CODE_GET_JSON);

            } else {
                intent = new Intent(getActivity(), MainContactActivity.class);
                intent.putExtra(Constants.INTENT_KEY.BASE_ENTITY_ID, baseEntityId);
                intent.putExtra(Constants.INTENT_KEY.CLIENT, personObjectClient);
                intent.putExtra(Constants.INTENT_KEY.FORM_NAME, partialContactRequest.getType());
                intent.putExtra(Constants.INTENT_KEY.CONTACT_NO, Integer.valueOf(personObjectClient.getDetails().get(DBConstants.KEY
                        .NEXT_CONTACT)));

                getActivity().startActivity(intent);
            }


        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            Utils.showToast(getActivity(), "Error proceeding to contact for client " + personObjectClient.getColumnmaps().get(DBConstants
                    .KEY.FIRST_NAME));
        }
    }

    private void changeBackIconInitialQuickCheck() {

    }


    private boolean hasPendingRequiredFields(JSONObject object) throws JSONException {
        if (object != null) {
            Iterator<String> keys = object.keys();

            while (keys.hasNext()) {
                String key = keys.next();

                if (key.startsWith(RuleConstant.STEP)) {
                    JSONArray stepArray = object.getJSONObject(key).getJSONArray(JsonFormConstants.FIELDS);

                    for (int i = 0; i < stepArray.length(); i++) {
                        JSONObject fieldObject = stepArray.getJSONObject(i);
                        ContactJsonFormUtils.processSpecialWidgets(fieldObject);
                        if (!fieldObject.getString(JsonFormConstants.TYPE).equals(JsonFormConstants.LABEL) && fieldObject.has
                                (JsonFormConstants.V_REQUIRED)) {

                            if (fieldObject.has(JsonFormConstants.VALUE) && !TextUtils.isEmpty(fieldObject.getString(JsonFormConstants
                                    .VALUE))) {//TO DO Remove/ Alter logical condition

                                return false;

                            }
                        }
                    }
                }
            }
        }
        return true;
    }
}

