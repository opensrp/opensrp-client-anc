package org.smartregister.anc.library.fragment;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.smartregister.anc.library.R;
import org.smartregister.anc.library.activity.HomeRegisterActivity;
import org.smartregister.view.activity.BaseRegisterActivity;

@SuppressLint("ValidFragment")
public class NoMatchDialogFragment extends DialogFragment {
    private final NoMatchDialogActionHandler noMatchDialogActionHandler = new NoMatchDialogActionHandler();
    private final BaseRegisterActivity baseRegisterActivity;
    private final String whoAncId;

    public NoMatchDialogFragment(BaseRegisterActivity baseRegisterActivity, String whoAncId) {
        this.whoAncId = whoAncId;
        this.baseRegisterActivity = baseRegisterActivity;
    }

    @Nullable
    public static NoMatchDialogFragment launchDialog(BaseRegisterActivity activity, String dialogTag, String whoAncId) {
        NoMatchDialogFragment noMatchDialogFragment = new NoMatchDialogFragment(activity, whoAncId);
        if (activity != null) {
            FragmentTransaction fragmentTransaction = activity.getFragmentManager().beginTransaction();
            Fragment prev = activity.getFragmentManager().findFragmentByTag(dialogTag);
            if (prev != null) {
                fragmentTransaction.remove(prev);
            }
            fragmentTransaction.addToBackStack(null);

            noMatchDialogFragment.show(fragmentTransaction, dialogTag);

            return noMatchDialogFragment;
        } else {
            return null;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup dialogView = (ViewGroup) inflater.inflate(R.layout.dialog_no_woman_match, container, false);
        Button cancel = dialogView.findViewById(R.id.cancel_no_match_dialog);
        cancel.setOnClickListener(noMatchDialogActionHandler);
        Button advancedSearch = dialogView.findViewById(R.id.go_to_advanced_search);
        advancedSearch.setOnClickListener(noMatchDialogActionHandler);

        return dialogView;
    }

    @Override
    public void onCancel(DialogInterface dialogInterface) {
        super.onCancel(dialogInterface);
        baseRegisterActivity.setSearchTerm("");
    }

    ////////////////////////////////////////////////////////////////
    // Inner classes
    ////////////////////////////////////////////////////////////////

    private class NoMatchDialogActionHandler implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            int i = view.getId();
            if (i == R.id.cancel_no_match_dialog) {
                dismiss();
                baseRegisterActivity.setSearchTerm("");
            } else if (i == R.id.go_to_advanced_search) {
                baseRegisterActivity.setSearchTerm("");
                goToAdvancedSearch(whoAncId);
                baseRegisterActivity.setSelectedBottomBarMenuItem(R.id.action_search);
                dismiss();
            }
        }

        private void goToAdvancedSearch(String whoAncId) {
            ((HomeRegisterActivity) baseRegisterActivity).startAdvancedSearch();
            android.support.v4.app.Fragment currentFragment =
                    baseRegisterActivity.findFragmentByPosition(BaseRegisterActivity.ADVANCED_SEARCH_POSITION);
            ((AdvancedSearchFragment) currentFragment).getAncId().setText(whoAncId);
        }
    }
}
