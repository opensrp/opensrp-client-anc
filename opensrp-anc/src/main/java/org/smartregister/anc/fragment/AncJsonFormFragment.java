package org.smartregister.anc.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.vijay.jsonwizard.activities.JsonFormActivity;
import com.vijay.jsonwizard.constants.JsonFormConstants;
import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.presenters.JsonFormFragmentPresenter;

import org.smartregister.anc.R;
import org.smartregister.anc.interactor.AncJsonFormInteractor;
import org.smartregister.anc.presenter.AncJsonFormFragmentPresenter;
import org.smartregister.anc.util.DBConstants;
import org.smartregister.anc.viewstate.AncJsonFormFragmentViewState;


/**
 * Created by ndegwamartin on 30/06/2018.
 */
public class AncJsonFormFragment extends JsonFormFragment {

    public static final String TAG = AncJsonFormFragment.class.getName();

    private BottomNavigationListener navigationListener = new BottomNavigationListener();
    private Button previousButton;
    private Button nextButton;

    private static final int MENU_NAVIGATION = 100001;

    public static AncJsonFormFragment getFormFragment(String stepName) {
        AncJsonFormFragment jsonFormFragment = new AncJsonFormFragment();
        Bundle bundle = new Bundle();
        bundle.putString(DBConstants.KEY.STEPNAME, stepName);
        jsonFormFragment.setArguments(bundle);
        return jsonFormFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.anc_form_fragment_json_wizard, null);

        this.mMainView = rootView.findViewById(R.id.main_layout);
        this.mScrollView = rootView.findViewById(R.id.scroll_view);

        previousButton = rootView.findViewById(R.id.previous);
        previousButton.setVisibility(View.INVISIBLE);

        previousButton.setOnClickListener(navigationListener);

        nextButton = rootView.findViewById(R.id.next);
        nextButton.setOnClickListener(navigationListener);

        setupCustomToolbar();

        return rootView;
    }

    @Override
    protected AncJsonFormFragmentViewState createViewState() {
        return new AncJsonFormFragmentViewState();
    }

    @Override
    protected JsonFormFragmentPresenter createPresenter() {
        return new AncJsonFormFragmentPresenter(this, AncJsonFormInteractor.getInstance());
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.add(Menu.NONE, MENU_NAVIGATION, 1, "Menu").setIcon(R.drawable.ic_action_menu).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
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
        }

        if (save || !next) {
            nextButton.setTag(R.id.NEXT_STATE, false);
            nextButton.setText(getString(R.string.submit));
        }

        if (getFragmentManager() != null) {
            if (getFragmentManager().getBackStackEntryCount() == 0) {
                previousButton.setVisibility(View.INVISIBLE);
            } else {
                previousButton.setVisibility(View.VISIBLE);
            }
        }
    }

    private void setupCustomToolbar() {
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_clear);
        setUpBackButton();
    }

    private AncJsonFormFragmentPresenter getPresenter() {
        return (AncJsonFormFragmentPresenter) presenter;
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

    ////////////////////////////////////////////////////////////////
    // Inner classes
    ////////////////////////////////////////////////////////////////

    private class BottomNavigationListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.next) {
                boolean next = (boolean) v.getTag(R.id.NEXT_STATE);
                if (next) {
                    next();
                } else {
                    save();
                }
            } else if (v.getId() == R.id.previous) {
                assert getFragmentManager() != null;
                getFragmentManager().popBackStack();
            }
        }
    }

}


