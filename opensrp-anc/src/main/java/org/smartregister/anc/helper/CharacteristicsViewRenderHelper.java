package org.smartregister.anc.helper;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import org.smartregister.anc.R;
import org.smartregister.anc.adapter.PopulationCharacteristicsAdapter;

/**
 * Created by ndegwamartin on 14/08/2018.
 */
public class CharacteristicsViewRenderHelper implements PopulationCharacteristicsAdapter.ItemClickListener {

    private AlertDialog characteristicAlertDialog;
    private Context context;

    public CharacteristicsViewRenderHelper(Context context) {

        this.context = context;

        createCharacteristicsAlertDialog();


    }

    protected void renderSubInfoAlertDialog(String info) {

        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(context, R.style.AncDialog);
        } else {
            builder = new AlertDialog.Builder(context);
        }
        builder.setTitle("info")
                .setMessage(info)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_info).show();
    }


    @NonNull
    protected void createCharacteristicsAlertDialog() {

        AlertDialog.Builder dialogBuilder;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialogBuilder = new AlertDialog.Builder(context, R.style.AncAlertDialogFullScreen);
        } else {
            dialogBuilder = new AlertDialog.Builder(context);
        }

        View characteristicDialogView = LayoutInflater.from(context).inflate(R.layout.alert_dialog_population_characteristics, null);

        dialogBuilder.setView(characteristicDialogView);

        RecyclerView recyclerView = characteristicDialogView.findViewById(R.id.population_characteristics);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);


        CharacteristicsHelper helper = new CharacteristicsHelper();
        PopulationCharacteristicsAdapter adapter = new PopulationCharacteristicsAdapter(context, helper.getPopulationCharacteristics());
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());

        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.addItemDecoration(dividerItemDecoration);

        characteristicDialogView.findViewById(R.id.closeButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                characteristicAlertDialog.dismiss();
            }
        });

        characteristicAlertDialog = dialogBuilder.create();
    }

    public void showDialog() {
        characteristicAlertDialog.show();
    }

    @Override
    public void onItemClick(View view, int position) {
        renderSubInfoAlertDialog(view.findViewById(R.id.info).getTag(R.id.CHARACTERISTIC_DESC).toString());

    }
}
