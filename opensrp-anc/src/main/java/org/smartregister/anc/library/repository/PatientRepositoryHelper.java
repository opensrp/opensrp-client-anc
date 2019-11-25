package org.smartregister.anc.library.repository;

import android.content.ContentValues;
import android.support.annotation.NonNull;
import android.util.Log;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.anc.library.AncLibrary;
import org.smartregister.anc.library.domain.WomanDetail;
import org.smartregister.anc.library.util.DBConstantsUtils;
import org.smartregister.anc.library.util.Utils;
import org.smartregister.repository.Repository;
import org.smartregister.view.activity.DrishtiApplication;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

/**
 * Created by ndegwamartin on 14/07/2018.
 */
public class PatientRepositoryHelper {

    private static final String TAG = PatientRepositoryHelper.class.getCanonicalName();

    private static final String[] projection =
            new String[]{DBConstantsUtils.KeyUtils.FIRST_NAME, DBConstantsUtils.KeyUtils.LAST_NAME, DBConstantsUtils.KeyUtils.DOB,
                    DBConstantsUtils.KeyUtils.DOB_UNKNOWN, DBConstantsUtils.KeyUtils.PHONE_NUMBER, DBConstantsUtils.KeyUtils.ALT_NAME,
                    DBConstantsUtils.KeyUtils.ALT_PHONE_NUMBER, DBConstantsUtils.KeyUtils.BASE_ENTITY_ID, DBConstantsUtils.KeyUtils.ANC_ID,
                    DBConstantsUtils.KeyUtils.REMINDERS, DBConstantsUtils.KeyUtils.HOME_ADDRESS, DBConstantsUtils.KeyUtils.EDD,
                    DBConstantsUtils.KeyUtils.CONTACT_STATUS, DBConstantsUtils.KeyUtils.NEXT_CONTACT, DBConstantsUtils.KeyUtils.NEXT_CONTACT_DATE,
                    DBConstantsUtils.KeyUtils.VISIT_START_DATE, DBConstantsUtils.KeyUtils.RED_FLAG_COUNT, DBConstantsUtils.KeyUtils.YELLOW_FLAG_COUNT,
                    DBConstantsUtils.KeyUtils.LAST_CONTACT_RECORD_DATE};

