package org.smartregister.anc.repository;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.smartregister.anc.BaseUnitTest;
import org.smartregister.anc.library.repository.PreviousContactRepository;

public class PreviousContactRepositoryTest extends BaseUnitTest {
    @Mock
    PreviousContactRepository previousContactRepository;


    @Before
    public void setUp() {
        previousContactRepository = Mockito.spy(PreviousContactRepository.class);
    }

    @Test
    public void testGetPreviousContactFacts() {
        String baseEntityId = "2ba62e25-3eaf-48e9-bcf7-d5748b023edb";
        String contactNo = "2";
        String jsonArray = "{\n" +
                "   \"ultrasound_date\":\"26-01-2022\",\n" +
                "   \"severe_preeclampsia\":\"0\",\n" +
                "   \"occupation\":\"[formal_employment]\",\n" +
                "   \"isRelevant\":true,\n" +
                "   \"behaviour_persist\":\"[none]\",\n" +
                "   \"select_gest_age_edd_lmp_ultrasound\":\"ultrasound\",\n" +
                "   \"pregest_weight\":\"54\",\n" +
                "   \"flu_immun_status\":\"{\\\"value\\\":\\\"seasonal_flu_dose_given\\\",\\\"text\\\":\\\"anc_profile.step5.flu_immun_status.options.seasonal_flu_dose_given.text\\\"}\",\n" +
                "   \"lmp_edd\":\"19-10-2022\",\n" +
                "   \"select_gest_age_edd\":\"ultrasound\",\n" +
                "   \"bp_diastolic\":\"78\",\n" +
                "   \"select_gest_age_edd_sfh_ultrasound\":\"0\",\n" +
                "   \"medications\":\"[aspirin, doxylamine, folic_acid]\",\n" +
                "   \"danger_signs\":\"[danger_bleeding]\",\n" +
                "   \"medications_value\":\"Aspirin, Doxylamine, Folic Acid\",\n" +
                "   \"height\":\"171\",\n" +
                "   \"eat_exercise_counsel\":\"{\\\"value\\\":\\\"done\\\",\\\"text\\\":\\\"\\\"}\",\n" +
                "   \"mat_percept_fetal_move\":\"{\\\"value\\\":\\\"reduced_fetal_move\\\",\\\"text\\\":\\\"\\\"}\",\n" +
                "   \"preeclampsia\":\"0\",\n" +
                "   \"severe_hypertension\":\"0\",\n" +
                "   \"pulse_rate_repeat\":\"67\",\n" +
                "   \"toaster26_hidden\":\"0\",\n" +
                "   \"no_of_fetuses\":\"1\",\n" +
                "   \"weight_gain\":\"0\",\n" +
                "   \"danger_signs_counsel\":\"{\\\"value\\\":\\\"done\\\",\\\"text\\\":\\\"\\\"}\",\n" +
                "   \"behaviour_persist_value\":\"None\",\n" +
                "   \"other_sym_lbpp_value\":\"Contractions, Pain during urination (dysuria)\",\n" +
                "   \"other_symptoms_value\":\"Cough lasting more than 3 weeks, Headache\",\n" +
                "   \"referred_hosp\":\"{\\\"value\\\":\\\"yes\\\",\\\"text\\\":\\\"\\\"}\",\n" +
                "   \"ipv_suspect\":\"0\",\n" +
                "   \"danger_signs_value\":\"Bleeding vaginally\",\n" +
                "   \"gest_age\":\"21 weeks 0 days\",\n" +
                "   \"tt1_date_done_date_today_hidden\":\"0\",\n" +
                "   \"ultrasound_ga_hidden\":\"0\",\n" +
                "   \"ultrasound_gest_age_wks\":\"21\",\n" +
                "   \"tobacco_user\":\"{\\\"value\\\":\\\"yes\\\",\\\"text\\\":\\\"\\\"}\",\n" +
                "   \"exp_weight_gain\":\"12.5 - 18\",\n" +
                "   \"pulse_rate\":\"56\",\n" +
                "   \"tt_immun_status\":\"{\\\"value\\\":\\\"3_doses\\\",\\\"text\\\":\\\"anc_profile.step5.tt_immun_status.options.3_doses.text\\\"}\",\n" +
                "   \"tt2_dose_number\":\"0\",\n" +
                "   \"weight_gain_duration\":\"1\",\n" +
                "   \"tt1_dose_number\":\"0\",\n" +
                "   \"caffeine_intake\":\"[commercially_brewed_coffee, more_than_48_pieces_squares_of_chocolate]\",\n" +
                "   \"flu_date_done_date_today_hidden\":\"26-01-2022\",\n" +
                "   \"alcohol_substance_counsel\":\"{\\\"value\\\":\\\"done\\\",\\\"text\\\":\\\"\\\"}\",\n" +
                "   \"phys_symptoms_value\":\"Heartburn, Constipation\",\n" +
                "   \"bmi\":\"18.47\",\n" +
                "   \"delivery_place\":\"{\\\"value\\\":\\\"facility\\\",\\\"text\\\":\\\"\\\"}\",\n" +
                "   \"ultrasound_edd\":\"08-06-2022\",\n" +
                "   \"gest_age_openmrs\":\"21\",\n" +
                "   \"tt2_date_done_date_today_hidden\":\"0\",\n" +
                "   \"select_gest_age_edd_all_values\":\"0\",\n" +
                "   \"gdm_risk\":\"0\",\n" +
                "   \"lmp_known_date\":\"12-01-2022\",\n" +
                "   \"ultrasound_date_today_hidden\":\"26-01-2022\",\n" +
                "   \"hiv_risk\":\"0\",\n" +
                "   \"helper\":null,\n" +
                "   \"flu_date\":\"{\\\"value\\\":\\\"done_today\\\",\\\"text\\\":\\\"\\\"}\",\n" +
                "   \"ultrasound_done_date\":\"26-01-2022\",\n" +
                "   \"ultrasound_value\":\"Done today\",\n" +
                "   \"edd\":\"08-06-2022\",\n" +
                "   \"phys_symptoms\":\"[heartburn, constipation]\",\n" +
                "   \"weight_cat\":\"Underweight\",\n" +
                "   \"hiv_positive\":\"0\",\n" +
                "   \"family_planning_counsel\":\"{\\\"value\\\":\\\"done\\\",\\\"text\\\":\\\"\\\"}\",\n" +
                "   \"occupation_value\":\"Formal employment\",\n" +
                "   \"fetal_heartbeat\":\"{\\\"value\\\":\\\"no\\\",\\\"text\\\":\\\"\\\"}\",\n" +
                "   \"bp_systolic\":\"53\",\n" +
                "   \"emergency_hosp_counsel\":\"{\\\"value\\\":\\\"done\\\",\\\"text\\\":\\\"\\\"}\",\n" +
                "   \"first_weight\":\"54\",\n" +
                "   \"sfh_edd\":\"0\",\n" +
                "   \"tot_weight_gain\":\"24\",\n" +
                "   \"current_weight\":\"78\",\n" +
                "   \"lmp_gest_age\":\"2 weeks 0 days\",\n" +
                "   \"lmp_ultrasound_gest_age_selection\":\"{\\\"value\\\":\\\"ultrasound\\\",\\\"text\\\":\\\"\\\"}\",\n" +
                "   \"lmp_known\":\"{\\\"value\\\":\\\"yes\\\",\\\"text\\\":\\\"anc_profile.step2.lmp_known.options.yes.text\\\"}\",\n" +
                "   \"preeclampsia_risk\":\"0\",\n" +
                "   \"partner_hiv_positive\":\"0\",\n" +
                "   \"alcohol_substance_use\":\"[alcohol, marijuana]\",\n" +
                "   \"alcohol_substance_use_value\":\"Alcohol, Marijuana\",\n" +
                "   \"parity\":\"0\",\n" +
                "   \"partner_hiv_status\":\"{\\\"value\\\":\\\"dont_know\\\",\\\"text\\\":\\\"\\\"}\",\n" +
                "   \"contact_reason\":\"{\\\"value\\\":\\\"first_contact\\\",\\\"text\\\":\\\"First contact\\\"}\",\n" +
                "   \"ultrasound_gest_age_concept\":\"0\",\n" +
                "   \"other_sym_lbpp\":\"[contractions, dysuria]\",\n" +
                "   \"ultrasound_done\":\"{\\\"value\\\":\\\"yes\\\",\\\"text\\\":\\\"anc_profile.step2.ultrasound_done.options.yes.text\\\"}\",\n" +
                "   \"educ_level\":\"{\\\"value\\\":\\\"secondary\\\",\\\"text\\\":\\\"\\\"}\",\n" +
                "   \"caffeine_counsel\":\"{\\\"value\\\":\\\"done\\\",\\\"text\\\":\\\"\\\"}\",\n" +
                "   \"other_symptoms\":\"[cough, headache]\",\n" +
                "   \"anaemic\":\"0\",\n" +
                "   \"sfh_ga_hidden\":\"0\",\n" +
                "   \"no_of_fetuses_hidden\":\"0\",\n" +
                "   \"ultrasound_gest_age\":\"21 weeks 0 days\",\n" +
                "   \"caffeine_intake_value\":\"More than 2 cups of coffee (brewed, filtered, instant or espresso), More than 12 bars (50 g) of chocolate\",\n" +
                "   \"ultrasound\":\"done_today\",\n" +
                "   \"marital_status\":\"{\\\"value\\\":\\\"single\\\",\\\"text\\\":\\\"\\\"}\",\n" +
                "   \"tobacco_counsel\":\"{\\\"value\\\":\\\"done\\\",\\\"text\\\":\\\"\\\"}\",\n" +
                "   \"previous_pregnancies\":\"0\",\n" +
                "   \"alcohol_substance_enquiry\":\"{\\\"value\\\":\\\"yes\\\",\\\"text\\\":\\\"\\\"}\",\n" +
                "   \"hypertension\":\"0\",\n" +
                "   \"gravida\":\"1\",\n" +
                "   \"body_temp\":\"35\"\n" +
                "}";
        Mockito.doReturn(jsonArray).when(previousContactRepository.getPreviousContactFacts(baseEntityId, contactNo, false));
        Assert.assertNotNull(previousContactRepository.getPreviousContactFacts(baseEntityId, contactNo, false));
    }
}
