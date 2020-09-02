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
import org.smartregister.anc.library.util.ConstantsUtils;

import java.util.ArrayList;
import java.util.List;

public class LastContactAdapterTest extends BaseUnitTest {
    private LastContactAdapter lastContactAdapter;

    @Mock
    private List<LastContactDetailsWrapper> lastContactDetailsList;

    @Mock
    private AncLibrary ancLibrary;

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
        Assert.assertEquals(0, lastContactAdapter.getItemCount());
    }

    @Test
    public void testIsFirstContactUpdateGABasedOnDueStrategyForContactOne() throws Exception {
        Mockito.doReturn(RuntimeEnvironment.application).when(ancLibrary).getApplicationContext();
        ReflectionHelpers.setStaticField(AncLibrary.class, "instance", ancLibrary);
        LastContactDetailsWrapper lastContactDetailsWrapper = Mockito.mock(LastContactDetailsWrapper.class);

        String result = Whitebox.invokeMethod(lastContactAdapter, "updateGABasedOnDueStrategy",
                "", "1", lastContactDetailsWrapper);
        Assert.assertEquals("-", result);
    }

    @Test
    public void testIsFirstContactUpdateGABasedOnDueStrategyForContactGreaterThanOne() throws Exception {
        Mockito.doReturn(RuntimeEnvironment.application).when(ancLibrary).getApplicationContext();
        ReflectionHelpers.setStaticField(AncLibrary.class, "instance", ancLibrary);
        Facts facts = new Facts();
        facts.put(ConstantsUtils.EDD, "06-06-2020");
        facts.put(ConstantsUtils.CONTACT_DATE, "2020-04-04");

        LastContactDetailsWrapper lastContactDetailsWrapper = Mockito.mock(LastContactDetailsWrapper.class);

        Mockito.doReturn(facts).when(lastContactDetailsWrapper).getFacts();
        List<LastContactDetailsWrapper> lastContactDetailsList = new ArrayList<>();
        lastContactDetailsList.add(lastContactDetailsWrapper);
        lastContactDetailsList.add(lastContactDetailsWrapper);
        Whitebox.setInternalState(lastContactAdapter, "lastContactDetailsList", lastContactDetailsList);

        //when gest_age_openmrs is null
        String resultOne = Whitebox.invokeMethod(lastContactAdapter, "updateGABasedOnDueStrategy",
                "", "2", lastContactDetailsWrapper);
        Assert.assertEquals("31", resultOne);

        facts.put(ConstantsUtils.GEST_AGE_OPENMRS, "32");

        //when gest_age_openmrs is not null
        String resultTwo = Whitebox.invokeMethod(lastContactAdapter, "updateGABasedOnDueStrategy",
                "", "2", lastContactDetailsWrapper);
        Assert.assertEquals("32", resultTwo);
    }
}
