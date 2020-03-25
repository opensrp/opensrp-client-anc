package org.smartregister.anc.library.repository;

import android.content.ContentValues;

import com.google.common.collect.ImmutableMap;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.anc.library.model.PartialContact;
import org.smartregister.anc.library.util.ConstantsUtils;
import org.smartregister.repository.BaseRepository;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class PartialContactRepository extends BaseRepository {
    public static final String TABLE_NAME = "partial_contact";
    public static final String ID = "_id";
    public static final String BASE_ENTITY_ID = "base_entity_id";
    public static final String TYPE = "type";
    public static final String FORM_JSON = "form_json";
    public static final String FORM_JSON_DRAFT = "form_json_draft";
    public static final String IS_FINALIZED = "is_finalized";
    public static final String CONTACT_NO = "contact_no";
    public static final String CREATED_AT = "created_at";
    public static final String UPDATED_AT_COLUMN = "updated_at";
    private static final String CREATE_TABLE_SQL =
            "CREATE TABLE " + TABLE_NAME + "(" + ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," + BASE_ENTITY_ID +
                    "  VARCHAR NOT NULL, " + TYPE + "  VARCHAR NOT NULL, " + FORM_JSON + "  VARCHAR, " + FORM_JSON_DRAFT +
                    "  VARCHAR, " + IS_FINALIZED + "  INTEGER DEFAULT 0," + CONTACT_NO + " INTEGER NOT NULL, " + CREATED_AT +
                    " INTEGER NOT NULL, " + UPDATED_AT_COLUMN + " INTEGER NOT NULL, " + "UNIQUE(" + BASE_ENTITY_ID + ", " +
                    TYPE + ", " + CONTACT_NO + ") ON CONFLICT IGNORE )";
    private static final String INDEX_ID =
            "CREATE INDEX " + TABLE_NAME + "_" + ID + "_index ON " + TABLE_NAME + "(" + ID + " COLLATE NOCASE);";
    private static final String INDEX_BASE_ENTITY_ID =
            "CREATE INDEX " + TABLE_NAME + "_" + BASE_ENTITY_ID + "_index ON " + TABLE_NAME + "(" + BASE_ENTITY_ID +
                    " COLLATE NOCASE);";
    private static final String INDEX_TYPE =
            "CREATE INDEX " + TABLE_NAME + "_" + TYPE + "_index ON " + TABLE_NAME + "(" + TYPE + " COLLATE NOCASE);";
    private Map<String, Integer> formProcessingOrderMap;
    private String[] projectionArgs =
            new String[]{ID, TYPE, FORM_JSON, FORM_JSON_DRAFT, CONTACT_NO, IS_FINALIZED, BASE_ENTITY_ID, CREATED_AT,
                    UPDATED_AT_COLUMN};

    public PartialContactRepository() {
        formProcessingOrderMap = ImmutableMap.<String, Integer>builder().put(ConstantsUtils.JsonFormUtils.ANC_QUICK_CHECK, 1)
                .put(ConstantsUtils.JsonFormUtils.ANC_PROFILE, 2).put(ConstantsUtils.JsonFormUtils.ANC_SYMPTOMS_FOLLOW_UP, 3)
                .put(ConstantsUtils.JsonFormUtils.ANC_PHYSICAL_EXAM, 4).put(ConstantsUtils.JsonFormUtils.ANC_TEST, 5)
                .put(ConstantsUtils.JsonFormUtils.ANC_COUNSELLING_TREATMENT, 6).put(ConstantsUtils.JsonFormUtils.ANC_TEST_TASKS, 7).build();
    }

    public static void createTable(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_SQL);
        database.execSQL(INDEX_ID);
        database.execSQL(INDEX_BASE_ENTITY_ID);
        database.execSQL(INDEX_TYPE);
    }

    public void savePartialContact(PartialContact partialContact) {
        if (partialContact == null) return;
        else if (partialContact.getUpdatedAt() == null) {
            partialContact.setUpdatedAt(Calendar.getInstance().getTimeInMillis());
        }
        if (partialContact.getId() == null) {
            PartialContact existingContact = getPartialContact(partialContact);
            if (existingContact != null) {
                partialContact.setId(existingContact.getId());
                if (partialContact.getFormJson() == null) {
                    partialContact.setFormJson(existingContact.getFormJson());
                }
                partialContact.setCreatedAt(existingContact.getCreatedAt());
                update(partialContact);
            } else {
                partialContact.setCreatedAt(Calendar.getInstance().getTimeInMillis());
                getWritableDatabase().insert(TABLE_NAME, null, createValuesFor(partialContact));
            }
        } else {
            update(partialContact);
        }
    }

    public PartialContact getPartialContact(PartialContact partialContact) {
        String selection = null;
        String[] selectionArgs = null;
        PartialContact dbPartialContact = null;
        Cursor mCursor = null;
        try {
            if (StringUtils.isNotBlank(partialContact.getBaseEntityId()) &&
                    StringUtils.isNotBlank(partialContact.getType()) && partialContact.getContactNo() != null) {
                selection = BASE_ENTITY_ID + " = ? " + BaseRepository.COLLATE_NOCASE + " AND " + TYPE + " = ? " + BaseRepository.COLLATE_NOCASE + " AND " +
                        CONTACT_NO + " = ? " + BaseRepository.COLLATE_NOCASE;
                selectionArgs = new String[]{partialContact.getBaseEntityId(), partialContact.getType(),
                        partialContact.getContactNo().toString()};
            }

            mCursor = getReadableDatabase()
                    .query(TABLE_NAME, projectionArgs, selection, selectionArgs, null, null, null, null);
            if (mCursor.getCount() > 0) {

                mCursor.moveToFirst();

                dbPartialContact = getContactResult(mCursor);
            }
        } catch (Exception e) {
            Timber.e(e, "PartialContactRepository --> getPartialContact");

        } finally {
            if (mCursor != null) {
                mCursor.close();
            }
        }
        return dbPartialContact;
    }

    private void update(PartialContact PartialContact) {
        ContentValues contentValues = createValuesFor(PartialContact);
        getWritableDatabase()
                .update(TABLE_NAME, contentValues, ID + " = ?", new String[]{PartialContact.getId().toString()});
    }

    private ContentValues createValuesFor(PartialContact PartialContact) {
        ContentValues values = new ContentValues();
        values.put(ID, PartialContact.getId());
        values.put(BASE_ENTITY_ID, PartialContact.getBaseEntityId());
        values.put(TYPE, PartialContact.getType());
        values.put(FORM_JSON, PartialContact.getFormJson());
        values.put(FORM_JSON_DRAFT, PartialContact.getFormJsonDraft());
        values.put(CONTACT_NO, PartialContact.getContactNo());
        values.put(IS_FINALIZED, PartialContact.getFinalized());
        values.put(CREATED_AT, PartialContact.getCreatedAt());
        values.put(UPDATED_AT_COLUMN, PartialContact.getUpdatedAt());
        return values;
    }

    private PartialContact getContactResult(Cursor cursor) {
        PartialContact partialContact = new PartialContact();
        partialContact.setId(cursor.getLong(cursor.getColumnIndex(ID)));
        partialContact.setType(cursor.getString(cursor.getColumnIndex(TYPE)));
        partialContact.setFormJson(cursor.getString(cursor.getColumnIndex(FORM_JSON)));
        partialContact.setContactNo(cursor.getInt(cursor.getColumnIndex(CONTACT_NO)));
        partialContact.setFormJsonDraft(cursor.getString(cursor.getColumnIndex(FORM_JSON_DRAFT)));
        partialContact.setFinalized(cursor.getInt(cursor.getColumnIndex(IS_FINALIZED)) != 0);
        partialContact.setBaseEntityId(cursor.getString(cursor.getColumnIndex(BASE_ENTITY_ID)));
        partialContact.setCreatedAt(cursor.getLong(cursor.getColumnIndex(CREATED_AT)));
        partialContact.setUpdatedAt(cursor.getLong(cursor.getColumnIndex(UPDATED_AT_COLUMN)));
        partialContact.setSortOrder(formProcessingOrderMap.get(partialContact.getType()));


        return partialContact;
    }

    public List<PartialContact> getPartialContacts(String baseEntityId, Integer contactNumber) {
        Cursor mCursor = null;
        String selection = "";
        String[] selectionArgs = null;
        List<PartialContact> partialContactList = null;
        try {
            SQLiteDatabase db = getWritableDatabase();

            if (StringUtils.isNotBlank(baseEntityId) && contactNumber != null) {
                selection = BASE_ENTITY_ID + " = ? " + BaseRepository.COLLATE_NOCASE + " AND " + CONTACT_NO + " = ? " + BaseRepository.COLLATE_NOCASE;
                selectionArgs = new String[]{baseEntityId, contactNumber.toString()};
            }

            mCursor = db.query(TABLE_NAME, projectionArgs, selection, selectionArgs, null, null, null, null);

            if (mCursor != null) {

                PartialContact partialContact;
                partialContactList = new ArrayList<>();

                while (mCursor.moveToNext()) {

                    partialContact = getContactResult(mCursor);
                    partialContactList.add(partialContact);

                }
                return partialContactList;
            }
        } catch (Exception e) {
            Timber.e(e, "PartialContactRepository --> getPartialContacts");
        } finally {
            if (mCursor != null) {
                mCursor.close();
            }
        }
        return partialContactList;
    }

    public void deleteDraftJson(String baseEntityId) {

        getWritableDatabase()
                .execSQL("UPDATE " + TABLE_NAME + " SET " + FORM_JSON_DRAFT + "= NULL WHERE " + BASE_ENTITY_ID + " = ?",
                        new String[]{baseEntityId});
    }

    public void saveFinalJson(String baseEntityId) {
        getWritableDatabase().execSQL(
                "UPDATE " + TABLE_NAME + " SET " + FORM_JSON + "=" + FORM_JSON_DRAFT + ", " + FORM_JSON_DRAFT +
                        "= NULL WHERE " + BASE_ENTITY_ID + " = ? AND " + FORM_JSON_DRAFT + " IS NOT NULL",
                new String[]{baseEntityId});

        PatientRepository.updateWomanAlertStatus(baseEntityId, ConstantsUtils.AlertStatusUtils.ACTIVE);
    }

    public void deletePartialContact(Long id) {
        getWritableDatabase().delete(TABLE_NAME, "_id=?", new String[]{id.toString()});
    }

    public void clearPartialRepository() {
        getWritableDatabase().delete(TABLE_NAME, "_id IS NOT NULL", null);
    }
}
