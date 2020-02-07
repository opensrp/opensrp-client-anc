package org.smartregister.anc.library.repository;

import android.content.ContentValues;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.Context;
import org.smartregister.anc.library.AncLibrary;
import org.smartregister.anc.library.activity.BaseUnitTest;
import org.smartregister.anc.library.model.Task;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.Repository;
import org.smartregister.view.activity.DrishtiApplication;

import timber.log.Timber;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ContactTasksRepositoryHelper.class, AncLibrary.class, SQLiteDatabase.class})
public class ContactTasksRepositoryHelperTest extends BaseUnitTest {
    protected static final String DUMMY_BASE_ENTITY_ID = "00ts-ime-hcla-0tib-0eht-ma0i";
    private static final String TABLE_NAME = "contact_tasks";
    private static final String ID = "_id";
    private static final String BASE_ENTITY_ID = "base_entity_id";
    private static final String CONTACT_NO = "contact_no";
    private static final String KEY = "key";
    private static final String VALUE = "value";
    private static final String IS_UPDATED = "is_updated";
    private static final String CREATED_AT = "created_at";

    @Mock
    private Context context;

    @Mock
    private Repository repository;

    @Mock
    private SQLiteDatabase sqLiteDatabase;

    @Mock
    private Cursor mCursor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        AncLibrary.init(context, 1);
    }

    @PrepareForTest({ContentValues.class})
    @Test
    public void testUpdateTasks() {
        try {
            ContactTasksRepositoryHelper contactTasksRepositoryHelper = PowerMockito.spy(new ContactTasksRepositoryHelper());
            PowerMockito.mockStatic(ContentValues.class);

            DrishtiApplication drishtiApplication = Mockito.mock(DrishtiApplication.class);
            ReflectionHelpers.setStaticField(DrishtiApplication.class, "mInstance", drishtiApplication);

            Mockito.doReturn(repository).when(drishtiApplication).getRepository();
            PowerMockito.when(contactTasksRepositoryHelper.getWritableDatabase()).thenReturn(sqLiteDatabase);
            ContentValues contentValues = Whitebox.invokeMethod(contactTasksRepositoryHelper, "createValuesFor", getTask());
            String sqlQuery = ID + " = ? " + BaseRepository.COLLATE_NOCASE;

            PowerMockito.when(contactTasksRepositoryHelper.getWritableDatabase().update(TABLE_NAME, contentValues, sqlQuery, new String[]{String.valueOf(getTask().getId())})).thenReturn(1);
            Assert.assertTrue(contactTasksRepositoryHelper.saveOrUpdateTasks(getTask()));
        } catch (Exception e) {
            Timber.e(e, " --> testUpdateTasks");
        }
    }

    private Task getTask() {
        Task task = new Task(DUMMY_BASE_ENTITY_ID, "myTask", String.valueOf(new JSONObject()), "2", true);
        task.setId(Long.valueOf(1));
        return task;
    }

    @PrepareForTest({ContentValues.class})
    @Test
    public void testSaveTasks() {
        try {
            ContactTasksRepositoryHelper contactTasksRepositoryHelper = PowerMockito.spy(new ContactTasksRepositoryHelper());
            PowerMockito.mockStatic(ContentValues.class);

            DrishtiApplication drishtiApplication = Mockito.mock(DrishtiApplication.class);
            ReflectionHelpers.setStaticField(DrishtiApplication.class, "mInstance", drishtiApplication);

            Mockito.doReturn(repository).when(drishtiApplication).getRepository();
            PowerMockito.when(contactTasksRepositoryHelper.getWritableDatabase()).thenReturn(sqLiteDatabase);
            Task task = getTask();
            task.setId(null);
            ContentValues contentValues = Whitebox.invokeMethod(contactTasksRepositoryHelper, "createValuesFor", task);

            PowerMockito.when(contactTasksRepositoryHelper.getWritableDatabase().insert(TABLE_NAME, null, contentValues)).thenReturn((long) 0);
            Assert.assertFalse(contactTasksRepositoryHelper.saveOrUpdateTasks(task));
        } catch (Exception e) {
            Timber.e(e, " --> testSaveTasks");
        }
    }

    @Test
    public void testSaveOrUpdateWithNullTasks() {
        try {
            ContactTasksRepositoryHelper contactTasksRepositoryHelper = PowerMockito.spy(new ContactTasksRepositoryHelper());
            DrishtiApplication drishtiApplication = Mockito.mock(DrishtiApplication.class);
            ReflectionHelpers.setStaticField(DrishtiApplication.class, "mInstance", drishtiApplication);
            Assert.assertFalse(contactTasksRepositoryHelper.saveOrUpdateTasks(null));
        } catch (Exception e) {
            Timber.e(e, " --> testSaveOrUpdateWithNullTasks");
        }
    }

    @Test
    public void testTasksCount() {
        try {
            ContactTasksRepositoryHelper contactTasksRepositoryHelper = PowerMockito.spy(new ContactTasksRepositoryHelper());
            DrishtiApplication drishtiApplication = Mockito.mock(DrishtiApplication.class);
            ReflectionHelpers.setStaticField(DrishtiApplication.class, "mInstance", drishtiApplication);

            Mockito.doReturn(repository).when(drishtiApplication).getRepository();
            PowerMockito.when(contactTasksRepositoryHelper.getReadableDatabase()).thenReturn(sqLiteDatabase);
            String sqlQuery = "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE " + BASE_ENTITY_ID + " = ? " + BaseRepository.COLLATE_NOCASE + " AND " + CONTACT_NO + " = ? " + BaseRepository.COLLATE_NOCASE+ " AND " + IS_UPDATED + " = ? " + BaseRepository.COLLATE_NOCASE;
            String[] selectionArgs = new String[]{DUMMY_BASE_ENTITY_ID, "2","0"};

            PowerMockito.when(contactTasksRepositoryHelper.getReadableDatabase().rawQuery(sqlQuery, selectionArgs)).thenReturn(mCursor);
            PowerMockito.when(mCursor.getCount()).thenReturn(1);
            PowerMockito.when(mCursor.getInt(0)).thenReturn(1);
            Assert.assertEquals("1", contactTasksRepositoryHelper.getTasksCount(DUMMY_BASE_ENTITY_ID));
        } catch (Exception e) {
            Timber.e(e, " --> testTasksCount");
        }
    }

    @Test
    public void testTasksCountWithNullVariables() {
        try {
            ContactTasksRepositoryHelper contactTasksRepositoryHelper = PowerMockito.spy(new ContactTasksRepositoryHelper());
            DrishtiApplication drishtiApplication = Mockito.mock(DrishtiApplication.class);
            ReflectionHelpers.setStaticField(DrishtiApplication.class, "mInstance", drishtiApplication);

            Mockito.doReturn(repository).when(drishtiApplication).getRepository();
            PowerMockito.when(contactTasksRepositoryHelper.getReadableDatabase()).thenReturn(sqLiteDatabase);
            String sqlQuery = "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE " + BASE_ENTITY_ID + " = ? " + BaseRepository.COLLATE_NOCASE + " AND " + CONTACT_NO + " = ? " + BaseRepository.COLLATE_NOCASE+ " AND " + IS_UPDATED + " = ? " + BaseRepository.COLLATE_NOCASE;
            String[] selectionArgs = new String[]{null, null};

            PowerMockito.when(contactTasksRepositoryHelper.getReadableDatabase().rawQuery(sqlQuery, selectionArgs)).thenReturn(mCursor);
            Assert.assertEquals("0", contactTasksRepositoryHelper.getTasksCount(null));
        } catch (Exception e) {
            Timber.e(e, " --> testTasksCountWithNullVariables");
        }
    }

    @Test
    public void testGetTasks() {
        try {
            String orderBy = ID + " DESC ";
            String[] projectionArgs = new String[]{ID, CONTACT_NO, KEY, VALUE, IS_UPDATED, BASE_ENTITY_ID, CREATED_AT};
            String selection = BASE_ENTITY_ID + " = ? " + BaseRepository.COLLATE_NOCASE;
            String[] selectionArgs = new String[]{DUMMY_BASE_ENTITY_ID};

            ContactTasksRepositoryHelper contactTasksRepositoryHelper = PowerMockito.spy(new ContactTasksRepositoryHelper());
            DrishtiApplication drishtiApplication = Mockito.mock(DrishtiApplication.class);
            ReflectionHelpers.setStaticField(DrishtiApplication.class, "mInstance", drishtiApplication);

            Mockito.doReturn(repository).when(drishtiApplication).getRepository();
            PowerMockito.when(contactTasksRepositoryHelper.getReadableDatabase()).thenReturn(sqLiteDatabase);

            PowerMockito.when(contactTasksRepositoryHelper.getReadableDatabase().query(TABLE_NAME, projectionArgs, selection, selectionArgs, null, null, orderBy, null)).thenReturn(mCursor);
            PowerMockito.when(mCursor.moveToNext()).then(new Answer<Object>() {
                int count = 0;

                @Override
                public Object answer(InvocationOnMock invocation) {
                    count++;
                    return count <= 2;
                }
            });

            Assert.assertEquals(2, contactTasksRepositoryHelper.getTasks(DUMMY_BASE_ENTITY_ID, null).size());
        } catch (Exception e) {
            Timber.e(e, " --> testTasksCountWithNullVariables");
        }
    }

    @Test
    public void testGetOpenTasks() {
        try {
            String orderBy = ID + " DESC ";
            String[] projectionArgs = new String[]{ID, CONTACT_NO, KEY, VALUE, IS_UPDATED, BASE_ENTITY_ID, CREATED_AT};
            String selection = BASE_ENTITY_ID + " = ? " + BaseRepository.COLLATE_NOCASE + " AND " + IS_UPDATED + " = ? " + BaseRepository.COLLATE_NOCASE;
            String[] selectionArgs = new String[]{DUMMY_BASE_ENTITY_ID, "0"};

            ContactTasksRepositoryHelper contactTasksRepositoryHelper = PowerMockito.spy(new ContactTasksRepositoryHelper());
            DrishtiApplication drishtiApplication = Mockito.mock(DrishtiApplication.class);
            ReflectionHelpers.setStaticField(DrishtiApplication.class, "mInstance", drishtiApplication);

            Mockito.doReturn(repository).when(drishtiApplication).getRepository();
            PowerMockito.when(contactTasksRepositoryHelper.getReadableDatabase()).thenReturn(sqLiteDatabase);

            PowerMockito.when(contactTasksRepositoryHelper.getReadableDatabase().query(TABLE_NAME, projectionArgs, selection, selectionArgs, null, null, orderBy, null)).thenReturn(mCursor);
            PowerMockito.when(mCursor.moveToNext()).then(new Answer<Object>() {
                int count = 0;

                @Override
                public Object answer(InvocationOnMock invocation) {
                    count++;
                    return count <= 3;
                }
            });

            Assert.assertEquals(3, contactTasksRepositoryHelper.getOpenTasks(DUMMY_BASE_ENTITY_ID).size());
        } catch (Exception e) {
            Timber.e(e, " --> testTasksCountWithNullVariables");
        }
    }

    @Test
    public void testGetClosedTasks() {
        try {
            String orderBy = ID + " DESC ";
            String[] projectionArgs = new String[]{ID, CONTACT_NO, KEY, VALUE, IS_UPDATED, BASE_ENTITY_ID, CREATED_AT};
            String selection = BASE_ENTITY_ID + " = ? " + BaseRepository.COLLATE_NOCASE + " AND " + IS_UPDATED + " = ? " + BaseRepository.COLLATE_NOCASE + " AND " + CONTACT_NO + " = ? " + BaseRepository.COLLATE_NOCASE;
            String[] selectionArgs = new String[]{DUMMY_BASE_ENTITY_ID, "1", "2"};

            ContactTasksRepositoryHelper contactTasksRepositoryHelper = PowerMockito.spy(new ContactTasksRepositoryHelper());
            DrishtiApplication drishtiApplication = Mockito.mock(DrishtiApplication.class);
            ReflectionHelpers.setStaticField(DrishtiApplication.class, "mInstance", drishtiApplication);

            Mockito.doReturn(repository).when(drishtiApplication).getRepository();
            PowerMockito.when(contactTasksRepositoryHelper.getReadableDatabase()).thenReturn(sqLiteDatabase);

            PowerMockito.when(contactTasksRepositoryHelper.getReadableDatabase().query(TABLE_NAME, projectionArgs, selection, selectionArgs, null, null, orderBy, null)).thenReturn(mCursor);
            PowerMockito.when(mCursor.moveToNext()).then(new Answer<Object>() {
                int count = 0;

                @Override
                public Object answer(InvocationOnMock invocation) {
                    count++;
                    return count <= 1;
                }
            });

            Assert.assertEquals(1, contactTasksRepositoryHelper.getClosedTasks(DUMMY_BASE_ENTITY_ID, "2").size());
        } catch (Exception e) {
            Timber.e(e, " --> testTasksCountWithNullVariables");
        }
    }
}
