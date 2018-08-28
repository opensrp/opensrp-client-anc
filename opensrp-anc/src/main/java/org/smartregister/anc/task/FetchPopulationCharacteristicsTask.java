package org.smartregister.anc.task;

import android.os.AsyncTask;

import org.smartregister.anc.contract.PopulationCharacteristicsContract;
import org.smartregister.anc.domain.Characteristic;
import org.smartregister.anc.helper.CharacteristicsHelper;

import java.util.List;

/**
 * Created by ndegwamartin on 28/08/2018.
 */
public class FetchPopulationCharacteristicsTask extends AsyncTask<Void, Void, List<Characteristic>> {

    PopulationCharacteristicsContract.Presenter presenter;

    public FetchPopulationCharacteristicsTask(PopulationCharacteristicsContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    protected List<Characteristic> doInBackground(final Void... params) {
        CharacteristicsHelper helper = new CharacteristicsHelper();
        return helper.getPopulationCharacteristics();
    }

    @Override
    protected void onPostExecute(final List<Characteristic> result) {
        presenter.renderView(result);
    }
}
