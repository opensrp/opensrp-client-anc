package org.smartregister.anc.library.adapter;

import android.widget.LinearLayout;

import org.jetbrains.annotations.NotNull;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.anc.library.activity.BaseUnitTest;
import org.smartregister.anc.library.model.ContactSummaryModel;
import org.smartregister.anc.library.util.ConstantsUtils;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by ndegwamartin on 20/08/2018.
 */
public class ContactScheduleAdapterTest extends BaseUnitTest {

    private ContactScheduleAdapter adapter;

    @Before
    public void setUp() {
        List<ContactSummaryModel> data = getContactSummaryModels();
        MockitoAnnotations.initMocks(this);
        adapter = new ContactScheduleAdapter(RuntimeEnvironment.application, data);
    }

    @NotNull
    private List<ContactSummaryModel> getContactSummaryModels() {
        List<ContactSummaryModel> data = new ArrayList<>();
        ContactSummaryModel newModel = new ContactSummaryModel();
        newModel.setContactDate("2019-04-30");
        newModel.setContactName("Contact 1");
        newModel.setContactWeeks("20");

        LocalDate localDate = new LocalDate("2019-07-23");
        LocalDate lmpDate = localDate.minusWeeks(ConstantsUtils.DELIVERY_DATE_WEEKS);
        newModel.setLocalDate(lmpDate.plusWeeks(Integer.valueOf("20")).toDate());
        data.add(newModel);
        return data;
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

    @Test
    public void testGenerateTimeAway() {
        try {
            String contactDate = "2019-01-01";
            int timeAway = Whitebox.invokeMethod(adapter, "generateTimeAway", contactDate);
            Assert.assertNotEquals(0, timeAway);
        } catch (Exception e) {
            Timber.e(e, this.getClass().getCanonicalName() + " --> testGenerateTimeAway");
        }
    }

    @Test
    public void testOnBindViewHolder() {
        LinearLayout viewGroup = new LinearLayout(RuntimeEnvironment.application);
        viewGroup.setLayoutParams(new LinearLayout.LayoutParams(100, 200));
        ContactScheduleAdapter.ViewHolder viewHolder = adapter.onCreateViewHolder(viewGroup, 0);
        Assert.assertNotNull(viewHolder);

        Whitebox.getInternalState(adapter, "contactsSchedule");
        adapter.onBindViewHolder(viewHolder, 0);
    }
}
