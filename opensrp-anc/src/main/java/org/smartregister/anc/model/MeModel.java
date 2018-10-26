package org.smartregister.anc.model;

import org.smartregister.anc.BuildConfig;
import org.smartregister.anc.util.Utils;
import org.smartregister.view.contract.MeContract;

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
        return new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(new Date(BuildConfig.BUILD_TIMESTAMP));
    }
}