    public static Map<String, String> getWomanProfileDetails(String baseEntityId) {
        Cursor cursor = null;

        Map<String, String> detailsMap = null;
        try {
            SQLiteDatabase db = getMasterRepository().getReadableDatabase();

            String query =
                    "SELECT " + StringUtils.join(projection, ",") + " FROM " + DBConstantsUtils.WOMAN_TABLE_NAME + " WHERE " +
                            DBConstantsUtils.KeyUtils.BASE_ENTITY_ID + " = ?";
            cursor = db.rawQuery(query, new String[]{baseEntityId});
            if (cursor != null && cursor.moveToFirst()) {
                detailsMap = new HashMap<>();
                detailsMap.put(DBConstantsUtils.KeyUtils.FIRST_NAME,
                        cursor.getString(cursor.getColumnIndex(DBConstantsUtils.KeyUtils.FIRST_NAME)));
                detailsMap
                        .put(DBConstantsUtils.KeyUtils.LAST_NAME, cursor.getString(cursor.getColumnIndex(DBConstantsUtils.KeyUtils.LAST_NAME)));
                detailsMap.put(DBConstantsUtils.KeyUtils.ANC_ID, cursor.getString(cursor.getColumnIndex(DBConstantsUtils.KeyUtils.ANC_ID)));
                detailsMap.put(DBConstantsUtils.KeyUtils.DOB, cursor.getString(cursor.getColumnIndex(DBConstantsUtils.KeyUtils.DOB)));
                detailsMap.put(DBConstantsUtils.KeyUtils.DOB_UNKNOWN,
                        cursor.getString(cursor.getColumnIndex(DBConstantsUtils.KeyUtils.DOB_UNKNOWN)));
                detailsMap.put(DBConstantsUtils.KeyUtils.BASE_ENTITY_ID,
                        cursor.getString(cursor.getColumnIndex(DBConstantsUtils.KeyUtils.BASE_ENTITY_ID)));
                detailsMap.put(DBConstantsUtils.KeyUtils.ID_LOWER_CASE,
                        cursor.getString(cursor.getColumnIndex(DBConstantsUtils.KeyUtils.BASE_ENTITY_ID)));
                detailsMap.put(DBConstantsUtils.KeyUtils.PHONE_NUMBER,
                        cursor.getString(cursor.getColumnIndex(DBConstantsUtils.KeyUtils.PHONE_NUMBER)));
                detailsMap.put(DBConstantsUtils.KeyUtils.ALT_NAME, cursor.getString(cursor.getColumnIndex(DBConstantsUtils.KeyUtils.ALT_NAME)));
                detailsMap.put(DBConstantsUtils.KeyUtils.ALT_PHONE_NUMBER,
                        cursor.getString(cursor.getColumnIndex(DBConstantsUtils.KeyUtils.ALT_PHONE_NUMBER)));
                detailsMap
                        .put(DBConstantsUtils.KeyUtils.REMINDERS, cursor.getString(cursor.getColumnIndex(DBConstantsUtils.KeyUtils.REMINDERS)));
                detailsMap.put(DBConstantsUtils.KeyUtils.HOME_ADDRESS,
                        cursor.getString(cursor.getColumnIndex(DBConstantsUtils.KeyUtils.HOME_ADDRESS)));
                detailsMap.put(DBConstantsUtils.KeyUtils.EDD, cursor.getString(cursor.getColumnIndex(DBConstantsUtils.KeyUtils.EDD)));
                detailsMap.put(DBConstantsUtils.KeyUtils.CONTACT_STATUS,
                        cursor.getString(cursor.getColumnIndex(DBConstantsUtils.KeyUtils.CONTACT_STATUS)));
                detailsMap.put(DBConstantsUtils.KeyUtils.NEXT_CONTACT,
                        cursor.getString(cursor.getColumnIndex(DBConstantsUtils.KeyUtils.NEXT_CONTACT)));
                detailsMap.put(DBConstantsUtils.KeyUtils.NEXT_CONTACT_DATE,
                        cursor.getString(cursor.getColumnIndex(DBConstantsUtils.KeyUtils.NEXT_CONTACT_DATE)));
                detailsMap.put(DBConstantsUtils.KeyUtils.VISIT_START_DATE,
                        cursor.getString(cursor.getColumnIndex(DBConstantsUtils.KeyUtils.VISIT_START_DATE)));
                detailsMap.put(DBConstantsUtils.KeyUtils.RED_FLAG_COUNT,
                        cursor.getString(cursor.getColumnIndex(DBConstantsUtils.KeyUtils.RED_FLAG_COUNT)));
                detailsMap.put(DBConstantsUtils.KeyUtils.YELLOW_FLAG_COUNT,
                        cursor.getString(cursor.getColumnIndex(DBConstantsUtils.KeyUtils.YELLOW_FLAG_COUNT)));
                detailsMap.put(DBConstantsUtils.KeyUtils.LAST_CONTACT_RECORD_DATE,
                        cursor.getString(cursor.getColumnIndex(DBConstantsUtils.KeyUtils.LAST_CONTACT_RECORD_DATE)));
            }
            return detailsMap;
        } catch (Exception e) {
            Timber.e(e, "");
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    protected static Repository getMasterRepository() {
        return DrishtiApplication.getInstance().getRepository();
    }

    public static void updateWomanAlertStatus(String baseEntityId, String alertStatus) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBConstantsUtils.KeyUtils.CONTACT_STATUS, alertStatus);
        contentValues.put(DBConstantsUtils.KeyUtils.LAST_INTERACTED_WITH, Calendar.getInstance().getTimeInMillis());

        getMasterRepository().getWritableDatabase()
                .update(DBConstantsUtils.WOMAN_TABLE_NAME, contentValues, DBConstantsUtils.KeyUtils.BASE_ENTITY_ID + " = ?",
                        new String[]{baseEntityId});
    }

