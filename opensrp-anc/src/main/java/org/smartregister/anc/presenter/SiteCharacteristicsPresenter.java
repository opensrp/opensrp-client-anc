package org.smartregister.anc.presenter;


import org.smartregister.anc.R;
import org.smartregister.anc.contract.SiteCharacteristicsContract;
import org.smartregister.anc.interactor.SiteCharacteristicsInteractor;
import org.smartregister.anc.model.SiteCharacteristicModel;

import java.lang.ref.WeakReference;
import java.util.Map;

/**
 * Created by ndegwamartin on 27/08/2018.
 */
public class SiteCharacteristicsPresenter implements SiteCharacteristicsContract.Presenter {

    private WeakReference<SiteCharacteristicsContract.View> view;
    private SiteCharacteristicsContract.Interactor interactor;
    private SiteCharacteristicsContract.Model model;

    public SiteCharacteristicsPresenter(SiteCharacteristicsContract.View view) {
        this.view = new WeakReference<>(view);
        interactor = new SiteCharacteristicsInteractor();
        model = new SiteCharacteristicModel();
    }

    @Override
    public void onDestroy(boolean isChangingConfiguration) {

        this.view = null;

    }

    @Override
    public void launchSiteCharacteristicsForm() {
        getSiteCharacteristicsView().launchSiteCharacteristicsSettingsForm();
    }

    @Override
    public SiteCharacteristicsContract.View getSiteCharacteristicsView() {
        if (this.view != null) {
            return this.view.get();
        } else {
            return null;
        }
    }

    @Override
    public void saveSiteCharacteristics(String jsonString) {
        getSiteCharacteristicsView().showProgressDialog(R.string.saving_dialog_title);

        Map<String, String> settings = model.processSiteCharacteristics(jsonString);
        interactor.saveSiteCharacteristics(settings);

        getSiteCharacteristicsView().hideProgressDialog();
        getSiteCharacteristicsView().goToHomeRegisterPage();
    }
}
