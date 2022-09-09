package org.smartregister.anc.library.task;

import org.smartregister.anc.library.contract.PopulationCharacteristicsContract;
import org.smartregister.anc.library.util.AppExecutors;
import org.smartregister.anc.library.util.ConstantsUtils;
import org.smartregister.domain.ServerSetting;
import org.smartregister.sync.helper.ServerSettingsHelper;

import java.util.List;

/**
 * Created by ndegwamartin on 28/08/2018.
 */
public class FetchPopulationCharacteristicsTask {

    private final PopulationCharacteristicsContract.Presenter presenter;
    private AppExecutors appExecutorService;

    public FetchPopulationCharacteristicsTask(PopulationCharacteristicsContract.Presenter presenter) {
        this.presenter = presenter;
    }

    public void execute() {
        appExecutorService = new AppExecutors();
        appExecutorService.diskIO().execute(() -> {
            List<ServerSetting> result = this.getServerSettingsService();
            appExecutorService.mainThread().execute(() -> this.renderViewOnPostExec(result));
        });
    }

    public List<ServerSetting> getServerSettingsService() {
        ServerSettingsHelper helper = new ServerSettingsHelper(ConstantsUtils.PrefKeyUtils.POPULATION_CHARACTERISTICS);
        return helper.getServerSettings();
    }

    public void renderViewOnPostExec(List<ServerSetting> result) {
        presenter.renderView(result);
    }
}
