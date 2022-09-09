package org.smartregister.anc.library.task;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.reflect.Whitebox;
import org.smartregister.anc.library.activity.BaseUnitTest;
import org.smartregister.anc.library.contract.PopulationCharacteristicsContract;
import org.smartregister.anc.library.task.FetchPopulationCharacteristicsTask;
import org.smartregister.domain.ServerSetting;
import org.smartregister.util.AppExecutors;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

public class FetchPopulationCharacteristicsTaskTest extends BaseUnitTest {
    private final List<ServerSetting> settingList = new ArrayList<>();
    Executor executor;
    @Mock
    FetchPopulationCharacteristicsTask fetchPopulationCharacteristicsTask;
    @Mock
    private AppExecutors appExecutors;
    @Mock
    private PopulationCharacteristicsContract.Presenter presenter;

    @Before
    public void setUp() {
        fetchPopulationCharacteristicsTask = Mockito.mock(FetchPopulationCharacteristicsTask.class);
        Whitebox.setInternalState(fetchPopulationCharacteristicsTask, "presenter", presenter);
        Whitebox.setInternalState(fetchPopulationCharacteristicsTask, "appExecutorService", appExecutors);
        executor = Mockito.mock(Executor.class);
        appExecutors = Mockito.mock(AppExecutors.class);
    }

    @Test
    public void testGetServerSettingsService() throws InterruptedException {
        Mockito.doAnswer((Answer<Void>) invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(executor).execute(Mockito.any(Runnable.class));
        Mockito.when(appExecutors.diskIO()).thenReturn(executor);
        FetchPopulationCharacteristicsTask characteristicsTask = new FetchPopulationCharacteristicsTask(presenter);
        characteristicsTask.execute();
        Thread.sleep(1000);
        Mockito.when(fetchPopulationCharacteristicsTask.getServerSettingsService()).thenReturn(settingList);
        Assert.assertNotNull(settingList.size());

    }

    @Test
    public void testRenderView() throws Exception {
        Mockito.doAnswer((Answer<Void>) invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(executor).execute(Mockito.any(Runnable.class));
        Mockito.when(appExecutors.mainThread()).thenReturn(executor);
        FetchPopulationCharacteristicsTask characteristicsTask = new FetchPopulationCharacteristicsTask(presenter);
        characteristicsTask.execute();
        Thread.sleep(1000);
        Mockito.when(fetchPopulationCharacteristicsTask.getServerSettingsService()).thenReturn(settingList);
        PowerMockito.verifyPrivate(fetchPopulationCharacteristicsTask).invoke("renderViewOnPostExec", ArgumentMatchers.any());
    }
}
