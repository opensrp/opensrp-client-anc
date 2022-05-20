package org.smartregister.anc.library.repository;

import android.content.ContentValues;
import androidx.annotation.NonNull;

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

    private static final String[] projection = getRegisterQueryProvider().mainColumns();

    /**
     * Provides all woman details needed for display and/or editing
     *
     * @param baseEntityId
     * @return {@link Map}
     */
    public static Map<String, String> getWomanProfileDetails(String baseEntityId) {
        Cursor cursor = null;

        Map<String, String> detailsMap = null;
        try {
            SQLiteDatabase db = getMasterRepository().getReadableDatabase();

            String query =
                    "SELECT " + StringUtils.join(projection, ",") + " FROM " + getRegisterQueryProvider().getDemographicTable() + " join " + getRegisterQueryProvider().getDetailsTable() +
                            " on " + getRegisterQueryProvider().getDemographicTable() + "." + DBConstantsUtils.KeyUtils.BASE_ENTITY_ID + " = " + getRegisterQueryProvider().getDetailsTable() + "." + DBConstantsUtils.KeyUtils.BASE_ENTITY_ID + " WHERE " +
                            getRegisterQueryProvider().getDemographicTable() + "." + DBConstantsUtils.KeyUtils.BASE_ENTITY_ID + " = ?";
            cursor = db.rawQuery(query, new String[]{baseEntityId});
            if (cursor != null && cursor.moveToFirst()) {
                detailsMap = new HashMap<>();
                for (int count = 0; count < projection.length; count++) {
                    String columnName = cursor.getColumnName(count);
                    detailsMap.put(columnName, cursor.getString(cursor.getColumnIndex(columnName)));
                }
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

    public static boolean isFirstVisit(@NonNull String baseEntityId) {
        SQLiteDatabase sqLiteDatabase = getMasterRepository().getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(getRegisterQueryProvider().getDetailsTable(),
                new String[]{DBConstantsUtils.KeyUtils.EDD},
                DBConstantsUtils.KeyUtils.BASE_ENTITY_ID + " = ? ",
                new String[]{baseEntityId}, null, null, null, "1");
        String isFirstVisit = null;
        if (cursor != null && cursor.moveToFirst()) {
            isFirstVisit = cursor.getString(cursor.getColumnIndex(DBConstantsUtils.KeyUtils.EDD));
            cursor.close();
        }

        return StringUtils.isBlank(isFirstVisit);
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

        getMasterRepository().getWritableDatabase()
                .update(getRegisterQueryProvider().getDetailsTable(), contentValues, DBConstantsUtils.KeyUtils.BASE_ENTITY_ID + " = ?",
                        new String[]{baseEntityId});

        updateLastInteractedWith(baseEntityId);
    }

    private static void updateLastInteractedWith(String baseEntityId) {
        ContentValues lastInteractedWithContentValue = new ContentValues();

        lastInteractedWithContentValue.put(DBConstantsUtils.KeyUtils.LAST_INTERACTED_WITH, Calendar.getInstance().getTimeInMillis());

        getMasterRepository().getWritableDatabase().update(getRegisterQueryProvider().getDemographicTable(), lastInteractedWithContentValue, DBConstantsUtils.KeyUtils.BASE_ENTITY_ID + " = ?",
                new String[]{baseEntityId});
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

        getMasterRepository().getWritableDatabase().update(getRegisterQueryProvider().getDetailsTable(), contentValues, DBConstantsUtils.KeyUtils.BASE_ENTITY_ID + " = ?",
                new String[]{patientDetail.getBaseEntityId()});

        updateLastInteractedWith(patientDetail.getBaseEntityId());
    }

    public static void updateEDDDate(String baseEntityId, String edd) {

        ContentValues contentValues = new ContentValues();
        if (edd != null) {
            contentValues.put(DBConstantsUtils.KeyUtils.EDD, edd);
        } else {
            contentValues.putNull(DBConstantsUtils.KeyUtils.EDD);
        }
        getMasterRepository().getWritableDatabase()
                .update(getRegisterQueryProvider().getDetailsTable(), contentValues, DBConstantsUtils.KeyUtils.BASE_ENTITY_ID + " = ?",
                        new String[]{baseEntityId});
    }

    public static void updateContactVisitStartDate(String baseEntityId, String contactVisitStartDate) {

        ContentValues contentValues = new ContentValues();
        if (contactVisitStartDate != null) {
            contentValues.put(DBConstantsUtils.KeyUtils.VISIT_START_DATE, contactVisitStartDate);
        } else {
            contentValues.putNull(DBConstantsUtils.KeyUtils.VISIT_START_DATE);
        }
        getMasterRepository().getWritableDatabase()
                .update(getRegisterQueryProvider().getDetailsTable(), contentValues, DBConstantsUtils.KeyUtils.BASE_ENTITY_ID + " = ?",
                        new String[]{baseEntityId});
    }

}
