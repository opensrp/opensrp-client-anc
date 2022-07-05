package org.smartregister.anc.library.repository;

import static org.smartregister.anc.library.repository.PreviousContactRepository.BASE_ENTITY_ID;
import static org.smartregister.anc.library.repository.PreviousContactRepository.CREATED_AT;
import static org.smartregister.anc.library.repository.PreviousContactRepository.ID;
import static org.smartregister.anc.library.repository.PreviousContactRepository.VALUE;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.jeasy.rules.api.Facts;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.anc.library.AncLibrary;
import org.smartregister.anc.library.activity.BaseUnitTest;
import org.smartregister.anc.library.model.PreviousContact;
import org.smartregister.anc.library.model.PreviousContactsSummaryModel;
import org.smartregister.anc.library.util.ConstantsUtils;
import org.smartregister.anc.library.util.Utils;
import org.smartregister.repository.Repository;
import org.smartregister.view.activity.DrishtiApplication;

import java.util.ArrayList;
import java.util.List;


@RunWith(PowerMockRunner.class)
@PrepareForTest({DrishtiApplication.class,Utils.class,PreviousContactRepository.class})
public class PreviousContactRepositoryTest extends BaseUnitTest {

    public static final String TABLE_NAME = "previous_contact";
    protected static final String DUMMY_BASE_ENTITY_ID = "4faf5afa-fa7f-4d98-b4cd-4ee39c8d1eb1";
    protected static final String CONTACT_NO = "1";
    protected static final String KEY = "attention_flag_facts";
    private static final PreviousContact previousContact = new PreviousContact();
    String orderBy = "order by abs_contact_no, contact_no,_id DESC";
    @Mock
    private SQLiteDatabase sqLiteDatabase;
    @Mock
    private DrishtiApplication drishtiApplication;
    @Mock
    private AncLibrary ancLibrary;

    private PreviousContactRepository previousContactRepository = new PreviousContactRepository();
    @Mock
    private Repository repository;
    private List<PreviousContactsSummaryModel> previousContactFacts = new ArrayList<>();

    @Before
    public void setUp() {
        PowerMockito.mockStatic(DrishtiApplication.class);
        PowerMockito.mockStatic(Utils.class);

        MockitoAnnotations.openMocks(this);

        ReflectionHelpers.setStaticField(AncLibrary.class, "instance", ancLibrary);

        PowerMockito.when(DrishtiApplication.getInstance()).thenReturn(drishtiApplication);

        PowerMockito.when(ancLibrary.getRegisterQueryProvider()).thenReturn(new RegisterQueryProvider());
       // PowerMockito.when(previousContactRepository.getWritableDatabase()).thenReturn(sqLiteDatabase);
        ReflectionHelpers.setStaticField(DrishtiApplication.class, "mInstance", drishtiApplication);
        PowerMockito.when(drishtiApplication.getRepository()).thenReturn(repository);
        PowerMockito.when(repository.getWritableDatabase()).thenReturn(sqLiteDatabase);

//        PowerMockito.when(DrishtiApplication.getInstance().getRepository().getWritableDatabase()).thenReturn(sqLiteDatabase);


    }

    @Test
    public void testCreateTable()
    {
        PreviousContactRepository.createTable(sqLiteDatabase);
        Mockito.verify(sqLiteDatabase, Mockito.times(5)).execSQL(Mockito.anyString());

    }

    @Test
    public void savePreviousContactTest()
    {
        PreviousContactRepository spyRepository = Mockito.spy(previousContactRepository);

        PowerMockito.mockStatic(Utils.class);
        PowerMockito.when(Utils.getDBDateToday()).thenReturn("12-2-2012");

        PowerMockito.mockStatic(DrishtiApplication.class);
        PowerMockito.when(DrishtiApplication.getInstance()).thenReturn(drishtiApplication);
        PowerMockito.when(drishtiApplication.getRepository()).thenReturn(repository);
        PowerMockito.when(repository.getWritableDatabase()).thenReturn(sqLiteDatabase);


        spyRepository.savePreviousContact(new PreviousContact());
        Mockito.verify(sqLiteDatabase,Mockito.times(1)).insert(Mockito.anyString(), Mockito.isNull(), Mockito.any());
    }

