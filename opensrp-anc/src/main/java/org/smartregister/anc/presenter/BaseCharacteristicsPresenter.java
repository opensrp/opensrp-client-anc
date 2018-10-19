package org.smartregister.anc.presenter;

import org.smartregister.anc.contract.BaseCharacteristicsContract;
import org.smartregister.anc.contract.PopulationCharacteristicsContract;
import org.smartregister.domain.Characteristic;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by ndegwamartin on 28/08/2018.
 */
public abstract class BaseCharacteristicsPresenter implements PopulationCharacteristicsContract.Presenter {

    private WeakReference<BaseCharacteristicsContract.View> view;
    private BaseCharacteristicsContract.Interactor interactor;

    public BaseCharacteristicsPresenter(BaseCharacteristicsContract.View view) {
        this.view = new WeakReference<>(view);
        interactor = getInteractor();
    }

    @Override
    public BaseCharacteristicsContract.View getView() {
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
    public void getCharacteristics() {
        interactor.fetchCharacteristics();
    }


    public abstract BaseCharacteristicsContract.Interactor getInteractor();

    @Override
    public void renderView(List<Characteristic> data) {
        getView().renderSettings(data);
    }
}
