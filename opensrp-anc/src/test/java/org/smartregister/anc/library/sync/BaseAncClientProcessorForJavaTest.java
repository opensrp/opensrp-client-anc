package org.smartregister.anc.library.sync;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.anc.library.activity.BaseUnitTest;
import org.smartregister.anc.library.util.ConstantsUtils;


public class BaseAncClientProcessorForJavaTest extends BaseUnitTest {

    private BaseAncClientProcessorForJava baseAncClientProcessorForJava;

    @Before
    public void setUp() throws Exception {
        baseAncClientProcessorForJava = new BaseAncClientProcessorForJava(RuntimeEnvironment.systemContext);
    }

    @Test
    public void canProcessShouldReturnTrueWhenProvidedAncEventTypes() {
        Assert.assertTrue(baseAncClientProcessorForJava.canProcess(ConstantsUtils.EventTypeUtils.REGISTRATION));
        Assert.assertTrue(baseAncClientProcessorForJava.canProcess(ConstantsUtils.EventTypeUtils.UPDATE_REGISTRATION));
        Assert.assertTrue(baseAncClientProcessorForJava.canProcess(ConstantsUtils.EventTypeUtils.QUICK_CHECK));
        Assert.assertTrue(baseAncClientProcessorForJava.canProcess(ConstantsUtils.EventTypeUtils.CONTACT_VISIT));
        Assert.assertTrue(baseAncClientProcessorForJava.canProcess(ConstantsUtils.EventTypeUtils.CLOSE));
        Assert.assertTrue(baseAncClientProcessorForJava.canProcess(ConstantsUtils.EventTypeUtils.SITE_CHARACTERISTICS));
    }

    @Test
    public void getEventTypesShouldReturn6EvenTypes() {
        Assert.assertEquals(6, baseAncClientProcessorForJava.getEventTypes().size());
    }
}