package org.smartregister.anc.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import org.smartregister.anc.R;
import org.smartregister.anc.contract.QuickCheckContract;
import org.smartregister.anc.presenter.QuickCheckPresenter;
import org.smartregister.anc.util.Constants;
import org.smartregister.anc.util.Utils;
import org.smartregister.configurableviews.model.Field;

import java.util.List;

public class QuickCheckFragment extends DialogFragment implements QuickCheckContract.View {

    private QuickCheckDialogClickListener actionHandler = new QuickCheckDialogClickListener();

    private QuickCheckContract.Presenter presenter;

    private ComplaintDangerAdapter complaintAdapter;
    private ComplaintDangerAdapter dangerSignAdapter;

    private View complaintLayout;
    private EditText specifyEditText;

    private View dangerSignLayout;

    private View navigationLayout;

    private Button refer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.AncFullScreenDialog);

        initializePresenter();
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

        updateReasonList(view);
        updateComplaintList(view);
        updateDangerList(view);

        setupViews(view);

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
    }

    @Override
    public void displayComplaintLayout() {
        if (complaintLayout != null) {
            complaintLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void hideComplaintLayout() {
        if (complaintLayout != null) {
            complaintLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void notifyComplaintAdapter() {
        if (complaintAdapter != null) {
            complaintAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void notifyDangerSignAdapter() {
        if (dangerSignAdapter != null) {
            dangerSignAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void displayDangerSignLayout() {
        if (dangerSignLayout != null) {
            dangerSignLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showSpecifyEditText() {
        if (specifyEditText != null) {
            specifyEditText.setVisibility(View.VISIBLE);
            specifyEditText.requestFocus();
        }
    }

    @Override
    public void hideSpecifyEditText() {
        if (specifyEditText != null) {
            specifyEditText.setVisibility(View.GONE);
            specifyEditText.setText("");
        }
    }

    @Override
    public void displayNavigationLayout() {
        if (navigationLayout != null) {
            navigationLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void displayReferButton() {
        if (refer != null) {
            refer.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void hideReferButton() {
        if (refer != null) {
            refer.setVisibility(View.GONE);
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

    private void initializePresenter() {
        presenter = new QuickCheckPresenter(this);
    }

    private void displayReferralDialog() {

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.referral_dialog, null);

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

    ////////////////////////////////////////////////////////////////
    // Inner classes
    ////////////////////////////////////////////////////////////////

    private class QuickCheckDialogClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.cancel_quick_check:
                    dismiss();
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

        public class ViewHolder extends RecyclerView.ViewHolder {
            private CheckedTextView checkedTextView;

            private ViewHolder(CheckedTextView v) {
                super(v);
                checkedTextView = v;
            }
        }

        private ReasonAdapter() {
            this.reasons = presenter.getConfig().getReasons();
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
                    checkedTextView.setChecked(true);

                    if (lastChecked != null) {
                        lastChecked.setChecked(false);
                    }
                    lastChecked = checkedTextView;

                    Object tag = v.getTag();
                    if (tag != null && tag instanceof Field) {
                        Field currentField = (Field) tag;
                        presenter.setReason(currentField);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return reasons.size();
        }

    }

    private class ComplaintDangerAdapter extends RecyclerView.Adapter<ComplaintDangerAdapter.ViewHolder> {
        private List<Field> list;
        private boolean isDangerSign;

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
                        presenter.addToComplaintsOrDangerList(currentField, isChecked, isDangerSign);
                    }
                }
            });

            if (presenter.containsComplaintOrDangerSign(field, isDangerSign) && !holder.checkedTextView.isChecked()) {
                holder.checkedTextView.setChecked(true);
            } else if (!presenter.containsComplaintOrDangerSign(field, isDangerSign) && holder.checkedTextView.isChecked()) {
                holder.checkedTextView.setChecked(false);
            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

    }
}
