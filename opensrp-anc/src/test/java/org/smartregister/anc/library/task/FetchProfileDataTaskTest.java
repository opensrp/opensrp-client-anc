package org.smartregister.anc.library.task;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.reflect.Whitebox;
import org.smartregister.Context;
import org.smartregister.anc.library.AncLibrary;
import org.smartregister.anc.library.activity.BaseUnitTest;
import org.smartregister.anc.library.task.FetchProfileDataTask;

import java.util.Map;

public class FetchProfileDataTaskTest extends BaseUnitTest {
    private final boolean isForEdit = false;
    private Map<String, String> getWomanDetail;
    @Mock
    private FetchProfileDataTask fetchProfileDataTask;
    @Mock
    private Context appContext;

    @Before
    public void setUp() {
        AncLibrary.init(appContext, 2);
        fetchProfileDataTask = Mockito.mock(FetchProfileDataTask.class);
    }

    @Test
    public void testGetWomanDetails() throws Exception {
        FetchProfileDataTask profileDataTask = new FetchProfileDataTask(isForEdit);
        profileDataTask.execute(BaseUnitTest.DUMMY_BASE_ENTITY_ID);
        Thread.sleep(ASYNC_TIMEOUT);
        org.smartregister.anc.library.repository.PatientRepository patientRepository = Mockito.mock(org.smartregister.anc.library.repository.PatientRepository.class);
        getWomanDetail = Whitebox.invokeMethod(fetchProfileDataTask, "getWomanDetailsOnBackground", BaseUnitTest.DUMMY_BASE_ENTITY_ID);
        Assert.assertNotNull(getWomanDetail.size());
    }

    @Test
    public void testPostStickEventOnPostExec() throws Exception {
        PowerMockito.verifyPrivate(fetchProfileDataTask).invoke("postStickEventOnPostExec", getWomanDetail);

    }
}
