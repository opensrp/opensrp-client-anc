package org.smartregister.anc.library.interactor;

import org.smartregister.anc.library.contract.BaseCharacteristicsContract;
import org.smartregister.anc.library.contract.PopulationCharacteristicsContract;
import org.smartregister.anc.library.task.FetchPopulationCharacteristicsTask;

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
