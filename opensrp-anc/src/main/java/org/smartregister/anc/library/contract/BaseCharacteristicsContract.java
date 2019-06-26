package org.smartregister.anc.library.contract;

import org.smartregister.domain.ServerSetting;

import java.util.List;

/**
 * Created by ndegwamartin on 31/08/2018.
 */
public interface BaseCharacteristicsContract {

    interface BasePresenter {

        void onDestroy(boolean isChangingConfiguration);

        void renderView(List<ServerSetting> data);

        void getCharacteristics();

        Interactor getInteractor();

    }


    interface View {

        void renderSettings(List<ServerSetting> settings);

    }

    interface Interactor {

        void onDestroy(boolean isChangingConfiguration);

        void fetchCharacteristics();
    }
}
