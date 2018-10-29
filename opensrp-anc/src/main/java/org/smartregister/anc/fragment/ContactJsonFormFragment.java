package org.smartregister.anc.fragment;

import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.vijay.jsonwizard.activities.JsonFormActivity;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.presenters.JsonFormFragmentPresenter;

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
public class ContactJsonFormFragment extends JsonFormFragment {

    public static final String TAG = ContactJsonFormFragment.class.getName();

    private BottomNavigationListener navigationListener = new BottomNavigationListener();

    private Button previousButton;
    private Button nextButton;

    private ImageView previousIcon;
    private ImageView nextIcon;

    private TextView stepName;

    private Toolbar navigationToolbar;

    private static final int MENU_NAVIGATION = 100001;

    public static ContactJsonFormFragment getFormFragment(String stepName) {
        ContactJsonFormFragment jsonFormFragment = new ContactJsonFormFragment();
        Bundle bundle = new Bundle();
        bundle.putString(DBConstants.KEY.STEPNAME, stepName);
        jsonFormFragment.setArguments(bundle);
        return jsonFormFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.contact_form_fragment_json_wizard, null);

        this.mMainView = rootView.findViewById(R.id.main_layout);
        this.mScrollView = rootView.findViewById(R.id.scroll_view);

        setupNavigation(rootView);

        setupCustomToolbar();

        return rootView;
    }

    @Override
    protected ContactJsonFormFragmentViewState createViewState() {
        return new ContactJsonFormFragmentViewState();
    }

    @Override
    protected JsonFormFragmentPresenter createPresenter() {
        return new ContactJsonFormFragmentPresenter(this, ContactJsonFormInteractor.getInstance());
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
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
    public void updateVisibilityOfNextAndSave(boolean next, boolean save) {
        getMenu().findItem(com.vijay.jsonwizard.R.id.action_next).setVisible(false);
        getMenu().findItem(com.vijay.jsonwizard.R.id.action_save).setVisible(false);

        if (next || !save) {
            nextButton.setTag(R.id.NEXT_STATE, true);
            nextButton.setText(getString(R.string.next));

            nextIcon.setVisibility(View.VISIBLE);
        }

        if (save || !next) {
            nextButton.setTag(R.id.NEXT_STATE, false);
            nextButton.setText(getString(R.string.submit));

            nextIcon.setVisibility(View.INVISIBLE);
        }

        if (getFragmentManager() != null) {
            if (getFragmentManager().getBackStackEntryCount() == 0) {
                previousButton.setVisibility(View.INVISIBLE);
                previousIcon.setVisibility(View.INVISIBLE);
            } else {
                previousButton.setVisibility(View.VISIBLE);
                previousIcon.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void setActionBarTitle(String title) {
        Contact contact = getContact();
        if (contact != null) {
            super.setActionBarTitle(contact.getName());
            if (stepName != null) {
                stepName.setText(title);
            }
        } else {
            super.setActionBarTitle(title);
        }
    }

    private void setupNavigation(View rootView) {
        previousButton = rootView.findViewById(R.id.previous);
        previousIcon = rootView.findViewById(R.id.previous_icon);

        previousButton.setVisibility(View.INVISIBLE);
        previousIcon.setVisibility(View.INVISIBLE);

        previousButton.setOnClickListener(navigationListener);
        previousIcon.setOnClickListener(navigationListener);

        nextButton = rootView.findViewById(R.id.next);
        nextIcon = rootView.findViewById(R.id.next_icon);

        nextButton.setOnClickListener(navigationListener);
        nextIcon.setOnClickListener(navigationListener);

        stepName = rootView.findViewById(R.id.step_title);

        navigationToolbar = rootView.findViewById(R.id.navigation_toolbar);
    }

    private void setupCustomToolbar() {
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_contact_menu);
        setUpBackButton();

        try {
            Contact contact = getContact();
            if (contact != null) {
                int actionBarColor = getResources().getColor(contact.getActionBarBackground());
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(actionBarColor));

                int navigationColor = getResources().getColor(contact.getNavigationBackground());
                if (navigationToolbar != null) {
                    navigationToolbar.setBackgroundColor(navigationColor);
                }
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, e.getMessage(), e);
        }

    }

    private void save() {
        try {
            Boolean skipValidation = ((JsonFormActivity) mMainView.getContext()).getIntent().getBooleanExtra(JsonFormConstants.SKIP_VALIDATION, false);
            save(skipValidation);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            save(false);
        }
    }

    private Contact getContact() {
        if (getActivity() != null && getActivity() instanceof ContactJsonFormActivity) {
            return ((ContactJsonFormActivity) getActivity()).getContact();
        }
        return null;
    } 

    ////////////////////////////////////////////////////////////////
    // Inner classes
    ////////////////////////////////////////////////////////////////

    private class BottomNavigationListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.next || v.getId() == R.id.next_icon) {
                Object tag = v.getTag(R.id.NEXT_STATE);
                if (tag == null) {
                    next();
                } else {
                    boolean next = (boolean) tag;
                    if (next) {
                        next();
                    } else {
                        save();
                    }
                }

            } else if (v.getId() == R.id.previous || v.getId() == R.id.previous_icon) {
                assert getFragmentManager() != null;
                getFragmentManager().popBackStack();
            }
        }
    }

}


