package org.smartregister.anc.library.fragment;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.anc.library.AncLibrary;
import org.smartregister.anc.library.R;
import org.smartregister.anc.library.activity.BaseHomeRegisterActivity;
import org.smartregister.anc.library.contract.RegisterFragmentContract;
import org.smartregister.anc.library.cursor.AdvancedMatrixCursor;
import org.smartregister.anc.library.event.SyncEvent;
import org.smartregister.anc.library.helper.DBQueryHelper;
import org.smartregister.anc.library.presenter.RegisterFragmentPresenter;
import org.smartregister.anc.library.provider.RegisterProvider;
import org.smartregister.anc.library.task.AttentionFlagsTask;
import org.smartregister.anc.library.util.ConstantsUtils;
import org.smartregister.anc.library.util.DBConstantsUtils;
import org.smartregister.anc.library.util.Utils;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.configurableviews.model.Field;
import org.smartregister.cursoradapter.RecyclerViewFragment;
import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.domain.FetchStatus;
import org.smartregister.job.DocumentConfigurationServiceJob;
import org.smartregister.job.SyncSettingsServiceJob;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;
import org.smartregister.view.activity.BaseRegisterActivity;
import org.smartregister.view.fragment.BaseRegisterFragment;
import org.smartregister.view.fragment.SecuredNativeSmartRegisterFragment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import timber.log.Timber;

/**
 * Created by keyman on 26/06/2018.
 */

public class HomeRegisterFragment extends BaseRegisterFragment implements RegisterFragmentContract.View, SyncStatusBroadcastReceiver.SyncStatusListener {
    public static final String CLICK_VIEW_NORMAL = "click_view_normal";
    public static final String CLICK_VIEW_ALERT_STATUS = "click_view_alert_status";
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
    public void setUniqueID(String qrCode) {
        BaseRegisterActivity baseRegisterActivity = (BaseRegisterActivity) getActivity();
        if (baseRegisterActivity != null) {
            Fragment currentFragment =
                    baseRegisterActivity.findFragmentByPosition(BaseRegisterActivity.ADVANCED_SEARCH_POSITION);
            if (currentFragment instanceof AdvancedSearchFragment) {
                ((AdvancedSearchFragment) currentFragment).getAncId().setText(qrCode);
            }
        }
    }

    @Override

    public void setAdvancedSearchFormData(HashMap<String, String> formData) {
        BaseRegisterActivity baseRegisterActivity = (BaseRegisterActivity) getActivity();
        if (baseRegisterActivity != null) {
            Fragment currentFragment =
                    baseRegisterActivity.findFragmentByPosition(BaseRegisterActivity.ADVANCED_SEARCH_POSITION);
            ((AdvancedSearchFragment) currentFragment).setSearchFormData(formData);
        }
    }

    @Override
    public void setupViews(View view) {
        super.setupViews(view);
        //Do not show filter button at the moment until all filters are implemented
        RelativeLayout filterSortRelativeLayout = view.findViewById(R.id.filter_sort_layout);
        if (filterSortRelativeLayout != null) {
            filterSortRelativeLayout.setVisibility(View.GONE);
        }

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
        View attentionFlag = view.findViewById(R.id.risk);
        if (attentionFlag != null) {
            attentionFlag.setOnClickListener(registerActionHandler);
        }
    }

    @Override
    protected void attachSyncButton(View view) {
        //Sync
        syncButton = view.findViewById(R.id.sync_refresh);
        if (syncButton != null) {
            syncButton.setOnClickListener(view1 -> {
                SyncSettingsServiceJob.scheduleJobImmediately(SyncSettingsServiceJob.TAG);
                DocumentConfigurationServiceJob.scheduleJobImmediately(DocumentConfigurationServiceJob.TAG);
            });
        }
    }

    @Override
    protected String getMainCondition() {
        return DBQueryHelper.getHomePatientRegisterCondition();
    }

    @Override
    protected String getDefaultSortQuery() {
        return DBConstantsUtils.KeyUtils.LAST_INTERACTED_WITH + " DESC";
    }

    @Override
    protected void startRegistration() {
        ((BaseHomeRegisterActivity) getActivity()).startFormActivity(ConstantsUtils.JsonFormUtils.ANC_REGISTER, null, "");
    }

    @Override
    protected void onViewClicked(View view) {
        if (getActivity() == null) {
            return;
        }

        final BaseHomeRegisterActivity baseHomeRegisterActivity = (BaseHomeRegisterActivity) getActivity();
        final CommonPersonObjectClient pc = (CommonPersonObjectClient) view.getTag();

        if (view.getTag() != null && view.getTag(R.id.VIEW_ID) == CLICK_VIEW_NORMAL) {
            Utils.navigateToProfile(getActivity(), (HashMap<String, String>) pc.getColumnmaps());
        } else if (view.getTag() != null && view.getTag(R.id.VIEW_ID) == CLICK_VIEW_ALERT_STATUS) {
            if (Integer.valueOf(view.getTag(R.id.GESTATION_AGE).toString()) >= ConstantsUtils.DELIVERY_DATE_WEEKS) {
                baseHomeRegisterActivity.showRecordBirthPopUp((CommonPersonObjectClient) view.getTag());
            } else {
                String baseEntityId = Utils.getValue(pc.getColumnmaps(), DBConstantsUtils.KeyUtils.BASE_ENTITY_ID, false);

                if (StringUtils.isNotBlank(baseEntityId)) {
                    Utils.proceedToContact(baseEntityId, (HashMap<String, String>) pc.getColumnmaps(), getActivity());
                }
            }
        } else if (view.getTag() != null && view.getTag(R.id.VIEW_ID) == CLICK_VIEW_ATTENTION_FLAG) {
            new AttentionFlagsTask(baseHomeRegisterActivity, pc).execute();
        } else if (view.getId() == R.id.filter_text_view) {
            baseHomeRegisterActivity.switchToFragment(BaseRegisterActivity.SORT_FILTER_POSITION);
        }
    }