    @Test
    public void getPreviousContactTest()
    {
        PowerMockito.mockStatic(DrishtiApplication.class);
        PowerMockito.when(DrishtiApplication.getInstance()).thenReturn(drishtiApplication);
        PowerMockito.when(drishtiApplication.getRepository()).thenReturn(repository);
        PowerMockito.when(repository.getReadableDatabase()).thenReturn(sqLiteDatabase);
        PreviousContact contact = new PreviousContact();
        contact.setBaseEntityId("base-ID");
        contact.setKey("key");
        PreviousContactRepository spyRepository = Mockito.spy(previousContactRepository);
        Cursor cursor = Mockito.mock(Cursor.class);
        PowerMockito.when(cursor.getCount()).thenReturn(1);
        PowerMockito.when(cursor.moveToFirst()).thenReturn(true);
        PowerMockito.when(sqLiteDatabase.query(Mockito.anyString(),Mockito.any(),Mockito.any(),Mockito.any(),Mockito.isNull(),Mockito.isNull(),Mockito.anyString(),Mockito.isNull())).thenReturn(cursor);


        PowerMockito.when(cursor.getColumnIndex(ArgumentMatchers.matches(PreviousContactRepository.KEY))).thenReturn(1);
        PowerMockito.when(cursor.getColumnIndex(ArgumentMatchers.matches(VALUE))).thenReturn(2);
        PowerMockito.when(cursor.getColumnIndex(ArgumentMatchers.matches(BASE_ENTITY_ID))).thenReturn(3);
        PowerMockito.when(cursor.getColumnIndex(ArgumentMatchers.matches(CREATED_AT))).thenReturn(4);



        PowerMockito.when(cursor.getLong(cursor.getColumnIndex(ArgumentMatchers.matches(ID)))).thenReturn(1L);
        PowerMockito.when(cursor.getString(1)).thenReturn("key");
        PowerMockito.when(cursor.getString(2)).thenReturn("value");
        PowerMockito.when(cursor.getString(3)).thenReturn("base_id");
        PowerMockito.when(cursor.getString(4)).thenReturn("created_at");
        PowerMockito.when(cursor.moveToNext()).thenReturn(true).thenReturn(false);


        PreviousContact returnedContact = spyRepository.getPreviousContact(contact);
        Assert.assertEquals(returnedContact.getKey(),"key");
        Assert.assertEquals(returnedContact.getValue(),"value");
        Assert.assertEquals(returnedContact.getBaseEntityId(),"base_id");


        List<PreviousContact> returnedContact2 = spyRepository.getPreviousContacts("baseID",null);
        Assert.assertNotNull(returnedContact2);

    }


    @Test
    public void getImmediatePreviousScheduleTest()
    {
        PowerMockito.mockStatic(DrishtiApplication.class);
        PowerMockito.when(DrishtiApplication.getInstance()).thenReturn(drishtiApplication);
        PowerMockito.when(drishtiApplication.getRepository()).thenReturn(repository);
        PowerMockito.when(repository.getWritableDatabase()).thenReturn(sqLiteDatabase);
        Cursor cursor = Mockito.mock(Cursor.class);
        PreviousContactRepository spyRepository = Mockito.spy(previousContactRepository);
        PowerMockito.when(sqLiteDatabase.query(Mockito.anyString(),Mockito.any(),Mockito.any(),Mockito.any(),Mockito.isNull(),Mockito.isNull(),Mockito.any(),Mockito.isNull())).thenReturn(cursor);

        PowerMockito.when(cursor.getColumnIndex(ArgumentMatchers.matches(PreviousContactRepository.KEY))).thenReturn(1);
        PowerMockito.when(cursor.getColumnIndex(ArgumentMatchers.matches(VALUE))).thenReturn(2);
        PowerMockito.when(cursor.moveToNext()).thenReturn(true).thenReturn(false);
        PowerMockito.when(cursor.getString(1)).thenReturn("key");
        PowerMockito.when(cursor.getString(2)).thenReturn("value");
        Facts facts = spyRepository.getImmediatePreviousSchedule("base_id","1");
        Assert.assertNotNull(facts);

    }