    public static void updateContactVisitDetails(WomanDetail patientDetail, boolean isFinalize) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBConstantsUtils.KeyUtils.NEXT_CONTACT, patientDetail.getNextContact());
        contentValues.put(DBConstantsUtils.KeyUtils.NEXT_CONTACT_DATE, patientDetail.getNextContactDate());
        contentValues.put(DBConstantsUtils.KeyUtils.YELLOW_FLAG_COUNT, patientDetail.getYellowFlagCount());
        contentValues.put(DBConstantsUtils.KeyUtils.RED_FLAG_COUNT, patientDetail.getRedFlagCount());
        contentValues.put(DBConstantsUtils.KeyUtils.LAST_INTERACTED_WITH, Calendar.getInstance().getTimeInMillis());
        contentValues.put(DBConstantsUtils.KeyUtils.CONTACT_STATUS, patientDetail.getContactStatus());
        if (isFinalize) {
            if (!patientDetail.isReferral()) {
                contentValues
                        .put(DBConstantsUtils.KeyUtils.LAST_CONTACT_RECORD_DATE, Utils.DB_DF.format(Calendar.getInstance().getTime()));
            } else {
                contentValues.put(DBConstantsUtils.KeyUtils.LAST_CONTACT_RECORD_DATE, patientDetail.getLastContactRecordDate());
            }
        }
        AncLibrary.getInstance().getRepository().getWritableDatabase()
                .update(DBConstantsUtils.WOMAN_TABLE_NAME, contentValues, DBConstantsUtils.KeyUtils.BASE_ENTITY_ID + " = ?",
                        new String[]{patientDetail.getBaseEntityId()});
    }

    public static void updateEDDDate(String baseEntityId, String edd) {

        ContentValues contentValues = new ContentValues();
        if (edd != null) {
            contentValues.put(DBConstantsUtils.KeyUtils.EDD, edd);
            //contentValues.put(DBConstants.KEY.LAST_INTERACTED_WITH, Calendar.getInstance().getTimeInMillis());
        } else {
            contentValues.putNull(DBConstantsUtils.KeyUtils.EDD);
        }
        AncLibrary.getInstance().getRepository().getWritableDatabase()
                .update(DBConstantsUtils.WOMAN_TABLE_NAME, contentValues, DBConstantsUtils.KeyUtils.BASE_ENTITY_ID + " = ?",
                        new String[]{baseEntityId});
    }

    public static void updateContactVisitStartDate(String baseEntityId, String contactVisitStartDate) {

        ContentValues contentValues = new ContentValues();
        if (contactVisitStartDate != null) {
            contentValues.put(DBConstantsUtils.KeyUtils.VISIT_START_DATE, contactVisitStartDate);
        } else {
            contentValues.putNull(DBConstantsUtils.KeyUtils.VISIT_START_DATE);
        }
        AncLibrary.getInstance().getRepository().getWritableDatabase()
                .update(DBConstantsUtils.WOMAN_TABLE_NAME, contentValues, DBConstantsUtils.KeyUtils.BASE_ENTITY_ID + " = ?",
                        new String[]{baseEntityId});
    }

    public static void performMigrations(@NonNull SQLiteDatabase database) {
        // Change table name and add change the anc_id field to register_id field

        if (Utils.isTableExists(database, "ec_woman")) {
            changeTableNameToEcMother(database);
        }
    }

    public static void changeTableNameToEcMother(@NonNull SQLiteDatabase database) {
        // Add fields to the ec_mother table
        // Move data from the
        // Set the default for the is_value_set to false
        database.execSQL("PRAGMA foreign_keys=off");
        database.beginTransaction();

        database.execSQL("CREATE TABLE ec_mother(id VARCHAR PRIMARY KEY,relationalid VARCHAR, details VARCHAR, is_closed TINYINT DEFAULT 0, base_entity_id VARCHAR,register_id VARCHAR,first_name VARCHAR,last_name VARCHAR,dob VARCHAR,dob_unknown VARCHAR,last_interacted_with VARCHAR,date_removed VARCHAR,phone_number VARCHAR,alt_name VARCHAR,alt_phone_number VARCHAR,reminders VARCHAR,home_address VARCHAR,edd VARCHAR,red_flag_count VARCHAR,yellow_flag_count VARCHAR,contact_status VARCHAR,next_contact VARCHAR,next_contact_date VARCHAR,last_contact_record_date VARCHAR,visit_start_date VARCHAR)");

        String copyDataSQL = String.format("INSERT INTO %s(%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s) SELECT %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s FROM %s",
                DBConstantsUtils.WOMAN_TABLE_NAME,
                "id",
                DBConstantsUtils.KeyUtils.RELATIONAL_ID,
                DBConstantsUtils.KeyUtils.BASE_ENTITY_ID,
                DBConstantsUtils.KeyUtils.ANC_ID,
                DBConstantsUtils.KeyUtils.FIRST_NAME,
                DBConstantsUtils.KeyUtils.LAST_NAME,
                DBConstantsUtils.KeyUtils.DOB,
                DBConstantsUtils.KeyUtils.DOB_UNKNOWN,
                DBConstantsUtils.KeyUtils.LAST_INTERACTED_WITH,
                DBConstantsUtils.KeyUtils.DATE_REMOVED,
                DBConstantsUtils.KeyUtils.PHONE_NUMBER,
                DBConstantsUtils.KeyUtils.ALT_NAME,
                DBConstantsUtils.KeyUtils.ALT_PHONE_NUMBER,
                DBConstantsUtils.KeyUtils.REMINDERS,
                DBConstantsUtils.KeyUtils.HOME_ADDRESS,
                DBConstantsUtils.KeyUtils.EDD,
                DBConstantsUtils.KeyUtils.RED_FLAG_COUNT,
                DBConstantsUtils.KeyUtils.YELLOW_FLAG_COUNT,
                DBConstantsUtils.KeyUtils.CONTACT_STATUS,
                DBConstantsUtils.KeyUtils.NEXT_CONTACT,
                DBConstantsUtils.KeyUtils.NEXT_CONTACT_DATE,
                DBConstantsUtils.KeyUtils.LAST_CONTACT_RECORD_DATE,
                DBConstantsUtils.KeyUtils.VISIT_START_DATE,
                "id",
                DBConstantsUtils.KeyUtils.RELATIONAL_ID,
                DBConstantsUtils.KeyUtils.BASE_ENTITY_ID,
                "anc_id",
                DBConstantsUtils.KeyUtils.FIRST_NAME,
                DBConstantsUtils.KeyUtils.LAST_NAME,
                DBConstantsUtils.KeyUtils.DOB,
                DBConstantsUtils.KeyUtils.DOB_UNKNOWN,
                DBConstantsUtils.KeyUtils.LAST_INTERACTED_WITH,
                DBConstantsUtils.KeyUtils.DATE_REMOVED,
                DBConstantsUtils.KeyUtils.PHONE_NUMBER,
                DBConstantsUtils.KeyUtils.ALT_NAME,
                DBConstantsUtils.KeyUtils.ALT_PHONE_NUMBER,
                DBConstantsUtils.KeyUtils.REMINDERS,
                DBConstantsUtils.KeyUtils.HOME_ADDRESS,
                DBConstantsUtils.KeyUtils.EDD,
                DBConstantsUtils.KeyUtils.RED_FLAG_COUNT,
                DBConstantsUtils.KeyUtils.YELLOW_FLAG_COUNT,
                DBConstantsUtils.KeyUtils.CONTACT_STATUS,
                DBConstantsUtils.KeyUtils.NEXT_CONTACT,
                DBConstantsUtils.KeyUtils.NEXT_CONTACT_DATE,
                DBConstantsUtils.KeyUtils.LAST_CONTACT_RECORD_DATE,
                DBConstantsUtils.KeyUtils.VISIT_START_DATE,
                "ec_woman");

        // Copy over the data
        database.execSQL(copyDataSQL);
        database.execSQL("DROP TABLE ec_woman");

        // Create ec_mother indexes
        database.execSQL("CREATE INDEX ec_mother_base_entity_id_index ON ec_mother(base_entity_id COLLATE NOCASE)");
        database.execSQL("CREATE INDEX ec_mother_id_index ON ec_mother(id COLLATE NOCASE)");
        database.execSQL("CREATE INDEX ec_mother_relationalid_index ON ec_mother(relationalid COLLATE NOCASE)");

        // Create ec_mother FTS table
        database.execSQL("create virtual table ec_mother_search using fts4 (object_id,object_relational_id,phrase,is_closed TINYINT DEFAULT 0,base_entity_id,first_name,last_name,last_interacted_with,date_removed)");

        // Copy the current FTS table ec_woman_sesarch into ec_mother_search
        database.execSQL("insert into ec_mother_search(object_id, object_relational_id, phrase, is_closed, base_entity_id, first_name, last_name, last_interacted_with, date_removed) " +
                "SELECT object_id, object_relational_id, phrase, is_closed, base_entity_id, first_name, last_name, last_interacted_with, date_removed FROM ec_woman_search");

        // Delete the previous FTS table ec_woman_search
        database.execSQL("DROP TABLE ec_woman_search");

        // Update the ec_details to use register_id
        database.execSQL("UPDATE ec_details SET key = 'register_id' WHERE key = 'anc_id'");

        database.setTransactionSuccessful();
        database.endTransaction();
        database.execSQL("PRAGMA foreign_keys=on;");
    }

}
