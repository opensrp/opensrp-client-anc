package org.smartregister.anc.library.job;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.smartregister.anc.library.activity.BaseUnitTest;
import org.smartregister.job.ImageUploadServiceJob;

/**
 * Created by ndegwamartin on 10/09/2018.
 */
public class ImageUploadServiceJobTest extends BaseUnitTest {

    @Mock
    private Context context;

    @Mock
    private ComponentName componentName;

    @Before
    public void setUp() {

        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testOnRunJobStartsCorrectService() throws Exception {

        ImageUploadServiceJob imageUploadServiceJob = new ImageUploadServiceJob();
        ImageUploadServiceJob imageSyncServiceJobSpy = Mockito.spy(imageUploadServiceJob);

        ArgumentCaptor<Intent> intent = ArgumentCaptor.forClass(Intent.class);

        Mockito.doReturn(context).when(imageSyncServiceJobSpy).getApplicationContext();
        Mockito.doReturn(componentName).when(context).startService(ArgumentMatchers.any(Intent.class));

        Whitebox.invokeMethod(imageSyncServiceJobSpy, "onRunJob", (Object) null);

        Mockito.verify(context).startService(intent.capture());

        Assert.assertEquals("org.smartregister.service.ImageUploadSyncService", intent.getValue().getComponent().getClassName());
    }
}
