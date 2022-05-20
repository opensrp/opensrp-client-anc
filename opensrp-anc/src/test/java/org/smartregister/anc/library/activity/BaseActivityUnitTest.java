package org.smartregister.anc.library.activity;

import android.app.Activity;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.anc.library.AncLibrary;
import org.smartregister.anc.library.util.Utils;
import org.smartregister.configurableviews.ConfigurableViewsLibrary;
import org.smartregister.configurableviews.helper.ConfigurableViewsHelper;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.FormDataRepository;
import org.smartregister.repository.Repository;
import org.smartregister.view.activity.DrishtiApplication;

import java.nio.charset.StandardCharsets;

/**
 * Created by ndegwamartin on 24/07/2018.
 */

public abstract class BaseActivityUnitTest extends BaseUnitTest {
    public Context context;

    @Mock
    private Repository repository;

    @Mock
    private FormDataRepository formDataRepository;

    @Mock
    private CoreLibrary coreLibrary;

    @Mock
    private ConfigurableViewsLibrary configurableViewsLibrary;

    public void setUp() {
        MockitoAnnotations.initMocks(this);

        context = Mockito.spy(Context.getInstance());
        context.updateApplicationContext(RuntimeEnvironment.application);
        ReflectionHelpers.setField(context, "formDataRepository", formDataRepository);
        //CoreLibrary.getInstance().context().allSharedPreferences()
        Mockito.doReturn(Mockito.mock(AllSharedPreferences.class)).when(context).allSharedPreferences();
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", coreLibrary);
        Mockito.when(coreLibrary.context()).thenReturn(context);
//        CoreLibrary.init(context);

        // For areas where the library has been initiated wrongly, this will fix that
        ReflectionHelpers.setStaticField(AncLibrary.class, "instance", null);
        AncLibrary.init(context, 1);
        LocationHelper.init(Utils.ALLOWED_LEVELS, Utils.DEFAULT_LOCATION_LEVEL);

        //Auto login by default
        String password = "pwd";
        context.session().start(context.session().lengthInMilliseconds());
        context.configuration().getDrishtiApplication().setPassword(password.getBytes(StandardCharsets.UTF_8));
        context.session().setPassword(password.getBytes(StandardCharsets.UTF_8));

        ReflectionHelpers.setStaticField(ConfigurableViewsLibrary.class, "instance", configurableViewsLibrary);
        ConfigurableViewsHelper configurableViewsHelper = Mockito.mock(ConfigurableViewsHelper.class);
        Mockito.when(configurableViewsLibrary.getConfigurableViewsHelper()).thenReturn(configurableViewsHelper);

        DrishtiApplication drishtiApplication = Mockito.mock(DrishtiApplication.class);
        ReflectionHelpers.setStaticField(DrishtiApplication.class, "mInstance", drishtiApplication);

        Mockito.doReturn(repository).when(drishtiApplication).getRepository();
        Mockito.doReturn(false).when(context).IsUserLoggedOut();
    }

    protected void destroyController() {
        try {
            if (getActivity() != null) {
                getActivity().finish();
                getActivityController().pause().stop().destroy(); //destroy controller if we can
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.gc();
    }

    protected abstract Activity getActivity();

    protected abstract ActivityController getActivityController();

}
