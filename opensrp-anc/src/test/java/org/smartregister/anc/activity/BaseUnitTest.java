package org.smartregister.anc.activity;

import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.smartregister.anc.BuildConfig;
import org.smartregister.anc.R;
import org.smartregister.anc.application.TestAncApplication;

/**
 * Created by ndegwamartin on 27/03/2018.
 */

@RunWith(RobolectricTestRunner.class)
@Config(application = TestAncApplication.class, constants = BuildConfig.class, sdk = 22)
public abstract class BaseUnitTest {

    public static int ASYNC_TIMEOUT = 1000;

    protected static final String DUMMY_USERNAME = "myusername";
    protected static final String DUMMY_PASSWORD = "mypassword";
    protected static final String DUMMY_BASE_ENTITY_ID = "00ts-ime-hcla-0tib-0eht-ma0i";
    protected static final String TEST_STRING = "teststring";
    protected static final int DEFAULT_PROFILE_IMAGE_RESOURCE_ID = R.drawable.ic_woman_with_baby;
    protected static final String WHO_ANC_ID = "12345678";
	protected static final String GLOBAL_IDENTIFIER = "identifier";
    protected static final String NULL_STRING = null;
    protected static final int INITIALS_RESOURCE_ID = R.drawable.bottom_bar_initials_background;
    protected static final String INITIALS_TEXT = "TR";

}
