package org.smartregister.anc.library.model;

import org.smartregister.anc.library.contract.SiteCharacteristicsContract;
import org.smartregister.anc.library.util.JsonFormUtils;

import java.util.Map;

public class SiteCharacteristicModel implements SiteCharacteristicsContract.Model {


    @Override
    public Map<String, String> processSiteCharacteristics(String jsonString) {
        return JsonFormUtils.processSiteCharacteristics(jsonString);
    }

}
