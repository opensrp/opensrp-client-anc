package org.smartregister.anc.library.task;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.reflect.Whitebox;
import org.smartregister.anc.library.activity.BaseUnitTest;
import org.smartregister.anc.library.task.FetchProfileDataTask;

import java.util.Map;

public class FetchProfileDataTaskTest extends BaseUnitTest {
    private final boolean isForEdit = false;
    private Map<String, String> getWomanDetail;
    @Mock
    private FetchProfileDataTask fetchProfileDataTask;;

    @Before
    public void setUp() {
        fetchProfileDataTask = Mockito.mock(FetchProfileDataTask.class);
    }

    @Test
    public void testGetWomanDetails() throws Exception {
        FetchProfileDataTask profileDataTask = new FetchProfileDataTask(isForEdit);
        profileDataTask.execute(BaseUnitTest.DUMMY_BASE_ENTITY_ID);
        Thread.sleep(ASYNC_TIMEOUT);
        getWomanDetail = Whitebox.invokeMethod(fetchProfileDataTask, "getWomanDetailsOnBackground", BaseUnitTest.DUMMY_BASE_ENTITY_ID);
        Assert.assertNotNull(getWomanDetail.size());
    }

    @Test
    public void testPostStickEventOnPostExec() throws Exception {
        PowerMockito.verifyPrivate(fetchProfileDataTask).invoke("postStickEventOnPostExec", getWomanDetail);

    }
}
