package org.smartregister.anc.presenter;

import org.smartregister.anc.contract.BaseCharacteristicsContract;
import org.smartregister.anc.interactor.SiteCharacteristicsInteractor;

/**
 * Created by ndegwamartin on 28/08/2018.
 */
public class SiteCharacteristicsPresenter extends BaseCharacteristicsPresenter {

    public SiteCharacteristicsPresenter(BaseCharacteristicsContract.View view) {
        super(view);
    }

    @Override
    public BaseCharacteristicsContract.Interactor getInteractor() {
        return new SiteCharacteristicsInteractor(this);
    }
}
