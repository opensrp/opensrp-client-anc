package org.smartregister.anc.library.repository;

import android.content.ContentValues;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.anc.library.AncLibrary;
import org.smartregister.anc.library.domain.WomanDetail;
import org.smartregister.anc.library.util.DBConstantsUtils;
import org.smartregister.anc.library.util.Utils;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.Repository;
import org.smartregister.view.activity.DrishtiApplication;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

/**
 * Created by ndegwamartin on 14/07/2018.
 */
public class PatientRepository extends BaseRepository {
    private static final String[] projection =
            new String[]{DBConstantsUtils.KeyUtils.FIRST_NAME, DBConstantsUtils.KeyUtils.LAST_NAME, DBConstantsUtils.KeyUtils.DOB,
                    DBConstantsUtils.KeyUtils.DOB_UNKNOWN, getRegisterQueryProvider().getDetailsTable() + "." + DBConstantsUtils.KeyUtils.PHONE_NUMBER, getRegisterQueryProvider().getDetailsTable() + "." + DBConstantsUtils.KeyUtils.ALT_NAME,
                    getRegisterQueryProvider().getDetailsTable() + "." + DBConstantsUtils.KeyUtils.ALT_PHONE_NUMBER, getRegisterQueryProvider().getDemographicTable() + "." + DBConstantsUtils.KeyUtils.BASE_ENTITY_ID, DBConstantsUtils.KeyUtils.ANC_ID,
                    getRegisterQueryProvider().getDetailsTable() + "." + DBConstantsUtils.KeyUtils.REMINDERS, DBConstantsUtils.KeyUtils.HOME_ADDRESS, getRegisterQueryProvider().getDetailsTable() + "." + DBConstantsUtils.KeyUtils.EDD,
                    getRegisterQueryProvider().getDetailsTable() + "." + DBConstantsUtils.KeyUtils.CONTACT_STATUS, getRegisterQueryProvider().getDetailsTable() + "." + DBConstantsUtils.KeyUtils.PREVIOUS_CONTACT_STATUS,
                    getRegisterQueryProvider().getDetailsTable() + "." + DBConstantsUtils.KeyUtils.NEXT_CONTACT, getRegisterQueryProvider().getDetailsTable() + "." + DBConstantsUtils.KeyUtils.NEXT_CONTACT_DATE,
                    getRegisterQueryProvider().getDetailsTable() + "." + DBConstantsUtils.KeyUtils.VISIT_START_DATE, getRegisterQueryProvider().getDetailsTable() + "." + DBConstantsUtils.KeyUtils.RED_FLAG_COUNT,
                    getRegisterQueryProvider().getDetailsTable() + "." + DBConstantsUtils.KeyUtils.YELLOW_FLAG_COUNT, getRegisterQueryProvider().getDetailsTable() + "." + DBConstantsUtils.KeyUtils.LAST_CONTACT_RECORD_DATE};

    public static Map<String, String> getWomanProfileDetails(String baseEntityId) {
        Cursor cursor = null;

        Map<String, String> detailsMap = null;
        try {
            SQLiteDatabase db = getMasterRepository().getReadableDatabase();

            String query =
                    "SELECT " + StringUtils.join(projection, ",") + " FROM " + getRegisterQueryProvider().getDemographicTable() + " join " + getRegisterQueryProvider().getDetailsTable() +
                            " on " + getRegisterQueryProvider().getDemographicTable() + ".base_entity_id = " + getRegisterQueryProvider().getDetailsTable() + ".base_entity_id WHERE " +
                            getRegisterQueryProvider().getDemographicTable() + "." + DBConstantsUtils.KeyUtils.BASE_ENTITY_ID + " = ?";
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
                detailsMap.put(DBConstantsUtils.KeyUtils.CONTACT_STATUS, cursor.getString(cursor.getColumnIndex(DBConstantsUtils.KeyUtils.CONTACT_STATUS)));
                detailsMap.put(DBConstantsUtils.KeyUtils.PREVIOUS_CONTACT_STATUS, cursor.getString(cursor.getColumnIndex(DBConstantsUtils.KeyUtils.PREVIOUS_CONTACT_STATUS)));
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
            Timber.e(e, "%s ==> getWomanProfileDetails()", PatientRepository.class.getCanonicalName());
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

    private static RegisterQueryProvider getRegisterQueryProvider() {
        return AncLibrary.getInstance().getRegisterQueryProvider();
    }

    public static void updateWomanAlertStatus(String baseEntityId, String alertStatus) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBConstantsUtils.KeyUtils.CONTACT_STATUS, alertStatus);

        updatePatient(baseEntityId, contentValues, getRegisterQueryProvider().getDetailsTable());

        updateLastInteractedWith(baseEntityId);
    }

