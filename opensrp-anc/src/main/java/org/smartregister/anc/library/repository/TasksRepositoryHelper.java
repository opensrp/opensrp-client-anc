package org.smartregister.anc.library.repository;

import android.content.ContentValues;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.anc.library.model.Task;
import org.smartregister.anc.library.util.ContactJsonFormUtils;
import org.smartregister.repository.BaseRepository;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class TasksRepositoryHelper extends BaseRepository {
    public static final String TABLE_NAME = "contact_tasks";
    public static final String ID = "_id";
    public static final String BASE_ENTITY_ID = "base_entity_id";
    public static final String CONTACT_NO = "contact_no";
    public static final String KEY = "key";
    public static final String VALUE = "value";
    public static final String CREATED_AT = "created_at";
    private static final String CREATE_TABLE_SQL = "CREATE TABLE " + TABLE_NAME + "("
            + ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
            + CONTACT_NO + "  VARCHAR NOT NULL, "
            + BASE_ENTITY_ID + "  VARCHAR NOT NULL, "
            + KEY + "  VARCHAR, "
            + VALUE + "  VARCHAR NOT NULL, "
            + CREATED_AT + " INTEGER NOT NULL, " +
            "UNIQUE(" + BASE_ENTITY_ID + ", " + CONTACT_NO + ", " + KEY + ", " + VALUE + ") ON CONFLICT REPLACE)";

    private static final String INDEX_ID =
            "CREATE INDEX " + TABLE_NAME + "_" + ID + "_index ON " + TABLE_NAME + "(" + ID + " COLLATE NOCASE);";

    private static final String INDEX_BASE_ENTITY_ID = "CREATE INDEX " + TABLE_NAME + "_" + BASE_ENTITY_ID +
            "_index ON " + TABLE_NAME + "(" + BASE_ENTITY_ID + " COLLATE NOCASE);";

    private static final String INDEX_KEY = "CREATE INDEX " + TABLE_NAME + "_" + KEY +
            "_index ON " + TABLE_NAME + "(" + KEY + " COLLATE NOCASE);";

    private static final String INDEX_CONTACT_NO = "CREATE INDEX " + TABLE_NAME + "_" + CONTACT_NO +
            "_index ON " + TABLE_NAME + "(" + CONTACT_NO + " COLLATE NOCASE);";

    private String[] projectionArgs = new String[]{ID, CONTACT_NO, KEY, VALUE, BASE_ENTITY_ID, CREATED_AT};

    public static void createTable(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_SQL);
        database.execSQL(INDEX_ID);
        database.execSQL(INDEX_BASE_ENTITY_ID);
        database.execSQL(INDEX_KEY);
        database.execSQL(INDEX_CONTACT_NO);
    }

    public void saveTasks(Task task) {
        if (task == null) return;
        getWritableDatabase().insert(TABLE_NAME, null, createValuesFor(task));
    }

    private ContentValues createValuesFor(Task task) {
        ContentValues values = new ContentValues();
        values.put(ID, task.getId());
        values.put(CONTACT_NO, task.getContactNo());
        values.put(BASE_ENTITY_ID, task.getBaseEntityId());
        values.put(VALUE, task.getValue());
        values.put(KEY, task.getKey());
        return values;
    }

    /**
     * @param tasksRequest object holding contact request params it MUST contain NON NULL values for key
     *                     baseEntityId contactNo
     */
    public Task getPreviousContact(Task tasksRequest) {
        String selection = null;
        String orderBy = ID + " DESC";
        String[] selectionArgs = null;
        Task dbPreviousContact = null;
        Cursor mCursor = null;
        try {
            if (StringUtils.isNotBlank(tasksRequest.getBaseEntityId()) &&
                    StringUtils.isNotBlank(tasksRequest.getKey())) {
                selection = BASE_ENTITY_ID + " = ? " + BaseRepository.COLLATE_NOCASE + " AND " + KEY + " = ? " + BaseRepository.COLLATE_NOCASE;
                selectionArgs = new String[]{tasksRequest.getBaseEntityId(), tasksRequest.getKey()};
            }

            mCursor = getReadableDatabase()
                    .query(TABLE_NAME, projectionArgs, selection, selectionArgs, null, null, orderBy, null);
            if (mCursor.getCount() > 0) {

                mCursor.moveToFirst();

                dbPreviousContact = getContactResult(mCursor);
            }
        } catch (Exception e) {
            Timber.e(e, " --> getPreviousContact");

        } finally {
            if (mCursor != null) {
                mCursor.close();
            }
        }
        return dbPreviousContact;
    }

    private Task getContactResult(Cursor cursor) {
        Task task = new Task();
        task.setId(cursor.getLong(cursor.getColumnIndex(ID)));
        task.setKey(cursor.getString(cursor.getColumnIndex(KEY)));
        task.setValue(cursor.getString(cursor.getColumnIndex(VALUE)));
        task.setBaseEntityId(cursor.getString(cursor.getColumnIndex(BASE_ENTITY_ID)));
        task.setContactNo(cursor.getString(cursor.getColumnIndex(CONTACT_NO)));

        return task;
    }

    /**
     * @param baseEntityId is the Base entity Id No to filter by
     * @param keysList     an optional list of keys to query null otherwise to get all keys for that base entity id
     */
    public List<Task> getTasks(String baseEntityId, List<String> keysList) {
        String orderBy = ID + " DESC ";
        Cursor mCursor = null;
        String selection = "";
        String[] selectionArgs = null;
        List<Task> taskList = new ArrayList<>();
        try {
            SQLiteDatabase db = getWritableDatabase();

            if (StringUtils.isNotBlank(baseEntityId)) {
                if (keysList != null) {

                    selection = BASE_ENTITY_ID + " = ? " + BaseRepository.COLLATE_NOCASE + " AND " + KEY + " IN (?) " + BaseRepository.COLLATE_NOCASE;
                    selectionArgs = new String[]{baseEntityId, ContactJsonFormUtils.getListValuesAsString(keysList)};

                } else {
                    selection = BASE_ENTITY_ID + " = ? " + BaseRepository.COLLATE_NOCASE;
                    selectionArgs = new String[]{baseEntityId};
                }
            }

            mCursor = db.query(TABLE_NAME, projectionArgs, selection, selectionArgs, null, null, orderBy, null);

            if (mCursor != null) {

                while (mCursor.moveToNext()) {

                    taskList.add(getContactResult(mCursor));

                }
                return taskList;
            }
        } catch (Exception e) {
            Timber.e(e, " --> getTasks");
        } finally {
            if (mCursor != null) {
                mCursor.close();
            }
        }

        return taskList;
    }
}
