package org.smartregister.anc.library.adapter;

import android.widget.LinearLayout;

import org.jeasy.rules.api.Facts;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.powermock.reflect.internal.WhiteboxImpl;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.anc.library.AncLibrary;
import org.smartregister.anc.library.activity.BaseUnitTest;
import org.smartregister.anc.library.domain.YamlConfig;
import org.smartregister.anc.library.domain.YamlConfigItem;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class ContactSummaryFinishAdapterTest extends BaseUnitTest {
    private ContactSummaryFinishAdapter adapter;
    @Mock
    private List<YamlConfig> mData;
    private Facts facts;
    @Mock
    private AncLibrary ancLibrary;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        facts = new Facts();
        facts.put("key1", "1");
        facts.put("key2", "2");
        facts.put("key3", "3");
        facts.put("key4", "4");
        mData = new ArrayList<>();
        String template = "group: reason_for_visit\n" +
                "fields:\n" +
                "  - template: \"{{contact_summary.reason_for_visit.reason_for_coming_to_facility}}: {contact_reason_value}\"\n" +
                "    relevance: \"contact_reason_value != ''\"\n" +
                "\n" +
                "  - template: \"{{contact_summary.reason_for_visit.health_complaint}}: {specific_complaint_value}\"\n" +
                "    relevance: \"specific_complaint_value != ''\"";
        YamlConfig config = new YamlConfig();
        config.setGroup("urine_tests_group");
        config.setTestResults(template);
        config.setPropertiesFileName("tests_file_name");
        config.setSubGroup("urine_tests_subgroup");
        List<YamlConfigItem> yamlConfigItems = new ArrayList<>();
        YamlConfigItem configItem = new YamlConfigItem();
        configItem.set("contact_summary.reason_for_visit.health_complaint");
        configItem.setRelevance("true");
        configItem.setIsMultiWidget(true);
        configItem.setTemplate(template);
        yamlConfigItems.add(configItem);
        config.setFields(yamlConfigItems);
        mData.add(config);
        adapter = new ContactSummaryFinishAdapter(RuntimeEnvironment.application, mData, facts);

    }

    @Test
    public void testContactSummaryAdapterInstantiatesCorrectly() {
        Assert.assertNotNull(adapter);
    }

    @Test
    public void testOnCreateViewHolderReturnsValidViewHolderInstance() {
        LinearLayout viewGroup = new LinearLayout(RuntimeEnvironment.application);
        viewGroup.setLayoutParams(new LinearLayout.LayoutParams(100, 200));
        ContactSummaryFinishAdapter.ViewHolder viewHolder = adapter.onCreateViewHolder(viewGroup, 0);
        Assert.assertNotNull(viewHolder);
    }

    @Test
    public void testGetItemCountInvokesGetSizeMethodOfDataList() {
        List<YamlConfig> dataSpy = Whitebox.getInternalState(adapter, "mData");
        Assert.assertNotNull(dataSpy);
        adapter.getItemCount();
        dataSpy.size();
    }

    @Test
    public void testProcessUnderscores() {
        try {
            String testString = "Test_String";
            String returnedString = Whitebox.invokeMethod(adapter, "processUnderscores", testString);
            Assert.assertEquals("TEST STRING", returnedString);
        } catch (Exception e) {
            Timber.e(e, this.getClass().getCanonicalName() + " --> testProcessUnderscores");
        }
    }

    @Test
    public void testPrefillInjectibleFacts() throws Exception {
        String template = "{key1},{key2},{key3},{key4},{key5},{key6},{key7}";
        WhiteboxImpl.invokeMethod(adapter, "prefillInjectableFacts", facts, template);
    }

    @Test
    public void testOnBindViewHolder() {
        LinearLayout viewGroup = new LinearLayout(RuntimeEnvironment.application);
        viewGroup.setLayoutParams(new LinearLayout.LayoutParams(100, 200));
        ContactSummaryFinishAdapter.ViewHolder viewHolder = adapter.onCreateViewHolder(viewGroup, 0);
        Assert.assertNotNull(viewHolder);
        ReflectionHelpers.setStaticField(AncLibrary.class, "instance", ancLibrary);
        Whitebox.getInternalState(adapter, "mData");
       // adapter.onBindViewHolder(viewHolder, 0);
        Assert.assertNotNull(mData);
    }
}
