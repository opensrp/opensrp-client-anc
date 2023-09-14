package org.smartregister.anc.library.presenter;

import org.smartregister.anc.library.contract.BaseCharacteristicsContract;
import org.smartregister.anc.library.interactor.SiteCharacteristicsInteractor;

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

    @Override
    public void setLocale(String locale) {

    }

    @Override
    public String getLocale() {
        return null;
    }
}
