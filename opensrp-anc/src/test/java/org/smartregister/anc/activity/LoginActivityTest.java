package org.smartregister.anc.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.shadows.ShadowApplication;
import org.smartregister.anc.R;
import org.smartregister.anc.contract.LoginContract;
import org.smartregister.anc.custom.SettingsTestMenuItem;
import org.smartregister.anc.event.ViewConfigurationSyncCompleteEvent;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by ndegwamartin on 27/06/2018.
 */
public class LoginActivityTest extends BaseUnitTest {

    private LoginActivity loginActivity;
    private ActivityController<LoginActivity> controller;
    private static final String STRING_SETTINGS = "Settings";

    @Mock
    Menu menu;

    @Mock
    SettingsTestMenuItem menuItem;

    @Mock
    LoginContract.Presenter presenter;

    @Mock
    ProgressDialog progressDialog;

    @Mock
    Button loginButton;

    @Mock
    TextView textView;

    @Mock
    KeyEvent keyEvent;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        controller = Robolectric.buildActivity(LoginActivity.class).create().start();
        loginActivity = controller.get();
    }

    @After
    public void tearDown() {
        destroyController();
    }

    @Test
    public void testUserNameEditTextIsInitialized() {

        EditText userNameEditText = Whitebox.getInternalState(loginActivity, "userNameEditText");
        assertNotNull(userNameEditText);
    }

    @Test
    public void testPasswordEditTextIsInitialized() {

        EditText userPasswordEditText = Whitebox.getInternalState(loginActivity, "passwordEditText");
        assertNotNull(userPasswordEditText);
    }


    @Test
    public void testShowPasswordCheckBoxIsInitialized() {

        CheckBox showPasswordCheckBox = Whitebox.getInternalState(loginActivity, "showPasswordCheckBox");
        assertNotNull(showPasswordCheckBox);
    }

    @Test
    public void testProgressDialogIsInitialized() {

        ProgressDialog progressDialog = Whitebox.getInternalState(loginActivity, "progressDialog");
        assertNotNull(progressDialog);
    }

    @Test
    public void testBuildDetailsAreInitialized() {

        TextView buildDetails = loginActivity.findViewById(R.id.login_build_text_view);
        assertNotNull(buildDetails.getText());
    }

    @Test
    public void testGoToHome() {

        try {
            Whitebox.invokeMethod(loginActivity, LoginActivity.class, "goToHome", false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertActivityStarted(loginActivity, new HomeRegisterActivity());
    }

    @Test
    public void testGoToHomeWithRemoteTrue() {

        try {
            Whitebox.invokeMethod(loginActivity, LoginActivity.class, "goToHome", true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertActivityStarted(loginActivity, new HomeRegisterActivity());
    }

    private void assertActivityStarted(Activity currActivity, Activity nextActivity) {

        Intent expectedIntent = new Intent(currActivity, nextActivity.getClass());
        Intent actual = ShadowApplication.getInstance().getNextStartedActivity();
        assertEquals(expectedIntent.getComponent(), actual.getComponent());
    }

    @Test
    public void testOnCreateOptionsMenuShouldAddSettingsItem() {

        LoginActivity spyActivity = Mockito.spy(loginActivity);
        spyActivity.onCreateOptionsMenu(menu);
        Mockito.verify(menu).add(STRING_SETTINGS);
    }

    @Test
    public void testShouldStartSettingsActivity() {
        LoginActivity loginActivitySpy = Mockito.spy(this.loginActivity);
        loginActivitySpy.onCreateOptionsMenu(menu);

        Mockito.doNothing().when(loginActivitySpy).startActivity(Mockito.any(Intent.class));
        Mockito.doReturn(STRING_SETTINGS).when(menuItem).getTitle();

        loginActivitySpy.onOptionsItemSelected(menuItem);
        Mockito.verify(loginActivitySpy).startActivity(Mockito.any(Intent.class));
    }


    @Test
    public void testOnResumeShouldCallProcessViewCustomizationsPresenterMethod() {

        LoginActivity spyActivity = Mockito.spy(loginActivity);
        Whitebox.setInternalState(spyActivity, "mLoginPresenter", presenter);
        spyActivity.onResume();
        Mockito.verify(presenter).processViewCustomizations();
    }

    @Test
    public void testOnResumeShouldCallIsUserLoggedOutPresenterMethod() {

        LoginActivity spyActivity = Mockito.spy(loginActivity);

        Whitebox.setInternalState(spyActivity, "mLoginPresenter", presenter);
        Mockito.doReturn(false).when(presenter).isUserLoggedOut();
        Mockito.doNothing().when(spyActivity).goToHome(false);

        spyActivity.onResume();

        Mockito.verify(presenter).isUserLoggedOut();
        Mockito.verify(spyActivity).goToHome(false);
    }

    @Test
    public void testOnDestroyShouldCallOnDestroyPresenterMethod() {

        LoginActivity spyActivity = Mockito.spy(loginActivity);
        Whitebox.setInternalState(spyActivity, "mLoginPresenter", presenter);
        spyActivity.onDestroy();
        Mockito.verify(presenter).onDestroy(Mockito.anyBoolean());
    }

    @Test
    public void testShowProgressShouldShowProgressDialogWhenParamIsTrue() {
        LoginActivity spyActivity = Mockito.spy(loginActivity);
        Whitebox.setInternalState(spyActivity, "progressDialog", progressDialog);
        spyActivity.showProgress(true);
        Mockito.verify(progressDialog).show();
    }

    @Test
    public void testShowProgressShouldDismissProgressDialogWhenParamIsFalse() {
        LoginActivity spyActivity = Mockito.spy(loginActivity);
        Whitebox.setInternalState(spyActivity, "progressDialog", progressDialog);
        spyActivity.showProgress(false);
        Mockito.verify(progressDialog).dismiss();
    }

    @Test
    public void testEnableLoginShouldCallLoginButtonSetClickableMethodWithCorrectParameter() {
        LoginActivity spyActivity = Mockito.spy(loginActivity);
        Whitebox.setInternalState(spyActivity, "loginButton", loginButton);
        spyActivity.enableLoginButton(false);
        Mockito.verify(loginButton).setClickable(Mockito.anyBoolean());
    }

    @Test
    public void testOnEditorActionShouldCallAttemptLoginMethodFromPresenterIfActionIsEnter() {

        LoginActivity spyActivity = Mockito.spy(loginActivity);
        Whitebox.setInternalState(spyActivity, "mLoginPresenter", presenter);
        spyActivity.onEditorAction(textView, 99292, keyEvent);
        Mockito.verify(presenter, Mockito.times(0)).attemptLogin(DUMMY_USERNAME, DUMMY_PASSWORD);

        EditText userNameEditText = Mockito.spy(new EditText(RuntimeEnvironment.application));
        userNameEditText.setText(DUMMY_USERNAME);

        EditText passwordEditText = Mockito.spy(new EditText(RuntimeEnvironment.application));
        passwordEditText.setText(DUMMY_PASSWORD);

        Whitebox.setInternalState(spyActivity, "userNameEditText", userNameEditText);
        Whitebox.setInternalState(spyActivity, "passwordEditText", passwordEditText);

        spyActivity.onEditorAction(textView, EditorInfo.IME_NULL, keyEvent);//Enter key
        Mockito.verify(presenter, Mockito.times(1)).attemptLogin(DUMMY_USERNAME, DUMMY_PASSWORD);
    }

    @Test
    public void testOnClickShouldInvokeAttemptLoginPresenterMethodIfLoginButtonClicked() {

        LoginActivity spyActivity = Mockito.spy(loginActivity);
        spyActivity.onClick(new View(RuntimeEnvironment.application));//default

        Whitebox.setInternalState(spyActivity, "mLoginPresenter", presenter);

        EditText editTextUsername = spyActivity.findViewById(R.id.login_user_name_edit_text);
        editTextUsername.setText(DUMMY_USERNAME);
        EditText editTextPassword = spyActivity.findViewById(R.id.login_password_edit_text);
        editTextPassword.setText(DUMMY_PASSWORD);

        Button loginButton = spyActivity.findViewById(R.id.login_login_btn);
        spyActivity.onClick(loginButton);

        Mockito.verify(presenter, Mockito.times(1)).attemptLogin(DUMMY_USERNAME, DUMMY_PASSWORD);
    }

    @Test
    public void testResetPasswordErrorShouldInvokeSetUsernameErrorWithNull() {

        LoginActivity spyActivity = Mockito.spy(loginActivity);

        Whitebox.setInternalState(spyActivity, "mLoginPresenter", presenter);

        EditText passwordEditText = Mockito.spy(new EditText(RuntimeEnvironment.application));

        Whitebox.setInternalState(spyActivity, "passwordEditText", passwordEditText);

        spyActivity.resetPaswordError();

        Mockito.verify(passwordEditText).setError(null);
    }

    @Test
    public void testSetPasswordErrorShouldShowErrorDialogWithCorrectMessage() {

        LoginActivity spyActivity = Mockito.spy(loginActivity);

        Whitebox.setInternalState(spyActivity, "mLoginPresenter", presenter);

        EditText passwordEditText = Mockito.spy(new EditText(RuntimeEnvironment.application));

        Whitebox.setInternalState(spyActivity, "passwordEditText", passwordEditText);

        Mockito.doNothing().when(spyActivity).showErrorDialog(RuntimeEnvironment.application.getString(R.string.unauthorized));

        spyActivity.setUsernameError(R.string.unauthorized);

        Mockito.verify(spyActivity).showErrorDialog(RuntimeEnvironment.application.getString(R.string.unauthorized));
    }

    @Test
    public void testSetUsernameErrorShouldShowErrorDialogWithCorrectMessage() {

        LoginActivity spyActivity = Mockito.spy(loginActivity);

        Whitebox.setInternalState(spyActivity, "mLoginPresenter", presenter);

        EditText userNameEditText = Mockito.spy(new EditText(RuntimeEnvironment.application));

        Whitebox.setInternalState(spyActivity, "userNameEditText", userNameEditText);

        Mockito.doNothing().when(spyActivity).showErrorDialog(RuntimeEnvironment.application.getString(R.string.unauthorized));

        spyActivity.setPasswordError(R.string.unauthorized);

        Mockito.verify(spyActivity).showErrorDialog(RuntimeEnvironment.application.getString(R.string.unauthorized));
    }

    @Test
    public void testResetUsernameErrorShouldInvokeSetUsernameErrorWithNull() {

        LoginActivity spyActivity = Mockito.spy(loginActivity);

        Whitebox.setInternalState(spyActivity, "mLoginPresenter", presenter);

        EditText userNameEditText = Mockito.spy(new EditText(RuntimeEnvironment.application));

        Whitebox.setInternalState(spyActivity, "userNameEditText", userNameEditText);

        spyActivity.resetUsernameError();

        Mockito.verify(userNameEditText).setError(null);
    }


    @Test
    public void testRefreshViewsInvokesProcessViewConfigurationsMethodOfPresenter() {

        LoginActivity spyActivity = Mockito.spy(loginActivity);

        Whitebox.setInternalState(spyActivity, "mLoginPresenter", presenter);

        spyActivity.refreshViews(new ViewConfigurationSyncCompleteEvent());

        Mockito.verify(presenter).processViewCustomizations();
    }


    @Test
    public void testGetActivityContextReturnsCorrectInstance() {
        LoginActivity spyActivity = Mockito.spy(loginActivity);

        Assert.assertEquals(spyActivity, spyActivity.getActivityContext());

    }

    private void destroyController() {
        try {
            loginActivity.finish();
            controller.pause().stop().destroy(); //destroy controller if we can

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.gc();
    }
}
