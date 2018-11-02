package org.smartregister.anc.model;

import org.smartregister.anc.util.Utils;
import org.smartregister.view.contract.MeContract;

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
        return Utils.getBuildDate(true);
    }
}
