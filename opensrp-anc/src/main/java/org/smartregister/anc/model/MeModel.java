package org.smartregister.anc.model;

import org.smartregister.anc.BuildConfig;
import org.smartregister.anc.contract.MeContract;
import org.smartregister.anc.util.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MeModel implements MeContract.Model {
    private Utils utils = new Utils();

    @Override
    public String getInitials() {
        return utils.getUserInitials();
    }

    @Override
    public String getName() {
        return utils.getName();
    }

    @Override
    public String getBuildDate() {
        return new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(new Date(BuildConfig.BUILD_TIMESTAMP));
    }
}
