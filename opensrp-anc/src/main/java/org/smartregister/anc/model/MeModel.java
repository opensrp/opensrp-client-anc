package org.smartregister.anc.model;

import org.smartregister.anc.util.Utils;
import org.smartregister.view.contract.MeContract;

public class MeModel implements MeContract.Model {

    @Override
    public String getInitials() {
        return Utils.getUserInitials();
    }

    @Override
    public String getName() {
        return new Utils().getName();
    }


    @Override
    public String getBuildDate() {
        return Utils.getBuildDate(true);
    }
}
