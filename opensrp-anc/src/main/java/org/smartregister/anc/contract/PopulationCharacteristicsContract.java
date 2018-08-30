package org.smartregister.anc.contract;

import org.smartregister.anc.domain.Characteristic;

import java.util.List;

/**
 * Created by ndegwamartin on 28/08/2018.
 */
public interface PopulationCharacteristicsContract {

    interface Presenter {

        PopulationCharacteristicsContract.View getView();

        void onDestroy(boolean isChangingConfiguration);

       void getPopulationCharacteristics();

        void renderView(List<Characteristic> data);
    }

    interface View {

        void renderSettings(List<Characteristic> settings);

    }

    interface Interactor {

        void onDestroy(boolean isChangingConfiguration);

        void fetchPopulationCharacteristics();

    }
}
