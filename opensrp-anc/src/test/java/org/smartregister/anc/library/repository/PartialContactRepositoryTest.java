package org.smartregister.anc.library.repository;

import android.content.ContentValues;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.anc.library.AncLibrary;
import org.smartregister.anc.library.model.PartialContact;
import org.smartregister.anc.library.util.Utils;
import org.smartregister.repository.Repository;
import org.smartregister.view.activity.DrishtiApplication;
import org.yaml.snakeyaml.events.Event;

import java.util.Calendar;
import java.util.List;

@RunWith(PowerMockRunner.class)
@PrepareForTest({DrishtiApplication.class, Calendar.class})
public class PartialContactRepositoryTest {

    @Mock
    private Repository repository;

    @Mock
    private SQLiteDatabase sqLiteDatabase;

    @Mock
    private DrishtiApplication drishtiApplication;

    @Captor
    private ArgumentCaptor argumentCaptor;

    @Mock
    private Cursor cursor;

    @Mock
    private Calendar calendar;
    PartialContactRepository partialContactRepository = new PartialContactRepository();


    @Before
    public void setUp() {
        PowerMockito.mockStatic(DrishtiApplication.class);
        PowerMockito.mockStatic(Calendar.class);
        //PowerMockito.mockStatic(Utils.class);

        MockitoAnnotations.openMocks(this);

        PowerMockito.when(DrishtiApplication.getInstance()).thenReturn(drishtiApplication);


        PowerMockito.when(drishtiApplication.getRepository()).thenReturn(repository);
        PowerMockito.when(repository.getWritableDatabase()).thenReturn(sqLiteDatabase);

        PowerMockito.when(Calendar.getInstance()).thenReturn(calendar);

    }

