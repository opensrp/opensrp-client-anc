package org.smartregister.anc.listener;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.MenuItem;
import org.smartregister.anc.R;
import org.smartregister.anc.activity.HomeRegisterActivity;

public class BottomNavigationListener implements BottomNavigationView.OnNavigationItemSelectedListener {
	private Activity context;
	
	public BottomNavigationListener(Activity context) {
		this.context = context;
	}
	
	@Override
	public boolean onNavigationItemSelected(@NonNull MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_clients:
				((HomeRegisterActivity) context).switchToBaseFragment();
				break;
			case R.id.action_search:
				((HomeRegisterActivity) context).switchToFragment(1);
				break;
			case R.id.action_register:
				((HomeRegisterActivity) context).startRegistration();
				break;
			case R.id.action_library:
				((HomeRegisterActivity) context).switchToFragment(4);
				break;
			default:
				break;
		}
		return true;
	}
}
