package org.smartregister.anc.repository;

import android.content.ContentValues;
import android.util.Log;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.anc.model.PreviousContact;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.Repository;

import java.util.Calendar;

public class PreviousContactRepository extends BaseRepository {
    private static final String TAG = PreviousContactRepository.class.getCanonicalName();

    public static final String TABLE_NAME = "previous_contact";
    public static final String ID = "_id";
    public static final String BASE_ENTITY_ID = "base_entity_id";
    public static final String TYPE = "type";
    public static final String FORM_JSON = "form_json";
    public static final String CONTACT_NO = "contact_no";
    public static final String CREATED_AT = "created_at";
    public static final String UPDATED_AT_COLUMN = "updated_at";

    private static final String CREATE_TABLE_SQL = "CREATE TABLE " + TABLE_NAME + "(" +
            ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
            BASE_ENTITY_ID + "  VARCHAR NOT NULL, " +
            TYPE + "  VARCHAR NOT NULL, " +
            FORM_JSON + "  VARCHAR, " +
            CONTACT_NO + " INTEGER NOT NULL, " +
            CREATED_AT + " INTEGER NOT NULL, " +
            UPDATED_AT_COLUMN + " INTEGER NOT NULL, " +
            "UNIQUE(" + BASE_ENTITY_ID + ", " + TYPE + ") ON CONFLICT REPLACE )";

    private static final String INDEX_ID = "CREATE INDEX " + TABLE_NAME + "_" + ID +
            "_index ON " + TABLE_NAME + "(" + ID + " COLLATE NOCASE);";

    private static final String INDEX_BASE_ENTITY_ID = "CREATE INDEX " + TABLE_NAME + "_" + BASE_ENTITY_ID +
            "_index ON " + TABLE_NAME + "(" + BASE_ENTITY_ID + " COLLATE NOCASE);";

    private static final String INDEX_TYPE = "CREATE INDEX " + TABLE_NAME + "_" + TYPE +
            "_index ON " + TABLE_NAME + "(" + TYPE + " COLLATE NOCASE);";

    private String[] projectionArgs = new String[]{ID, TYPE, FORM_JSON, CONTACT_NO, BASE_ENTITY_ID, CREATED_AT, UPDATED_AT_COLUMN};

    public PreviousContactRepository(Repository repository) {
        super(repository);
    }

    protected static void createTable(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_SQL);
        database.execSQL(INDEX_ID);
        database.execSQL(INDEX_BASE_ENTITY_ID);
        database.execSQL(INDEX_TYPE);
    }

    public void savePreviousContact(PreviousContact previousContact) {
        if (previousContact == null)
            return;
        else if (previousContact.getUpdatedAt() == null) {
            previousContact.setUpdatedAt(Calendar.getInstance().getTimeInMillis());
        }
        if (previousContact.getId() == null) {
            PreviousContact existingContact = getPreviousContact(previousContact);
            if (existingContact != null) {
                previousContact.setId(existingContact.getId());
                if (previousContact.getFormJson() == null) {
                    previousContact.setFormJson(existingContact.getFormJson());
                }
                previousContact.setCreatedAt(existingContact.getCreatedAt());
                update(previousContact);
            } else {
                previousContact.setCreatedAt(Calendar.getInstance().getTimeInMillis());
                getWritableDatabase().insert(TABLE_NAME, null, createValuesFor(previousContact));
            }
        } else {
            update(previousContact);
        }
    }

    private void update(PreviousContact PreviousContact) {
        ContentValues contentValues = createValuesFor(PreviousContact);
        getWritableDatabase().update(TABLE_NAME, contentValues, ID + " = ?", new String[]{PreviousContact.getId().toString()});
    }

    private ContentValues createValuesFor(PreviousContact PreviousContact) {
        ContentValues values = new ContentValues();
        values.put(ID, PreviousContact.getId());
        values.put(BASE_ENTITY_ID, PreviousContact.getBaseEntityId());
        values.put(TYPE, PreviousContact.getType());
        values.put(FORM_JSON, PreviousContact.getFormJson());
        values.put(CONTACT_NO, PreviousContact.getContactNo());
        values.put(CREATED_AT, PreviousContact.getCreatedAt());
        values.put(UPDATED_AT_COLUMN, PreviousContact.getUpdatedAt());
        return values;
    }

    public PreviousContact getPreviousContact(PreviousContact previousContact) {
        String selection = null;
        String[] selectionArgs = null;
        PreviousContact dbPreviousContact = null;
        Cursor mCursor = null;
        try {
            if (StringUtils.isNotBlank(previousContact.getBaseEntityId()) && StringUtils.isNotBlank(previousContact.getType()) && previousContact.getContactNo() != null) {
                selection = BASE_ENTITY_ID + " = ? " + COLLATE_NOCASE + " AND " + TYPE + " = ? " + COLLATE_NOCASE + " AND " + CONTACT_NO + " = ? " + COLLATE_NOCASE;
                selectionArgs = new String[]{previousContact.getBaseEntityId(), previousContact.getType(), previousContact.getContactNo().toString()};
            }

            mCursor = getReadableDatabase().query(TABLE_NAME, projectionArgs, selection, selectionArgs, null, null, null, null);
            if (mCursor.getCount() > 0) {

                mCursor.moveToFirst();

                dbPreviousContact = getContactResult(mCursor);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());

        } finally {
            if (mCursor != null) {
                mCursor.close();
            }
        }
        return dbPreviousContact;
    }

    /**
     * @param type is the file name
     */
    public PreviousContact getPreviousContacts(String baseEntityId, Integer contactNumber, String type) {
        Cursor mCursor = null;
        String selection = "";
        String[] selectionArgs = null;
        PreviousContact previousContact = null;
        try {
            SQLiteDatabase db = getWritableDatabase();

            if (StringUtils.isNotBlank(baseEntityId) && contactNumber != null) {
                selection = BASE_ENTITY_ID + " = ? " + COLLATE_NOCASE + " AND " + CONTACT_NO + " = ? " + COLLATE_NOCASE + " AND " + TYPE + " = ? " + COLLATE_NOCASE;
                selectionArgs = new String[]{baseEntityId, contactNumber.toString(), type};
            }

            mCursor = db.query(TABLE_NAME, projectionArgs, selection, selectionArgs, null, null, null, null);

            if (mCursor != null) {

                while (mCursor.moveToNext()) {

                    previousContact = getContactResult(mCursor);

                }
                return previousContact;
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
        } finally {
            if (mCursor != null) {
                mCursor.close();
            }
        }
        return previousContact;
    }

    private PreviousContact getContactResult(Cursor cursor) {

        PreviousContact previousContact = new PreviousContact();
        previousContact.setId(cursor.getLong(cursor.getColumnIndex(ID)));
        previousContact.setType(cursor.getString(cursor.getColumnIndex(TYPE)));
        previousContact.setFormJson(cursor.getString(cursor.getColumnIndex(FORM_JSON)));
        previousContact.setContactNo(cursor.getInt(cursor.getColumnIndex(CONTACT_NO)));
        previousContact.setBaseEntityId(cursor.getString(cursor.getColumnIndex(BASE_ENTITY_ID)));
        previousContact.setCreatedAt(cursor.getLong(cursor.getColumnIndex(CREATED_AT)));
        previousContact.setUpdatedAt(cursor.getLong(cursor.getColumnIndex(UPDATED_AT_COLUMN)));

        return previousContact;
    }
}
