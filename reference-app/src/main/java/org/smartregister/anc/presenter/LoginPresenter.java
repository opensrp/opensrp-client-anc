package org.smartregister.anc.presenter;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.anc.interactor.LoginInteractor;
import org.smartregister.anc.library.AncLibrary;
import org.smartregister.anc.library.R;
import org.smartregister.anc.library.util.ConstantsUtils;
import org.smartregister.anc.library.util.ImageLoaderRequestUtils;
import org.smartregister.configurableviews.model.LoginConfiguration;
import org.smartregister.configurableviews.model.ViewConfiguration;
import org.smartregister.domain.Setting;
import org.smartregister.login.model.BaseLoginModel;
import org.smartregister.login.presenter.BaseLoginPresenter;
import org.smartregister.view.contract.BaseLoginContract;

import java.lang.ref.WeakReference;


/**
 * Created by ndegwamartin on 22/06/2018.
 */
public class LoginPresenter extends BaseLoginPresenter implements BaseLoginContract.Presenter {

    private static final String TAG = LoginPresenter.class.getCanonicalName();

    public LoginPresenter(BaseLoginContract.View loginView) {
        mLoginView = new WeakReference<>(loginView);
        mLoginInteractor = new LoginInteractor(this);
        mLoginModel = new BaseLoginModel();
    }

    @Override
    public void processViewCustomizations() {
        try {
            String jsonString =
                    getJsonViewFromPreference(ConstantsUtils.VIEW_CONFIGURATION_PREFIX + ConstantsUtils.ConfigurationUtils.LOGIN);
            if (jsonString == null) {
                return;
            }

            ViewConfiguration loginView = AncLibrary.getJsonSpecHelper().getConfigurableView(jsonString);
            LoginConfiguration metadata = (LoginConfiguration) loginView.getMetadata();
            LoginConfiguration.Background background = metadata.getBackground();

            CheckBox showPasswordCheckBox =
                    getLoginView().getActivityContext().findViewById(R.id.login_show_password_checkbox);
            TextView showPasswordTextView =
                    getLoginView().getActivityContext().findViewById(R.id.login_show_password_text_view);
            if (!metadata.getShowPasswordCheckbox()) {
                showPasswordCheckBox.setVisibility(View.GONE);
                showPasswordTextView.setVisibility(View.GONE);
            } else {
                showPasswordCheckBox.setVisibility(View.VISIBLE);
                showPasswordTextView.setVisibility(View.VISIBLE);
            }

            if (background.getOrientation() != null && background.getStartColor() != null &&
                    background.getEndColor() != null) {
                View loginLayout = getLoginView().getActivityContext().findViewById(R.id.login_layout);
                GradientDrawable gradientDrawable = new GradientDrawable();
                gradientDrawable.setShape(GradientDrawable.RECTANGLE);
                gradientDrawable.setOrientation(GradientDrawable.Orientation.valueOf(background.getOrientation()));
                gradientDrawable.setColors(
                        new int[]{Color.parseColor(background.getStartColor()), Color.parseColor(background.getEndColor())});
                loginLayout.setBackground(gradientDrawable);
            }

            if (metadata.getLogoUrl() != null) {
                ImageView imageView = getLoginView().getActivityContext().findViewById(R.id.login_logo);
                ImageLoaderRequestUtils.getInstance(getLoginView().getActivityContext()).getImageLoader()
                        .get(metadata.getLogoUrl(),
                                ImageLoader.getImageListener(imageView, R.drawable.ic_who_logo, R.drawable.ic_who_logo))
                        .getBitmap();
            }

        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
    }

    @Override
    public boolean isServerSettingsSet() {

        try {
            Setting setting = AncLibrary.getInstance().getContext().allSettings()
                    .getSetting(ConstantsUtils.PrefKeyUtils.SITE_CHARACTERISTICS);

            JSONObject jsonObject = setting != null ? new JSONObject(setting.getValue()) : null;
            JSONArray settingArray = jsonObject != null && jsonObject.has(AllConstants.SETTINGS) ?
                    jsonObject.getJSONArray(AllConstants.SETTINGS) : null;

            if (settingArray != null && settingArray.length() > 0) {

                JSONObject settingObject = settingArray.getJSONObject(0);// get first setting to test
                return !settingObject.isNull(ConstantsUtils.KeyUtils.VALUE);

            }
        } catch (JSONException e) {
            return false;
        }

        return false;
    }
}
