package org.smartregister.anc.library.interactor;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.smartregister.anc.library.activity.BaseUnitTest;
import org.smartregister.anc.library.contract.BaseCharacteristicsContract;
import org.smartregister.anc.library.contract.PopulationCharacteristicsContract;

/**
 * Created by ndegwamartin on 28/08/2018.
 */
public class PopulationCharacteristicsInteractorTest extends BaseUnitTest {

    private static final String FIELD_PRESENTER = "presenter";
    private BaseCharacteristicsContract.Interactor interactor;
    @Mock
    private PopulationCharacteristicsContract.Presenter presenter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        interactor = new PopulationCharacteristicsInteractor(presenter);
    }

    @Test
    public void testPopulationCharacteristicsInteractorInstantiatesCorrectly() {
        Assert.assertNotNull(interactor);
    }

    @Test
    public void testOnDestroyShouldNotResetThePresenterIfIsChangingConfigurationChangeIsTrue() {

        PopulationCharacteristicsContract.Presenter presenter = Whitebox.getInternalState(interactor, FIELD_PRESENTER);
        Assert.assertNotNull(presenter);

        interactor.onDestroy(true);//configuration change

        presenter = Whitebox.getInternalState(interactor, FIELD_PRESENTER);
        Assert.assertNotNull(presenter);
    }

    @Test
    public void testOnDestroyShouldResetThePresenterIfIsChangingConfigurationChangeIsFalse() {

        PopulationCharacteristicsContract.Presenter presenter = Whitebox.getInternalState(interactor, FIELD_PRESENTER);
        Assert.assertNotNull(presenter);

        interactor.onDestroy(false);//Not configuration change

        presenter = Whitebox.getInternalState(interactor, FIELD_PRESENTER);
        Assert.assertNull(presenter);

    }
}
