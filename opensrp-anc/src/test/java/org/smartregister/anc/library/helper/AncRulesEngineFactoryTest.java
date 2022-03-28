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
        globalValues.put("pallor", "yes");
        globalValues.put("select-rule", "step2_accordion_hiv");
        Whitebox.setInternalState(ancRulesEngineFactory, "globalValues", globalValues);
        Whitebox.setInternalState(ancRulesEngineFactory, "selectedRuleName", "Test");
        Facts facts = ancRulesEngineFactory.initializeFacts(new Facts());
        Assert.assertEquals(5, facts.asMap().size());
        Assert.assertTrue(facts.asMap().containsKey("global_pallor"));
        Assert.assertEquals("yes", facts.asMap().get("global_pallor"));
    }
}
