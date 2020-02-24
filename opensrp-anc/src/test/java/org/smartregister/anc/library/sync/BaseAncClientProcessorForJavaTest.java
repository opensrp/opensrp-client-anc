package org.smartregister.anc.library.sync;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.anc.library.activity.BaseUnitTest;
import org.smartregister.anc.library.util.ConstantsUtils;

import static org.junit.Assert.*;

public class BaseAncClientProcessorForJavaTest extends BaseUnitTest {

    private BaseAncClientProcessorForJava baseAncClientProcessorForJava;

    @Before
    public void setUp() throws Exception {
        baseAncClientProcessorForJava = new BaseAncClientProcessorForJava(RuntimeEnvironment.systemContext);
    }

    @Test
    public void canProcessShouldReturnTrueWhenProvidedAncEventTypes() {
        assertTrue(baseAncClientProcessorForJava.canProcess(ConstantsUtils.EventTypeUtils.REGISTRATION));
        assertTrue(baseAncClientProcessorForJava.canProcess(ConstantsUtils.EventTypeUtils.UPDATE_REGISTRATION));
        assertTrue(baseAncClientProcessorForJava.canProcess(ConstantsUtils.EventTypeUtils.QUICK_CHECK));
        assertTrue(baseAncClientProcessorForJava.canProcess(ConstantsUtils.EventTypeUtils.CONTACT_VISIT));
        assertTrue(baseAncClientProcessorForJava.canProcess(ConstantsUtils.EventTypeUtils.CLOSE));
        assertTrue(baseAncClientProcessorForJava.canProcess(ConstantsUtils.EventTypeUtils.SITE_CHARACTERISTICS));
    }

    @Test
    public void getEventTypesShouldReturn6EvenTypes() {
        assertEquals(6, baseAncClientProcessorForJava.getEventTypes().size());
    }
}