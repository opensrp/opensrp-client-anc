package org.smartregister.anc.model;

import org.smartregister.anc.contract.MeContract;
import org.smartregister.anc.util.Utils;

public class MeFragmentModel implements MeContract.Model {
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
        return utils.getAppBuildDate();
    }
}
