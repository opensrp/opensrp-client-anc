package org.smartregister.anc.library.activity;

import android.preference.PreferenceManager;

import org.smartregister.anc.library.AppConfig;
import org.smartregister.anc.library.R;
import org.smartregister.anc.library.contract.BaseCharacteristicsContract;
import org.smartregister.anc.library.presenter.PopulationCharacteristicsPresenter;
import org.smartregister.anc.library.util.Utils;
import org.smartregister.repository.AllSharedPreferences;

public class PopulationCharacteristicsActivity extends BaseCharacteristicsActivity {

    @Override
    protected BaseCharacteristicsContract.BasePresenter getPresenter() {
        PopulationCharacteristicsPresenter presenter = new PopulationCharacteristicsPresenter(this);
        AllSharedPreferences allSharedPreferences = new AllSharedPreferences(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
        String current = allSharedPreferences.fetchLanguagePreference();
        if(current==null) current = AppConfig.DefaultLocale.getLanguage();
        presenter.setLocale(current);
        return presenter;
    }

    @Override
    protected String getToolbarTitle() {
        return getString(R.string.population_characteristics);
    }
}
