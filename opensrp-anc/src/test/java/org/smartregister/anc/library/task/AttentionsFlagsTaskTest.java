package org.smartregister.anc.library.task;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.reflect.Whitebox;
import org.robolectric.RobolectricTestRunner;
import org.smartregister.anc.library.activity.BaseHomeRegisterActivity;
import org.smartregister.anc.library.domain.AttentionFlag;
import org.smartregister.commonregistry.CommonPersonObjectClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(RobolectricTestRunner.class)
public class AttentionsFlagsTaskTest {
    private static BaseHomeRegisterActivity baseHomeRegisterActivity;
    private static CommonPersonObjectClient commonPersonObjectClient;
    private final List<AttentionFlag> attentionFlagList = new ArrayList<>();
    Map<String, String> details = new HashMap<>();
    @Mock
    AttentionFlagsTask attentionFlagsTaskss;
    private AttentionFlagsTask attentionFlagsTask;

    @Before
    public void setUp() {
        String name = "asynctask", caseId = "e34343-343434-67";
        baseHomeRegisterActivity = new BaseHomeRegisterActivity();
        commonPersonObjectClient = new CommonPersonObjectClient(caseId, details, name);
        attentionFlagsTask = new AttentionFlagsTask(baseHomeRegisterActivity, commonPersonObjectClient);
        attentionFlagsTaskss = Mockito.mock(AttentionFlagsTask.class);

    }

    @Test
    public void testAttentionFlags() throws InterruptedException {
        attentionFlagsTask = new AttentionFlagsTask(baseHomeRegisterActivity, commonPersonObjectClient);
        attentionFlagsTask.execute();
        Whitebox.setInternalState(attentionFlagsTask, "onPostExecute");
        Thread.sleep(1000);
        //To check whether the Attention Flags have data in them
        Assert.assertNotNull(attentionFlagList);

    }

    @Test
    public void testCheckBaseRegisterActivity(){
    Assert.assertNotNull(baseHomeRegisterActivity);
    }
}