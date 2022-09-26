//package org.smartregister.anc.library.task;
//
//import static org.robolectric.Shadows.shadowOf;
//
//import android.content.Context;
//import android.content.Intent;
//import android.os.Looper;
//
//import org.jeasy.rules.api.Facts;
//import org.junit.Before;
//import org.junit.Test;
//import org.mockito.ArgumentMatchers;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.stubbing.Answer;
//import org.powermock.reflect.Whitebox;
//import org.smartregister.anc.library.activity.BaseUnitTest;
//import org.smartregister.anc.library.contract.ProfileContract;
//import org.smartregister.util.AppExecutors;
//
//import java.util.concurrent.Executor;
//
//public class LoadContactSummaryDataTaskTest extends BaseUnitTest {
//    Executor executor;
//    @Mock
//    private ProfileContract.Presenter mProfilePresenter;
//    private LoadContactSummaryDataTask loadContactSummaryDataTaskMock;
//    @Mock
//    private Context context;
//    @Mock
//    private Intent intent;
//    private Facts facts;
//    private LoadContactSummaryDataTask loadContactSummaryDataTask;
//    @Mock
//    private AppExecutors appExecutors;
//
//    @Before
//    public void setUp() {
//        loadContactSummaryDataTaskMock = Mockito.mock(LoadContactSummaryDataTask.class);
//        context = Mockito.mock(Context.class);
//        intent = Mockito.mock(Intent.class);
//        facts = new Facts();
//        Whitebox.setInternalState(loadContactSummaryDataTaskMock, "context", context);
//        Whitebox.setInternalState(loadContactSummaryDataTaskMock, "intent", intent);
//        Whitebox.setInternalState(loadContactSummaryDataTaskMock, "mProfilePresenter", mProfilePresenter);
//        Whitebox.setInternalState(loadContactSummaryDataTaskMock, "baseEntityId", DUMMY_BASE_ENTITY_ID);
//        Whitebox.setInternalState(loadContactSummaryDataTaskMock, "facts", facts);
//        Whitebox.setInternalState(loadContactSummaryDataTaskMock, "appExecutors", appExecutors);
//        executor = Mockito.mock(Executor.class);
//        appExecutors = Mockito.mock(AppExecutors.class);
//        loadContactSummaryDataTask = new LoadContactSummaryDataTask(context, intent, mProfilePresenter, facts, DUMMY_BASE_ENTITY_ID);
//    }
//
//    @Test
//    public void testOnProcess() throws Exception {
//        execute();
//        Thread.sleep(ASYNC_TIMEOUT);
//        Mockito.verify(loadContactSummaryDataTaskMock, Mockito.atLeastOnce()).onProcess();
//    }
//
//    @Test
//    public void testShowDialog() throws Exception {
//        Mockito.doReturn("Please wait").when(context).getString(ArgumentMatchers.anyInt());
//        execute();
//        shadowOf(Looper.getMainLooper()).idle();
//        Mockito.verify(loadContactSummaryDataTaskMock, Mockito.atLeastOnce()).showDialog();
//    }
//
//    @Test
//    public void testFinishAdapterOnPostExecute() throws Exception {
//        execute();
//        Mockito.verify(loadContactSummaryDataTaskMock, Mockito.atLeastOnce()).finishAdapterOnPostExecute();
//    }
//
//    private void execute() throws Exception {
//        Mockito.doAnswer((Answer<Void>) invocation -> {
//            Runnable runnable = invocation.getArgument(0);
//            runnable.run();
//            return null;
//        }).when(executor).execute(Mockito.any(Runnable.class));
//        Mockito.when(appExecutors.mainThread()).thenReturn(executor);
//        loadContactSummaryDataTask.execute();
//        Thread.sleep(ASYNC_TIMEOUT);
//    }
//}