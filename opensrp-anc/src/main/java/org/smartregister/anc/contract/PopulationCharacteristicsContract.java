package org.smartregister.anc.contract;

/**
 * Created by ndegwamartin on 28/08/2018.
 */
public interface PopulationCharacteristicsContract {

    interface Presenter extends BaseCharacteristicsContract.BasePresenter {

        BaseCharacteristicsContract.View getView();
    }

    interface View extends BaseCharacteristicsContract.View {

    }

    interface Interactor extends BaseCharacteristicsContract.Interactor {

    }
}
