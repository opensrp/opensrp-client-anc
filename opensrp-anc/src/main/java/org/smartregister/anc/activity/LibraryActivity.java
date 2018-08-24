package org.smartregister.anc.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import org.smartregister.anc.R;
import org.smartregister.anc.contract.MeContract;
import org.smartregister.anc.fragment.LibraryFragment;

public class LibraryActivity extends Activity implements MeContract.View {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_me);
		if (savedInstanceState != null) {
			FragmentManager fragmentManager = getFragmentManager();
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
			
			LibraryFragment libraryFragment = new LibraryFragment();
			fragmentTransaction.attach(libraryFragment).commit();
		}
	}
}
