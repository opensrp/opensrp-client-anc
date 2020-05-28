package org.smartregister.anc.library.fragment;

import org.junit.Before;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.anc.library.AncLibrary;
import org.smartregister.anc.library.activity.BaseUnitTest;

public class MeFragmentTest extends BaseUnitTest {
    @Mock
    private AncLibrary ancLibrary;

    private MeFragment meFragment;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        meFragment = Mockito.spy(MeFragment.class);
    }
}