    public static void updatePatient(String baseEntityId, ContentValues contentValues, String table) {
        getMasterRepository().getWritableDatabase()
                .update(table, contentValues, DBConstantsUtils.KeyUtils.BASE_ENTITY_ID + " = ?",
                        new String[]{baseEntityId});
    }

    private static void updateLastInteractedWith(String baseEntityId) {
        ContentValues lastInteractedWithContentValue = new ContentValues();

        lastInteractedWithContentValue.put(DBConstantsUtils.KeyUtils.LAST_INTERACTED_WITH, Calendar.getInstance().getTimeInMillis());

        updatePatient(baseEntityId, lastInteractedWithContentValue, getRegisterQueryProvider().getDemographicTable());
    }

    public static void updateContactVisitDetails(WomanDetail patientDetail, boolean isFinalize) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBConstantsUtils.KeyUtils.NEXT_CONTACT, patientDetail.getNextContact());
        contentValues.put(DBConstantsUtils.KeyUtils.NEXT_CONTACT_DATE, patientDetail.getNextContactDate());
        contentValues.put(DBConstantsUtils.KeyUtils.YELLOW_FLAG_COUNT, patientDetail.getYellowFlagCount());
        contentValues.put(DBConstantsUtils.KeyUtils.RED_FLAG_COUNT, patientDetail.getRedFlagCount());
        contentValues.put(DBConstantsUtils.KeyUtils.CONTACT_STATUS, patientDetail.getContactStatus());
        if (isFinalize) {
            if (!patientDetail.isReferral()) {
                contentValues
                        .put(DBConstantsUtils.KeyUtils.LAST_CONTACT_RECORD_DATE, Utils.DB_DF.format(Calendar.getInstance().getTime()));
                contentValues.put(DBConstantsUtils.KeyUtils.PREVIOUS_CONTACT_STATUS, patientDetail.getContactStatus());
            } else {
                contentValues.put(DBConstantsUtils.KeyUtils.LAST_CONTACT_RECORD_DATE, patientDetail.getLastContactRecordDate());
                contentValues.put(DBConstantsUtils.KeyUtils.CONTACT_STATUS, patientDetail.getPreviousContactStatus());
            }
        }

        updatePatient(patientDetail.getBaseEntityId(), contentValues, getRegisterQueryProvider().getDetailsTable());

        updateLastInteractedWith(patientDetail.getBaseEntityId());
    }

    public static void updateEDDDate(String baseEntityId, String edd) {

        ContentValues contentValues = new ContentValues();
        if (edd != null) {
            contentValues.put(DBConstantsUtils.KeyUtils.EDD, edd);
        } else {
            contentValues.putNull(DBConstantsUtils.KeyUtils.EDD);
        }
        updatePatient(baseEntityId, contentValues, getRegisterQueryProvider().getDetailsTable());
    }

    public static void updateContactVisitStartDate(String baseEntityId, String contactVisitStartDate) {

        ContentValues contentValues = new ContentValues();
        if (contactVisitStartDate != null) {
            contentValues.put(DBConstantsUtils.KeyUtils.VISIT_START_DATE, contactVisitStartDate);
        } else {
            contentValues.putNull(DBConstantsUtils.KeyUtils.VISIT_START_DATE);
        }
        updatePatient(baseEntityId, contentValues, getRegisterQueryProvider().getDetailsTable());
    }

}
