package org.smartregister.anc.task;

import android.os.AsyncTask;

import org.smartregister.anc.contract.PopulationCharacteristicsContract;
import org.smartregister.anc.util.Constants;
import org.smartregister.domain.ServerSetting;
import org.smartregister.sync.helper.ServerSettingsHelper;

import java.util.List;

/**
 * Created by ndegwamartin on 28/08/2018.
 */
public class FetchPopulationCharacteristicsTask extends AsyncTask<Void, Void, List<ServerSetting>> {

    private PopulationCharacteristicsContract.Presenter presenter;

    public FetchPopulationCharacteristicsTask(PopulationCharacteristicsContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    protected List<ServerSetting> doInBackground(final Void... params) {
        ServerSettingsHelper helper = new ServerSettingsHelper(Constants.PREF_KEY.POPULATION_CHARACTERISTICS);
        return helper.getServerSettings();
    }

    @Override
    protected void onPostExecute(final List<ServerSetting> result) {
        presenter.renderView(result);
    }
}
