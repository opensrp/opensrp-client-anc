package org.smartregister.anc.helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.smartregister.anc.R;
import org.smartregister.anc.util.Constants;

public class BottomNavigationHelper {
	/*
	 * This solution is hacky of any app using the support library < 28.0.0-alpha1. When we upgrade to => 28.0.0-alpha1
	 * please use this
	 * bottomNavigationView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED)
	 * */
	
	/**
	 * Removes the default bottom navigation bar behaviour to enable display of all 5 icons and text
	 * @param view
	 */
	@SuppressLint("RestrictedApi")
	public static void disableShiftMode(BottomNavigationView view) {
		BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
		try {
			java.lang.reflect.Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
			shiftingMode.setAccessible(true);
			shiftingMode.setBoolean(menuView, false);
			shiftingMode.setAccessible(false);
			for (int i = 0; i < menuView.getChildCount(); i++) {
				BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
				item.setShiftingMode(false);
				// set once again checked value, so view will be updated
				item.setChecked(item.getItemData().isChecked());
			}
		} catch (NoSuchFieldException e) {
			//Timber.e(e, "Unable to get shift mode field");
		} catch (IllegalAccessException e) {
			//Timber.e(e, "Unable to change value of shift mode");
		}
	}
	
	/**
	 * Adds the bottom navigation bar me icon.
	 * @param view
	 * @param context
	 */
	public static void addMeMenuItem(BottomNavigationView view, Context context){
	BottomNavigationMenuView bottomNavigation = (BottomNavigationMenuView) view.getChildAt(0);
		View meBadge = LayoutInflater.from(context).inflate(R.layout.me_menu,bottomNavigation,false);
		meBadge.setId(Constants.BOTTOM_NAV_MENU_ME);
		bottomNavigation.addView(meBadge);
	/*view.getMenu().add(Menu.NONE,4,Menu.NONE,R.string.me).setA;*/
	}
	
	/*public static void setCheckedBottomNavItem(BottomNavigationView bottomNavItem,int index) {
		for (int i = 0; i < bottomNavItem.getMenu().size(); i++) {
			MenuItem menuItem = bottomNavItem.getMenu().getItem(i);
			if(menuItem.getItemId() == index) {
				menuItem.setChecked(true);
			} else {
				menuItem.setChecked(false);
			}
		}
	}*/
}
