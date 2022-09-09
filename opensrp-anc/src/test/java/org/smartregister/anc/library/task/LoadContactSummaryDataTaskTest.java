package org.smartregister.anc.library.task;

import android.content.Intent;

import org.jeasy.rules.api.Facts;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.Context;
import org.smartregister.anc.library.AncLibrary;
import org.smartregister.anc.library.activity.BaseUnitTest;
import org.smartregister.anc.library.contract.ProfileContract;

public class LoadContactSummaryDataTaskTest extends BaseUnitTest {
    @Mock
    private ProfileContract.Presenter mProfilePresenter;
    private LoadContactSummaryDataTask loadContactSummaryDataTaskMock;
    @Mock
    private Context appContext;
    @Mock
    private android.content.Context context;
    @Mock
    private Intent intent;
    private Facts facts;
    private LoadContactSummaryDataTask loadContactSummaryDataTask;

    @Before
    public void setUp() {
        AncLibrary.init(appContext, 2);
        loadContactSummaryDataTaskMock = Mockito.mock(LoadContactSummaryDataTask.class);
        facts = new Facts();
        loadContactSummaryDataTask = new LoadContactSummaryDataTask(context, intent, mProfilePresenter, facts, DUMMY_BASE_ENTITY_ID);
    }

    @Test
    public void testOnProcess() throws Exception {
        execute();
        PowerMockito.verifyPrivate(loadContactSummaryDataTaskMock).invoke("onProcess");
    }

    @Test
    public void testShowDialog() throws Exception {
        execute();
        PowerMockito.verifyPrivate(loadContactSummaryDataTaskMock).invoke("showDialog");
    }

    @Test
    public void testFinishAdapterOnPostExecute() throws Exception {
        execute();
        PowerMockito.verifyPrivate(loadContactSummaryDataTaskMock).invoke("finishAdapterOnPostExecute");
        Assert.assertNotNull(mProfilePresenter);

    }

    private void execute() throws Exception {
        loadContactSummaryDataTask.execute();
        Thread.sleep(ASYNC_TIMEOUT);
    }
}
