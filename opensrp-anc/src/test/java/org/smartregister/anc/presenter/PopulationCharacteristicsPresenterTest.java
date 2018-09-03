package org.smartregister.anc.presenter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.smartregister.anc.activity.BaseUnitTest;
import org.smartregister.anc.contract.PopulationCharacteristicsContract;
import org.smartregister.anc.interactor.PopulationCharacteristicsInteractor;

/**
 * Created by ndegwamartin on 28/08/2018.
 */
public class PopulationCharacteristicsPresenterTest extends BaseUnitTest {

    private PopulationCharacteristicsContract.Presenter presenter;

    @Mock
    private PopulationCharacteristicsContract.View view;

    private static final String FIELD_INTERACTOR = "interactor";

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
    public void testOnDestroyShouldNotResetThePresenterIfIsChangingConfigurationChangeIsTrue() {

        PopulationCharacteristicsInteractor interactor = Whitebox.getInternalState(presenter, FIELD_INTERACTOR);
        Assert.assertNotNull(interactor);

        presenter.onDestroy(true);//configuration change

        interactor = Whitebox.getInternalState(presenter, FIELD_INTERACTOR);
        Assert.assertNotNull(interactor);
    }

    @Test
    public void testOnDestroyShouldResetThePresenterIfIsChangingConfigurationChangeIsFalse() {

        PopulationCharacteristicsInteractor interactor = Whitebox.getInternalState(presenter, FIELD_INTERACTOR);
        Assert.assertNotNull(interactor);

        presenter.onDestroy(false);//Not configuration change

        interactor = Whitebox.getInternalState(presenter, FIELD_INTERACTOR);
        Assert.assertNull(interactor);

    }
}
