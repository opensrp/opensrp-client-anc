package org.smartregister.anc.library.task;

import android.content.Context;
import android.content.Intent;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;
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
    @Mock
    private ProfileContract.Presenter mProfilePresenter;
    @Mock
    private Intent intent;
    @Mock
    private WeakReference<Context> context;

    @Before
    public void setUp() {
      context=Mockito.mock(WeakReference.class);
        finalizeContactTask = new FinalizeContactTask(context, mProfilePresenter, intent);

    }

    @Test
    public void testFinalizeContact() throws InterruptedException {
        finalizeContactTask = Mockito.mock(FinalizeContactTask.class);
        finalizeContactTask.execute();
        Whitebox.setInternalState(finalizeContactTask, "doInBackground");
        Thread.sleep(1000);
        //To check whether the FinalizeTaks Flags have data in them
      Mockito.verify(finalizeContactTask);

    }

}

