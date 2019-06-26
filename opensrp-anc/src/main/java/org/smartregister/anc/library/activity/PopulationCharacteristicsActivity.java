package org.smartregister.anc.library.activity;

import org.smartregister.anc.library.R;
import org.smartregister.anc.library.contract.BaseCharacteristicsContract;
import org.smartregister.anc.library.presenter.PopulationCharacteristicsPresenter;

public class PopulationCharacteristicsActivity extends BaseCharacteristicsActivity {

    @Override
    protected BaseCharacteristicsContract.BasePresenter getPresenter() {
        return new PopulationCharacteristicsPresenter(this);
    }

    @Override
    protected String getToolbarTitle() {
        return getString(R.string.population_characteristics);
    }
}
