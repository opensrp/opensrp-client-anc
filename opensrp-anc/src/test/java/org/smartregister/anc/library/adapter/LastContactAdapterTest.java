package org.smartregister.anc.library.adapter;


import android.widget.LinearLayout;

import org.jeasy.rules.api.Facts;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.anc.library.AncLibrary;
import org.smartregister.anc.library.activity.BaseUnitTest;
import org.smartregister.anc.library.domain.LastContactDetailsWrapper;
import org.smartregister.anc.library.domain.YamlConfigItem;
import org.smartregister.anc.library.domain.YamlConfigWrapper;
import org.smartregister.anc.library.helper.AncRulesEngineHelper;

import java.util.ArrayList;
import java.util.List;

public class LastContactAdapterTest extends BaseUnitTest {
    private LastContactAdapter lastContactAdapter;
    @Mock
    private List<LastContactDetailsWrapper> lastContactDetailsList;
    @Mock
    private AncLibrary ancLibrary;
    @Mock
    private AncRulesEngineHelper ancRulesEngineHelper;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        lastContactAdapter = new LastContactAdapter(lastContactDetailsList, RuntimeEnvironment.application);
        ReflectionHelpers.setStaticField(AncLibrary.class, "instance", ancLibrary);
    }

    @Test
    public void testContactSummaryAdapterInstantiatesCorrectly() {
        Assert.assertNotNull(lastContactAdapter);
    }

    @Test
    public void testOnCreateViewHolderReturnsValidViewHolderInstance() {
        LinearLayout viewGroup = new LinearLayout(RuntimeEnvironment.application);
        viewGroup.setLayoutParams(new LinearLayout.LayoutParams(100, 200));
        LastContactAdapter.ViewHolder viewHolder = lastContactAdapter.onCreateViewHolder(viewGroup, 0);
        Assert.assertNotNull(viewHolder);
    }

    @Test
    public void testGetItemCountInvokesGetSizeMethodOfDataList() {
        Whitebox.setInternalState(lastContactAdapter, "lastContactDetailsList", new ArrayList<>());
        Assert.assertEquals(Mockito.anyInt(), lastContactAdapter.getItemCount());
    }

   /* @Test
    public void testOnBindViewHolder() {
        LinearLayout viewGroup = new LinearLayout(RuntimeEnvironment.application);
        viewGroup.setLayoutParams(new LinearLayout.LayoutParams(100, 200));
        LastContactAdapter.ViewHolder viewHolder = lastContactAdapter.onCreateViewHolder(viewGroup, 0);
        Assert.assertNotNull(viewHolder);

        Whitebox.setInternalState(ancLibrary, "ancRulesEngineHelper", new AncRulesEngineHelper(RuntimeEnvironment.application));

        Whitebox.setInternalState(lastContactAdapter, "lastContactDetailsList", getLastContactDetailsList());
        lastContactAdapter.onBindViewHolder(viewHolder, 0);
    }

    private List<LastContactDetailsWrapper> getLastContactDetailsList() {
        List<LastContactDetailsWrapper> lastContactDetailsWrapperList = new ArrayList<>();
        LastContactDetailsWrapper lastContactDetailsWrapper = new LastContactDetailsWrapper("1", "2019-04-01", getYamlConfigWrappers(), new Facts());
        lastContactDetailsWrapperList.add(lastContactDetailsWrapper);
        return lastContactDetailsWrapperList;
    }

    private List<YamlConfigWrapper> getYamlConfigWrappers() {
        List<YamlConfigWrapper> yamlConfigWrapperList = new ArrayList<>();
        YamlConfigWrapper yamlConfigWrapper = new YamlConfigWrapper("overview_of_pregnancy", "current_pregnancy", new YamlConfigItem("GA: {gest_age}", "gest_age != ''", "gest_age > 40"), "Not Sure");
        YamlConfigWrapper yamlConfigWrapper1 = new YamlConfigWrapper("overview_of_pregnancy", "current_pregnancy", new YamlConfigItem("GA: {gest_age}", "gest_age != ''", "gest_age > 20"), "Very Sure");
        yamlConfigWrapperList.add(yamlConfigWrapper);
        yamlConfigWrapperList.add(yamlConfigWrapper1);
        return yamlConfigWrapperList;
    }*/
}
