package org.smartregister.anc.fragment;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.vijay.jsonwizard.fragments.JsonWizardFormFragment;

import org.smartregister.anc.R;
import org.smartregister.anc.activity.ContactJsonFormActivity;
import org.smartregister.anc.domain.Contact;
import org.smartregister.anc.interactor.ContactJsonFormInteractor;
import org.smartregister.anc.presenter.ContactJsonFormFragmentPresenter;
import org.smartregister.anc.util.DBConstants;
import org.smartregister.anc.viewstate.ContactJsonFormFragmentViewState;


/**
 * Created by ndegwamartin on 30/06/2018.
 */
public class ContactJsonFormFragment extends JsonWizardFormFragment {

    public static final String TAG = ContactJsonFormFragment.class.getName();

    private static final int MENU_NAVIGATION = 100001;
    private TextView contactTitle;
    private ContactJsonFormFragmentPresenter contactJsonFormFragmentPresenter;

    public ContactJsonFormFragment() {
        contactJsonFormFragmentPresenter = createPresenter();
    }

    public static ContactJsonFormFragment getFormFragment(String stepName) {
        ContactJsonFormFragment jsonFormFragment = new ContactJsonFormFragment();
        Bundle bundle = new Bundle();
        bundle.putString(DBConstants.KEY.STEPNAME, stepName);
        jsonFormFragment.setArguments(bundle);
        return jsonFormFragment;
    }
    private Contact getContact() {
        if (getActivity() != null && getActivity() instanceof ContactJsonFormActivity) {
            return ((ContactJsonFormActivity) getActivity()).getContact();
        }
        return null;
    }
    @Override
    protected ContactJsonFormFragmentViewState createViewState() {
        return new ContactJsonFormFragmentViewState();
    }

    @Override
    public void setActionBarTitle(String title) {
        Contact contact = getContact();
        if (contact != null) {
            contactTitle.setText(contact.getName());
            if (getStepName() != null) {
                getStepName().setText(title);
            }
        } else {
            contactTitle.setText(title);
        }
    }

    @Override
    protected ContactJsonFormFragmentPresenter createPresenter() {
        return new ContactJsonFormFragmentPresenter(this, ContactJsonFormInteractor.getInstance());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        //menu.add(Menu.NONE, MENU_NAVIGATION, 1, "Menu").setIcon(R.drawable.ic_action_menu).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (super.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case MENU_NAVIGATION:
                Toast.makeText(getActivity(), "Right navigation item clicked", Toast.LENGTH_SHORT).show();
                return true;
            default:
                break;
        }
        return false;
    }

    @Override
    protected void setupCustomToolbar() {
        super.setupCustomToolbar();
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
}


