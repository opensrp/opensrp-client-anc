package org.smartregister.anc.presenter;


import com.google.common.collect.ImmutableMap;

import org.smartregister.anc.R;
import org.smartregister.anc.application.AncApplication;
import org.smartregister.anc.contract.SiteCharacteristicsContract;
import org.smartregister.anc.interactor.SiteCharacteristicsInteractor;
import org.smartregister.anc.model.SiteCharacteristicModel;
import org.smartregister.anc.util.Constants;
import org.smartregister.repository.AllSettings;

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
    public void launchSiteCharacteristicsFormForEdit() {
        AllSettings allSettings = AncApplication.getInstance().getContext().allSettings();
        Map<String, String> settings = getSettingsMap(allSettings);
        getSiteCharacteristicsView().launchSiteCharacteristicsSettingsFormForEdit(settings);
    }

    protected Map<String, String> getSettingsMap(AllSettings allSettings) {
        return ImmutableMap.of(Constants.SITE_CHARACTERISTICS_KEY.ULTRASOUND, allSettings.get(Constants.SITE_CHARACTERISTICS_KEY.ULTRASOUND),
                Constants.SITE_CHARACTERISTICS_KEY.BP_TOOL, allSettings.get(Constants.SITE_CHARACTERISTICS_KEY.BP_TOOL),
                Constants.SITE_CHARACTERISTICS_KEY.HIV, allSettings.get(Constants.SITE_CHARACTERISTICS_KEY.HIV),
                Constants.SITE_CHARACTERISTICS_KEY.IPV_ASSESS, allSettings.get(Constants.SITE_CHARACTERISTICS_KEY.IPV_ASSESS));


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
        getSiteCharacteristicsView().goToSiteCharacteristicsExitPage();
    }
}
