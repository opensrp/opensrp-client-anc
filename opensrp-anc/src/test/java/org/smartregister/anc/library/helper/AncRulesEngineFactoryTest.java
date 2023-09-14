package org.smartregister.anc.library.helper;

import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rule;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.anc.library.activity.BaseUnitTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

public class AncRulesEngineFactoryTest extends BaseUnitTest {
    private AncRulesEngineFactory ancRulesEngineFactory;
    private Map<String, String> globalValues = new HashMap<>();

    @Mock
    private Rule rule;

    @Mock
    private Facts facts;


    @Before
    public void setUp() {
        try {
            globalValues.put("pallor", "{\"value\":\"yes\",\"text\":\"anc_profile.step2.lmp_known.options.yes.text\"}");
            globalValues.put("symptoms", "[{\"value\":\"nausea\",\"text\":\"anc_profile.step2.nausea.text\"},{\"value\":\"headache\",\"text\":\"anc_profile.step2.headache.text\"}]");
            globalValues.put("select-rule", "step2_accordion_hiv");
            MockitoAnnotations.initMocks(this);
            ancRulesEngineFactory = new AncRulesEngineFactory(RuntimeEnvironment.application, globalValues, new JSONObject(DUMMY_JSON_OBJECT));
        } catch (JSONException exception) {
            Timber.e(exception, this.getClass().getCanonicalName() + " --> setup");
        }
    }

    @Test
    public void testRulesEngineFactoryConstructorCreatesValidInstance() {
        Assert.assertNotNull(ancRulesEngineFactory);
    }

    @Test
    public void testBeforeEvaluateWithEmptyRuleAndFacts() {
        Whitebox.setInternalState(ancRulesEngineFactory, "selectedRuleName", "Test");
        boolean isSelectedRule = ancRulesEngineFactory.beforeEvaluate(rule, facts);
        Assert.assertFalse(isSelectedRule);
    }

    @Test
    public void testInitializeFacts() {
        Whitebox.setInternalState(ancRulesEngineFactory, "selectedRuleName", "Test");
        Facts facts = ancRulesEngineFactory.initializeFacts(new Facts());
        Assert.assertEquals(4, facts.asMap().size());
        Assert.assertTrue(facts.asMap().containsKey("global_pallor"));
        Assert.assertTrue(facts.asMap().containsKey("global_symptoms"));
        Assert.assertEquals("yes", facts.asMap().get("global_pallor"));
        Assert.assertTrue(facts.asMap().get("global_symptoms") instanceof ArrayList);
        Assert.assertTrue(((ArrayList<?>) facts.asMap().get("global_symptoms")).contains("nausea"));
        Assert.assertTrue(((ArrayList<?>) facts.asMap().get("global_symptoms")).contains("headache"));
    }
}