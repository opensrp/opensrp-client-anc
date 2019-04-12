package org.smartregister.anc.adapter;

import android.widget.LinearLayout;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.anc.activity.BaseUnitTest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ndegwamartin on 20/08/2018.
 */
public class ContactScheduleAdapterTest extends BaseUnitTest {

    private ContactScheduleAdapter adapter;

    @Before
    public void setUp() {
        List<String> data = new ArrayList<>();
        data.add("33:2019-04-14");
        data.add("23:2018-12-23");
        data.add("34:2019-04-15");
        MockitoAnnotations.initMocks(this);
        adapter = new ContactScheduleAdapter(RuntimeEnvironment.application, data);
    }

    @Test
    public void testPopulationCharacteristicsAdapterInstantiatesCorrectly() {
        Assert.assertNotNull(adapter);
    }

    @Test
    public void testOnCreateViewHolderReturnsValidViewHolderInstance() {
        LinearLayout viewGroup = new LinearLayout(RuntimeEnvironment.application);
        viewGroup.setLayoutParams(new LinearLayout.LayoutParams(100, 200));
        ContactScheduleAdapter.ViewHolder viewHolder = adapter.onCreateViewHolder(viewGroup, 0);
        Assert.assertNotNull(viewHolder);
    }

    @Test
    public void testGetItemCountInvokesGetSizeMethodOfDataList() {
        Assert.assertEquals(3, adapter.getItemCount());
    }

    @Test
    public void testGenerateWeeksAway() {
        int weeks = adapter.generateTimeAway("2019-04-30");
        Assert.assertEquals(2, weeks);
    }
}
