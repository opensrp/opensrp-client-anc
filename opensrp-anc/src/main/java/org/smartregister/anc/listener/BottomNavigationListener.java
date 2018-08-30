package org.smartregister.anc.listener;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.MenuItem;
import android.view.View;
import org.smartregister.anc.R;
import org.smartregister.anc.activity.HomeRegisterActivity;
import org.smartregister.anc.util.Constants;

public class BottomNavigationListener implements BottomNavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
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
			case Constants.BOTTOM_NAV_MENU_ME:
				((HomeRegisterActivity) context).switchToFragment(3);
				break;
			default:
				break;
		}
		return true;
	}
	
	@Override
	public void onClick(View view) {
		if (context == null) {
			return;
		}
		
		switch (view.getId()) {
			case R.id.name_initials:
				((HomeRegisterActivity) context).switchToFragment(3);
				break;
			case  R.id.name_initials_text:
				((HomeRegisterActivity) context).switchToFragment(3);
				break;
			default:
				break;
		}
	}
}
