package org.smartregister.anc.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.view.LayoutInflater;
import android.view.View;
import org.smartregister.anc.R;

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
	public static void addMeTextOnBottomBar(BottomNavigationView view, Context context){
		BottomNavigationMenuView bottomNavigation = (BottomNavigationMenuView) view.getChildAt(0);
		View menuView = bottomNavigation.getChildAt(4);
		
		BottomNavigationItemView itemView = (BottomNavigationItemView) menuView;
		
		View meBadge = LayoutInflater.from(context).inflate(R.layout.me_menu,bottomNavigation,false);
		itemView.addView(meBadge);
	}
}
