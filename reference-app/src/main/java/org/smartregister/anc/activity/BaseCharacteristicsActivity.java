package org.smartregister.anc.activity;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import org.smartregister.anc.R;
import org.smartregister.anc.adapter.CharacteristicsAdapter;
import org.smartregister.anc.contract.BaseCharacteristicsContract;
import org.smartregister.anc.contract.PopulationCharacteristicsContract;
import org.smartregister.domain.ServerSetting;

import java.util.List;

/**
 * Created by ndegwamartin on 31/08/2018.
 */
public abstract class BaseCharacteristicsActivity extends BaseActivity
        implements CharacteristicsAdapter.ItemClickListener, PopulationCharacteristicsContract.View {

    protected Toolbar mToolbar;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_characteristics);

        recyclerView = findViewById(R.id.population_characteristics);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);


        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        BaseCharacteristicsContract.BasePresenter basePresenter = getPresenter();
        basePresenter.getCharacteristics();

        mToolbar = findViewById(R.id.register_toolbar);
        mToolbar.findViewById(R.id.close_characteristics).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        TextView titleTextView = mToolbar.findViewById(R.id.characteristics_toolbar_title);
        String title = getToolbarTitle();
        titleTextView.setText(title);

        mToolbar.findViewById(R.id.characteristics_toolbar_edit)
                .setVisibility(title.equals(getString(R.string.population_characteristics)) ? View.GONE : View.VISIBLE);

    }

    @Override
    public void onItemClick(View view, int position) {
        renderSubInfoAlertDialog(view.findViewById(R.id.info).getTag(R.id.CHARACTERISTIC_DESC).toString());

    }

    protected void renderSubInfoAlertDialog(String info) {

        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, R.style.AncAlertDialog);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle("Info").setMessage(info)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setIcon(R.drawable.ic_info);

        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(TypedValue.COMPLEX_UNIT_SP, 18.0f);
    }

    @Override
    public void renderSettings(List<ServerSetting> characteristics) {

        CharacteristicsAdapter adapter = new CharacteristicsAdapter(this, characteristics);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    protected abstract BaseCharacteristicsContract.BasePresenter getPresenter();

    protected abstract String getToolbarTitle();
}
