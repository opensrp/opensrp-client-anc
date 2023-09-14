package org.smartregister.anc.library.fragment;

import android.content.Intent;
import android.os.Bundle;
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
    private final Map<String, Locale> locales = new HashMap<>();
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

                    saveLanguage(selectedLanguage);
                    MeFragment.this.reloadClass();
                    AncLibrary.getInstance().notifyAppContextChange();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    private void saveLanguage(String selectedLanguage) {
        if (MeFragment.this.getActivity() != null && StringUtils.isNotBlank(selectedLanguage)) {
            Locale selectedLanguageLocale = locales.get(selectedLanguage);
            if (selectedLanguageLocale != null) {
                LangUtils.saveLanguage(MeFragment.this.getActivity().getApplication(), getFullLanguage(selectedLanguageLocale));
            } else {
                Timber.i("Language could not be set");
            }
        }
    }

    private void reloadClass() {
        if (getActivity() != null) {
            Intent intent = getActivity().getIntent();
            getActivity().finish();
            getActivity().startActivity(intent);
        }
    }

    private void registerLanguageSwitcher() {
        if (getActivity() != null) {
            addLanguages();

            languages = new String[locales.size()];
            Locale current = getActivity().getResources().getConfiguration().locale;
            int x = 0;
            for (Map.Entry<String, Locale> language : locales.entrySet()) {
                languages[x] = language.getKey(); //Update the languages strings array with the languages to be displayed on the alert dialog
                x++;

                if (getFullLanguage(current).equalsIgnoreCase(getFullLanguage(language.getValue()))) {
                    languageSwitcherText.setText(String.format(getActivity().getResources().getString(R.string.default_language_string), language.getKey()));
                }
            }
        }
    }

    private String getFullLanguage(Locale locale) {
        return StringUtils.isNotEmpty(locale.getCountry()) ? locale.getLanguage() + "_" + locale.getCountry() : locale.getLanguage();
    }

    private void addLanguages() {
        locales.put(getString(R.string.english_language), Locale.ENGLISH);
        locales.put(getString(R.string.bahasa_indonesia_language),   Constants.Locales.INDONESIAN_ID);
    }

}