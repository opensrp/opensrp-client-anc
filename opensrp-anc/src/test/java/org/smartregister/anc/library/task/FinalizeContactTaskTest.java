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
import org.powermock.reflect.Whitebox;
import org.robolectric.RobolectricTestRunner;
import org.smartregister.anc.library.AncLibrary;
import org.smartregister.anc.library.activity.BaseUnitTest;
import org.smartregister.anc.library.contract.ProfileContract;

import java.lang.ref.WeakReference;
import java.util.HashMap;

@RunWith(RobolectricTestRunner.class)
public class FinalizeContactTaskTest extends BaseUnitTest {

    @Mock
    private HashMap<String, String> newWomanProfileDetails;
    private FinalizeContactTask finalizeContactTask;
    private WeakReference context;
    public  org.smartregister.Context context1;
    private ProfileContract.Presenter mProfilePresenter;
    private Intent intent;

    @Before
    public void setUp() {
        context = Mockito.mock(WeakReference.class);
        finalizeContactTask = new FinalizeContactTask(context, mProfilePresenter, intent);

    }


    @Test
    public void testDoBackground() throws Exception {
        PowerMockito.whenNew(FinalizeContactTask.class).withArguments(context, mProfilePresenter, new Intent()).thenReturn(finalizeContactTask);
        FinalizeContactTask filter = new FinalizeContactTask(context, mProfilePresenter, new Intent()) {
            public FinalizeContactTask callProtectedMethod() {
                doInBackground();
                return this;
            }
        }.callProtectedMethod();
        filter.doInBackground();
    }
}

