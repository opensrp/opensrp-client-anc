package org.smartregister.anc.fragment;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import com.vijay.jsonwizard.fragments.JsonWizardFormFragment;

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

    public static ContactJsonFormFragment getFormFragment(String stepName) {
        ContactJsonFormFragment jsonFormFragment = new ContactJsonFormFragment();
        Bundle bundle = new Bundle();
        bundle.putString(DBConstants.KEY.STEPNAME, stepName);
        jsonFormFragment.setArguments(bundle);
        return jsonFormFragment;
    }

    @Override
    protected ContactJsonFormFragmentViewState createViewState() {
        return new ContactJsonFormFragmentViewState();
    }

    @Override
    protected ContactJsonFormFragmentPresenter createPresenter() {
        return new ContactJsonFormFragmentPresenter(this, ContactJsonFormInteractor.getInstance());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
//        getSupportActionBar().setDisplayShowHomeEnabled(false);
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
}


