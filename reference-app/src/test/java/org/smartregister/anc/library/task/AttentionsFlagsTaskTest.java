package org.smartregister.anc.library.task;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.reflect.Whitebox;
import org.robolectric.RobolectricTestRunner;
import org.smartregister.anc.BaseUnitTest;
import org.smartregister.anc.library.activity.BaseHomeRegisterActivity;
import org.smartregister.anc.library.domain.AttentionFlag;
import org.smartregister.commonregistry.CommonPersonObjectClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(RobolectricTestRunner.class)
public class AttentionsFlagsTaskTest extends BaseUnitTest {
    private static BaseHomeRegisterActivity baseHomeRegisterActivity;
    private static CommonPersonObjectClient commonPersonObjectClient;
    private final List<AttentionFlag> attentionFlagList = new ArrayList<>();
    Map<String, String> details = new HashMap<>();
    private AttentionFlagsTask attentionFlagsTask;

    @Before
    public void setUp() {
        String name = "asynctask";
        String caseId = "e34343-343434-67";
        baseHomeRegisterActivity = new BaseHomeRegisterActivity();
        commonPersonObjectClient = new CommonPersonObjectClient(caseId, details, name);
        attentionFlagsTask = new AttentionFlagsTask(baseHomeRegisterActivity, commonPersonObjectClient);

    }

    @Test
    public void testAttentionFlags() throws InterruptedException {
        attentionFlagsTask = new AttentionFlagsTask(baseHomeRegisterActivity, commonPersonObjectClient);
        attentionFlagsTask.execute();
        Whitebox.setInternalState(attentionFlagsTask, "onPostExecute");
        Thread.sleep(ASYNC_TIMEOUT);
        //To check whether the Attention Flags have data in them
        Assert.assertNotNull(attentionFlagList);

    }
}
