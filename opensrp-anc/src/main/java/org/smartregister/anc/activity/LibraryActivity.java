package org.smartregister.anc.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import org.smartregister.anc.R;
import org.smartregister.anc.contract.MeContract;
import org.smartregister.anc.fragment.LibraryFragment;

public class LibraryActivity extends FragmentActivity implements MeContract.View {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_library);
		getSupportFragmentManager().beginTransaction().replace(R.id.fragment_library, new LibraryFragment()).commit();
	}
}
