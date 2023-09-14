package org.smartregister.anc.library.presenter;

import org.smartregister.anc.library.contract.BaseCharacteristicsContract;
import org.smartregister.anc.library.interactor.PopulationCharacteristicsInteractor;

/**
 * Created by ndegwamartin on 28/08/2018.
 */
public class PopulationCharacteristicsPresenter extends BaseCharacteristicsPresenter {
    private String locale;
    public PopulationCharacteristicsPresenter(BaseCharacteristicsContract.View view) {
        super(view);
    }

    @Override
    public BaseCharacteristicsContract.Interactor getInteractor() {
        return new PopulationCharacteristicsInteractor(this);
    }

    @Override
    public void setLocale(String locale) {
        this.locale = locale;
    }

    @Override
    public String getLocale() {
        return this.locale;
    }
}
