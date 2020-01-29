package org.smartregister.anc.library.presenter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.smartregister.anc.library.activity.BaseUnitTest;
import org.smartregister.anc.library.contract.PopulationCharacteristicsContract;
import org.smartregister.anc.library.interactor.PopulationCharacteristicsInteractor;

/**
 * Created by ndegwamartin on 28/08/2018.
 */
public class PopulationCharacteristicsPresenterTest extends BaseUnitTest {

    private static final String FIELD_INTERACTOR = "interactor";
    private PopulationCharacteristicsContract.Presenter presenter;
    @Mock
    private PopulationCharacteristicsContract.View view;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        presenter = new PopulationCharacteristicsPresenter(view);
    }

    @Test
    public void testPopulationCharacteristicsPresenterInstantiatesCorrectly() {
        Assert.assertNotNull(presenter);
    }

    @Test
    public void testOnDestroyShouldNotResetTheInteractorIfIsChangingConfigurationChangeIsTrue() {

        PopulationCharacteristicsInteractor interactor = Whitebox.getInternalState(presenter, FIELD_INTERACTOR);
        Assert.assertNotNull(interactor);

        presenter.onDestroy(true);//configuration change

        interactor = Whitebox.getInternalState(presenter, FIELD_INTERACTOR);
        Assert.assertNotNull(interactor);
    }

    @Test
    public void testOnDestroyShouldResetTheInteractorIfIsChangingConfigurationChangeIsFalse() {

        PopulationCharacteristicsInteractor interactor = Whitebox.getInternalState(presenter, FIELD_INTERACTOR);
        Assert.assertNotNull(interactor);

        presenter.onDestroy(false);//Not configuration change

        interactor = Whitebox.getInternalState(presenter, FIELD_INTERACTOR);
        Assert.assertNull(interactor);

    }
}