    @Test
    public void savePartialContactTest()
    {
        PowerMockito.mockStatic(DrishtiApplication.class);
        PowerMockito.mockStatic(Calendar.class);


        PowerMockito.when(DrishtiApplication.getInstance()).thenReturn(drishtiApplication);

        PowerMockito.when(drishtiApplication.getRepository()).thenReturn(repository);
        PowerMockito.when(repository.getWritableDatabase()).thenReturn(sqLiteDatabase);
        PowerMockito.when(repository.getReadableDatabase()).thenReturn(sqLiteDatabase);

       PowerMockito.when(sqLiteDatabase.query(
                Mockito.anyString(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.isNull(),
                Mockito.isNull(),
                Mockito.isNull(),
                Mockito.isNull()))
               .thenReturn(cursor);

        PowerMockito.when(cursor.getCount()).thenReturn(1);
        PowerMockito.when(cursor.moveToFirst()).thenReturn(true);

        PowerMockito.when(cursor.getColumnIndex(PartialContactRepository.ID)).thenReturn(1);
        PowerMockito.when(cursor.getColumnIndex(PartialContactRepository.TYPE)).thenReturn(2);
        PowerMockito.when(cursor.getColumnIndex(PartialContactRepository.FORM_JSON)).thenReturn(3);
        PowerMockito.when(cursor.getColumnIndex(PartialContactRepository.CONTACT_NO)).thenReturn(4);
        PowerMockito.when(cursor.getColumnIndex(PartialContactRepository.FORM_JSON_DRAFT)).thenReturn(5);
        PowerMockito.when(cursor.getColumnIndex(PartialContactRepository.IS_FINALIZED)).thenReturn(6);
        PowerMockito.when(cursor.getColumnIndex(PartialContactRepository.BASE_ENTITY_ID)).thenReturn(7);
        PowerMockito.when(cursor.getColumnIndex(PartialContactRepository.CREATED_AT)).thenReturn(8);
        PowerMockito.when(cursor.getColumnIndex(PartialContactRepository.UPDATED_AT_COLUMN)).thenReturn(9);


        PowerMockito.when(cursor.getLong(1)).thenReturn(1000L);
        PowerMockito.when(cursor.getString(2)).thenReturn("anc_profile");
        PowerMockito.when(cursor.getString(3)).thenReturn("form");
        PowerMockito.when(cursor.getInt(4)).thenReturn(123123);
        PowerMockito.when(cursor.getString(5)).thenReturn("draft");
        PowerMockito.when(cursor.getInt(6)).thenReturn(1);
        PowerMockito.when(cursor.getString(7)).thenReturn("base_id");
        PowerMockito.when(cursor.getLong(8)).thenReturn(123L);
        PowerMockito.when(cursor.getLong(9)).thenReturn(123L);





        PartialContactRepository spyRepository = Mockito.spy(partialContactRepository);
        PartialContact partialContact = new PartialContact();
        partialContact.setBaseEntityId("base_ID");
        partialContact.setType("anc_profile");
        partialContact.setContactNo(132312132);
        partialContact.setFinalized(true);
        partialContact.setCreatedAt(123123L);
        partialContact.setUpdatedAt(12312313L);
        spyRepository.savePartialContact(partialContact);


        Mockito.verify(sqLiteDatabase).update(Mockito.eq("partial_contact"), (ContentValues) argumentCaptor.capture(),
                (String) argumentCaptor.capture(), (String[]) argumentCaptor.capture());


    }


    @Test
    public void testCreateTable()
    {
        PartialContactRepository.createTable(sqLiteDatabase);
        Mockito.verify(sqLiteDatabase,Mockito.times(4)).execSQL(Mockito.anyString());

    }
    @Test
    public void getPartialContactsTest()
    {

        PowerMockito.mockStatic(DrishtiApplication.class);
        PowerMockito.mockStatic(Calendar.class);

        PowerMockito.when(DrishtiApplication.getInstance()).thenReturn(drishtiApplication);


        PowerMockito.when(drishtiApplication.getRepository()).thenReturn(repository);
        PowerMockito.when(repository.getWritableDatabase()).thenReturn(sqLiteDatabase);
        PowerMockito.when(repository.getReadableDatabase()).thenReturn(sqLiteDatabase);

        PowerMockito.when(sqLiteDatabase.query(
                Mockito.anyString(),
                Mockito.any(),
                Mockito.any(),
                Mockito.any(),
                Mockito.isNull(),
                Mockito.isNull(),
                Mockito.isNull(),
                Mockito.isNull()))
                .thenReturn(cursor);



            Mockito.when(cursor.moveToNext()).thenReturn(true).thenReturn(false);


        PowerMockito.when(cursor.getColumnIndex(PartialContactRepository.ID)).thenReturn(1);
        PowerMockito.when(cursor.getColumnIndex(PartialContactRepository.TYPE)).thenReturn(2);
        PowerMockito.when(cursor.getColumnIndex(PartialContactRepository.FORM_JSON)).thenReturn(3);
        PowerMockito.when(cursor.getColumnIndex(PartialContactRepository.CONTACT_NO)).thenReturn(4);
        PowerMockito.when(cursor.getColumnIndex(PartialContactRepository.FORM_JSON_DRAFT)).thenReturn(5);
        PowerMockito.when(cursor.getColumnIndex(PartialContactRepository.IS_FINALIZED)).thenReturn(6);
        PowerMockito.when(cursor.getColumnIndex(PartialContactRepository.BASE_ENTITY_ID)).thenReturn(7);
        PowerMockito.when(cursor.getColumnIndex(PartialContactRepository.CREATED_AT)).thenReturn(8);
        PowerMockito.when(cursor.getColumnIndex(PartialContactRepository.UPDATED_AT_COLUMN)).thenReturn(9);


        PowerMockito.when(cursor.getLong(1)).thenReturn(1000L);
        PowerMockito.when(cursor.getString(2)).thenReturn("anc_profile");
        PowerMockito.when(cursor.getString(3)).thenReturn("form");
        PowerMockito.when(cursor.getInt(4)).thenReturn(123123);
        PowerMockito.when(cursor.getString(5)).thenReturn("draft");
        PowerMockito.when(cursor.getInt(6)).thenReturn(1);
        PowerMockito.when(cursor.getString(7)).thenReturn("base_id");
        PowerMockito.when(cursor.getLong(8)).thenReturn(123L);
        PowerMockito.when(cursor.getLong(9)).thenReturn(123L);


        PartialContactRepository spyRepository = Mockito.spy(partialContactRepository);
       List<PartialContact> partialContactList =  spyRepository.getPartialContacts("baseID",1);
       Assert.assertEquals(partialContactList.size(),1);



    }




}
