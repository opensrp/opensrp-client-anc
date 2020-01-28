package org.smartregister.anc.library.util;

import android.content.Context;
import android.support.annotation.NonNull;

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
            ancSiteCharacteristicsTemplate.put(ConstantsUtils.TemplateUtils.SiteCharacteristics.teamId, teamId);
            ancSiteCharacteristicsTemplate.put(ConstantsUtils.TemplateUtils.SiteCharacteristics.team, team);
            ancSiteCharacteristicsTemplate.put(ConstantsUtils.TemplateUtils.SiteCharacteristics.locationId, locationId);
            ancSiteCharacteristicsTemplate.put(ConstantsUtils.TemplateUtils.SiteCharacteristics.providerId, providerId);
        }

        return ancSiteCharacteristicsTemplate;
    }
}
