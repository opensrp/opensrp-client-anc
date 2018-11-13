package org.smartregister.anc.fragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.anc.R;
import org.smartregister.anc.activity.ContactActivity;
import org.smartregister.anc.activity.HomeRegisterActivity;
import org.smartregister.anc.activity.ProfileActivity;
import org.smartregister.anc.contract.RegisterFragmentContract;
import org.smartregister.anc.cursor.AdvancedMatrixCursor;
import org.smartregister.anc.event.SyncEvent;
import org.smartregister.anc.helper.DBQueryHelper;
import org.smartregister.anc.presenter.RegisterFragmentPresenter;
import org.smartregister.anc.provider.RegisterProvider;
import org.smartregister.anc.repository.PatientRepository;
import org.smartregister.anc.util.Constants;
import org.smartregister.anc.util.DBConstants;
import org.smartregister.anc.util.Utils;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.configurableviews.model.Field;
import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter;
import org.smartregister.domain.FetchStatus;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;
import org.smartregister.view.activity.BaseRegisterActivity;
import org.smartregister.view.fragment.BaseRegisterFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Created by keyman on 26/06/2018.
 */

public class HomeRegisterFragment extends BaseRegisterFragment implements RegisterFragmentContract.View, SyncStatusBroadcastReceiver.SyncStatusListener {

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

            CommonPersonObjectClient pc = (CommonPersonObjectClient) view.getTag();
            final String baseEntityId = org.smartregister.util.Utils.getValue(pc.getColumnmaps(), DBConstants.KEY.BASE_ENTITY_ID, false);

            Map<String, String> detMap = PatientRepository.getWomanProfileDetails(baseEntityId);

            final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(homeRegisterActivity);


            View attentionFlagDialogView = LayoutInflater.from(homeRegisterActivity).inflate(R.layout.alert_dialog_dummy_contact_details, null);
            dialogBuilder.setView(attentionFlagDialogView);

            final EditText contactNoText = attentionFlagDialogView.findViewById(R.id.next_contact_number);
            if (detMap.get(DBConstants.KEY.NEXT_CONTACT) != null)
                contactNoText.setText(detMap.get(DBConstants.KEY.NEXT_CONTACT));

            final EditText edittext = attentionFlagDialogView.findViewById(R.id.next_contact_date);
            if (detMap.get(DBConstants.KEY.NEXT_CONTACT_DATE) != null) {
                edittext.setText(Utils.reverseHyphenSeperatedValues(detMap.get(DBConstants.KEY.NEXT_CONTACT_DATE), "/"));
                edittext.setTag(detMap.get(DBConstants.KEY.NEXT_CONTACT_DATE));
            }


            final Calendar myCalendar = Calendar.getInstance();
            final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear,
                                      int dayOfMonth) {
                    // TODO Auto-generated method stub
                    myCalendar.set(Calendar.YEAR, year);
                    myCalendar.set(Calendar.MONTH, monthOfYear);
                    myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);


                    edittext.setText(new SimpleDateFormat("dd/MM/yyyy").format(myCalendar.getTime()));
                    edittext.setTag(new SimpleDateFormat(Constants.SQLITE_DATE_TIME_FORMAT).format(myCalendar.getTime()));
                }

            };


            edittext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new DatePickerDialog(homeRegisterActivity, date, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            });

            final EditText eddText = attentionFlagDialogView.findViewById(R.id.edd);
            if (detMap.get(DBConstants.KEY.EDD) != null) {
                eddText.setText(Utils.reverseHyphenSeperatedValues(detMap.get(DBConstants.KEY.EDD), "/"));
                eddText.setTag(detMap.get(DBConstants.KEY.EDD));
            }

            final DatePickerDialog.OnDateSetListener date2 = new DatePickerDialog.OnDateSetListener() {

                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear,
                                      int dayOfMonth) {
                    // TODO Auto-generated method stub
                    myCalendar.set(Calendar.YEAR, year);
                    myCalendar.set(Calendar.MONTH, monthOfYear);
                    myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);


                    eddText.setText(new SimpleDateFormat("dd/MM/yyyy").format(myCalendar.getTime()));
                    eddText.setTag(new SimpleDateFormat(Constants.SQLITE_DATE_TIME_FORMAT).format(myCalendar.getTime()));
                }

            };

            eddText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new DatePickerDialog(homeRegisterActivity, date2, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            });


            final AlertDialog alertDialog = dialogBuilder.create();
            attentionFlagDialogView.findViewById(R.id.ok_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    PatientRepository.updateContactVisitDetailsTemporary(baseEntityId, Integer.valueOf(contactNoText.getText().toString()), edittext.getTag().toString(), eddText.getTag().toString());


                    homeRegisterActivity.refreshList(FetchStatus.fetched);

                    alertDialog.dismiss();
                }
            });


            attentionFlagDialogView.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    alertDialog.dismiss();
                }
            });

            alertDialog.show();


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
        android.support.v4.app.Fragment currentFragment =
                baseRegisterActivity.findFragmentByPosition(BaseRegisterActivity.ADVANCED_SEARCH_POSITION);
        ((AdvancedSearchFragment) currentFragment).getAncId().setText(qrCode);
    }

    @Override
    public void initializeAdapter
            (Set<org.smartregister.configurableviews.model.View> visibleColumns) {
        RegisterProvider registerProvider = new RegisterProvider(getActivity(), commonRepository(), visibleColumns, registerActionHandler, paginationViewHandler);
        clientAdapter = new RecyclerViewPaginatedAdapter(null, registerProvider, context().commonrepository(this.tablename));
        clientAdapter.setCurrentlimit(20);
        clientsView.setAdapter(clientAdapter);
    }

    private void goToPatientDetailActivity(CommonPersonObjectClient patient,
                                           boolean launchDialog) {
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
        Intent intent = new Intent(getActivity(), ContactActivity.class);
        intent.putExtra(Constants.INTENT_KEY.BASE_ENTITY_ID, baseEntityId);
        intent.putExtra(Constants.INTENT_KEY.CLIENT, personObjectClient);
        getActivity().startActivity(intent);
    }


}

