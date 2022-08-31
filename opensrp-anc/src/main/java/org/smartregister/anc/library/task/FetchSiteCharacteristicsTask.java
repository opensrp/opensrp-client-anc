package org.smartregister.anc.library.task;

import org.smartregister.anc.library.contract.BaseCharacteristicsContract;
import org.smartregister.anc.library.contract.PopulationCharacteristicsContract;
import org.smartregister.anc.library.util.AppExecutorService;
import org.smartregister.anc.library.util.ConstantsUtils;
import org.smartregister.domain.ServerSetting;
import org.smartregister.sync.helper.ServerSettingsHelper;

import java.util.List;

/**
 * Created by ndegwamartin on 28/08/2018.
 */
public class FetchSiteCharacteristicsTask {

    private final BaseCharacteristicsContract.BasePresenter presenter;
    AppExecutorService appExecutorService;

    public FetchSiteCharacteristicsTask(PopulationCharacteristicsContract.Presenter presenter) {
        this.presenter = presenter;
    }

    /***
     * function that incorporates both background and UI threads
     */
    public void init() {
        appExecutorService=new AppExecutorService();
        /**
         * Background Thread
         */
        appExecutorService.executorService().execute(() -> {
            List<ServerSetting> result = this.getServerSettingsSWorkerService();
            if (!result.isEmpty()) {
                /***
                 * UI Thread
                 */
                appExecutorService.mainThread().execute(() -> this.renderViewOnPostExecute(result));
            }
        });


    }

    protected List<ServerSetting> getServerSettingsSWorkerService() {
        ServerSettingsHelper helper = new ServerSettingsHelper(ConstantsUtils.PrefKeyUtils.SITE_CHARACTERISTICS);
        return helper.getServerSettings();

    }

    protected void renderViewOnPostExecute(List<ServerSetting> result) {
        presenter.renderView(result);
    }
}
