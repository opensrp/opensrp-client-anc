package org.smartregister.anc.library.presenter;

import android.view.View;

import com.google.common.collect.ImmutableMap;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.smartregister.anc.library.activity.BaseUnitTest;
import org.smartregister.anc.library.contract.SiteCharacteristicsContract;

import java.lang.ref.WeakReference;

/**
 * Created by ndegwamartin on 30/08/2018.
 */
public class CharacteristicsPresenterTest extends BaseUnitTest {

    private SiteCharacteristicsContract.Presenter presenter;

    @Mock
    private SiteCharacteristicsContract.View view;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        presenter = new CharacteristicsPresenter(view);
    }

    @Test
    public void testLaunchSiteCharacteristicsFormInvokesMethodOnViewCorrectly() {

        presenter.launchSiteCharacteristicsForm();
        Mockito.verify(view).launchSiteCharacteristicsSettingsForm();
    }

    @Test
    public void testLaunchSiteCharacteristicsSettingsFormForEditInvokesMethodOnViewCorrectly() {
        CharacteristicsPresenter presenter = new CharacteristicsPresenter(view);
        CharacteristicsPresenter presenterSpy = Mockito.spy(presenter);
        Mockito.doReturn(ImmutableMap.of(TEST_STRING, TEST_STRING)).when(presenterSpy).getSettingsMapByType(ArgumentMatchers.anyString());
        presenterSpy.launchSiteCharacteristicsFormForEdit();
        Mockito.verify(view).launchSiteCharacteristicsSettingsFormForEdit(ArgumentMatchers.anyMap());
    }

    @Test
    public void testOnDestroySetsViewFieldObjectToNull() {

        WeakReference<View> view = Whitebox.getInternalState(presenter, "view");
        Assert.assertNotNull(view);

        presenter.onDestroy(true);

        view = Whitebox.getInternalState(presenter, "view");
        Assert.assertNull(view);

    }

}
