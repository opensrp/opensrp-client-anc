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

/**
 * This class providers helper functions to perform any CRUD functions on the ANC Tasks. This tasks are mainly created from the ANC Tests.
 * They include test that weren't done & all ordered tests.
 *
 * @author dubdabasoduba
 */
public class ContactTasksRepositoryHelper extends BaseRepository {
    public static final String TABLE_NAME = "contact_tasks";
    public static final String ID = "_id";
    public static final String BASE_ENTITY_ID = "base_entity_id";
    public static final String CONTACT_NO = "contact_no";
    public static final String KEY = "key";
    public static final String VALUE = "value";
    public static final String IS_UPDATED = "is_updated";
    public static final String CREATED_AT = "created_at";
    private static final String CREATE_TABLE_SQL = "CREATE TABLE " + TABLE_NAME + "("
            + ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"
            + CONTACT_NO + "  VARCHAR NOT NULL, "
            + BASE_ENTITY_ID + "  VARCHAR NOT NULL, "
            + KEY + "  VARCHAR, "
            + VALUE + "  VARCHAR NOT NULL, "
            + IS_UPDATED + "  INTEGER NOT NULL, "
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

    private String[] projectionArgs = new String[]{ID, CONTACT_NO, KEY, VALUE, IS_UPDATED, BASE_ENTITY_ID, CREATED_AT};

