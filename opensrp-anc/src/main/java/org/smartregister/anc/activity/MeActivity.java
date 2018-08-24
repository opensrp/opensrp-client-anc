package org.smartregister.anc.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import org.smartregister.anc.R;
import org.smartregister.anc.contract.MeContract;
import org.smartregister.anc.fragment.MeFragment;

public class MeActivity extends AppCompatActivity implements MeContract.View {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_me);
		if (savedInstanceState != null) {
			FragmentManager fragmentManager = getFragmentManager();
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
			
			MeFragment meFragment = new MeFragment();
			fragmentTransaction.attach(meFragment).commit();
		}
	}
}