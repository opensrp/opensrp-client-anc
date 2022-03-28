package org.smartregister.anc.library.util;

import android.content.Context;

import androidx.annotation.NonNull;

import org.json.JSONObject;
import org.smartregister.anc.library.AncLibrary;
import org.smartregister.repository.AllSharedPreferences;

public class SiteCharacteristicsFormUtils {

    public static JSONObject structureFormForRequest(@NonNull Context context) throws Exception {
        AllSharedPreferences allSharedPreferences = AncLibrary.getInstance().getContext().userService().getAllSharedPreferences();
        String providerId = allSharedPreferences.fetchRegisteredANM();
        String locationId = allSharedPreferences.fetchDefaultLocalityId(providerId);
        String team = allSharedPreferences.fetchDefaultTeam(providerId);
        String teamId = allSharedPreferences.fetchDefaultTeamId(providerId);

        JSONObject ancSiteCharacteristicsTemplate = TemplateUtils.getTemplateAsJson(context, ConstantsUtils.PrefKeyUtils.SITE_CHARACTERISTICS);
        if (ancSiteCharacteristicsTemplate != null) {
            ancSiteCharacteristicsTemplate.put(ConstantsUtils.TemplateUtils.SiteCharacteristics.TEAM_ID, teamId);
            ancSiteCharacteristicsTemplate.put(ConstantsUtils.TemplateUtils.SiteCharacteristics.TEAM, team);
            ancSiteCharacteristicsTemplate.put(ConstantsUtils.TemplateUtils.SiteCharacteristics.LOCATION_ID, locationId);
            ancSiteCharacteristicsTemplate.put(ConstantsUtils.TemplateUtils.SiteCharacteristics.PROVIDER_ID, providerId);
        }

        return ancSiteCharacteristicsTemplate;
    }
}