    /**
     * Creates the contact_tasks table and adds the indexes on the table.
     *
     * @param database {@link SQLiteDatabase}
     * @author dubdabasoduba
     */
    public static void createTable(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_SQL);
        database.execSQL(INDEX_ID);
        database.execSQL(INDEX_BASE_ENTITY_ID);
        database.execSQL(INDEX_KEY);
        database.execSQL(INDEX_CONTACT_NO);
    }

    /**
     * Inserts or updates the tasks into the contact_tasks table. Returns a boolean which is used to refresh the tasks view on the profile pages
     *
     * @param task {@link Task}
     * @return true/false {@link Boolean}
     * @author dubdabasoduba
     */
    public boolean saveOrUpdateTasks(Task task) {
        if (task == null) return false;
        if (task.getId() != null) {
            String sqlQuery = ID + " = ? " + BaseRepository.COLLATE_NOCASE;
            getWritableDatabase().update(TABLE_NAME, createValuesFor(task), sqlQuery, new String[]{String.valueOf(task.getId())});
            return true;
        } else {
            getWritableDatabase().insert(TABLE_NAME, null, createValuesFor(task));
            return false;
        }
    }

    private ContentValues createValuesFor(Task task) {
        ContentValues values = new ContentValues();
        values.put(ID, task.getId());
        values.put(CONTACT_NO, task.getContactNo());
        values.put(BASE_ENTITY_ID, task.getBaseEntityId());
        values.put(VALUE, task.getValue());
        values.put(KEY, task.getKey());
        values.put(IS_UPDATED, task.isUpdated());
        values.put(CREATED_AT, task.getCreatedAt());
        return values;
    }

    /**
     * Gets the count of the tasks for a specific content
     *
     * @param baseEntityId {@link String} - The patient's base entity id
     * @param contactNo    {@link String} - The current contact number
     * @return taskCount {@link String} - The number of tasks for the patient on the specific contact.
     * @author dubdabasoduba
     */
    public String getTasksCount(String baseEntityId, String contactNo) {
        int tasksCount = 0;
        String sqlQuery = "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE " + BASE_ENTITY_ID + " = ? " + BaseRepository.COLLATE_NOCASE + " AND " + CONTACT_NO + " = ? " + BaseRepository.COLLATE_NOCASE + " AND " + IS_UPDATED + " = ? " + BaseRepository.COLLATE_NOCASE;
        String[] selectionArgs = new String[]{};
        Cursor mCursor = null;
        try {
            if (StringUtils.isNotBlank(baseEntityId) && StringUtils.isNotBlank(contactNo)) {
                selectionArgs = new String[]{baseEntityId, contactNo, "0"};
            }

            mCursor = getReadableDatabase().rawQuery(sqlQuery, selectionArgs);
            if (mCursor != null && mCursor.getCount() > 0) {
                mCursor.moveToFirst();
                tasksCount = mCursor.getInt(0);
            }
        } catch (Exception e) {
            Timber.e(e, " --> getTasksCount");
        } finally {
            if (mCursor != null) {
                mCursor.close();
            }
        }

        return String.valueOf(tasksCount);
    }

    /**
     * Gets all the tasks for a specific patient using their patient base entity id and the update status. We should only display the not done/ordered tasks
     * We also provide an optional list of keys to query null otherwise to get all keys for that base entity id
     *
     * @param baseEntityId {@link String}
     * @param keysList     {@link List}
     * @author dubdabasoduba
     */
    public List<Task> getTasks(String baseEntityId, List<String> keysList) {
        String orderBy = ID + " DESC ";
        Cursor mCursor = null;
        String selection = "";
        String[] selectionArgs = new String[]{};
        List<Task> taskList = new ArrayList<>();
        try {
            SQLiteDatabase sqLiteDatabase = getReadableDatabase();

            if (StringUtils.isNotBlank(baseEntityId)) {
                if (keysList != null) {
                    selection = BASE_ENTITY_ID + " = ? " + BaseRepository.COLLATE_NOCASE + " AND " + KEY + " IN (?) " + BaseRepository.COLLATE_NOCASE;
                    selectionArgs = new String[]{baseEntityId, ContactJsonFormUtils.getListValuesAsString(keysList)};
                } else {
                    selection = BASE_ENTITY_ID + " = ? " + BaseRepository.COLLATE_NOCASE;
                    selectionArgs = new String[]{baseEntityId};
                }
            }

            mCursor = sqLiteDatabase.query(TABLE_NAME, projectionArgs, selection, selectionArgs, null, null, orderBy, null);
            if (mCursor != null) {
                while (mCursor.moveToNext()) {
                    taskList.add(getTaskResult(mCursor));
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

    private Task getTaskResult(Cursor cursor) {
        Task task = new Task();
        task.setId(cursor.getLong(cursor.getColumnIndex(ID)));
        task.setKey(cursor.getString(cursor.getColumnIndex(KEY)));
        task.setValue(cursor.getString(cursor.getColumnIndex(VALUE)));
        task.setBaseEntityId(cursor.getString(cursor.getColumnIndex(BASE_ENTITY_ID)));
        task.setContactNo(cursor.getString(cursor.getColumnIndex(CONTACT_NO)));
        task.setCreatedAt(cursor.getLong(cursor.getColumnIndex(CREATED_AT)));
        task.setUpdated(updateBooleanValue(cursor.getString(cursor.getColumnIndex(IS_UPDATED))));
        return task;
    }

    /**
     * Since the {@link Boolean#parseBoolean(String)} only expects "true" or "false" string we have to convert "1" & "0"
     * to a boolean using this function
     *
     * @param isUpdated {@link String}
     * @return isUpdate {@link Boolean}
     */
    private boolean updateBooleanValue(String isUpdated) {
        boolean isUpdate = false;
        if (isUpdated.equals("1")) {
            isUpdate = true;
        }
        return isUpdate;
    }

    /**
     * Gets all the closed or updated tasks. We fetch this using the baseEntityId,contact number & the is updated flag
     *
     * @param baseEntityId {@link String}
     * @param contactNo    {@link String}
     * @return tasksList {@link List}
     * @author dubdabasoduba
     */
    public List<Task> getClosedTasks(String baseEntityId, String contactNo) {
        String orderBy = ID + " DESC ";
        Cursor mCursor = null;
        String selection = "";
        String[] selectionArgs = new String[]{};
        List<Task> taskList = new ArrayList<>();
        try {
            SQLiteDatabase sqLiteDatabase = getReadableDatabase();
            if (StringUtils.isNotBlank(baseEntityId)) {
                selection = BASE_ENTITY_ID + " = ? " + BaseRepository.COLLATE_NOCASE + " AND " + IS_UPDATED + " = ? " + BaseRepository.COLLATE_NOCASE + " AND " + CONTACT_NO + " = ? " + BaseRepository.COLLATE_NOCASE;
                selectionArgs = new String[]{baseEntityId, "1", contactNo};
            }
            mCursor = sqLiteDatabase.query(TABLE_NAME, projectionArgs, selection, selectionArgs, null, null, orderBy, null);
            if (mCursor != null) {
                while (mCursor.moveToNext()) {
                    taskList.add(getTaskResult(mCursor));
                }
                return taskList;
            }
        } catch (Exception e) {
            Timber.e(e, " --> getClosedTasks");
        } finally {
            if (mCursor != null) {
                mCursor.close();
            }
        }

        return taskList;
    }

    /**
     * Gets all the closed or updated tasks. We fetch this using the baseEntityId & the is updated flag
     *
     * @param baseEntityId {@link String}
     * @return tasksList {@link List}
     * @author dubdabasoduba
     */
    public List<Task> getOpenTasks(String baseEntityId) {
        String orderBy = ID + " DESC ";
        Cursor mCursor = null;
        String selection = "";
        String[] selectionArgs = new String[]{};
        List<Task> taskList = new ArrayList<>();
        try {
            SQLiteDatabase sqLiteDatabase = getReadableDatabase();
            if (StringUtils.isNotBlank(baseEntityId)) {
                selection = BASE_ENTITY_ID + " = ? " + BaseRepository.COLLATE_NOCASE + " AND " + IS_UPDATED + " = ? " + BaseRepository.COLLATE_NOCASE;
                selectionArgs = new String[]{baseEntityId, "0"};
            }
            mCursor = sqLiteDatabase.query(TABLE_NAME, projectionArgs, selection, selectionArgs, null, null, orderBy, null);
            if (mCursor != null) {
                while (mCursor.moveToNext()) {
                    taskList.add(getTaskResult(mCursor));
                }
                return taskList;
            }
        } catch (Exception e) {
            Timber.e(e, " --> getOpenTasks");
        } finally {
            if (mCursor != null) {
                mCursor.close();
            }
        }

        return taskList;
    }

    /**
     * This deletes a given task after the finalize button on the contacts pressed
     *
     * @param id {@link Long} - The task id
     * @author dubdabasoduba
     */
    public void deleteContactTask(Long id) {
        getWritableDatabase().delete(TABLE_NAME, "_id=?", new String[]{id.toString()});
    }
}
