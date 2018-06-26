package org.smartregister.anc.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.joda.time.DateTime;
import org.smartregister.Context;
import org.smartregister.anc.BuildConfig;
import org.smartregister.anc.R;
import org.smartregister.anc.application.AncApplication;
import org.smartregister.anc.contract.LoginContract;
import org.smartregister.anc.event.ViewConfigurationSyncCompleteEvent;
import org.smartregister.anc.presenter.LoginPresenter;
import org.smartregister.anc.task.SaveTeamLocationsTask;
import org.smartregister.anc.util.Constants;
import org.smartregister.anc.util.ImageLoaderRequest;
import org.smartregister.configurableviews.model.LoginConfiguration;
import org.smartregister.configurableviews.model.ViewConfiguration;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.util.Utils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;
import static org.smartregister.util.Log.logError;
import static org.smartregister.util.Log.logInfo;

/**
 * Created by ndegwamartin on 21/06/2018.
 */
public class LoginActivity extends AppCompatActivity implements LoginContract.View, TextView.OnEditorActionListener, View.OnClickListener {

    private static final String TAG = LoginActivity.class.getName();
    private EditText userNameEditText;
    private EditText passwordEditText;
    private CheckBox showPasswordCheckBox;
    private ProgressDialog progressDialog;
    private Button loginButton;

