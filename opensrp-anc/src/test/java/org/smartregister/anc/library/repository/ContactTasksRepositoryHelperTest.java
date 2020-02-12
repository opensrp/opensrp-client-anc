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

@RunWith(PowerMockRunner.class)
@PrepareForTest({AncLibrary.class, SQLiteDatabase.class, DrishtiApplication.class})
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
    public void testUpdateTasks() throws Exception {
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
    }

    private Task getTask() {
        Task task = new Task(DUMMY_BASE_ENTITY_ID, "myTask", String.valueOf(new JSONObject()), "2", true);
        task.setId(Long.valueOf(1));
        return task;
    }

    @PrepareForTest({ContentValues.class})
    @Test
    public void testSaveTasks() throws Exception {
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
    }

    @Test
    public void testSaveOrUpdateWithNullTasks() {
        ContactTasksRepositoryHelper contactTasksRepositoryHelper = PowerMockito.spy(new ContactTasksRepositoryHelper());
        DrishtiApplication drishtiApplication = Mockito.mock(DrishtiApplication.class);
        ReflectionHelpers.setStaticField(DrishtiApplication.class, "mInstance", drishtiApplication);
        Assert.assertFalse(contactTasksRepositoryHelper.saveOrUpdateTasks(null));
    }

    @Test
    public void testTasksCount() {
        ContactTasksRepositoryHelper contactTasksRepositoryHelper = PowerMockito.spy(new ContactTasksRepositoryHelper());
        DrishtiApplication drishtiApplication = Mockito.mock(DrishtiApplication.class);
        ReflectionHelpers.setStaticField(DrishtiApplication.class, "mInstance", drishtiApplication);

        Mockito.doReturn(repository).when(drishtiApplication).getRepository();
        PowerMockito.when(contactTasksRepositoryHelper.getReadableDatabase()).thenReturn(sqLiteDatabase);
        String sqlQuery = "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE " + BASE_ENTITY_ID + " = ? " + BaseRepository.COLLATE_NOCASE + " AND " + IS_UPDATED + " = ? " + BaseRepository.COLLATE_NOCASE;
        String[] selectionArgs = new String[]{DUMMY_BASE_ENTITY_ID, "0"};

        PowerMockito.when(contactTasksRepositoryHelper.getReadableDatabase().rawQuery(sqlQuery, selectionArgs)).thenReturn(mCursor);
        PowerMockito.when(mCursor.getCount()).thenReturn(1);
        PowerMockito.when(mCursor.getInt(0)).thenReturn(1);
        Assert.assertEquals("1", contactTasksRepositoryHelper.getTasksCount(DUMMY_BASE_ENTITY_ID));
    }

    @Test
    public void testTasksCountWithNullVariables() {
        ContactTasksRepositoryHelper contactTasksRepositoryHelper = PowerMockito.spy(new ContactTasksRepositoryHelper());
        DrishtiApplication drishtiApplication = Mockito.mock(DrishtiApplication.class);
        ReflectionHelpers.setStaticField(DrishtiApplication.class, "mInstance", drishtiApplication);

        Mockito.doReturn(repository).when(drishtiApplication).getRepository();
        PowerMockito.when(contactTasksRepositoryHelper.getReadableDatabase()).thenReturn(sqLiteDatabase);
        String sqlQuery = "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE " + BASE_ENTITY_ID + " = ? " + BaseRepository.COLLATE_NOCASE + " AND " + IS_UPDATED + " = ? " + BaseRepository.COLLATE_NOCASE;
        String[] selectionArgs = new String[]{null, null};

        PowerMockito.when(contactTasksRepositoryHelper.getReadableDatabase().rawQuery(sqlQuery, selectionArgs)).thenReturn(mCursor);
        Assert.assertEquals("0", contactTasksRepositoryHelper.getTasksCount(null));
    }

    @Test
    public void testTasksCountWithNullCursor() {
        ContactTasksRepositoryHelper contactTasksRepositoryHelper = PowerMockito.spy(new ContactTasksRepositoryHelper());
        DrishtiApplication drishtiApplication = Mockito.mock(DrishtiApplication.class);
        ReflectionHelpers.setStaticField(DrishtiApplication.class, "mInstance", drishtiApplication);

        Mockito.doReturn(repository).when(drishtiApplication).getRepository();
        PowerMockito.when(contactTasksRepositoryHelper.getReadableDatabase()).thenReturn(sqLiteDatabase);
        String sqlQuery = "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE " + BASE_ENTITY_ID + " = ? " + BaseRepository.COLLATE_NOCASE + " AND " + IS_UPDATED + " = ? " + BaseRepository.COLLATE_NOCASE;
        String[] selectionArgs = new String[]{null, null};

        PowerMockito.when(contactTasksRepositoryHelper.getReadableDatabase().rawQuery(sqlQuery, selectionArgs)).thenReturn(null);
        Assert.assertEquals("0", contactTasksRepositoryHelper.getTasksCount(null));
    }

    @Test
    public void testTasksCountWithZeroItems() {
        ContactTasksRepositoryHelper contactTasksRepositoryHelper = PowerMockito.spy(new ContactTasksRepositoryHelper());
        DrishtiApplication drishtiApplication = Mockito.mock(DrishtiApplication.class);
        ReflectionHelpers.setStaticField(DrishtiApplication.class, "mInstance", drishtiApplication);

        Mockito.doReturn(repository).when(drishtiApplication).getRepository();
        PowerMockito.when(contactTasksRepositoryHelper.getReadableDatabase()).thenReturn(sqLiteDatabase);
        String sqlQuery = "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE " + BASE_ENTITY_ID + " = ? " + BaseRepository.COLLATE_NOCASE + " AND " + IS_UPDATED + " = ? " + BaseRepository.COLLATE_NOCASE;
        String[] selectionArgs = new String[]{DUMMY_BASE_ENTITY_ID, "0"};

        PowerMockito.when(contactTasksRepositoryHelper.getReadableDatabase().rawQuery(sqlQuery, selectionArgs)).thenReturn(mCursor);
        PowerMockito.when(mCursor.getCount()).thenReturn(0);
        PowerMockito.when(mCursor.getInt(0)).thenReturn(0);
        Assert.assertEquals("0", contactTasksRepositoryHelper.getTasksCount(DUMMY_BASE_ENTITY_ID));
    }

    @Test
    public void testGetTasks() {
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
    }

    @Test
    public void testGetTasksWithNullVariables() {
        String orderBy = ID + " DESC ";
        String[] projectionArgs = new String[]{ID, CONTACT_NO, KEY, VALUE, IS_UPDATED, BASE_ENTITY_ID, CREATED_AT};
        String selection = BASE_ENTITY_ID + " = ? " + BaseRepository.COLLATE_NOCASE;
        String[] selectionArgs = new String[]{DUMMY_BASE_ENTITY_ID};

        ContactTasksRepositoryHelper contactTasksRepositoryHelper = PowerMockito.spy(new ContactTasksRepositoryHelper());
        DrishtiApplication drishtiApplication = Mockito.mock(DrishtiApplication.class);
        ReflectionHelpers.setStaticField(DrishtiApplication.class, "mInstance", drishtiApplication);

        Mockito.doReturn(repository).when(drishtiApplication).getRepository();
        PowerMockito.when(contactTasksRepositoryHelper.getReadableDatabase()).thenReturn(sqLiteDatabase);

        PowerMockito.when(contactTasksRepositoryHelper.getReadableDatabase().query(TABLE_NAME, projectionArgs, selection, selectionArgs, null, null, orderBy, null)).thenReturn(null);
        Assert.assertEquals(0, contactTasksRepositoryHelper.getTasks(null, null).size());
    }

    @Test
    public void testGetOpenTasks() {
        String orderBy = ID + " DESC ";
        String[] projectionArgs = new String[]{ID, CONTACT_NO, KEY, VALUE, IS_UPDATED, BASE_ENTITY_ID, CREATED_AT};
        String selection = BASE_ENTITY_ID + " = ? " + BaseRepository.COLLATE_NOCASE + " AND " + IS_UPDATED + " = ? " + BaseRepository.COLLATE_NOCASE;
        String[] selectionArgs = new String[]{DUMMY_BASE_ENTITY_ID, "0"};

        ContactTasksRepositoryHelper contactTasksRepositoryHelper = PowerMockito.spy(new ContactTasksRepositoryHelper());
        DrishtiApplication drishtiApplication = Mockito.mock(DrishtiApplication.class);
        ReflectionHelpers.setStaticField(DrishtiApplication.class, "mInstance", drishtiApplication);

        Mockito.doReturn(repository).when(drishtiApplication).getRepository();
        PowerMockito.when(repository.getReadableDatabase()).thenReturn(sqLiteDatabase);
        PowerMockito.when(sqLiteDatabase.query(TABLE_NAME, projectionArgs, selection, selectionArgs, null, null, orderBy, null)).thenReturn(mCursor);
        PowerMockito.when(mCursor.moveToNext()).then(new Answer<Object>() {
            int count = 0;

            @Override
            public Object answer(InvocationOnMock invocation) {
                count++;
                return count <= 3;
            }
        });

        Assert.assertEquals(3, contactTasksRepositoryHelper.getOpenTasks(DUMMY_BASE_ENTITY_ID).size());
    }

    @Test
    public void testGetOpenTasksWithNullVariables() {
        String orderBy = ID + " DESC ";
        String[] projectionArgs = new String[]{ID, CONTACT_NO, KEY, VALUE, IS_UPDATED, BASE_ENTITY_ID, CREATED_AT};
        String selection = BASE_ENTITY_ID + " = ? " + BaseRepository.COLLATE_NOCASE + " AND " + IS_UPDATED + " = ? " + BaseRepository.COLLATE_NOCASE;
        String[] selectionArgs = new String[]{DUMMY_BASE_ENTITY_ID, "0"};

        ContactTasksRepositoryHelper contactTasksRepositoryHelper = PowerMockito.spy(new ContactTasksRepositoryHelper());
        DrishtiApplication drishtiApplication = Mockito.mock(DrishtiApplication.class);
        ReflectionHelpers.setStaticField(DrishtiApplication.class, "mInstance", drishtiApplication);

        Mockito.doReturn(repository).when(drishtiApplication).getRepository();
        PowerMockito.when(repository.getReadableDatabase()).thenReturn(sqLiteDatabase);
        PowerMockito.when(sqLiteDatabase.query(TABLE_NAME, projectionArgs, selection, selectionArgs, null, null, orderBy, null)).thenReturn(null);
        Assert.assertEquals(0, contactTasksRepositoryHelper.getOpenTasks(null).size());
    }

    @Test
    public void testGetClosedTasks() {
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
    }

    @Test
    public void testGetClosedTasksWithNullBaseEntityIdAndContactNo() {
        String orderBy = ID + " DESC ";
        String[] projectionArgs = new String[]{ID, CONTACT_NO, KEY, VALUE, IS_UPDATED, BASE_ENTITY_ID, CREATED_AT};
        String selection = BASE_ENTITY_ID + " = ? " + BaseRepository.COLLATE_NOCASE + " AND " + IS_UPDATED + " = ? " + BaseRepository.COLLATE_NOCASE + " AND " + CONTACT_NO + " = ? " + BaseRepository.COLLATE_NOCASE;
        String[] selectionArgs = new String[]{DUMMY_BASE_ENTITY_ID, "1", "2"};

        ContactTasksRepositoryHelper contactTasksRepositoryHelper = PowerMockito.spy(new ContactTasksRepositoryHelper());
        DrishtiApplication drishtiApplication = Mockito.mock(DrishtiApplication.class);
        ReflectionHelpers.setStaticField(DrishtiApplication.class, "mInstance", drishtiApplication);

        Mockito.doReturn(repository).when(drishtiApplication).getRepository();
        PowerMockito.when(contactTasksRepositoryHelper.getReadableDatabase()).thenReturn(sqLiteDatabase);

        PowerMockito.when(contactTasksRepositoryHelper.getReadableDatabase().query(TABLE_NAME, projectionArgs, selection, selectionArgs, null, null, orderBy, null)).thenReturn(null);
        Assert.assertEquals(0, contactTasksRepositoryHelper.getClosedTasks(null, null).size());
    }

    @Test
    public void testGetClosedTasksWithEmptyBaseEntityIdAndContactNo() {
        String orderBy = ID + " DESC ";
        String[] projectionArgs = new String[]{ID, CONTACT_NO, KEY, VALUE, IS_UPDATED, BASE_ENTITY_ID, CREATED_AT};
        String selection = BASE_ENTITY_ID + " = ? " + BaseRepository.COLLATE_NOCASE + " AND " + IS_UPDATED + " = ? " + BaseRepository.COLLATE_NOCASE + " AND " + CONTACT_NO + " = ? " + BaseRepository.COLLATE_NOCASE;
        String[] selectionArgs = new String[]{DUMMY_BASE_ENTITY_ID, "1", "2"};

        ContactTasksRepositoryHelper contactTasksRepositoryHelper = PowerMockito.spy(new ContactTasksRepositoryHelper());
        DrishtiApplication drishtiApplication = Mockito.mock(DrishtiApplication.class);
        ReflectionHelpers.setStaticField(DrishtiApplication.class, "mInstance", drishtiApplication);

        Mockito.doReturn(repository).when(drishtiApplication).getRepository();
        PowerMockito.when(contactTasksRepositoryHelper.getReadableDatabase()).thenReturn(sqLiteDatabase);

        PowerMockito.when(contactTasksRepositoryHelper.getReadableDatabase().query(TABLE_NAME, projectionArgs, selection, selectionArgs, null, null, orderBy, null)).thenReturn(null);
        Assert.assertEquals(0, contactTasksRepositoryHelper.getClosedTasks("", "").size());
    }

    @Test
    public void testUpdateBooleanValueWith1() throws Exception {
        ContactTasksRepositoryHelper contactTasksRepositoryHelper = new ContactTasksRepositoryHelper();
        boolean isBoolean = Whitebox.invokeMethod(contactTasksRepositoryHelper, "updateBooleanValue", "1");
        Assert.assertTrue(isBoolean);
    }

    @Test
    public void testUpdateBooleanValueWithZeroValue() throws Exception {
        ContactTasksRepositoryHelper contactTasksRepositoryHelper = new ContactTasksRepositoryHelper();
        boolean isBoolean = Whitebox.invokeMethod(contactTasksRepositoryHelper, "updateBooleanValue", "0");
        Assert.assertFalse(isBoolean);
    }
}
