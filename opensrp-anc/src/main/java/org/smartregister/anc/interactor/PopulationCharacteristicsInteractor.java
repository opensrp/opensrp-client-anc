package org.smartregister.anc.interactor;

import org.smartregister.anc.contract.BaseCharacteristicsContract;
import org.smartregister.anc.contract.PopulationCharacteristicsContract;
import org.smartregister.anc.task.FetchPopulationCharacteristicsTask;

/**
 * Created by ndegwamartin on 28/08/2018.
 */
public class PopulationCharacteristicsInteractor implements BaseCharacteristicsContract.Interactor {
    private PopulationCharacteristicsContract.Presenter presenter;

    public PopulationCharacteristicsInteractor(PopulationCharacteristicsContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {

        if (!isChangingConfiguration) {
            presenter = null;
        }
    }

    @Override
    public void fetchCharacteristics() {
        new FetchPopulationCharacteristicsTask(presenter).execute();
    }

}
