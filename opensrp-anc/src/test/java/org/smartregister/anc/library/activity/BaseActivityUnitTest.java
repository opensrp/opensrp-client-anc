package org.smartregister.anc.library.activity;

import android.app.Activity;

import org.junit.Before;
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
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.repository.FormDataRepository;
import org.smartregister.repository.Repository;
import org.smartregister.view.activity.DrishtiApplication;

/**
 * Created by ndegwamartin on 24/07/2018.
 */
public abstract class BaseActivityUnitTest extends BaseUnitTest {


    @Mock
    private Repository repository;

    @Mock
    private FormDataRepository formDataRepository;

    public Context context;
    
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        context = Mockito.spy(Context.getInstance());
        context.updateApplicationContext(RuntimeEnvironment.application);
        ReflectionHelpers.setField(context, "formDataRepository", formDataRepository);

        CoreLibrary.init(context);
        AncLibrary.init(context, repository, 1);
        LocationHelper.init(Utils.ALLOWED_LEVELS, Utils.DEFAULT_LOCATION_LEVEL);

        //Auto login by default
        String password = "pwd";
        context.session().start(context.session().lengthInMilliseconds());
        context.configuration().getDrishtiApplication().setPassword(password);
        context.session().setPassword(password);

        DrishtiApplication drishtiApplication = Mockito.mock(DrishtiApplication.class);
        ReflectionHelpers.setStaticField(DrishtiApplication.class, "mInstance", drishtiApplication);

        Mockito.doReturn(repository).when(drishtiApplication).getRepository();
        Mockito.doReturn(false).when(context).IsUserLoggedOut();
    }

    protected void destroyController() {
        try {
            getActivity().finish();
            getActivityController().pause().stop().destroy(); //destroy controller if we can

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.gc();
    }

    protected abstract Activity getActivity();

    protected abstract ActivityController getActivityController();


}
