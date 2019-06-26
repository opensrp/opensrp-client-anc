package org.smartregister.anc.library.presenter;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.smartregister.anc.library.activity.BaseUnitTest;
import org.smartregister.anc.library.contract.BaseCharacteristicsContract;
import org.smartregister.anc.library.contract.SiteCharacteristicsContract;
import org.smartregister.anc.library.interactor.SiteCharacteristicsInteractor;

import java.lang.ref.WeakReference;

/**
 * Created by ndegwamartin on 04/09/2018.
 */
public class SiteCharacteristicsPresenterTest extends BaseUnitTest {

    private static String FIELD_INTERACTOR = "interactor";
    private static String FIELD_VIEW = "view";

    @Mock
    private BaseCharacteristicsContract.View view;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSiteCharacteristicsPresenterInstantiatesCorrectly() {


        SiteCharacteristicsPresenter presenter = new SiteCharacteristicsPresenter(view);
        Assert.assertNotNull(presenter);

        SiteCharacteristicsInteractor interactor = Whitebox.getInternalState(presenter, FIELD_INTERACTOR);
        Assert.assertNotNull(interactor);

        WeakReference<SiteCharacteristicsContract.View> view = Whitebox.getInternalState(presenter, FIELD_VIEW);
        Assert.assertNotNull(view);

    }

    @Test
    public void testGetInteractorReturnsValidInstance() {

        SiteCharacteristicsPresenter presenter = new SiteCharacteristicsPresenter(view);
        BaseCharacteristicsContract.Interactor interactor = presenter.getInteractor();

        Assert.assertNotNull(interactor);
    }

    @Test
    public void testOnDestroyShouldNotResetTheInteractorIfIsChangingConfigurationChangeIsTrue() {

        SiteCharacteristicsPresenter presenter = new SiteCharacteristicsPresenter(view);

        BaseCharacteristicsContract.Interactor interactor = Whitebox.getInternalState(presenter, FIELD_INTERACTOR);
        org.junit.Assert.assertNotNull(interactor);

        presenter.onDestroy(true);

        interactor = Whitebox.getInternalState(presenter, FIELD_INTERACTOR);
        Assert.assertNotNull(interactor);
    }

    @Test
    public void testOnDestroyShouldResetTheInteractorIfIsChangingConfigurationChangeIsFalse() {

        SiteCharacteristicsPresenter presenter = new SiteCharacteristicsPresenter(view);

        BaseCharacteristicsContract.Interactor interactor = Whitebox.getInternalState(presenter, FIELD_INTERACTOR);
        org.junit.Assert.assertNotNull(interactor);

        presenter.onDestroy(false);

        interactor = Whitebox.getInternalState(presenter, FIELD_INTERACTOR);
        Assert.assertNull(interactor);

    }

}
