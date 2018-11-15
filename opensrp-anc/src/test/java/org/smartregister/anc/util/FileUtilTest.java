package org.smartregister.anc.util;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.smartregister.view.activity.DrishtiApplication;

import java.io.FileOutputStream;

/**
 * Created by ndegwamartin on 13/11/2018.
 */
@RunWith(PowerMockRunner.class)
public class FileUtilTest {

    private static final String DUMMY_STRING = "dummy-test-string";

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @PrepareForTest(DrishtiApplication.class)
    public void testSaveStaticImageToDiskInvokesCorrectMethods() {


        PowerMockito.mockStatic(DrishtiApplication.class);
        PowerMockito.mock(FileOutputStream.class);
        PowerMockito.when(DrishtiApplication.getAppDir()).thenReturn("/images");

        FileUtil.createFileFromPath(DUMMY_STRING);
    }
}
