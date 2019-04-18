package org.smartregister.anc.adapter;

import android.widget.LinearLayout;

import junit.framework.Assert;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.anc.activity.BaseUnitTest;
import org.smartregister.anc.model.ContactSummaryModel;
import org.smartregister.anc.util.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ndegwamartin on 20/08/2018.
 */
public class ContactScheduleAdapterTest extends BaseUnitTest {

    private ContactScheduleAdapter adapter;

    @Before
    public void setUp() {
        List<ContactSummaryModel> data = new ArrayList<>();
        ContactSummaryModel newModel = new ContactSummaryModel();
        newModel.setContactDate("2019-04-30");
        newModel.setContactName("Contact 1");
        newModel.setContactWeeks("20");

        LocalDate localDate = new LocalDate("2019-07-23");
        LocalDate lmpDate = localDate.minusWeeks(Constants.DELIVERY_DATE_WEEKS);
        newModel.setLocalDate(lmpDate.plusWeeks(Integer.valueOf("20")).toDate());
        data.add(newModel);
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
        Assert.assertEquals(1, adapter.getItemCount());
    }
}
