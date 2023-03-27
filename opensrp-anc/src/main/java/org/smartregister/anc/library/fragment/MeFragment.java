package org.smartregister.anc.library.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.anc.library.AncLibrary;
import org.smartregister.anc.library.R;
import org.smartregister.anc.library.activity.AncP2pModeSelectActivity;
import org.smartregister.anc.library.activity.BaseHomeRegisterActivity;
import org.smartregister.anc.library.activity.PopulationCharacteristicsActivity;
import org.smartregister.anc.library.activity.SiteCharacteristicsActivity;
import org.smartregister.anc.library.constants.Constants;
import org.smartregister.anc.library.presenter.MePresenter;
import org.smartregister.anc.library.util.Utils;
import org.smartregister.util.LangUtils;
import org.smartregister.view.activity.DrishtiApplication;
import org.smartregister.view.contract.MeContract;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import timber.log.Timber;

public class MeFragment extends org.smartregister.view.fragment.MeFragment implements MeContract.View {
    private RelativeLayout mePopCharacteristicsSection;
    private RelativeLayout siteCharacteristicsSection;
    private RelativeLayout languageSwitcherSection;
    private RelativeLayout p2pSyncSetion;
    private TextView languageSwitcherText;
    private Locale[] locales;
    private String[] languages;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_me, container, false);
    }

    @Override
    protected void setUpViews(View view) {
        super.setUpViews(view);
        mePopCharacteristicsSection = view.findViewById(R.id.me_pop_characteristics_section);
        siteCharacteristicsSection = view.findViewById(R.id.site_characteristics_section);
        p2pSyncSetion = view.findViewById(R.id.p2p_section);

        if (Utils.enableLanguageSwitching()) {
            languageSwitcherSection = view.findViewById(R.id.language_switcher_section);
            languageSwitcherSection.setVisibility(View.VISIBLE);

            View meLanguageSwitcherSeparator = view.findViewById(R.id.me_language_switcher_separator);
            meLanguageSwitcherSeparator.setVisibility(View.VISIBLE);

            languageSwitcherText = languageSwitcherSection.findViewById(R.id.language_switcher_text);
            registerLanguageSwitcher();
        }

        setManifestVersion(view);
    }

    private void setManifestVersion(View view) {
        TextView manifestVersionText = view.findViewById(R.id.form_manifest_version);
        if (getActivity() != null) {
            manifestVersionText.setText(new Utils().getManifestVersion(getActivity().getBaseContext()));
        } else {
            manifestVersionText.setVisibility(View.INVISIBLE);
        }
    }

    protected void setClickListeners() {
        super.setClickListeners();
        mePopCharacteristicsSection.setOnClickListener(meFragmentActionHandler);
        siteCharacteristicsSection.setOnClickListener(meFragmentActionHandler);
        if (Utils.enableLanguageSwitching()) {
            languageSwitcherSection.setOnClickListener(meFragmentActionHandler);
        }
        p2pSyncSetion.setOnClickListener(meFragmentActionHandler);
    }

    protected void initializePresenter() {
        presenter = new MePresenter(this);
    }

    @Override
    protected void onViewClicked(View view) {
        int viewId = view.getId();
        if (viewId == R.id.logout_section) {
            DrishtiApplication.getInstance().logoutCurrentUser();
        } else if (viewId == R.id.site_characteristics_section) {
            if (getContext() != null) {
                getContext().startActivity(new Intent(getContext(), SiteCharacteristicsActivity.class));
            }
        } else if (viewId == R.id.me_pop_characteristics_section) {
            if (getContext() != null) {
                getContext().startActivity(new Intent(getContext(), PopulationCharacteristicsActivity.class));
            }
        } else if (viewId == R.id.language_switcher_section) {
            languageSwitcherDialog();
        } else if (viewId == R.id.p2p_section) {
            startActivity(new Intent(getContext(), AncP2pModeSelectActivity.class));
        }
    }

    private void languageSwitcherDialog() {
        if (getActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(getActivity().getResources().getString(R.string.choose_language));
            builder.setItems(languages, (dialog, position) -> {
                if (MeFragment.this.getActivity() != null) {
                    String selectedLanguage = languages[position];
                    languageSwitcherText.setText(String.format(MeFragment.this.getActivity().getResources().getString(R.string.default_language_string), selectedLanguage));
                    saveLanguage(locales[position]);
                    AncLibrary.getInstance().notifyAppContextChange();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    private void saveLanguage(Locale selectedLocale) {
        if (MeFragment.this.getActivity() != null && selectedLocale != null) {
            if (getActivity() instanceof BaseHomeRegisterActivity) {
                BaseHomeRegisterActivity parentActivity = (BaseHomeRegisterActivity) getActivity();
                parentActivity.setLocale(selectedLocale);
            }
        }
    }

    private void registerLanguageSwitcher() {
        if (getActivity() != null) {
            setLocaleList();
            languages = new String[locales.length];
            Locale currentLocale = getActivity().getResources().getConfiguration().getLocales().get(0);
            for (int index = 0; index < locales.length; index++) {
                languages[index] = locales[index].getDisplayLanguage() + " (" + locales[index].getDisplayCountry() + ")";
            }
            languageSwitcherText.setText(String.format(getActivity().getResources().getString(R.string.default_language_string), currentLocale.getDisplayLanguage()));
        }
    }

    private void setLocaleList() {
        locales = new Locale[]{
            Constants.Locales.ENGLISH_US,
            Constants.Locales.FRANCE_RW,
            Constants.Locales.INDONESIAN_ID,
            Constants.Locales.NEPALI_NP,
            Constants.Locales.PORTUGESE_BR,
        };
    }

}