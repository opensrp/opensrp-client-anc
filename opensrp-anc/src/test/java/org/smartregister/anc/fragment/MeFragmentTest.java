package org.smartregister.anc.fragment;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.smartregister.anc.activity.BaseUnitTest;
import org.smartregister.anc.contract.MeContract;

public class MeFragmentTest extends BaseUnitTest {
    private MeContract.View view;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    @Ignore
    public void testBuildDetailsAreInitialized() {

        //TextView buildDetails = loginActivity.findViewById(R.id.login_build_text_view);
        // Assert.assertNotNull(buildDetails.getText());
    }
}
