package org.smartregister.anc.library.interactor;

import org.smartregister.anc.library.contract.BaseCharacteristicsContract;
import org.smartregister.anc.library.contract.PopulationCharacteristicsContract;
import org.smartregister.anc.library.task.FetchSiteCharacteristicsTask;

/**
 * Created by ndegwamartin on 28/08/2018.
 */
public class SiteCharacteristicsInteractor implements BaseCharacteristicsContract.Interactor {
    private PopulationCharacteristicsContract.Presenter presenter;

    public SiteCharacteristicsInteractor(PopulationCharacteristicsContract.Presenter presenter) {
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
        new FetchSiteCharacteristicsTask(presenter).execute();
    }

}
