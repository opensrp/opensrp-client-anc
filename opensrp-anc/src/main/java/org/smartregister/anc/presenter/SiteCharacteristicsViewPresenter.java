package org.smartregister.anc.presenter;

import org.smartregister.anc.contract.BaseCharacteristicsContract;
import org.smartregister.anc.interactor.SiteCharacteristicsViewInteractor;

/**
 * Created by ndegwamartin on 28/08/2018.
 */
public class SiteCharacteristicsViewPresenter extends BaseCharacteristicsPresenter {

    public SiteCharacteristicsViewPresenter(BaseCharacteristicsContract.View view) {
        super(view);
    }

    @Override
    public BaseCharacteristicsContract.Interactor getInteractor() {
        return new SiteCharacteristicsViewInteractor(this);
    }
}
