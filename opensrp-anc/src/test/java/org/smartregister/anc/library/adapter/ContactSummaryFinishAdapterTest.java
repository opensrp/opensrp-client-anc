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
import org.smartregister.anc.library.activity.BaseUnitTest;
import org.smartregister.anc.library.domain.YamlConfig;

import java.util.List;

import timber.log.Timber;

public class ContactSummaryFinishAdapterTest extends BaseUnitTest {
    private ContactSummaryFinishAdapter adapter;
    @Mock
    private List<YamlConfig> mData;
    @Mock
    private Facts facts;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
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
        Mockito.verify(dataSpy).size();
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
}
