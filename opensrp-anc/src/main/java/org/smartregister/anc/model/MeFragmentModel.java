package org.smartregister.anc.model;

import org.smartregister.anc.contract.MeContract;
import org.smartregister.anc.util.Utils;

public class MeFragmentModel implements MeContract.Model {
	
	@Override
	public String getInitials() {
		return Utils.getInitials();
	}
}
