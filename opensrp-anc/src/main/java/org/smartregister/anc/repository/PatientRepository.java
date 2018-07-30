package org.smartregister.anc.repository;

import android.util.Log;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.anc.application.AncApplication;
import org.smartregister.anc.util.DBConstants;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ndegwamartin on 14/07/2018.
 */
public class PatientRepository {

    private static final String TAG = PatientRepository.class.getCanonicalName();

    public static Map<String, String> getWomanProfileDetails(String baseEntityId) {
        Cursor cursor = null;

        Map<String, String> detailsMap = null;
        try {
            SQLiteDatabase db = AncApplication.getInstance().getRepository().getReadableDatabase();

            String query = "SELECT " + DBConstants.KEY.FIRST_NAME + "," + DBConstants.KEY.LAST_NAME + "," + DBConstants.KEY.DOB + ","  + DBConstants.KEY.DOB_UNKNOWN + "," + DBConstants.KEY.PHONE_NUMBER + "," + DBConstants.KEY.ALT_NAME + "," + DBConstants.KEY.ALT_PHONE_NUMBER + "," + DBConstants.KEY.BASE_ENTITY_ID + "," + DBConstants.KEY.ANC_ID+ "," + DBConstants.KEY.REMINDERS  + " FROM " + DBConstants.WOMAN_TABLE_NAME + " WHERE " + DBConstants.KEY.BASE_ENTITY_ID + " = ?";
            cursor = db.rawQuery(query, new String[]{baseEntityId});
            if (cursor != null && cursor.moveToFirst()) {
                detailsMap = new HashMap<>();
                detailsMap.put(DBConstants.KEY.FIRST_NAME, cursor.getString(cursor.getColumnIndex(DBConstants.KEY.FIRST_NAME)));
                detailsMap.put(DBConstants.KEY.LAST_NAME, cursor.getString(cursor.getColumnIndex(DBConstants.KEY.LAST_NAME)));
                detailsMap.put(DBConstants.KEY.ANC_ID, cursor.getString(cursor.getColumnIndex(DBConstants.KEY.ANC_ID)));
                detailsMap.put(DBConstants.KEY.DOB, cursor.getString(cursor.getColumnIndex(DBConstants.KEY.DOB)));
                detailsMap.put(DBConstants.KEY.DOB_UNKNOWN, cursor.getString(cursor.getColumnIndex(DBConstants.KEY.DOB_UNKNOWN)));
                detailsMap.put(DBConstants.KEY.FIRST_NAME, cursor.getString(cursor.getColumnIndex(DBConstants.KEY.FIRST_NAME)));
                detailsMap.put(DBConstants.KEY.BASE_ENTITY_ID, cursor.getString(cursor.getColumnIndex(DBConstants.KEY.BASE_ENTITY_ID)));
                detailsMap.put(DBConstants.KEY.PHONE_NUMBER, cursor.getString(cursor.getColumnIndex(DBConstants.KEY.PHONE_NUMBER)));
                detailsMap.put(DBConstants.KEY.ALT_NAME, cursor.getString(cursor.getColumnIndex(DBConstants.KEY.ALT_NAME)));
                detailsMap.put(DBConstants.KEY.ALT_PHONE_NUMBER, cursor.getString(cursor.getColumnIndex(DBConstants.KEY.ALT_PHONE_NUMBER)));
                detailsMap.put(DBConstants.KEY.REMINDERS, cursor.getString(cursor.getColumnIndex(DBConstants.KEY.REMINDERS)));

            }
            return detailsMap;
        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }
}
