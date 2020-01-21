package org.smartregister.anc.library.listener;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;

import org.smartregister.anc.library.R;

public class ContactTaskDisplayClickListener implements View.OnClickListener {
    @Override
    public void onClick(View view) {
        if (view != null) {
            if (view.getId() == R.id.accordion_info_icon) {
                infoAlertDialog(view);
            }
        }
    }

    /**
     * Displays the extra info on the expansion panel widget.
     *
     * @param view {@link View}
     */
    private void infoAlertDialog(View view) {
        Context context = ((Context) view.getTag(R.id.accordion_context));
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context, R.style.AppThemeAlertDialog);
        builderSingle.setTitle((String) view.getTag(R.id.accordion_info_title));
        builderSingle.setMessage((String) view.getTag(R.id.accordion_info_text));
        builderSingle.setIcon(com.vijay.jsonwizard.R.drawable.dialog_info_filled);
        builderSingle.setNegativeButton(context.getResources().getString(R.string.ok),
                (dialog, which) -> dialog.dismiss());

        builderSingle.show();
    }
}
