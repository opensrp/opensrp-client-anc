package org.smartregister.anc.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.vijay.jsonwizard.fragments.JsonFormFragment;
import com.vijay.jsonwizard.presenters.JsonFormFragmentPresenter;

import org.smartregister.anc.interactor.AncJsonFormInteractor;
import org.smartregister.anc.util.DBConstants;
import org.smartregister.anc.viewstate.AncJsonFormFragmentViewState;


/**
 * Created by ndegwamartin on 30/06/2018.
 */
public class AncJsonFormFragment extends JsonFormFragment {

    public static AncJsonFormFragment getFormFragment(String stepName) {
        AncJsonFormFragment jsonFormFragment = new AncJsonFormFragment();
        Bundle bundle = new Bundle();
        bundle.putString(DBConstants.KEY.STEPNAME, stepName);
        jsonFormFragment.setArguments(bundle);
        return jsonFormFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected AncJsonFormFragmentViewState createViewState() {
        return new AncJsonFormFragmentViewState();
    }

    @Override
    protected JsonFormFragmentPresenter createPresenter() {
        return new JsonFormFragmentPresenter(this, AncJsonFormInteractor.getInstance());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);

    }


}


