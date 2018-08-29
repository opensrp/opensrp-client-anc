package org.smartregister.anc.model;

import org.smartregister.anc.contract.LibraryContract;
import org.smartregister.anc.util.Utils;

public class LibraryFragmentModel implements LibraryContract.Model {
	@Override
	public String getInitials() {
		return Utils.getInitials();
	}
}