    @Test
    public void testGetPreviousContactFacts() {
        ReflectionHelpers.setStaticField(DrishtiApplication.class, "mInstance", drishtiApplication);
        PowerMockito.when(ancLibrary.getPreviousContactRepository()).thenReturn(previousContactRepository);
        PowerMockito.when(previousContactRepository.getReadableDatabase()).thenReturn(sqLiteDatabase);
        String sql = "select *,  abs(" + CONTACT_NO + ") as abs_contact_no from " + TABLE_NAME + " where "
                + DUMMY_BASE_ENTITY_ID + " = ? and ( " + KEY + " = ? or " + KEY + " = ? or " + KEY + " = ? or " + KEY +
                " = ? or " + KEY + " = ? ) " + orderBy;
        Cursor cursor = Mockito.mock(Cursor.class);
        PowerMockito.when(sqLiteDatabase.rawQuery(sql, new String[]{DUMMY_BASE_ENTITY_ID, KEY})).thenReturn(cursor);
        PowerMockito.when(cursor.moveToFirst()).thenReturn(true);
        PowerMockito.when(cursor.getColumnName(1)).thenReturn(ConstantsUtils.CONTACT_NO);
        PowerMockito.when(cursor.getColumnName(2)).thenReturn(ConstantsUtils.KeyUtils.VALUE);
        previousContactFacts =previousContactRepository.getPreviousContactsFacts(DUMMY_BASE_ENTITY_ID);
        Assert.assertNotNull(previousContactFacts);
    }

    @Test
    public void getPreviousContactTestsFactsTest()
    {
        PowerMockito.mockStatic(DrishtiApplication.class);
        PowerMockito.when(DrishtiApplication.getInstance()).thenReturn(drishtiApplication);
        PowerMockito.when(drishtiApplication.getRepository()).thenReturn(repository);
        PowerMockito.when(repository.getWritableDatabase()).thenReturn(sqLiteDatabase);
        Cursor cursor = Mockito.mock(Cursor.class);
        PreviousContactRepository spyRepository = Mockito.spy(previousContactRepository);
        PowerMockito.when(sqLiteDatabase.query(Mockito.anyString(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.isNull(), Mockito.any(), Mockito.isNull())).thenReturn(cursor);

        PowerMockito.when(cursor.getColumnIndex(ArgumentMatchers.matches(PreviousContactRepository.KEY))).thenReturn(1);
        PowerMockito.when(cursor.getColumnIndex(ArgumentMatchers.matches(VALUE))).thenReturn(2);
        PowerMockito.when(cursor.moveToNext())
                .thenReturn(true)
                .thenReturn(false);
        PowerMockito.when(cursor.getString(1)).thenReturn("key");
        PowerMockito.when(cursor.getString(2)).thenReturn("value");

        Facts facts = spyRepository.getPreviousContactTestsFacts("BaseID");
        Assert.assertNotNull(facts);


    }

    @Test
    public void getPreviousContact() {
        PreviousContactRepository previousContactRepository=Mockito.mock(PreviousContactRepository.class);
        Mockito.when(ancLibrary.getPreviousContactRepository()).thenReturn(previousContactRepository);
        previousContact.setContactNo("2");
        previousContact.setBaseEntityId("4faf5afa-fa7f-4d98-b4cd-4ee39c8d1eb1");
        previousContact.setKey("weight_cat");
        Assert.assertNotNull(previousContact);
    }
}
