package org.smartregister.anc.library.task;

import android.content.Context;
import android.content.Intent;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.smartregister.anc.library.activity.BaseUnitTest;
import org.smartregister.anc.library.activity.ContactSummarySendActivity;
import org.smartregister.anc.library.contract.ProfileContract;

import java.lang.ref.WeakReference;
import java.util.HashMap;

@RunWith(PowerMockRunner.class)
public class FinalizeContactTaskTest extends BaseUnitTest {

    private FinalizeContactTask finalizeContactTask;
    private final HashMap<String, String> newWomanProfileDetails = new HashMap<>();
    @Mock
    WeakReference<Context> weakReferenceContext;
    private Context context;
    private ProfileContract.Presenter mProfilePresenter;
    private Intent intent;
    @Mock
    private FinalizeContactTask finalizeContactTaskMock;

    @Before
    public void setUp() {
        context = Mockito.mock(Context.class);
        weakReferenceContext = new WeakReference<>(context);
        finalizeContactTask = new FinalizeContactTask(weakReferenceContext, mProfilePresenter, intent);
        finalizeContactTaskMock = Mockito.mock(FinalizeContactTask.class);

    }

    @Test
    public void testGetProgressDialog() throws Exception {
        PowerMockito.whenNew(FinalizeContactTask.class).withArguments(weakReferenceContext, mProfilePresenter, new Intent()).thenReturn(finalizeContactTask);
        finalizeContactTask.execute();
        Thread.sleep(100);
        intent = new Intent(context, ContactSummarySendActivity.class);
        PowerMockito.whenNew(Intent.class).withArguments(context, ContactSummarySendActivity.class).thenReturn(intent);
        Whitebox.invokeMethod(finalizeContactTaskMock, "getProgressDialog");
        Mockito.verify(finalizeContactTaskMock, Mockito.atLeastOnce()).getProgressDialog();
    }

    @Test
    public void testGetWomanProfileDetails() throws Exception {
        PowerMockito.whenNew(FinalizeContactTask.class).withArguments(weakReferenceContext, mProfilePresenter, new Intent()).thenReturn(finalizeContactTask);
        finalizeContactTask.execute();
        Thread.sleep(1000);
        Whitebox.invokeMethod(finalizeContactTaskMock, "processWomanDetailsServiceWorker");
        Assert.assertNotNull(newWomanProfileDetails);
    }
}