package org.smartregister.anc.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import org.smartregister.anc.R;
import org.smartregister.anc.activity.PopulationCharacteristicsActivity;
import org.smartregister.anc.activity.SiteCharacteristicsActivity;
import org.smartregister.anc.application.AncApplication;
import org.smartregister.anc.presenter.MePresenter;
import org.smartregister.view.contract.MeContract;

public class MeFragment extends org.smartregister.view.fragment.MeFragment implements MeContract.View {

    private RelativeLayout me_pop_characteristics_section;
    private RelativeLayout site_characteristics_section;
    private RelativeLayout contact_summary;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_me, container, false);
    }

    @Override
    protected void setUpViews(View view) {
        super.setUpViews(view);
        me_pop_characteristics_section = view.findViewById(R.id.me_pop_characteristics_section);
        site_characteristics_section = view.findViewById(R.id.site_characteristics_section);

        contact_summary = view.findViewById(R.id.contact_summary);

    }

    protected void setClickListeners() {
        super.setClickListeners();
        me_pop_characteristics_section.setOnClickListener(meFragmentActionHandler);
        site_characteristics_section.setOnClickListener(meFragmentActionHandler);
        contact_summary.setOnClickListener(meFragmentActionHandler);
    }

    protected void initializePresenter() {
        presenter = new MePresenter(this);
    }

    @Override
    protected void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.logout_section:
                AncApplication.getInstance().logoutCurrentUser();
                break;
            case R.id.setting_section:
                //ToDO Add the functionality for the setting page after that is decided.
                break;
            case R.id.site_characteristics_section:
                getContext().startActivity(new Intent(getContext(), SiteCharacteristicsActivity.class));
                break;
            case R.id.me_pop_characteristics_section:
                getContext().startActivity(new Intent(getContext(), PopulationCharacteristicsActivity.class));
                break;
            default:
                break;
        }
    }

}
