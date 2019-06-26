package org.smartregister.anc.library.task;

import android.os.AsyncTask;

import org.smartregister.anc.library.contract.BaseCharacteristicsContract;
import org.smartregister.anc.library.contract.PopulationCharacteristicsContract;
import org.smartregister.anc.library.util.Constants;
import org.smartregister.domain.ServerSetting;
import org.smartregister.sync.helper.ServerSettingsHelper;

import java.util.List;

/**
 * Created by ndegwamartin on 28/08/2018.
 */
public class FetchSiteCharacteristicsTask extends AsyncTask<Void, Void, List<ServerSetting>> {

    private BaseCharacteristicsContract.BasePresenter presenter;

    public FetchSiteCharacteristicsTask(PopulationCharacteristicsContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    protected List<ServerSetting> doInBackground(final Void... params) {
        ServerSettingsHelper helper = new ServerSettingsHelper(Constants.PREF_KEY.SITE_CHARACTERISTICS);
        List<ServerSetting> characteristics = helper.getServerSettings();

        return characteristics;
    }

    @Override
    protected void onPostExecute(final List<ServerSetting> result) {
        presenter.renderView(result);
    }
}
