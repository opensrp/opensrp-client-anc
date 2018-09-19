package org.smartregister.anc.task;

import android.os.AsyncTask;

import org.smartregister.anc.contract.BaseCharacteristicsContract;
import org.smartregister.anc.contract.PopulationCharacteristicsContract;
import org.smartregister.anc.domain.Characteristic;
import org.smartregister.anc.helper.CharacteristicsHelper;
import org.smartregister.anc.util.Constants;

import java.util.List;

/**
 * Created by ndegwamartin on 28/08/2018.
 */
public class FetchSiteCharacteristicsTask extends AsyncTask<Void, Void, List<Characteristic>> {

    private BaseCharacteristicsContract.BasePresenter presenter;

    public FetchSiteCharacteristicsTask(PopulationCharacteristicsContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    protected List<Characteristic> doInBackground(final Void... params) {
        CharacteristicsHelper helper = new CharacteristicsHelper(Constants.PREF_KEY.SITE_CHARACTERISTICS);
        List<Characteristic> characteristics = helper.getCharacteristics();

        return characteristics;
    }

    @Override
    protected void onPostExecute(final List<Characteristic> result) {
        presenter.renderView(result);
    }
}
