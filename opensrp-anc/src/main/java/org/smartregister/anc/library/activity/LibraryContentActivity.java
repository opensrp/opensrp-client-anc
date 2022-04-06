package org.smartregister.anc.library.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.anc.library.R;
import org.smartregister.anc.library.util.ConstantsUtils;

public class LibraryContentActivity extends AppCompatActivity {
    private TextView toolbarHeaderTextview;
    private String contentName;
    private View libraryBirthAndEmergency;
    private View libraryPhysicalActivityContent;
    private View libraryBalanceNutritionContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library_content);
        setUpViews();

        if (getIntent() != null && getIntent().getExtras() != null) {
            contentName = getIntent().getExtras().getString(ConstantsUtils.IntentKeyUtils.LIBRARY_HEADER);
        }
        updateTheToolbarHeader();
        toggleViews();
    }

    private void setUpViews() {
        Toolbar mToolbar = findViewById(R.id.activity_library_toolbar);
        mToolbar.findViewById(R.id.close_library).setOnClickListener(view -> onBackPressed());

        toolbarHeaderTextview = mToolbar.findViewById(R.id.activity_library_toolbar_title);
        libraryBirthAndEmergency = findViewById(R.id.library_birth_and_emergency);
        libraryPhysicalActivityContent = findViewById(R.id.library_physical_activity_content);
        libraryBalanceNutritionContent = findViewById(R.id.library_balance_nutrition_content);
    }

    private void updateTheToolbarHeader() {
        if (StringUtils.isNotBlank(contentName)) {
            toolbarHeaderTextview.setText(contentName);
        }
    }

    private void toggleViews() {
        if (StringUtils.isNotBlank(contentName)) {
            if (getResources().getString(R.string.birth_and_emergency_plan).equals(contentName)) {
                libraryBirthAndEmergency.setVisibility(View.VISIBLE);
                libraryPhysicalActivityContent.setVisibility(View.GONE);
                libraryBalanceNutritionContent.setVisibility(View.GONE);
            } else if (getResources().getString(R.string.balanced_nutrition).equals(contentName)) {
                libraryBirthAndEmergency.setVisibility(View.GONE);
                libraryPhysicalActivityContent.setVisibility(View.GONE);
                libraryBalanceNutritionContent.setVisibility(View.VISIBLE);
            } else {
                libraryBirthAndEmergency.setVisibility(View.GONE);
                libraryPhysicalActivityContent.setVisibility(View.VISIBLE);
                libraryBalanceNutritionContent.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }
}
