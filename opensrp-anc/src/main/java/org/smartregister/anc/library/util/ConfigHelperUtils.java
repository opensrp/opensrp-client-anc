package org.smartregister.anc.library.util;

import android.content.Context;

import org.smartregister.anc.library.R;
import org.smartregister.configurableviews.model.Field;
import org.smartregister.configurableviews.model.RegisterConfiguration;

import java.util.ArrayList;
import java.util.List;

public class ConfigHelperUtils {
    public static RegisterConfiguration defaultRegisterConfiguration(Context context) {
        if (context == null) {
            return null;
        }

        RegisterConfiguration config = new RegisterConfiguration();
        config.setEnableAdvancedSearch(true);
        config.setEnableFilterList(true);
        config.setEnableSortList(true);
        config.setSearchBarText(context.getString(R.string.search_hint));
        config.setEnableJsonViews(false);

        List<Field> filers = new ArrayList<>();
        filers.add(new Field(context.getString(R.string.has_tasks_due), "has_tasks_due"));
        filers.add(new Field(context.getString(R.string.risky_pregnancy), "risky_pregnancy"));
        filers.add(new Field(context.getString(R.string.syphilis_positive), "syphilis_positive"));
        filers.add(new Field(context.getString(R.string.hiv_positive), "hiv_positive"));
        filers.add(new Field(context.getString(R.string.hypertensive), "hypertensive"));
        config.setFilterFields(filers);

        List<Field> sortFields = new ArrayList<>();
        sortFields.add(new Field(context.getString(R.string.updated_recent_first), "updated_at desc"));
        sortFields.add(new Field(context.getString(R.string.ga_older_first), "ga asc"));
        sortFields.add(new Field(context.getString(R.string.ga_younger_first), "ga desc"));
        sortFields.add(new Field(context.getString(R.string.id), "id"));
        sortFields.add(new Field(context.getString(R.string.first_name_a_to_z), "first_name asc"));
        sortFields.add(new Field(context.getString(R.string.first_name_z_to_a), "first_name desc"));
        sortFields.add(new Field(context.getString(R.string.last_name_a_to_z), "last_name asc"));
        sortFields.add(new Field(context.getString(R.string.last_name_z_to_a), "last_name desc"));
        config.setSortFields(sortFields);

        return config;
    }
}
