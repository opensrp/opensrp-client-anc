package org.smartregister.anc.activity;

import org.smartregister.anc.contract.BaseCharacteristicsContract;
import org.smartregister.anc.presenter.SiteCharacteristicsViewPresenter;

/**
 * Created by ndegwamartin on 30/08/2018.
 */
public class SiteCharacteristicsActivity extends BaseCharacteristicsActivity {

    @Override
    protected BaseCharacteristicsContract.BasePresenter getPresenter() {
        return new SiteCharacteristicsViewPresenter(this);
    }
}
