package org.smartregister.anc.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.MenuItem;

import org.smartregister.anc.fragment.HomeRegisterFragment;
import org.smartregister.anc.presenter.RegisterPresenter;
import org.smartregister.anc.util.Constants;

import java.util.Arrays;
import java.util.List;

/**
 * Created by keyman on 26/06/2018.
 */

public class HomeRegisterActivity extends BaseRegisterActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

    }

    @Override
    public Fragment getRegisterFragment() {
        return new HomeRegisterFragment();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return superOnOptionsItemsSelected(item);

    }

    @Override
    protected void onCreation() {//Do something on creation

    }

    @Override
    protected void initializePresenter() {
        presenter = new RegisterPresenter(this);
    }

    @Override
    public List<String> getViewIdentifiers() {
        return Arrays.asList(Constants.CONFIGURATION.HOME_REGISTER);
    }

    protected boolean superOnOptionsItemsSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }


}
