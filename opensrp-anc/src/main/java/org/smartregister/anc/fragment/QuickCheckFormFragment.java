package org.smartregister.anc.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.vijay.jsonwizard.fragments.JsonWizardFormFragment;

import org.smartregister.anc.R;
import org.smartregister.anc.activity.QuickCheckFormActivity;
import org.smartregister.anc.domain.Contact;
import org.smartregister.anc.util.DBConstants;

public class QuickCheckFormFragment extends JsonWizardFormFragment {
    private static final int MENU_NAVIGATION = 100002;
    private TextView contactTitle;
    public static QuickCheckFormFragment getFormFragment(){
        QuickCheckFormFragment quickCheckFormFragment =new QuickCheckFormFragment();
        Bundle bundle = new Bundle();
        bundle.putString(DBConstants.KEY.STEPNAME, "step1");
        quickCheckFormFragment.setArguments(bundle);
        return quickCheckFormFragment;

    }
    private Contact getContact() {
        if (getActivity() != null && getActivity() instanceof QuickCheckFormActivity) {
            return ((QuickCheckFormActivity) getActivity()).getContact();
        }
        return null;
    }
    @Override
    public void setActionBarTitle(String title) {
        Contact contact = getContact();
        if (contact != null) {
            contactTitle.setText(contact.getName());
            getStepName().setText(title);
        } else {
            contactTitle.setText(title);
        }
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
    }
    @Override
    protected void setupCustomUI() {
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.contact_form_toolbar);
        View view = getSupportActionBar().getCustomView();
        ImageButton goBackButton = view.findViewById(R.id.contact_menu);
        contactTitle = view.findViewById(R.id.contact_title);
        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backClick();
            }
        });
    }
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(com.vijay.jsonwizard.R.layout.native_json_form_fragment_wizard, null);

        this.mMainView = rootView.findViewById(com.vijay.jsonwizard.R.id.main_layout);
        this.mScrollView = rootView.findViewById(com.vijay.jsonwizard.R.id.scroll_view);

        setupCustomUI();

        return rootView;
    }
}
