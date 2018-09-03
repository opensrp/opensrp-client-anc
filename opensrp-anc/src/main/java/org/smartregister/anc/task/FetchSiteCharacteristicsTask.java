package org.smartregister.anc.task;

import android.os.AsyncTask;

import org.smartregister.anc.application.AncApplication;
import org.smartregister.anc.contract.BaseCharacteristicsContract;
import org.smartregister.anc.contract.PopulationCharacteristicsContract;
import org.smartregister.anc.domain.Characteristic;
import org.smartregister.anc.helper.CharacteristicsHelper;
import org.smartregister.repository.AllSettings;

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
        CharacteristicsHelper helper = new CharacteristicsHelper("site_characteristics");
        List<Characteristic> characteristics = helper.getPopulationCharacteristics();

        AllSettings settings = AncApplication.getInstance().getContext().allSettings();

        for (Characteristic characteristic : characteristics) {
            characteristic.setValue("1".equals(settings.get(characteristic.getKey())));
        }

        return characteristics;
    }

    @Override
    protected void onPostExecute(final List<Characteristic> result) {
        presenter.renderView(result);
    }
}
