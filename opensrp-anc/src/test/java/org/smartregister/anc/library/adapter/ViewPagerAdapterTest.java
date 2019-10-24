package org.smartregister.anc.library.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

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

public class ViewPagerAdapterTest extends BaseUnitTest {
    @Mock
    private ProfileOverviewFragment profileOverviewFragment;
    @Mock
    private ProfileContactsFragment profileContactsFragment;
    @Mock
    private FragmentManager mFragmentManager;
    private ViewPagerAdapter mViewPagerAdapter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mViewPagerAdapter = new ViewPagerAdapter(mFragmentManager);
    }

    @Test
    public void testGetItemCountInvokesGetSizeMethodOfDataList() {
        mViewPagerAdapter.addFragment(profileOverviewFragment, "Profile Overview");
        mViewPagerAdapter.addFragment(profileContactsFragment, "Profile Contact");
        Assert.assertEquals(2, mViewPagerAdapter.getCount());
    }

    @Test
    public void testAddFragment() {
        mViewPagerAdapter.addFragment(profileOverviewFragment, "Profile Overview");
        mViewPagerAdapter.addFragment(profileContactsFragment, "Profile Contact");
        List<Fragment> fragmentList = Whitebox.getInternalState(mViewPagerAdapter, "mFragmentList");
        List<String> fragmentTitles = Whitebox.getInternalState(mViewPagerAdapter, "mFragmentTitleList");

        Assert.assertNotNull(fragmentList);
        Assert.assertNotNull(fragmentTitles);

        Assert.assertEquals(2, fragmentList.size());
        Assert.assertEquals(2, fragmentTitles.size());
    }

    @Test
    public void testGetPageTitle() {
        mViewPagerAdapter.addFragment(profileOverviewFragment, "Profile Overview");
        String title = String.valueOf(mViewPagerAdapter.getPageTitle(0));

        Assert.assertEquals("Profile Overview", title);
    }

    @Test
    public void testGetItem() {
        mViewPagerAdapter.addFragment(profileOverviewFragment, "Profile Overview");
        Fragment fragment = mViewPagerAdapter.getItem(0);

        Assert.assertNotNull(fragment);
        Assert.assertEquals(profileOverviewFragment, fragment);
    }

}
