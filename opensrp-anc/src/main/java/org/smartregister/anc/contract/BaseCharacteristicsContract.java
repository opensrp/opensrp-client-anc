package org.smartregister.anc.contract;

import org.smartregister.domain.Characteristic;

import java.util.List;

/**
 * Created by ndegwamartin on 31/08/2018.
 */
public interface BaseCharacteristicsContract {

    interface BasePresenter {

        void onDestroy(boolean isChangingConfiguration);

        void renderView(List<Characteristic> data);

        void getCharacteristics();

        Interactor getInteractor();

    }


    interface View {

        void renderSettings(List<Characteristic> settings);

    }

    interface Interactor {

        void onDestroy(boolean isChangingConfiguration);

        void fetchCharacteristics();
    }
}
