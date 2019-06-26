package org.smartregister.anc.library.presenter;

import org.smartregister.anc.library.contract.BaseCharacteristicsContract;
import org.smartregister.anc.library.interactor.PopulationCharacteristicsInteractor;

/**
 * Created by ndegwamartin on 28/08/2018.
 */
public class PopulationCharacteristicsPresenter extends BaseCharacteristicsPresenter {

    public PopulationCharacteristicsPresenter(BaseCharacteristicsContract.View view) {
        super(view);
    }

    @Override
    public BaseCharacteristicsContract.Interactor getInteractor() {
        return new PopulationCharacteristicsInteractor(this);
    }
}
