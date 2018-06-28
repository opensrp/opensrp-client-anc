package org.smartregister.anc.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.powermock.reflect.Whitebox;
import org.robolectric.Robolectric;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.shadows.ShadowApplication;
import org.smartregister.anc.R;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by ndegwamartin on 27/06/2018.
 */
public class LoginActivityTest extends BaseUnitTest {

    private LoginActivity loginActivity;
    private ActivityController<LoginActivity> controller;

    @Before
    public void setUp() {
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

    private void assertActivityStarted(Activity currActivity, Activity nextActivity) {

        Intent expectedIntent = new Intent(currActivity, nextActivity.getClass());
        Intent actual = ShadowApplication.getInstance().getNextStartedActivity();
        assertEquals(expectedIntent.getComponent(), actual.getComponent());
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
