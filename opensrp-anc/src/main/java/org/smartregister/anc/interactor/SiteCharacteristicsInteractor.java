package org.smartregister.anc.interactor;

import org.smartregister.anc.application.AncApplication;
import org.smartregister.anc.contract.SiteCharacteristicsContract;

import java.util.Map;

/**
 * Created by ndegwamartin on 13/07/2018.
 */
public class SiteCharacteristicsInteractor implements SiteCharacteristicsContract.Interactor {

    @Override
    public void saveSiteCharacteristics(Map<String, String> siteCharacteristicsSettingsMap) {

        for (Map.Entry<String, String> setting : siteCharacteristicsSettingsMap.entrySet()) {
            AncApplication.getInstance().getContext().allSettings().put(setting.getKey(), setting.getValue());
        }

    }

}
