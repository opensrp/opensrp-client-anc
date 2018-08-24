package org.smartregister.anc.util;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.MenuItem;
import org.smartregister.anc.R;

public class BottomNavigationBarActionHandler implements BottomNavigationView.OnNavigationItemSelectedListener {
	
	@Override
	public boolean onNavigationItemSelected(@NonNull MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_clients:
				break;
			case R.id.action_search:
				break;
			case R.id.action_register:
				break;
			case R.id.action_library:
				break;
			case R.id.action_me:
				break;
			default:
				break;
		}
		return true;
	}
}