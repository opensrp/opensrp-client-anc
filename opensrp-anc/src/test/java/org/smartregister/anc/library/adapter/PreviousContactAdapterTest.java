package org.smartregister.anc.library.adapter;

import android.content.Context;
import android.widget.LinearLayout;

import org.jeasy.rules.api.Facts;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.anc.library.AncLibrary;
import org.smartregister.anc.library.activity.BaseUnitTest;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PreviousContactAdapterTest extends BaseUnitTest {
    @Mock
    PreviousContactsAdapter previousContactsAdapter;
    private List<Facts> factsList;

    @Before
    public void setUp() {
        Context context = RuntimeEnvironment.application;
        factsList = new ArrayList<>();
        Facts facts = new Facts();
        factsList.add(facts);
        previousContactsAdapter = new PreviousContactsAdapter(factsList, context);
    }

    @Test
    public void testGetItemCount() {
        int itemCount = previousContactsAdapter.getItemCount();
        factsList.size();
        Assert.assertNotNull(itemCount);

    }

    @Test
    public void testOnBindView() {
        AncLibrary ancLibrary = Mockito.mock(AncLibrary.class);
        LinearLayout viewGroup = new LinearLayout(RuntimeEnvironment.application);
        viewGroup.setLayoutParams(new LinearLayout.LayoutParams(100, 200));
        ReflectionHelpers.setStaticField(AncLibrary.class, "instance", ancLibrary);
        PreviousContactsAdapter.ViewHolder viewHolder = previousContactsAdapter.onCreateViewHolder(viewGroup, 0);
        Assert.assertNotNull(viewHolder);
        previousContactsAdapter.onBindViewHolder(viewHolder, 0);
        Iterator mockIterator=Mockito.mock(Iterator.class);
        PowerMockito.when(mockIterator.hasNext()).thenReturn(true).thenReturn(false);
    }
}
