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
import org.robolectric.RobolectricTestRunner;
import org.smartregister.anc.library.activity.BaseUnitTest;
import org.smartregister.anc.library.contract.ProfileContract;

import java.lang.ref.WeakReference;
import java.util.HashMap;

@RunWith(RobolectricTestRunner.class)
public class FinalizeContactTaskTest extends BaseUnitTest {

    @Mock
    private HashMap<String, String> newWomanProfileDetails;
    private FinalizeContactTask finalizeContactTask;
    private Context context;
    public  org.smartregister.Context context1;
    private ProfileContract.Presenter mProfilePresenter;
    private Intent intent;

    @Before
    public void setUp() {
        context = Mockito.mock(Context.class);
        WeakReference<Context> weakReferenceContext = new WeakReference<>(context);
        finalizeContactTask = new FinalizeContactTask(weakReferenceContext, mProfilePresenter, intent);

    }

//    @Test
//    public void testFinalizeContact() throws InterruptedException {
//        finalizeContactTask = new FinalizeContactTask(context, mProfilePresenter, new Intent());
//        finalizeContactTask.execute();
//        Whitebox.setInternalState(finalizeContactTask, "onPostExecute");
//        Thread.sleep(1000);
//        //To check whether the FinalizeTaks Flags have data in them
//        Assert.assertNotNull(newWomanProfileDetails);
//
//    }

    @Test
    public void testDoBackground() throws Exception {
        WeakReference<Context> weakReferenceContext = new WeakReference<>(context);
        PowerMockito.whenNew(FinalizeContactTask.class).withArguments(weakReferenceContext, mProfilePresenter, new Intent()).thenReturn(finalizeContactTask);
        FinalizeContactTask filter = new FinalizeContactTask(weakReferenceContext, mProfilePresenter, new Intent()) {
            public FinalizeContactTask callProtectedMethod() {
                doInBackground();
                return this;
            }
        }.callProtectedMethod();
        filter.doInBackground();
    }
}

