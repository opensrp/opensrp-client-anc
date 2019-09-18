package org.smartregister.anc.model;

import org.smartregister.anc.contract.SiteCharacteristicsContract;
import org.smartregister.anc.util.JsonFormUtils;

import java.util.Map;

public class SiteCharacteristicModel implements SiteCharacteristicsContract.Model {


    @Override
    public Map<String, String> processSiteCharacteristics(String jsonString) {
        return JsonFormUtils.processSiteCharacteristics(jsonString);
    }

}
