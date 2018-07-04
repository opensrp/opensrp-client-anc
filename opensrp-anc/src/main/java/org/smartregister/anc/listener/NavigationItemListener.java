package org.smartregister.anc.listener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;

import org.smartregister.anc.R;
import org.smartregister.anc.activity.HomeRegisterActivity;
import org.smartregister.anc.application.AncApplication;

/**
 * Created by keyman on 4/07/18.
 */

public class NavigationItemListener implements View.OnClickListener {

    private Activity context;

    public NavigationItemListener(Activity context) {
        this.context = context;
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.anc_register) {
            Intent intent = new Intent(context, HomeRegisterActivity.class);
            context.startActivity(intent);
        } else if (id == R.id.counseling_resources) {

        } else if (id == R.id.site_characteristics) {

        } else if (id == R.id.sync_data) {
            //ServiceTools.startSyncService(context.getApplicationContext());
        } else if (id == R.id.logout) {
            AncApplication.getInstance().logoutCurrentUser();
        }

        DrawerLayout drawer = context.findViewById(R.id.drawer_layout);

        drawer.closeDrawer(GravityCompat.START);
    }

}
