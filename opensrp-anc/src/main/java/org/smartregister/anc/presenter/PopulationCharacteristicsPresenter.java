package org.smartregister.anc.presenter;

import org.smartregister.anc.contract.PopulationCharacteristicsContract;
import org.smartregister.anc.domain.Characteristic;
import org.smartregister.anc.interactor.PopulationCharacteristicsInteractor;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by ndegwamartin on 28/08/2018.
 */
public class PopulationCharacteristicsPresenter implements PopulationCharacteristicsContract.Presenter {

    private WeakReference<PopulationCharacteristicsContract.View> view;
    private PopulationCharacteristicsContract.Interactor interactor;

    public PopulationCharacteristicsPresenter(PopulationCharacteristicsContract.View view) {
        this.view = new WeakReference<>(view);
        interactor = new PopulationCharacteristicsInteractor(this);
    }

    @Override
    public PopulationCharacteristicsContract.View getView() {
        if (view != null) {
            return view.get();
        } else {
            return null;
        }
    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {

        view = null;//set to null on destroy

        // Inform interactor
        interactor.onDestroy(isChangingConfiguration);

        // Activity destroyed set interactor to null
        if (!isChangingConfiguration) {
            interactor = null;
        }

    }

    @Override
    public void getPopulationCharacteristics() {
        interactor.fetchPopulationCharacteristics();
    }

    @Override
    public void renderView(List<Characteristic> data) {
        getView().renderSettings(data);
    }
}