    @Override
    public void onSyncInProgress(FetchStatus fetchStatus) {
        Utils.postEvent(new SyncEvent(fetchStatus));
    }

    @SuppressLint("NewApi")
    @Override
    public void showNotFoundPopup(String whoAncId) {
        NoMatchDialogFragment
                .launchDialog((BaseRegisterActivity) requireActivity(), SecuredNativeSmartRegisterFragment.DIALOG_TAG, whoAncId);
    }

    @Override
    public void initializeAdapter(Set<org.smartregister.configurableviews.model.View> visibleColumns) {
        RegisterProvider registerProvider =
                new RegisterProvider(getActivity(), commonRepository(), visibleColumns, registerActionHandler,
                        paginationViewHandler);
        clientAdapter = new RecyclerViewPaginatedAdapter(null, registerProvider, context().commonrepository(this.tablename));
        clientAdapter.setCurrentlimit(20);
        clientsView.setAdapter(clientAdapter);
    }

    @Override
    public void recalculatePagination(AdvancedMatrixCursor matrixCursor) {
        clientAdapter.setTotalcount(matrixCursor.getCount());
        Timber.tag("total count here").v("%d", clientAdapter.getTotalcount());
        clientAdapter.setCurrentlimit(20);
        if (clientAdapter.getTotalcount() > 0) {
            clientAdapter.setCurrentlimit(clientAdapter.getTotalcount());
        }
        clientAdapter.setCurrentoffset(0);
    }

    public void updateSortAndFilter(List<Field> filterList, Field sortField) {
        ((RegisterFragmentPresenter) presenter).updateSortAndFilter(filterList, sortField);
    }

    @Override
    public void countExecute() {
        try {
            String sql = AncLibrary.getInstance().getRegisterQueryProvider().getCountExecuteQuery(mainCondition, filters);
            Timber.i(sql);
            int totalCount = commonRepository().countSearchIds(sql);
            clientAdapter.setTotalcount(totalCount);
            Timber.i("Total Register Count %d", clientAdapter.getTotalcount());
            clientAdapter.setCurrentlimit(20);
            clientAdapter.setCurrentoffset(0);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        final AdvancedMatrixCursor matrixCursor = ((RegisterFragmentPresenter) presenter).getMatrixCursor();
        if (!globalQrSearch || matrixCursor == null) {
            if (id == LOADER_ID) {
                return new CursorLoader(getActivity()) {
                    @Override
                    public Cursor loadInBackground() {
                        String query = filterAndSortQuery();
                        return commonRepository().rawCustomQueryForAdapter(query);
                    }
                };
            } else {
                return null;
            }
        } else {
            globalQrSearch = false;
            if (id == RecyclerViewFragment.LOADER_ID) {// Returns a new CursorLoader
                return new CursorLoader(getActivity()) {
                    @Override
                    public Cursor loadInBackground() {
                        return matrixCursor;
                    }
                };
            }// An invalid id was passed in
            return null;
        }
    }

    private String filterAndSortQuery() {
        SmartRegisterQueryBuilder registerQueryBuilder = new SmartRegisterQueryBuilder(mainSelect);

        String query = "";
        try {
            if (isValidFilterForFts(commonRepository())) {
                String sql = AncLibrary.getInstance().getRegisterQueryProvider().getObjectIdsQuery(mainCondition, filters) + (StringUtils.isBlank(getDefaultSortQuery()) ? "" : " order by " + getDefaultSortQuery());
                sql = registerQueryBuilder.addlimitandOffset(sql, clientAdapter.getCurrentlimit(), clientAdapter.getCurrentoffset());
                List<String> ids = commonRepository().findSearchIds(sql);
                query = AncLibrary.getInstance().getRegisterQueryProvider().mainRegisterQuery() + " where _id IN (%s) " + (StringUtils.isBlank(getDefaultSortQuery()) ? "" : " order by " + getDefaultSortQuery());

                String joinedIds = "'" + StringUtils.join(ids, "','") + "'";
                return query.replace("%s", joinedIds);
            } else {
                if (!TextUtils.isEmpty(filters) && TextUtils.isEmpty(Sortqueries)) {
                    registerQueryBuilder.addCondition(filters);
                    query = registerQueryBuilder.orderbyCondition(Sortqueries);
                    query = registerQueryBuilder.Endquery(registerQueryBuilder.addlimitandOffset(query
                            , clientAdapter.getCurrentlimit()
                            , clientAdapter.getCurrentoffset()));
                }
                return query;
            }
        } catch (Exception e) {
            Timber.e(e);
        }

        return query;
    }
}

