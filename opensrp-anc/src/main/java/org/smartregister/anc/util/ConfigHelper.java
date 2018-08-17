package org.smartregister.anc.util;

import android.content.Context;

import org.smartregister.anc.R;
import org.smartregister.anc.domain.QuickCheckConfiguration;
import org.smartregister.configurableviews.model.Field;
import org.smartregister.configurableviews.model.RegisterConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigHelper {
    public static RegisterConfiguration defaultRegisterConfiguration(Context context) {
        if (context == null) {
            return null;
        }

        RegisterConfiguration config = new RegisterConfiguration();
        config.setEnableAdvancedSearch(true);
        config.setEnableFilterList(true);
        config.setEnableSortList(true);
        config.setSearchBarText(context.getString(R.string.search_name_or_id));
        config.setEnableJsonViews(false);

        List<Field> filers = new ArrayList<>();
        filers.add(new Field(context.getString(R.string.has_tasks_due), "has_tasks_due"));
        filers.add(new Field(context.getString(R.string.risky_pregnancy), "risky_pregnancy"));
        filers.add(new Field(context.getString(R.string.syphilis_positive), "syphilis_positive"));
        filers.add(new Field(context.getString(R.string.hiv_positive), "hiv_positive"));
        filers.add(new Field(context.getString(R.string.hypertensive), "hypertensive"));
        config.setFilterFields(filers);

        List<Field> sortFields = new ArrayList<>();
        sortFields.add(new Field(context.getString(R.string.updated_recent_first), "updated_at desc"));
        sortFields.add(new Field(context.getString(R.string.ga_older_first), "ga asc"));
        sortFields.add(new Field(context.getString(R.string.ga_younger_first), "ga desc"));
        sortFields.add(new Field(context.getString(R.string.id), "id"));
        sortFields.add(new Field(context.getString(R.string.first_name_a_to_z), "first_name asc"));
        sortFields.add(new Field(context.getString(R.string.first_name_z_to_a), "first_name desc"));
        sortFields.add(new Field(context.getString(R.string.last_name_a_to_z), "last_name asc"));
        sortFields.add(new Field(context.getString(R.string.last_name_z_to_a), "last_name desc"));
        config.setSortFields(sortFields);

        return config;
    }


    public static QuickCheckConfiguration defaultQuickCheckConfiguration(Context context) {
        if (context == null) {
            return null;
        }

        QuickCheckConfiguration config = new QuickCheckConfiguration();

        List<Field> reasons = new ArrayList<>();
        reasons.add(new Field(context.getString(R.string.first_contact), "first_contact"));
        reasons.add(new Field(context.getString(R.string.scheduled_contact), "scheduled_contact"));
        reasons.add(new Field(context.getString(R.string.specific_complaint), "specific_complaint"));
        config.setReasons(reasons);

        List<Field> specificComplaints = new ArrayList<>();
        specificComplaints.add(new Field(context.getString(R.string.abnormal_discharge), "abnormal_discharge"));
        specificComplaints.add(new Field(context.getString(R.string.altered_skin_color), "altered_skin_color"));
        specificComplaints.add(new Field(context.getString(R.string.changes_in_bp), "changes_in_bp"));
        specificComplaints.add(new Field(context.getString(R.string.constipation), "constipation"));
        specificComplaints.add(new Field(context.getString(R.string.contractions), "contractions"));
        specificComplaints.add(new Field(context.getString(R.string.cough), "cough"));
        specificComplaints.add(new Field(context.getString(R.string.depressive_anxious), "depressive_anxious"));
        specificComplaints.add(new Field(context.getString(R.string.dizziness), "dizziness"));
        specificComplaints.add(new Field(context.getString(R.string.domestic_violence), "domestic_violence"));
        specificComplaints.add(new Field(context.getString(R.string.extreme_pelvic_pain), "extreme_pelvic_pain"));
        specificComplaints.add(new Field(context.getString(R.string.fever), "fever"));
        specificComplaints.add(new Field(context.getString(R.string.full_abdominal_pain), "full_abdominal_pain"));
        specificComplaints.add(new Field(context.getString(R.string.flu_symptoms), "flu_symptoms"));
        specificComplaints.add(new Field(context.getString(R.string.fluid_loss), "fluid_loss"));
        specificComplaints.add(new Field(context.getString(R.string.headache), "headache"));
        specificComplaints.add(new Field(context.getString(R.string.heartburn), "heartburn"));
        specificComplaints.add(new Field(context.getString(R.string.leg_cramps), "leg_cramps"));
        specificComplaints.add(new Field(context.getString(R.string.leg_pain), "leg_pain"));
        specificComplaints.add(new Field(context.getString(R.string.leg_redness), "leg_redness"));
        specificComplaints.add(new Field(context.getString(R.string.low_back_pelvic_pain), "low_back_pelvic_pain"));
        specificComplaints.add(new Field(context.getString(R.string.nausea_vomiting_diarrhea), "nausea_vomiting_diarrhea"));
        specificComplaints.add(new Field(context.getString(R.string.no_fetal_movement), "no_fetal_movement"));
        specificComplaints.add(new Field(context.getString(R.string.oedema), "oedema"));
        specificComplaints.add(new Field(context.getString(R.string.other_bleeding), "other_bleeding"));
        specificComplaints.add(new Field(context.getString(R.string.other_pain), "other_pain"));
        specificComplaints.add(new Field(context.getString(R.string.other_psychological_symptoms), "other_psychological_symptoms"));
        specificComplaints.add(new Field(context.getString(R.string.other_skin_disorder), "other_skin_disorder"));
        specificComplaints.add(new Field(context.getString(R.string.other_types_of_voilence), "other_types_of_voilence"));
        specificComplaints.add(new Field(context.getString(R.string.dysuria), "dysuria"));
        specificComplaints.add(new Field(context.getString(R.string.pruritus), "pruritus"));
        specificComplaints.add(new Field(context.getString(R.string.reduced_fetal_movement), "reduced_fetal_movement"));
        specificComplaints.add(new Field(context.getString(R.string.shortness_of_breath), "shortness_of_breath"));
        specificComplaints.add(new Field(context.getString(R.string.tiredness), "tiredness"));
        specificComplaints.add(new Field(context.getString(R.string.trauma), "trauma"));
        specificComplaints.add(new Field(context.getString(R.string.bleeding), "bleeding"));
        specificComplaints.add(new Field(context.getString(R.string.visual_disturbance), "visual_disturbance"));
        specificComplaints.add(new Field(context.getString(R.string.complaint_other_specify), "other_specify"));
        config.setComplaints(specificComplaints);

        List<Field> dangerSigns = new ArrayList<>();
        dangerSigns.add(new Field(context.getString(R.string.danger_none), "danger_none"));
        dangerSigns.add(new Field(context.getString(R.string.danger_bleeding), "danger_bleeding"));
        dangerSigns.add(new Field(context.getString(R.string.central_cyanosis), "central_cyanosis"));
        dangerSigns.add(new Field(context.getString(R.string.convulsing), "convulsing"));
        dangerSigns.add(new Field(context.getString(R.string.danger_fever), "danger_fever"));
        dangerSigns.add(new Field(context.getString(R.string.headache_visual_disturbance), "headache_visual_disturbance"));
        dangerSigns.add(new Field(context.getString(R.string.imminent_delivery), "imminent_delivery"));
        dangerSigns.add(new Field(context.getString(R.string.labour), "labour"));
        dangerSigns.add(new Field(context.getString(R.string.looks_very_ill), "looks_very_ill"));
        dangerSigns.add(new Field(context.getString(R.string.severe_vomiting), "severe_vomiting"));
        dangerSigns.add(new Field(context.getString(R.string.severe_pain), "severe_pain"));
        dangerSigns.add(new Field(context.getString(R.string.severe_abdominal_pain), "severe_abdominal_pain"));
        dangerSigns.add(new Field(context.getString(R.string.unconscious), "unconscious"));
        config.setDangerSigns(dangerSigns);

        Map<String, String> infoMap = new HashMap<>();
        infoMap.put(context.getString(R.string.central_cyanosis), context.getString(R.string.cyanosis_info));
        config.setInfoMap(infoMap);

        return config;
    }


}