    private LoginPresenter mLoginPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.black)));

        setupViews();
        setLanguage();
        initializePresenter();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("Settings");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getTitle().toString().equalsIgnoreCase("Settings")) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        processViewCustomizations();
        if (!getOpenSRPContext().IsUserLoggedOut()) {
            goToHome(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLoginPresenter.onDestroy(isChangingConfigurations());
    }

    private void setupViews() {
        positionViews();
        initializeLoginFields();
        initializeProgressDialog();
        initializeBuildDetails();
        setDoneActionHandlerOnPasswordField();
        setListenerOnShowPasswordCheckbox();

    }

    private void initializeLoginFields() {
        userNameEditText = findViewById(R.id.login_user_name_edit_text);
        passwordEditText = findViewById(R.id.login_password_edit_text);
        showPasswordCheckBox = findViewById(R.id.login_show_password_checkbox);
        loginButton = findViewById(R.id.login_login_btn);
        loginButton.setOnClickListener(this);
    }

    private void initializeProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle(getString(org.smartregister.R.string.loggin_in_dialog_title));
        progressDialog.setMessage(getString(org.smartregister.R.string.loggin_in_dialog_message));
    }

    private void initializeBuildDetails() {
        TextView buildDetailsView = findViewById(R.id.login_build_text_view);
        try {
            buildDetailsView.setText("Version " + getVersion() + ", Built on: " + getBuildDate());
        } catch (Exception e) {
            logError("Error fetching build details: " + e);
        }
    }


    private String getVersion() throws PackageManager.NameNotFoundException {
        PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        return packageInfo.versionName;
    }

    private String getBuildDate() throws PackageManager.NameNotFoundException, IOException {
        return new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(new Date(BuildConfig.BUILD_TIMESTAMP));
    }

    private void setDoneActionHandlerOnPasswordField() {
        passwordEditText.setOnEditorActionListener(this);
    }

    private void setListenerOnShowPasswordCheckbox() {
        showPasswordCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    passwordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
    }

    public static void setLanguage() {
        AllSharedPreferences allSharedPreferences = new AllSharedPreferences(getDefaultSharedPreferences(getOpenSRPContext().applicationContext()));
        String preferredLocale = allSharedPreferences.fetchLanguagePreference();
        Resources resources = getOpenSRPContext().applicationContext().getResources();

        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        Configuration configuration = resources.getConfiguration();
        configuration.locale = new Locale(preferredLocale);
        resources.updateConfiguration(configuration, displayMetrics);
    }

    @Override
    public void goToHome(boolean remote) {
        if (remote) {
            Utils.startAsyncTask(new SaveTeamLocationsTask(), null);
        }
        Intent intent = new Intent(this, HomeRegisterActivity.class);
        intent.putExtra(Constants.IS_REMOTE_LOGIN, remote);
        startActivity(intent);

        finish();
    }

    private void processViewCustomizations() {
        try {
            String jsonString = Utils.getPreference(this, Constants.VIEW_CONFIGURATION_PREFIX + Constants.CONFIGURATION.LOGIN, null);
            if (jsonString == null) {
                return;
            }

            ViewConfiguration loginView = AncApplication.getJsonSpecHelper().getConfigurableView(jsonString);
            LoginConfiguration metadata = (LoginConfiguration) loginView.getMetadata();
            LoginConfiguration.Background background = metadata.getBackground();

            TextView showPasswordTextView = findViewById(R.id.login_show_password_text_view);
            if (!metadata.getShowPasswordCheckbox()) {
                showPasswordCheckBox.setVisibility(View.GONE);
                showPasswordTextView.setVisibility(View.GONE);
            } else {
                showPasswordCheckBox.setVisibility(View.VISIBLE);
                showPasswordTextView.setVisibility(View.VISIBLE);
            }

            if (background.getOrientation() != null && background.getStartColor() != null && background.getEndColor() != null) {
                View loginLayout = findViewById(R.id.login_layout);
                GradientDrawable gradientDrawable = new GradientDrawable();
                gradientDrawable.setShape(GradientDrawable.RECTANGLE);
                gradientDrawable.setOrientation(
                        GradientDrawable.Orientation.valueOf(background.getOrientation()));
                gradientDrawable.setColors(new int[]{Color.parseColor(background.getStartColor()),
                        Color.parseColor(background.getEndColor())});
                loginLayout.setBackground(gradientDrawable);
            }

            if (metadata.getLogoUrl() != null) {
                ImageView imageView = findViewById(R.id.login_logo);
                ImageLoaderRequest.getInstance(this.getApplicationContext()).getImageLoader()
                        .get(metadata.getLogoUrl(), ImageLoader.getImageListener(imageView,
                                R.drawable.ic_who_logo, R.drawable.ic_who_logo)).getBitmap();
                TextView loginBuild = findViewById(R.id.login_build_text_view);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(loginBuild.getLayoutParams());
                lp.setMargins(0, 0, 0, 0);
                loginBuild.setLayoutParams(lp);
            }

        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
    }

    @Override
    public void showErrorDialog(String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle(getString(org.smartregister.R.string.login_failed_dialog_title))
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create();
        alertDialog.show();

    }

    public void showProgress(final boolean show) {
        if (show) {
            progressDialog.show();
        } else

        {
            progressDialog.dismiss();
        }
    }

    @Override
    public void hideKeyboard() {

        Log.i(getClass().getName(), "Hiding Keyboard " + DateTime.now().toString());
        org.smartregister.anc.util.Utils.hideKeyboard(this);
    }

    @Override
    public void enableLoginButton(boolean isClickable) {
        loginButton.setClickable(isClickable);
    }

    private void initializePresenter() {
        mLoginPresenter = new LoginPresenter(this);
    }

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        if (actionId == R.id.login || actionId == EditorInfo.IME_NULL || actionId == EditorInfo.IME_ACTION_DONE) {
            String username = userNameEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            mLoginPresenter.attemptLogin(username, password);
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_login_btn:
                String username = userNameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                mLoginPresenter.attemptLogin(username, password);
                break;
            default:
                break;
        }
    }

    @Override
    public void setUsernameError(int resourceId) {
        userNameEditText.setError(getString(resourceId));
        userNameEditText.requestFocus();
        showErrorDialog(getResources().getString(R.string.unauthorized));
    }

    @Override
    public void resetUsernameError() {
        userNameEditText.setError(null);
    }

    @Override
    public void setPasswordError(int resourceId) {
        passwordEditText.setError(getString(resourceId));
        passwordEditText.requestFocus();
        showErrorDialog(getResources().getString(R.string.unauthorized));
    }

    @Override
    public void resetPaswordError() {
        passwordEditText.setError(null);
    }


    private void positionViews() {
        final ScrollView canvasSV = findViewById(R.id.canvasSV);
        final RelativeLayout canvasRL = findViewById(R.id.login_layout);
        final LinearLayout logoCanvasLL = findViewById(R.id.bottom_section);
        final LinearLayout credentialsCanvasLL = findViewById(R.id.middle_section);

        canvasSV.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                canvasSV.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                int windowHeight = canvasSV.getHeight();
                int topMargin = (windowHeight / 2)
                        - (credentialsCanvasLL.getHeight() / 2)
                        - logoCanvasLL.getHeight();
                topMargin = topMargin / 2;

                RelativeLayout.LayoutParams logoCanvasLP = (RelativeLayout.LayoutParams) logoCanvasLL.getLayoutParams();
                logoCanvasLP.setMargins(0, topMargin, 0, 0);
                logoCanvasLL.setLayoutParams(logoCanvasLP);

                canvasRL.setMinimumHeight(windowHeight);
            }
        });
    }

    public static Context getOpenSRPContext() {
        return AncApplication.getInstance().getContext();
    }

    @Override
    public boolean isPasswordValid(String password) {
        return !TextUtils.isEmpty(password) && password.length() > 1;
    }

    @Override
    public boolean isEmptyUsername(String username) {
        return TextUtils.isEmpty(username);
    }

    @Subscribe(threadMode = ThreadMode.MAIN_ORDERED)
    public void refreshViews(ViewConfigurationSyncCompleteEvent syncCompleteEvent) {
        if (syncCompleteEvent != null) {
            logInfo("Refreshing Login View...");
            processViewCustomizations();
        }
    }
}

