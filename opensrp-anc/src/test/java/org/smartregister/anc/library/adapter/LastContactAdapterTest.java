package org.smartregister.anc.library.adapter;


import android.widget.LinearLayout;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.anc.library.AncLibrary;
import org.smartregister.anc.library.activity.BaseUnitTest;
import org.smartregister.anc.library.domain.LastContactDetailsWrapper;

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
}
