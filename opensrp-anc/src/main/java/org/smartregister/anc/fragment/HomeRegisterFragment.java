package org.smartregister.anc.fragment;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.MergeCursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;

import org.apache.commons.lang3.StringUtils;
import org.jeasy.rules.api.Facts;
import org.json.JSONObject;
import org.smartregister.anc.R;
import org.smartregister.anc.activity.HomeRegisterActivity;
import org.smartregister.anc.application.AncApplication;
import org.smartregister.anc.contract.RegisterFragmentContract;
import org.smartregister.anc.cursor.AdvancedMatrixCursor;
import org.smartregister.anc.domain.AttentionFlag;
import org.smartregister.anc.domain.YamlConfig;
import org.smartregister.anc.domain.YamlConfigItem;
import org.smartregister.anc.event.SyncEvent;
import org.smartregister.anc.helper.DBQueryHelper;
import org.smartregister.anc.presenter.RegisterFragmentPresenter;
import org.smartregister.anc.provider.RegisterProvider;
import org.smartregister.anc.util.Constants;
import org.smartregister.anc.util.DBConstants;
import org.smartregister.anc.util.FilePath;
import org.smartregister.anc.util.Utils;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.configurableviews.model.Field;
import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter;
import org.smartregister.domain.FetchStatus;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;
import org.smartregister.view.activity.BaseRegisterActivity;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Created by keyman on 26/06/2018.
 */

public class HomeRegisterFragment extends BaseRegisterFragment
        implements RegisterFragmentContract.View, SyncStatusBroadcastReceiver
        .SyncStatusListener {

    private static final String TAG = HomeRegisterFragment.class.getCanonicalName();

    public static final String CLICK_VIEW_NORMAL = "click_view_normal";
    public static final String CLICK_VIEW_ALERT_STATUS = "click_view_alert_status";
    public static final String CLICK_VIEW_SYNC = "click_view_sync";
    public static final String CLICK_VIEW_ATTENTION_FLAG = "click_view_attention_flag";
    private Utils utils = new Utils();

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
        final CommonPersonObjectClient pc = (CommonPersonObjectClient) view.getTag();

        if (view.getTag() != null && view.getTag(R.id.VIEW_ID) == CLICK_VIEW_NORMAL) {
            Utils.navigateToProfile(getActivity(), (HashMap<String, String>) pc.getColumnmaps());
        } else if (view.getTag() != null && view.getTag(R.id.VIEW_ID) == CLICK_VIEW_ALERT_STATUS) {
            if (Integer.valueOf(view.getTag(R.id.GESTATION_AGE).toString()) >= Constants.DELIVERY_DATE_WEEKS) {
                homeRegisterActivity.showRecordBirthPopUp((CommonPersonObjectClient) view.getTag());
            } else {
                String baseEntityId = Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.BASE_ENTITY_ID, false);

                if (StringUtils.isNotBlank(baseEntityId)) {
                    utils.proceedToContact(baseEntityId, (HashMap<String, String>) pc.getColumnmaps(), getActivity());
                }
            }

        } else if (view.getTag() != null && view.getTag(R.id.VIEW_ID) == CLICK_VIEW_ATTENTION_FLAG) {
            new AsyncTask<Void, Void, Void>() {
                private List<AttentionFlag> attentionFlagList = new ArrayList<>();

                @Override
                protected void onPreExecute() {

                    //  showProgressDialog("Saving contact progress...");
                }

                @Override
                protected Void doInBackground(Void... nada) {
                    try {

                        JSONObject jsonObject = new JSONObject(
                                AncApplication.getInstance().getDetailsRepository().getAllDetailsForClient(pc.getCaseId())
                                        .get(Constants.DETAILS_KEY.ATTENTION_FLAG_FACTS));

                        Facts facts = new Facts();


                        Iterator<String> keys = jsonObject.keys();

                        while (keys.hasNext()) {
                            String key = keys.next();
                            facts.put(key, jsonObject.get(key));
                        }

                        Iterable<Object> ruleObjects = AncApplication.getInstance().readYaml(FilePath.FILE.ATTENTION_FLAGS);

                        for (Object ruleObject : ruleObjects) {
                            YamlConfig attentionFlagConfig = (YamlConfig) ruleObject;

                            for (YamlConfigItem yamlConfigItem : attentionFlagConfig.getFields()) {

                                if (AncApplication.getInstance().getRulesEngineHelper()
                                        .getRelevance(facts, yamlConfigItem.getRelevance())) {

                                    attentionFlagList
                                            .add(new AttentionFlag(Utils.fillTemplate(yamlConfigItem.getTemplate(), facts),
                                                    attentionFlagConfig.getGroup().equals(Constants.ATTENTION_FLAG.RED)));

                                }

                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                    }

                    return null;
                }

                @Override
                protected void onPostExecute(Void result) {
                    // hideProgressDialog();
                    homeRegisterActivity.showAttentionFlagsDialog(attentionFlagList);

                }
            }.execute();

        } /*else if (view.getTag() != null && view.getTag(R.id.VIEW_ID) == CLICK_VIEW_SYNC) { // Need to implement move to catchment
                // TODO Move to catchment
            }*/ else if (view.getId() == R.id.filter_text_view) {
            homeRegisterActivity.switchToFragment(BaseRegisterActivity.SORT_FILTER_POSITION);
        }
    }


    @SuppressLint ("NewApi")
    @Override
    public void showNotFoundPopup(String whoAncId) {
        NoMatchDialogFragment
                .launchDialog((BaseRegisterActivity) Objects.requireNonNull(getActivity()), DIALOG_TAG, whoAncId);
    }

    @Override
    public void setUniqueID(String qrCode) {
        BaseRegisterActivity baseRegisterActivity = (BaseRegisterActivity) getActivity();
        android.support.v4.app.Fragment currentFragment = baseRegisterActivity.findFragmentByPosition(BaseRegisterActivity
                .ADVANCED_SEARCH_POSITION);
        ((AdvancedSearchFragment) currentFragment).getAncId().setText(qrCode);
    }

    @Override
    public void setAdvancedSearchFormData(HashMap<String, String> formData) {
        BaseRegisterActivity baseRegisterActivity = (BaseRegisterActivity) getActivity();
        android.support.v4.app.Fragment currentFragment = baseRegisterActivity.findFragmentByPosition(BaseRegisterActivity
                .ADVANCED_SEARCH_POSITION);
        ((AdvancedSearchFragment) currentFragment).setSearchFormData(formData);
    }

    @Override
    public void initializeAdapter(Set<org.smartregister.configurableviews.model.View> visibleColumns) {
        RegisterProvider registerProvider = new RegisterProvider(getActivity(), commonRepository(), visibleColumns,
                registerActionHandler, paginationViewHandler);
        clientAdapter = new RecyclerViewPaginatedAdapter(null, registerProvider, context().commonrepository(this.tablename));
        clientAdapter.setCurrentlimit(20);
        clientsView.setAdapter(clientAdapter);
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
    public void recalculatePagination(MergeCursor matrixCursor) {
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
}

