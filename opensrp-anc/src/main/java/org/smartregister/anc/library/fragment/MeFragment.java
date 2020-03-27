package org.smartregister.anc.library.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.apache.commons.lang3.tuple.Pair;
import org.smartregister.anc.library.AncLibrary;
import org.smartregister.anc.library.R;
import org.smartregister.anc.library.activity.PopulationCharacteristicsActivity;
import org.smartregister.anc.library.activity.SiteCharacteristicsActivity;
import org.smartregister.anc.library.constants.AncAppPropertyConstants;
import org.smartregister.anc.library.presenter.MePresenter;
import org.smartregister.util.LangUtils;
import org.smartregister.view.activity.DrishtiApplication;
import org.smartregister.view.contract.MeContract;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MeFragment extends org.smartregister.view.fragment.MeFragment implements MeContract.View {
    private RelativeLayout mePopCharacteristicsSection;
    private RelativeLayout siteCharacteristicsSection;
    private RelativeLayout languageSwitcherSection;
    private TextView languageSwitcherText;
    private List<Pair<String, Locale>> locales;
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

        if (AncLibrary.getInstance().getProperties().getPropertyBoolean(AncAppPropertyConstants.keyUtils.LANGUAGE_SWITCHING_ENABLED)) {
            languageSwitcherSection = view.findViewById(R.id.language_switcher_section);
            languageSwitcherSection.setVisibility(View.VISIBLE);

            View meLanguageSwitcherSeparator = view.findViewById(R.id.me_language_switcher_separator);
            meLanguageSwitcherSeparator.setVisibility(View.VISIBLE);

            languageSwitcherText = languageSwitcherSection.findViewById(R.id.language_switcher_text);
            registerLanguageSwitcher();
        }
    }

    protected void setClickListeners() {
        super.setClickListeners();
        mePopCharacteristicsSection.setOnClickListener(meFragmentActionHandler);
        siteCharacteristicsSection.setOnClickListener(meFragmentActionHandler);
        if (AncLibrary.getInstance().getProperties().getPropertyBoolean(AncAppPropertyConstants.keyUtils.LANGUAGE_SWITCHING_ENABLED)) {
            languageSwitcherSection.setOnClickListener(meFragmentActionHandler);
        }
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
            getContext().startActivity(new Intent(getContext(), SiteCharacteristicsActivity.class));
        } else if (viewId == R.id.me_pop_characteristics_section) {
            getContext().startActivity(new Intent(getContext(), PopulationCharacteristicsActivity.class));
        } else if (viewId == R.id.language_switcher_section) {
            languageSwitcherDialog(locales, languages);
        }
    }

    private void languageSwitcherDialog(List<Pair<String, Locale>> locales, String[] languages) {
        if (getActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(getActivity().getResources().getString(R.string.choose_language));
            builder.setItems(languages, (dialog, which) -> {
                Pair<String, Locale> lang = locales.get(which);
                languageSwitcherText.setText(String.format(getActivity().getResources().getString(R.string.default_language_string), lang.getLeft()));
                LangUtils.saveLanguage(getActivity().getApplication(), lang.getValue().getLanguage());
                Intent intent = getActivity().getIntent();
                getActivity().finish();
                getActivity().startActivity(intent);
                AncLibrary.getInstance().notifyAppContextChange();
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    private void registerLanguageSwitcher() {
        if (getActivity() != null) {
            locales = Arrays.asList(Pair.of("English", Locale.ENGLISH), Pair.of("French", Locale.FRENCH));

            languages = new String[locales.size()];
            Locale current = getActivity().getResources().getConfiguration().locale;
            int x = 0;
            while (x < locales.size()) {
                languages[x] = locales.get(x).getKey();
                if (current.getLanguage().equals(locales.get(x).getValue().getLanguage())) {
                    languageSwitcherText.setText(String.format(getActivity().getResources().getString(R.string.default_language_string), locales.get(x).getKey()));
                }
                x++;
            }
        }
    }

}