package org.smartregister.anc.activity;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;

import org.smartregister.anc.R;
import org.smartregister.anc.adapter.PopulationCharacteristicsAdapter;
import org.smartregister.anc.contract.BaseCharacteristicsContract;
import org.smartregister.anc.contract.PopulationCharacteristicsContract;
import org.smartregister.anc.domain.Characteristic;

import java.util.List;

/**
 * Created by ndegwamartin on 31/08/2018.
 */
public abstract class BaseCharacteristicsActivity extends AppCompatActivity implements PopulationCharacteristicsAdapter.ItemClickListener, PopulationCharacteristicsContract.View {

    private RecyclerView recyclerView;
    protected BaseCharacteristicsContract.BasePresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_characteristics);

        recyclerView = findViewById(R.id.population_characteristics);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);


        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        BaseCharacteristicsContract.BasePresenter presenter = getPresenter();
        presenter.getCharacteristics();

        Toolbar mToolbar = findViewById(R.id.register_toolbar);
        mToolbar.findViewById(R.id.close_population_characteristics).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


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
        builder.setTitle("Info")
                .setMessage(info)
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
    public void renderSettings(List<Characteristic> populationCharacteristics) {

        PopulationCharacteristicsAdapter adapter = new PopulationCharacteristicsAdapter(this, populationCharacteristics);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    protected abstract BaseCharacteristicsContract.BasePresenter getPresenter();
}
