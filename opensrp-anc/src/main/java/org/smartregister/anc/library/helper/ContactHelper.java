package org.smartregister.anc.library.helper;

import android.content.Context;

import java.sql.Array;

/**
 * ContactHelper
 * A class to help us developers handling contact data.
 * */
public class ContactHelper {
	private Context context;

	/**
	 * Assign Android Context
	 * @param context - Android context
	 */
	public ContactHelper(Context context) {
		this.context = context;
	}

	public String getContactString(String key) {
		// Prefix to the key string
		String prefix = "contact_";

		// Cleanup key before processing
		String cleanKey = key;

		/*
		String[] ignoredChars = { "'", "[", "]", "{", "}" };
		for (String ignored : ignoredChars){
			cleanKey.replace(ignored, "");
		}
		*/

		// Get identifier
		int identifier = this.context.getResources().getIdentifier(prefix + cleanKey, "string", this.context.getPackageName());

		// Get string from resources
		try {
			// Return string from resources
			return this.context.getResources().getString(identifier);
		} catch (Exception e) {
			// If not found, return key
			return key;
		}
	}


}
