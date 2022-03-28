package org.smartregister.anc.library.adapter;

import android.widget.LinearLayout;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.anc.library.activity.BaseUnitTest;
import org.smartregister.anc.library.model.ContactSummaryModel;
import org.smartregister.domain.ServerSetting;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ContactSummaryAdapterTest extends BaseUnitTest {
    private ContactSummaryAdapter adapter;
    @Mock
    private List<ContactSummaryModel> contactDates;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        adapter = new ContactSummaryAdapter();
        adapter.setContactDates(contactDates);
    }

    @Test
    public void testContactSummaryAdapterInstantiatesCorrectly() {
        Assert.assertNotNull(adapter);
    }

    @Test
    public void testOnCreateViewHolderReturnsValidViewHolderInstance() {
        LinearLayout viewGroup = new LinearLayout(RuntimeEnvironment.application);
        viewGroup.setLayoutParams(new LinearLayout.LayoutParams(100, 200));
        ContactSummaryAdapter.ViewHolder viewHolder = adapter.onCreateViewHolder(viewGroup, 0);
        Assert.assertNotNull(viewHolder);
    }

    @Test
    public void testGetItemCountInvokesGetSizeMethodOfDataList() {
        adapter.setContactDates(getContactSummaryModel());
        List<ServerSetting> dataSpy = Whitebox.getInternalState(adapter, "contactDates");
        Assert.assertNotNull(dataSpy);
        Assert.assertEquals(1, adapter.getItemCount());
    }

    private List<ContactSummaryModel> getContactSummaryModel() {
        List<ContactSummaryModel> contactSummaryModels = new ArrayList<>();
        ContactSummaryModel contactSummaryModel = new ContactSummaryModel();
        contactSummaryModel.setContactDate("2019-01-01");
        contactSummaryModel.setContactName("Contact 1");
        contactSummaryModel.setContactWeeks("23");
        contactSummaryModel.setLocalDate(new Date());
        contactSummaryModels.add(contactSummaryModel);
        return contactSummaryModels;
    }

    @Test
    public void testOnBindViewHolder() {
        LinearLayout viewGroup = new LinearLayout(RuntimeEnvironment.application);
        viewGroup.setLayoutParams(new LinearLayout.LayoutParams(100, 200));
        ContactSummaryAdapter.ViewHolder viewHolder = adapter.onCreateViewHolder(viewGroup, 0);
        Assert.assertNotNull(viewHolder);

        getContactSummaryModel();
        adapter.setContactDates(getContactSummaryModel());
        adapter.onBindViewHolder(viewHolder, 0);
    }
}
