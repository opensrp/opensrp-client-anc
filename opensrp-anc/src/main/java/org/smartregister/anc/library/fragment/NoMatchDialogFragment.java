package org.smartregister.anc.library.fragment;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import org.smartregister.anc.library.R;
import org.smartregister.anc.library.activity.BaseHomeRegisterActivity;
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
            FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
            Fragment prev = activity.getSupportFragmentManager().findFragmentByTag(dialogTag);
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
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.AncDialog);
    }

    @Override
    public void onCancel(DialogInterface dialogInterface) {
        super.onCancel(dialogInterface);
        baseRegisterActivity.setSearchTerm("");
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
            ((BaseHomeRegisterActivity) baseRegisterActivity).startAdvancedSearch();
            Fragment currentFragment =
                    baseRegisterActivity.findFragmentByPosition(BaseRegisterActivity.ADVANCED_SEARCH_POSITION);
            ((AdvancedSearchFragment) currentFragment).getAncId().setText(whoAncId);
        }
    }
}
