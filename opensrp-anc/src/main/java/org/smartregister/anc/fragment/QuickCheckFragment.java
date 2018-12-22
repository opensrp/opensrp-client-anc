package org.smartregister.anc.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.smartregister.anc.R;
import org.smartregister.anc.activity.ContactActivity;
import org.smartregister.anc.application.AncApplication;
import org.smartregister.anc.contract.QuickCheckContract;
import org.smartregister.anc.model.PartialContact;
import org.smartregister.anc.presenter.QuickCheckPresenter;
import org.smartregister.anc.util.Constants;
import org.smartregister.anc.util.Utils;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.clientandeventmodel.Obs;
import org.smartregister.configurableviews.model.Field;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuickCheckFragment extends DialogFragment implements QuickCheckContract.View {

    private static String TAG = QuickCheckFragment.class.getCanonicalName();

    private QuickCheckDialogClickListener actionHandler = new QuickCheckDialogClickListener();

    private QuickCheckContract.Presenter presenter;

    private ComplaintDangerAdapter complaintAdapter;
    private ComplaintDangerAdapter dangerSignAdapter;

    private View complaintLayout;
    private EditText specifyEditText;

    private View dangerSignLayout;

    private View navigationLayout;

    private Button refer;

    private Map<String, List<Object>> obsMap = new HashMap<>();

    private static final Type EVENT_TYPE = new TypeToken<Event>() {
    }.getType();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.AncFullScreenDialog);

        initializePresenter();

        initializeEvent();

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new Dialog(getActivity(), getTheme()) {
            @Override
            public void onBackPressed() {
                confirmClose();
            }
        };
    }

    public static void launchDialog(Activity activity,
                                    String dialogTag) {
        QuickCheckFragment dialogFragment = new QuickCheckFragment();

        Bundle args = activity.getIntent().getExtras();
        if (args == null) {
            args = new Bundle();
        }
        dialogFragment.setArguments(args);

        FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
        android.app.Fragment prev = activity.getFragmentManager().findFragmentByTag(dialogTag);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        dialogFragment.show(ft, dialogTag);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.fragment_quick_check,
                container, false);

        Bundle args = getArguments();
        String baseEntityId = args.getString(Constants.INTENT_KEY.BASE_ENTITY_ID);
        presenter.setBaseEntityId(baseEntityId);

        Integer contactNo = args.getInt(Constants.INTENT_KEY.CONTACT_NO);
        presenter.setContactNumber(contactNo);

        updateReasonList(view);
        updateComplaintList(view);
        updateDangerList(view);

        setupViews(view);

        setupBackButtonIcon(view);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog == null) {
            return;
        }

        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
    }

    private void updateReasonList(final View view) {

        RecyclerView recyclerView = view.findViewById(R.id.reason_recycler_view);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        ReasonAdapter reasonAdapter = new ReasonAdapter();
        recyclerView.setAdapter(reasonAdapter);
    }


    private void updateComplaintList(final View view) {

        RecyclerView recyclerView = view.findViewById(R.id.complaint_recycler_view);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        complaintAdapter = new ComplaintDangerAdapter(false);
        recyclerView.setAdapter(complaintAdapter);

    }

    private void updateDangerList(final View view) {


        RecyclerView recyclerView = view.findViewById(R.id.danger_sign_recycler_view);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        dangerSignAdapter = new ComplaintDangerAdapter(true);
        recyclerView.setAdapter(dangerSignAdapter);
    }

    private void setupViews(View view) {
        View cancel = view.findViewById(R.id.cancel_quick_check);
        cancel.setOnClickListener(actionHandler);

        complaintLayout = view.findViewById(R.id.complaint_layout);

        specifyEditText = view.findViewById(R.id.specify);
        specifyEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    Utils.hideKeyboard(getActivity(), v);
                } else {
                    Field otherSpecify = complaintAdapter.getSpecifyField();
                    if (otherSpecify != null) {
                        presenter.modifyComplaintsOrDangerList(otherSpecify, true, false);
                        try {
                            complaintAdapter.notifyDataSetChanged();
                        } catch (Exception e) {
                            Log.e(TAG, e.getMessage());
                        }
                    }
                }
            }
        });

        specifyEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                //Overriden
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
                //Overriden
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                Field otherSpecify = complaintAdapter.getSpecifyField();

                if (s.length() > 0 && otherSpecify != null) {
                    presenter.modifyComplaintsOrDangerList(otherSpecify, true, false);
                    try {
                        complaintAdapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                    }
                }

            }
        });

        specifyEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT ||
                        (event.getAction() == KeyEvent.ACTION_DOWN &&
                                event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    Utils.hideKeyboard(getActivity(), v);
                    return true;
                }
                return false;
            }
        });

        dangerSignLayout = view.findViewById(R.id.danger_sign_layout);
        navigationLayout = view.findViewById(R.id.navigation_layout);

        Button proceed = view.findViewById(R.id.proceed);
        proceed.setOnClickListener(actionHandler);

        refer = view.findViewById(R.id.refer);
        refer.setOnClickListener(actionHandler);

        setupComplaintOtherEditText();
    }

    private void setupComplaintOtherEditText() {
        if (specifyEditText.getVisibility() == View.VISIBLE && obsMap.containsKey("specific_complaint_other") && obsMap.get("specific_complaint_other").size() > 0) {
            specifyEditText.setText(obsMap.get("specific_complaint_other").get(0).toString());

        }
    }

    @Override
    public void displayComplaintLayout() {
        if (complaintLayout != null && complaintLayout.getVisibility() != View.VISIBLE) {
            complaintLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void hideComplaintLayout() {
        if (complaintLayout != null && complaintLayout.getVisibility() != View.GONE) {
            complaintLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void notifyComplaintAdapter() {
        try {
            if (complaintAdapter != null) {
                complaintAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void notifyDangerSignAdapter() {
        try {
            if (dangerSignAdapter != null) {
                dangerSignAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void displayDangerSignLayout() {
        if (dangerSignLayout != null && dangerSignLayout.getVisibility() != View.VISIBLE) {
            dangerSignLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void displayNavigationLayout() {
        if (navigationLayout != null && navigationLayout.getVisibility() != View.VISIBLE) {
            navigationLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void displayReferButton() {
        if (refer != null && refer.getVisibility() != View.VISIBLE) {
            refer.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void hideReferButton() {
        if (refer != null && refer.getVisibility() != View.GONE) {
            refer.setVisibility(View.GONE);
        }
    }

    @Override
    public void enableSpecifyEditText() {
        if (specifyEditText != null) {
            specifyEditText.requestFocus();
        }
    }

    @Override
    public void disableSpecifyEditText() {
        if (specifyEditText != null) {
            specifyEditText.clearFocus();
        }
    }

    @Override
    public void displayToast(int resourceId) {
        Utils.showShortToast(getActivity(), getActivity().getString(resourceId));
    }

    @Override
    public Context getContext() {
        return getActivity();
    }

    @Override
    public void proceedToContact(String baseEntityId, Integer contactNo) {
        Intent intent = new Intent(getActivity(), ContactActivity.class);
        intent.putExtra(Constants.INTENT_KEY.BASE_ENTITY_ID, baseEntityId);
        intent.putExtra(Constants.INTENT_KEY.CONTACT_NO, contactNo);
        getActivity().startActivity(intent);
    }

    private void initializePresenter() {
        presenter = new QuickCheckPresenter(this);
    }

    private void displayReferralDialog() {

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.alert_referral_dialog, null);

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);

        Button yes = view.findViewById(R.id.refer_yes);
        final Button no = view.findViewById(R.id.refer_no);

        final AlertDialog dialog = builder.create();

        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams param = window.getAttributes();
            param.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
            window.setAttributes(param);
            window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.referAndCloseContact(getSpecifyText(), true);
                dialog.dismiss();
            }
        });

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.referAndCloseContact(getSpecifyText(), false);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private String getSpecifyText() {
        return specifyEditText != null ? specifyEditText.getText().toString() : null;
    }

    private void confirmClose() {
        if (!obsMap.isEmpty()) {

            presenter.proceedToNormalContact(getSpecifyText());
        } else {


            AlertDialog dialog = new AlertDialog.Builder(getActivity(), R.style.AncAlertDialog)
                    .setTitle(com.vijay.jsonwizard.R.string.confirm_form_close)
                    .setMessage(com.vijay.jsonwizard.R.string.confirm_form_close_explanation)
                    .setNegativeButton(com.vijay.jsonwizard.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dismiss();
                        }
                    })
                    .setPositiveButton(com.vijay.jsonwizard.R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create();

            dialog.show();
        }

    }

    ////////////////////////////////////////////////////////////////
    // Inner classes
    ////////////////////////////////////////////////////////////////

    private class QuickCheckDialogClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.cancel_quick_check:
                    confirmClose();
                    break;
                case R.id.proceed:
                    presenter.proceedToNormalContact(getSpecifyText());
                    break;
                case R.id.refer:
                    displayReferralDialog();
                    break;
                default:
                    break;
            }
        }
    }


    private class ReasonAdapter extends RecyclerView.Adapter<ReasonAdapter.ViewHolder> {
        private List<Field> reasons;
        private CheckedTextView lastChecked;
        private Map<String, String> obReasons = new HashMap<>();

        public class ViewHolder extends RecyclerView.ViewHolder {
            private CheckedTextView checkedTextView;

            private ViewHolder(CheckedTextView v) {
                super(v);
                checkedTextView = v;
            }
        }

        private ReasonAdapter() {
            this.reasons = presenter.getConfig().getReasons();

            List<Object> observation = obsMap.get("contact_reason");

            if (observation != null) {
                for (Object ob : observation) {
                    obReasons.put("contact_reason", ob.toString());
                }
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                             int viewType) {
            CheckedTextView v = (CheckedTextView) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.quick_check_reason_item, parent, false);

            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Field reason = reasons.get(position);
            holder.checkedTextView.setText(reason.getDisplayName());
            holder.checkedTextView.setTag(reason);

            holder.checkedTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    CheckedTextView checkedTextView = (CheckedTextView) v;
                    if (lastChecked != null) {
                        if (lastChecked.equals(checkedTextView)) {
                            return;
                        } else {
                            lastChecked.setChecked(false);
                        }
                    }
                    lastChecked = checkedTextView;
                    lastChecked.setChecked(true);

                    Object tag = v.getTag();
                    if (tag != null && tag instanceof Field) {
                        Field currentField = (Field) tag;
                        presenter.setReason(currentField);
                    }
                }
            });

            if (!obReasons.isEmpty() && obReasons.containsValue(reason.getDisplayName())) {
                holder.checkedTextView.callOnClick();
            }

        }


        @Override
        public int getItemCount() {
            return reasons.size();
        }

    }

    private class ComplaintDangerAdapter extends RecyclerView.Adapter<ComplaintDangerAdapter.ViewHolder> {
        private List<Field> list;
        private boolean isDangerSign;
        private Map<String, String> obReasons = new HashMap<>();

        public class ViewHolder extends RecyclerView.ViewHolder {
            public CheckedTextView checkedTextView;

            public ViewHolder(CheckedTextView v) {
                super(v);
                checkedTextView = v;
            }
        }

        private ComplaintDangerAdapter(boolean isDangerSign) {
            this.isDangerSign = isDangerSign;
            this.list = isDangerSign ? presenter.getConfig().getDangerSigns() : presenter.getConfig().getComplaints();

            List<Object> observation = isDangerSign ? obsMap.get("danger_signs") : obsMap.get("specific_complaint");

            if (observation != null) {
                for (Object ob : observation) {
                    obReasons.put(ob.toString(), ob.toString());
                }
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                             int viewType) {
            CheckedTextView v = (CheckedTextView) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.quick_check_complaint_danger_item, parent, false);

            return new ViewHolder(v);
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
            Field field = list.get(position);
            holder.checkedTextView.setText(field.getDisplayName());
            holder.checkedTextView.setTag(field);

            if (field.getDisplayName().equals(getString(R.string.central_cyanosis))) {
                holder.checkedTextView.setCompoundDrawablesWithIntrinsicBounds(Utils.getAttributeDrawableResource(getActivity(), android.R.attr.listChoiceIndicatorMultiple), 0, R.drawable.ic_info, 0);

                holder.checkedTextView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        final int DRAWABLE_RIGHT = 2;

                        if (event.getAction() == MotionEvent.ACTION_UP && event.getRawX() >= (holder.checkedTextView.getRight() - (holder.checkedTextView.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width() + (2 * holder.checkedTextView.getPaddingEnd())))) {

                            AlertDialog.Builder builderSingle = new AlertDialog.Builder(getContext(), R.style.AncAlertDialog);
                            builderSingle.setTitle(R.string.central_cyanosis);
                            builderSingle.setMessage(R.string.cyanosis_info);
                            builderSingle.setIcon(R.drawable.ic_info);

                            builderSingle.setNegativeButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                            builderSingle.show();

                            return true;
                        }

                        return false;
                    }
                });

            }

            holder.checkedTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((CheckedTextView) v).toggle();
                    boolean isChecked = ((CheckedTextView) v).isChecked();
                    Object tag = v.getTag();
                    if (tag != null && tag instanceof Field) {
                        Field currentField = (Field) tag;
                        presenter.modifyComplaintsOrDangerList(currentField, isChecked, isDangerSign);
                    }
                }
            });

            if (presenter.containsComplaintOrDangerSign(field, isDangerSign) && !holder.checkedTextView.isChecked()) {
                holder.checkedTextView.setChecked(true);
            } else if (!presenter.containsComplaintOrDangerSign(field, isDangerSign) && holder.checkedTextView.isChecked()) {
                holder.checkedTextView.setChecked(false);
            }

            //autoselect
            if (!obReasons.isEmpty() && obReasons.containsValue(field.getDisplayName())) {
                holder.checkedTextView.callOnClick();
            }

        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public Field getSpecifyField() {
            return presenter.getField(list, getString(R.string.complaint_other_specify));
        }

    }

    private void initializeEvent() {

        Bundle args = getArguments();
        String baseEntityId = args.getString(Constants.INTENT_KEY.BASE_ENTITY_ID);
        Integer contactNo = args.getInt(Constants.INTENT_KEY.CONTACT_NO);

        PartialContact partialContactRequest = new PartialContact();
        partialContactRequest.setBaseEntityId(baseEntityId);
        partialContactRequest.setType("Quick Check");
        partialContactRequest.setContactNo(contactNo);

        PartialContact partialContactList = AncApplication.getInstance().getPartialContactRepository().getPartialContact(partialContactRequest);

        try {

            Gson gson = new Gson();
            Event event = gson.fromJson(partialContactList.getFormJson(), EVENT_TYPE);
            List<Obs> obs = event.getObs();
            for (Obs ob : obs) {
                obsMap.put(ob.getFieldCode(), ob.getValues());
            }

        } catch (Exception e) {

            Log.e(TAG, e.getMessage());
        }
    }

    private void setupBackButtonIcon(View view) {
        ImageButton backButton = view.findViewById(R.id.cancel_quick_check);
        if (backButton != null && !obsMap.isEmpty()) {
            backButton.setImageResource(R.drawable.ic_contact_menu);
        }
    }
}
