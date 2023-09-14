package org.smartregister.anc.library.contract;

/**
 * Created by ndegwamartin on 28/08/2018.
 */
public interface PopulationCharacteristicsContract {

    interface Presenter extends BaseCharacteristicsContract.BasePresenter {

        BaseCharacteristicsContract.View getView();

        void setLocale(String locale);

        String getLocale();

    }

    interface View extends BaseCharacteristicsContract.View {

    }

    interface Interactor extends BaseCharacteristicsContract.Interactor {

    }
}
