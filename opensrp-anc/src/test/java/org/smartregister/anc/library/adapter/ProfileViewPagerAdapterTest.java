package org.smartregister.anc.library.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.smartregister.anc.library.activity.BaseUnitTest;
import org.smartregister.anc.library.fragment.ProfileContactsFragment;
import org.smartregister.anc.library.fragment.ProfileOverviewFragment;

import java.util.List;

public class ProfileViewPagerAdapterTest extends BaseUnitTest {
    @Mock
    private ProfileOverviewFragment profileOverviewFragment;
    @Mock
    private ProfileContactsFragment profileContactsFragment;
    @Mock
    private FragmentManager mFragmentManager;
    private ProfileViewPagerAdapter mProfileViewPagerAdapter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mProfileViewPagerAdapter = new ProfileViewPagerAdapter(mFragmentManager);
    }

    @Test
    public void testGetItemCountInvokesGetSizeMethodOfDataList() {
        mProfileViewPagerAdapter.addFragment(profileOverviewFragment, "Profile Overview");
        mProfileViewPagerAdapter.addFragment(profileContactsFragment, "Profile Contact");
        Assert.assertEquals(2, mProfileViewPagerAdapter.getCount());
    }

    @Test
    public void testAddFragment() {
        mProfileViewPagerAdapter.addFragment(profileOverviewFragment, "Profile Overview");
        mProfileViewPagerAdapter.addFragment(profileContactsFragment, "Profile Contact");
        List<Fragment> fragmentList = Whitebox.getInternalState(mProfileViewPagerAdapter, "mFragmentList");
        List<String> fragmentTitles = Whitebox.getInternalState(mProfileViewPagerAdapter, "mFragmentTitleList");

        Assert.assertNotNull(fragmentList);
        Assert.assertNotNull(fragmentTitles);

        Assert.assertEquals(2, fragmentList.size());
        Assert.assertEquals(2, fragmentTitles.size());
    }

    @Test
    public void testGetPageTitle() {
        mProfileViewPagerAdapter.addFragment(profileOverviewFragment, "Profile Overview");
        String title = String.valueOf(mProfileViewPagerAdapter.getPageTitle(0));

        Assert.assertEquals("Profile Overview", title);
    }

    @Test
    public void testGetItem() {
        mProfileViewPagerAdapter.addFragment(profileOverviewFragment, "Profile Overview");
        Fragment fragment = mProfileViewPagerAdapter.getItem(0);

        Assert.assertNotNull(fragment);
        Assert.assertEquals(profileOverviewFragment, fragment);
    }

}
