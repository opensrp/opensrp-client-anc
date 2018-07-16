package org.smartregister.anc.presenter;

import android.app.Activity;
import android.view.ViewTreeObserver;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RuntimeEnvironment;
import org.smartregister.anc.R;
import org.smartregister.anc.activity.BaseUnitTest;
import org.smartregister.anc.contract.LoginContract;
import org.smartregister.anc.model.LoginModel;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


/**
 * Created by ndegwamartin on 27/06/2018.
 */
public class LoginPresenterTest extends BaseUnitTest {

    @Mock
    private LoginContract.View view;

    @Mock
    private LoginContract.Interactor interactor;

    @Mock
    private LoginContract.Model model;

    private LoginContract.Presenter presenter;

    @Mock
    ViewTreeObserver viewTreeObserver;

    @Mock
    ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        presenter = new LoginPresenter(view);
    }

    @Test
    public void testGetBuildDateShouldReturnCorrectValue() {
        String dateFormat = "dd MMM yyyy";
        String todaysDate = new SimpleDateFormat(dateFormat, Locale.getDefault()).format(Calendar.getInstance().getTime());
        Assert.assertEquals(todaysDate, presenter.getBuildDate());
    }

    @Test
    public void testIsUserLoggedOutShouldReturnTrue() {
        Assert.assertTrue(presenter.isUserLoggedOut());
    }

    @Test
    public void testGetOpenSRPContextShouldReturnValidValue() {
        Assert.assertNotNull(presenter.getOpenSRPContext());

        LoginPresenter presenter = new LoginPresenter(view);
        presenter.setLoginModel(model);//set mocked model
        presenter.getOpenSRPContext();
        Mockito.verify(model).getOpenSRPContext();
    }

    @Test
    public void testOnDestroyShouldCallInteractorOnDestroyWithCorrectParameter() {

        LoginPresenter presenter = new LoginPresenter(view);
        presenter.setLoginInteractor(interactor);//set mocked interactor

        Mockito.doNothing().when(interactor).onDestroy(ArgumentMatchers.anyBoolean());
        presenter.onDestroy(true);
        Mockito.verify(interactor).onDestroy(true);
        presenter.onDestroy(false);
        Mockito.verify(interactor).onDestroy(false);
    }

    @Test
    public void testAttemptLoginShouldValidateCredentialsCorrectly() {

        LoginPresenter presenter = new LoginPresenter(view);
        presenter.setLoginModel(model);//set mocked model
        presenter.setLoginInteractor(interactor); //set mocked interactor

        Mockito.doReturn(false).when(model).isEmptyUsername(ArgumentMatchers.anyString());
        Mockito.doReturn(true).when(model).isPasswordValid(ArgumentMatchers.anyString());
        presenter.attemptLogin(DUMMY_USERNAME, DUMMY_PASSWORD);
        Mockito.verify(view).resetPaswordError();
        Mockito.verify(view).resetUsernameError();
        Mockito.verify(model).isEmptyUsername(DUMMY_USERNAME);
        Mockito.verify(model).isPasswordValid(DUMMY_PASSWORD);
        Mockito.verify(interactor).login(ArgumentMatchers.any(WeakReference.class), ArgumentMatchers.eq(DUMMY_USERNAME), ArgumentMatchers.eq(DUMMY_PASSWORD));

    }

    @Test
    public void testAttemptLoginShouldCallLoginMethodWithCorrectParametersWhenValidationPasses() {

        LoginPresenter presenter = new LoginPresenter(view);
        presenter.setLoginModel(model);//set mocked model
        presenter.setLoginInteractor(interactor); //set mocked interactor

        Mockito.doReturn(false).when(model).isEmptyUsername(ArgumentMatchers.anyString());
        Mockito.doReturn(true).when(model).isPasswordValid(ArgumentMatchers.anyString());
        presenter.attemptLogin(DUMMY_USERNAME, DUMMY_PASSWORD);
        Mockito.verify(interactor).login(ArgumentMatchers.any(WeakReference.class), ArgumentMatchers.eq(DUMMY_USERNAME), ArgumentMatchers.eq(DUMMY_PASSWORD));

    }

    @Test
    public void testAttemptLoginShouldNotCallLoginMethodWithCorrectParametersWhenValidationFails() {

        LoginPresenter presenter = new LoginPresenter(view);
        presenter.setLoginModel(new LoginModel());//create real model
        presenter.setLoginInteractor(interactor); //set mocked interactor

        presenter.attemptLogin(null, DUMMY_PASSWORD);
        String NULL_USERNAME = null;
        Mockito.verify(view).setUsernameError(R.string.error_field_required);
        Mockito.verify(view).enableLoginButton(true);
        Mockito.verify(interactor, Mockito.times(0)).login(ArgumentMatchers.any(WeakReference.class), ArgumentMatchers.eq(NULL_USERNAME), ArgumentMatchers.eq(DUMMY_PASSWORD));


    }

    @Test
    public void testAttemptLoginShouldNotCallLoginMethodWhenValidationFails() {

        LoginPresenter presenter = new LoginPresenter(view);
        presenter.setLoginModel(model);//set mocked model
        presenter.setLoginInteractor(interactor); //set mocked interactor

        Mockito.doReturn(false).when(model).isPasswordValid(ArgumentMatchers.anyString());

        presenter.attemptLogin(DUMMY_USERNAME, DUMMY_PASSWORD);

        Mockito.verify(interactor, Mockito.times(0)).login(ArgumentMatchers.any(WeakReference.class), ArgumentMatchers.eq(DUMMY_USERNAME), ArgumentMatchers.eq(DUMMY_PASSWORD));

    }

    @Test
    public void testCanvasGlobalLayoutListenerShouldInvokeCorrectProcessLayoutOperations() {
        LoginPresenter presenter = new LoginPresenter(view);
        LoginPresenter presenterSpy = Mockito.spy(presenter);

        ScrollView scrollView = new ScrollView(RuntimeEnvironment.application);
        ScrollView scrollViewSpy = Mockito.spy(scrollView);

        int DUMMY_VIEW_DIMENSION = 200;
        Mockito.doReturn(DUMMY_VIEW_DIMENSION).when(scrollViewSpy).getHeight();
        Mockito.doReturn(DUMMY_VIEW_DIMENSION).when(scrollViewSpy).getWidth();

        Mockito.doReturn(viewTreeObserver).when(scrollViewSpy).getViewTreeObserver();
        Mockito.doNothing().when(viewTreeObserver).removeOnGlobalLayoutListener(globalLayoutListener);
        Mockito.doReturn(view).when(presenterSpy).getLoginView();

        Activity activity = Mockito.mock(Activity.class);
        Mockito.doReturn(activity).when(view).getActivityContext();

        RelativeLayout relativeLayout = new RelativeLayout(RuntimeEnvironment.application);

        RelativeLayout canvasRL = Mockito.spy(relativeLayout);
        Mockito.doReturn(canvasRL).when(activity).findViewById(R.id.login_layout);


        LinearLayout linearLayout = new LinearLayout(RuntimeEnvironment.application);
        linearLayout.setLayoutParams(new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        Mockito.doReturn(linearLayout).when(activity).findViewById(R.id.bottom_section);
        Mockito.doReturn(linearLayout).when(activity).findViewById(R.id.middle_section);

        Mockito.doNothing().when(viewTreeObserver).removeOnGlobalLayoutListener(globalLayoutListener);

        presenterSpy.canvasGlobalLayoutListenerProcessor(scrollViewSpy, globalLayoutListener);

        Mockito.verify(viewTreeObserver, Mockito.times(1)).removeOnGlobalLayoutListener(globalLayoutListener);
        Mockito.verify(canvasRL).setMinimumHeight(ArgumentMatchers.anyInt());
        Assert.assertTrue(canvasRL.getMinimumHeight() > 0);
    }

    @Test
    public void testGetLoginViewShouldReturnCorrectInstance() {
        Assert.assertNotNull(presenter.getLoginView());
        Assert.assertEquals(view, presenter.getLoginView());

        LoginPresenter presenter = new LoginPresenter(null);
        Assert.assertNull(presenter.getLoginView());

    }

    @Test
    public void testGetLoginViewShouldReturnNullIfNoViewIsSet() {
        LoginPresenter presenter = new LoginPresenter(null);
        Assert.assertNull(presenter.getLoginView());

    }

    @Test
    public void testProcessViewCustomizationsShouldPerformCorrectOperations() {

        LoginPresenter presenter = new LoginPresenter(view);
        LoginPresenter spyPresenter = Mockito.spy(presenter);

        Assert.assertNotNull(presenter);
        Assert.assertNotNull(spyPresenter);

        Mockito.doReturn("{\n" +
                "   \"_id\": \"92141b17040021a7ce326194ff0029f7\",\n" +
                "   \"_rev\": \"55-249eb8fa2e7b4e1012314a13cd9ebb9b\",\n" +
                "   \"type\": \"ViewConfiguration\",\n" +
                "   \"serverVersion\": 1527760504591,\n" +
                "   \"identifier\": \"login\",\n" +
                "   \"metadata\": {\n" +
                "       \"type\": \"Login\",\n" +
                "       \"showPasswordCheckbox\": true,\n" +
                "       \"logoUrl\": \"https://static.wixstatic.com/media/038e80_18d8a285652c4270b4ed782a57092411.png\",\n" +
                "       \"background\": {\n" +
                "           \"orientation\": \"BOTTOM_TOP\",\n" +
                "           \"startColor\": \"#4DB6AC\",\n" +
                "           \"endColor\": \"#26A69A\"\n" +
                "       }\n" +
                "   }\n" +
                "}").when(spyPresenter).getJsonViewFromPreference(Mockito.anyString());


        Activity activity = Mockito.mock(Activity.class);
        Mockito.doReturn(activity).when(view).getActivityContext();

        RelativeLayout loginLayout = Mockito.mock(RelativeLayout.class);
        Mockito.doReturn(loginLayout).when(activity).findViewById(R.id.login_layout);


        CheckBox checkBox = Mockito.mock(CheckBox.class);
        Mockito.doReturn(checkBox).when(activity).findViewById(R.id.login_show_password_checkbox);


        TextView textView = Mockito.mock(TextView.class);
        Mockito.doReturn(textView).when(activity).findViewById(R.id.login_show_password_text_view);


        ImageView imageView = Mockito.mock(ImageView.class);
        Mockito.doReturn(imageView).when(activity).findViewById(R.id.login_logo);

        textView = new TextView(RuntimeEnvironment.application);
        TextView textViewSpy = Mockito.spy(textView);
        Mockito.doReturn(textViewSpy).when(activity).findViewById(R.id.login_build_text_view);
        Mockito.doReturn(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)).when(textViewSpy).getLayoutParams();

        spyPresenter.processViewCustomizations();

        Mockito.verify(textViewSpy, Mockito.times(1)).setLayoutParams(ArgumentMatchers.any(LinearLayout.LayoutParams.class));

    }

}
