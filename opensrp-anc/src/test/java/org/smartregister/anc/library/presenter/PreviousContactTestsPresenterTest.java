package org.smartregister.anc.library.presenter;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

import org.jeasy.rules.api.Facts;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricTestRunner;
import org.smartregister.anc.library.AncLibrary;
import org.smartregister.anc.library.activity.BaseUnitTest;
import org.smartregister.anc.library.contract.PreviousContactsTests;
import org.smartregister.anc.library.domain.LastContactDetailsWrapper;
import org.smartregister.anc.library.domain.YamlConfig;
import org.smartregister.anc.library.domain.YamlConfigItem;
import org.smartregister.anc.library.helper.AncRulesEngineHelper;
import org.smartregister.anc.library.repository.PreviousContactRepository;
import org.smartregister.anc.library.util.FilePathUtils;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.emory.mathcs.backport.java.util.Collections;

/**
 * This allows integration of both powermock and robolectric
 * PowerMockIgnore annotations excludes the classes specified as params to avoid having duplicates
 */
@PowerMockRunnerDelegate(RobolectricTestRunner.class)
@PowerMockIgnore({
        "org.powermock.*", "org.mockito.*", "org.robolectric.*", "android.*", "androidx.*",
        "javax.xml.parsers.*", "com.sun.org.apache.xerces.internal.jaxp.*"
})
public class PreviousContactTestsPresenterTest extends BaseUnitTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    @Mock
    private PreviousContactsTests.View profileView;

    @Mock
    private PreviousContactsTests.Presenter previousContactTestPresenter;

    @Mock
    private AncLibrary ancLibrary;

    @Mock
    private AncRulesEngineHelper rulesEngineHelper;

    @Mock
    private PreviousContactRepository previousContactRepository;

    @Captor
    private ArgumentCaptor<List<LastContactDetailsWrapper>> listArgumentCaptor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        previousContactTestPresenter = new PreviousContactTestsPresenter(profileView);
    }

    @PrepareForTest(AncLibrary.class)
    @Test
    public void testLoadPreviousContactsTest() throws ParseException, IOException {
        PowerMockito.mockStatic(AncLibrary.class);
        PowerMockito.when(AncLibrary.getInstance()).thenReturn(ancLibrary);
        //Create dummy yml configuration settings
        Iterable<Object> testsRuleObjects = new ArrayList<>();

        populateYamlConfiRules((ArrayList<Object>) testsRuleObjects);

        PowerMockito.when(ancLibrary.getPreviousContactRepository()).thenReturn(previousContactRepository);
        PowerMockito.when(AncLibrary.getInstance().readYaml(FilePathUtils.FileUtils.PROFILE_LAST_CONTACT_TEST)).thenReturn(testsRuleObjects);
        PowerMockito.when(ancLibrary.getAncRulesEngineHelper()).thenReturn(rulesEngineHelper);
        String baseEntityId = BaseUnitTest.DUMMY_BASE_ENTITY_ID;
        String contactNo = "1";
        String lastContactDate = "2019-09-01";
        //Initialize dummy previous contact facts
        Facts previousContactFacts = new Facts();
        previousContactFacts.put("ultrasound_date", "24-05-2019");
        previousContactFacts.put("ultrasound", "done");
        previousContactFacts.put("syph_test_status", "done");
        previousContactFacts.put("syph_test_type", "bad_syphilis");
        previousContactFacts.put("hiv_prep", "done");
        previousContactFacts.put("no_of_fetuses", "2");
        previousContactFacts.put("vita_supp", "done");
        previousContactFacts.put("tobacco_user", "no");

        Mockito.doReturn(previousContactFacts).when(previousContactRepository).getPreviousContactTestsFacts(baseEntityId);

        previousContactTestPresenter.loadPreviousContactsTest(baseEntityId, contactNo, lastContactDate);
        //Verify that the recyclerview adapter is refreshed with new data
        verify(profileView, atLeastOnce()).setUpContactTestsDetailsRecycler(listArgumentCaptor.capture());
    }

    private void populateYamlConfiRules(ArrayList<Object> testsRuleObjects) {
        List configItem1 = Collections.unmodifiableList(Arrays.asList(
                new YamlConfigItem("Ultrasound test: {ultrasound}", "ultrasound != ''", null),
                new YamlConfigItem("Ultrasound done date: {ultrasound_date}", "ultrasound_date != ''", null),
                new YamlConfigItem("Ultrasound not done reason: {ultrasound_notdone}", "ultrasound_notdone != ''", null)));

        List configItem2 = Collections.unmodifiableList(Arrays.asList(
                new YamlConfigItem("Syphilis test status: {syph_test_status}", "syph_test_status != ''", null),
                new YamlConfigItem("Syphilis test done date: {syphilis_test_date}", "syphilis_test_date != ''", null),
                new YamlConfigItem("Syphilis test type: {syph_test_type}", "syph_test_type != ''", "true")));

        List configItem3 = Collections.unmodifiableList(Arrays.asList(
                new YamlConfigItem("Blood type test status: {blood_type_test_status}", "blood_type_test_status != ''", null),
                new YamlConfigItem("Blood Type: {blood_type}", "blood_type != ''", null),
                new YamlConfigItem("Blood type test done date: {blood_type_test_date}", "blood_type_test_date != ''", null)));

        testsRuleObjects.add(new YamlConfig(null, "ultrasound_tests_results", configItem1, null));
        testsRuleObjects.add(new YamlConfig(null, "syphilis_test_results", configItem2, null));
        testsRuleObjects.add(new YamlConfig(null, "blood_type_test_results", configItem3, null));
    }
}
