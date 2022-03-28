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
import org.smartregister.anc.library.domain.TestResults;

import java.util.ArrayList;
import java.util.List;

public class LastContactAllTestsResultsDialogAdapterTest extends BaseUnitTest {
    @Mock
    private List<TestResults> mData;
    private LastContactAllTestsResultsDialogAdapter lastContactAllTestsResultsDialogAdapter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        lastContactAllTestsResultsDialogAdapter = new LastContactAllTestsResultsDialogAdapter(RuntimeEnvironment.application, mData);
    }

    @Test
    public void testContactSummaryAdapterInstantiatesCorrectly() {
        Assert.assertNotNull(lastContactAllTestsResultsDialogAdapter);
    }

    @Test
    public void testOnCreateViewHolderReturnsValidViewHolderInstance() {
        LinearLayout viewGroup = new LinearLayout(RuntimeEnvironment.application);
        viewGroup.setLayoutParams(new LinearLayout.LayoutParams(100, 200));
        LastContactAllTestsResultsDialogAdapter.ViewHolder viewHolder = lastContactAllTestsResultsDialogAdapter.onCreateViewHolder(viewGroup, 0);
        Assert.assertNotNull(viewHolder);
    }

    @Test
    public void testGetItemCountInvokesGetSizeMethodOfDataList() {
        Whitebox.setInternalState(lastContactAllTestsResultsDialogAdapter, "mData", getTestResults());
        Assert.assertEquals(2, lastContactAllTestsResultsDialogAdapter.getItemCount());
    }

    private List<TestResults> getTestResults() {
        List<TestResults> testResults = new ArrayList<>();
        TestResults testResult1 = new TestResults("12", "2019-01-01", "Not Sure");
        TestResults testResult2 = new TestResults("113", "2019-04-01", "Very positive");
        testResults.add(testResult1);
        testResults.add(testResult2);
        return testResults;
    }

    @Test
    public void testOnBindViewHolder() {
        LinearLayout viewGroup = new LinearLayout(RuntimeEnvironment.application);
        viewGroup.setLayoutParams(new LinearLayout.LayoutParams(100, 200));
        LastContactAllTestsResultsDialogAdapter.ViewHolder viewHolder = lastContactAllTestsResultsDialogAdapter.onCreateViewHolder(viewGroup, 0);
        Assert.assertNotNull(viewHolder);

        Whitebox.setInternalState(lastContactAllTestsResultsDialogAdapter, "mData", getTestResults());
        lastContactAllTestsResultsDialogAdapter.onBindViewHolder(viewHolder, 0);
    }
}
