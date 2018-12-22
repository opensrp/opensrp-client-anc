package org.smartregister.anc.repository;

import android.content.ContentValues;
import android.util.Log;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.anc.application.AncApplication;
import org.smartregister.anc.util.DBConstants;
import org.smartregister.anc.util.Utils;
import org.smartregister.repository.Repository;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ndegwamartin on 14/07/2018.
 */
public class PatientRepository {

    private static final String TAG = PatientRepository.class.getCanonicalName();

    private static final String[] projection = new String[]{DBConstants.KEY.FIRST_NAME, DBConstants.KEY.LAST_NAME, DBConstants.KEY.DOB, DBConstants.KEY.DOB_UNKNOWN, DBConstants.KEY.PHONE_NUMBER, DBConstants.KEY.ALT_NAME, DBConstants.KEY.ALT_PHONE_NUMBER, DBConstants.KEY.BASE_ENTITY_ID, DBConstants.KEY.ANC_ID, DBConstants.KEY.REMINDERS, DBConstants.KEY.HOME_ADDRESS, DBConstants.KEY.EDD, DBConstants.KEY.CONTACT_STATUS, DBConstants.KEY.NEXT_CONTACT, DBConstants.KEY.NEXT_CONTACT_DATE};

    public static Map<String, String> getWomanProfileDetails(String baseEntityId) {
        Cursor cursor = null;

        Map<String, String> detailsMap = null;
        try {
            SQLiteDatabase db = getMasterRepository().getReadableDatabase();

            String query = "SELECT " + StringUtils.join(projection, ",") + " FROM " + DBConstants.WOMAN_TABLE_NAME + " WHERE " + DBConstants.KEY.BASE_ENTITY_ID + " = ?";
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
                detailsMap.put(DBConstants.KEY.HOME_ADDRESS, cursor.getString(cursor.getColumnIndex(DBConstants.KEY.HOME_ADDRESS)));
                detailsMap.put(DBConstants.KEY.EDD, cursor.getString(cursor.getColumnIndex(DBConstants.KEY.EDD)));
                detailsMap.put(DBConstants.KEY.CONTACT_STATUS, cursor.getString(cursor.getColumnIndex(DBConstants.KEY.CONTACT_STATUS)));
                detailsMap.put(DBConstants.KEY.NEXT_CONTACT, cursor.getString(cursor.getColumnIndex(DBConstants.KEY.NEXT_CONTACT)));
                detailsMap.put(DBConstants.KEY.NEXT_CONTACT_DATE, cursor.getString(cursor.getColumnIndex(DBConstants.KEY.NEXT_CONTACT_DATE)));

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

    public static void updateWomanProfileDetails(String baseEntityId) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBConstants.KEY.CONTACT_STATUS, "active");
        contentValues.put(DBConstants.KEY.LAST_INTERACTED_WITH, Calendar.getInstance().getTimeInMillis());

        getMasterRepository().getWritableDatabase().update(DBConstants.WOMAN_TABLE_NAME, contentValues, DBConstants.KEY.BASE_ENTITY_ID + " = ?", new String[]{baseEntityId});
    }

    protected static Repository getMasterRepository() {
        return AncApplication.getInstance().getRepository();
    }

    public static void updateContactVisitDetails(String baseEntityId, Integer nextContact, String nextContactDate) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBConstants.KEY.NEXT_CONTACT, nextContact);
        contentValues.put(DBConstants.KEY.NEXT_CONTACT_DATE, nextContactDate);
        contentValues.put(DBConstants.KEY.LAST_INTERACTED_WITH, Calendar.getInstance().getTimeInMillis());

        AncApplication.getInstance().getRepository().getWritableDatabase().update(DBConstants.WOMAN_TABLE_NAME, contentValues, DBConstants.KEY.BASE_ENTITY_ID + " = ?", new String[]{baseEntityId});
    }

    public static void updateEDD(String baseEntityId, String edd) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBConstants.KEY.EDD, edd);
        contentValues.put(DBConstants.KEY.LAST_CONTACT_RECORD_DATE, Utils.DB_DF.format(Calendar.getInstance().getTime()));
        AncApplication.getInstance().getRepository().getWritableDatabase().update(DBConstants.WOMAN_TABLE_NAME, contentValues, DBConstants.KEY.BASE_ENTITY_ID + " = ?", new String[]{baseEntityId});
    }


}
