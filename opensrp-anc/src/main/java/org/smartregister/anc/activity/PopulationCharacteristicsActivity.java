package org.smartregister.anc.activity;

import org.smartregister.anc.contract.BaseCharacteristicsContract;
import org.smartregister.anc.presenter.PopulationCharacteristicsPresenter;

public class PopulationCharacteristicsActivity extends BaseCharacteristicsActivity {

    @Override
    protected BaseCharacteristicsContract.BasePresenter getPresenter() {
        return new PopulationCharacteristicsPresenter(this);
    }
}
