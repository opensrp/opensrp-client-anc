package org.smartregister.anc.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;

import org.smartregister.anc.R;
import org.smartregister.anc.contract.QuickCheckContract;
import org.smartregister.anc.presenter.QuickCheckPresenter;
import org.smartregister.configurableviews.model.Field;

import java.util.List;

public class QuickCheckFragment extends DialogFragment implements QuickCheckContract.View {

    private QuickCheckContract.Presenter presenter;
    private ReasonAdapter reasonAdapter;
    private ComplaintAdapter complaintAdapter;
    private DangerSignAdapter dangerSignAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.AncFullScreenDialog);

        initializePresenter();
    }

    public static void launchDialog(Activity activity,
                                    String dialogTag) {
        QuickCheckFragment dialogFragment = new QuickCheckFragment();
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

        updateReasonList(view, presenter.getConfig().getReasons());
        updateComplaintList(view, presenter.getConfig().getComplaints());
        updateDangerList(view, presenter.getConfig().getDangerSigns());
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

    protected void updateReasonList(final View view, final List<Field> reasons) {

        if (reasons == null) {
            return;
        }

        RecyclerView recyclerView = view.findViewById(R.id.reason_recycler_view);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        reasonAdapter = new ReasonAdapter(reasons);
        recyclerView.setAdapter(reasonAdapter);
    }


    protected void updateComplaintList(final View view, final List<Field> complaints) {

        if (complaints == null) {
            return;
        }

        RecyclerView recyclerView = view.findViewById(R.id.complaint_recycler_view);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        complaintAdapter = new ComplaintAdapter(complaints);
        recyclerView.setAdapter(complaintAdapter);
    }

    protected void updateDangerList(final View view, final List<Field> dangerSigns) {

        if (dangerSigns == null) {
            return;
        }

        RecyclerView recyclerView = view.findViewById(R.id.complaint_recycler_view);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        dangerSignAdapter = new DangerSignAdapter(dangerSigns);
        recyclerView.setAdapter(complaintAdapter);
    }


    private void initializePresenter() {
        presenter = new QuickCheckPresenter(this);
    }

    ////////////////////////////////////////////////////////////////
    // Inner classes
    ////////////////////////////////////////////////////////////////

    private class ReasonAdapter extends RecyclerView.Adapter<ReasonAdapter.ViewHolder> {
        private List<Field> reasons;
        private CheckedTextView lastChecked;

        public class ViewHolder extends RecyclerView.ViewHolder {
            public CheckedTextView checkedTextView;

            public ViewHolder(CheckedTextView v) {
                super(v);
                checkedTextView = v;
            }
        }

        public ReasonAdapter(List<Field> reasons) {
            this.reasons = reasons;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent,
                                             int viewType) {
            CheckedTextView v = (CheckedTextView) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.quick_check_reason_item, parent, false);

            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
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

    private class ComplaintAdapter extends RecyclerView.Adapter<ComplaintAdapter.ViewHolder> {
        private List<Field> list;

        public class ViewHolder extends RecyclerView.ViewHolder {
            public CheckedTextView checkedTextView;

            public ViewHolder(CheckedTextView v) {
                super(v);
                checkedTextView = v;
            }
        }

        public ComplaintAdapter(List<Field> list) {
            this.list = list;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent,
                                             int viewType) {
            CheckedTextView v = (CheckedTextView) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.complaint_danger_item, parent, false);

            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Field field = list.get(position);
            holder.checkedTextView.setText(field.getDisplayName());
            holder.checkedTextView.setTag(field);

            holder.checkedTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((CheckedTextView) v).toggle();
                    boolean isChecked = ((CheckedTextView) v).isChecked();
                    Object tag = v.getTag();
                    if (tag != null && tag instanceof Field) {
                        Field currentField = (Field) tag;
                        if (isChecked) {
                            // TODO
                        } else {
                            // TODO
                        }

                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    private class DangerSignAdapter extends RecyclerView.Adapter<DangerSignAdapter.ViewHolder> {
        private List<Field> list;

        public class ViewHolder extends RecyclerView.ViewHolder {
            public CheckedTextView checkedTextView;

            public ViewHolder(CheckedTextView v) {
                super(v);
                checkedTextView = v;
            }
        }

        public DangerSignAdapter(List<Field> list) {
            this.list = list;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent,
                                             int viewType) {
            CheckedTextView v = (CheckedTextView) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.complaint_danger_item, parent, false);

            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Field field = list.get(position);
            holder.checkedTextView.setText(field.getDisplayName());
            holder.checkedTextView.setTag(field);

            holder.checkedTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((CheckedTextView) v).toggle();
                    boolean isChecked = ((CheckedTextView) v).isChecked();
                    Object tag = v.getTag();
                    if (tag != null && tag instanceof Field) {
                        Field currentField = (Field) tag;
                        if (isChecked) {
                            // TODO
                        } else {
                            // TODO
                        }

                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }
}
