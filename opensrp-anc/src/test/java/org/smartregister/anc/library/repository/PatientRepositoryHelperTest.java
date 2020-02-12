package org.smartregister.anc.library.repository;

import android.content.ContentValues;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.smartregister.Context;
import org.smartregister.anc.library.AncLibrary;
import org.smartregister.anc.library.util.DBConstantsUtils;
import org.smartregister.repository.Repository;
import org.smartregister.view.activity.DrishtiApplication;

import java.util.Map;

/**
 * Created by ndegwamartin on 14/07/2018.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({AncLibrary.class, SQLiteDatabase.class, DrishtiApplication.class, ContentValues.class})
public class PatientRepositoryHelperTest {

    protected static final String DUMMY_BASE_ENTITY_ID = "00ts-ime-hcla-0tib-0eht-ma0i";

    @Mock
    private Repository repository;

    @Mock
    private SQLiteDatabase sqLiteDatabase;

    @Mock
    private DrishtiApplication drishtiApplication;

    @Mock
    private AncLibrary ancLibrary;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(AncLibrary.class);
        PowerMockito.when(AncLibrary.getInstance()).thenReturn(ancLibrary);
        PowerMockito.when(ancLibrary.getRegisterQueryProvider()).thenReturn(new RegisterQueryProvider());
    }

    @Test
    public void testPatientRepositoryInstantiatesCorrectly() {
        PatientRepositoryHelper patientRepositoryHelper = new PatientRepositoryHelper();
        Assert.assertNotNull(patientRepositoryHelper);

        Map<String, String> womanProfileDetails = PatientRepositoryHelper.getWomanProfileDetails(DUMMY_BASE_ENTITY_ID);
        Assert.assertNull(womanProfileDetails);

    }

    @Test
    public void testUpdateWomanDetailsInvokesUpdateMethodOfWritableDatabase() {
        PowerMockito.mockStatic(DrishtiApplication.class);
        PowerMockito.when(DrishtiApplication.getInstance()).thenReturn(drishtiApplication);
        PowerMockito.when(drishtiApplication.getRepository()).thenReturn(repository);
        PowerMockito.when(repository.getReadableDatabase()).thenReturn(sqLiteDatabase);
        String sql = "SELECT first_name,last_name,dob,dob_unknown,ec_mother_details.phone_number,ec_mother_details.alt_name,ec_mother_details.alt_phone_number," +
                "ec_client.base_entity_id,register_id,ec_mother_details.reminders,home_address,ec_mother_details.edd,ec_mother_details.contact_status,ec_mother_details.previous_contact_status," +
                "ec_mother_details.next_contact,ec_mother_details.next_contact_date,ec_mother_details.visit_start_date,ec_mother_details.red_flag_count,ec_mother_details.yellow_flag_count," +
                "ec_mother_details.last_contact_record_date FROM ec_client " +
                "join ec_mother_details on ec_client.base_entity_id = ec_mother_details.base_entity_id WHERE ec_client.base_entity_id = ?";
        Cursor cursor = Mockito.mock(Cursor.class);
        PowerMockito.when(sqLiteDatabase.rawQuery(sql, new String[]{DUMMY_BASE_ENTITY_ID})).thenReturn(cursor);
        PowerMockito.when(cursor.moveToFirst()).thenReturn(true);
        PowerMockito.when(cursor.getColumnIndex(DBConstantsUtils.KeyUtils.FIRST_NAME)).thenReturn(1);
        PowerMockito.when(cursor.getColumnIndex(DBConstantsUtils.KeyUtils.LAST_NAME)).thenReturn(2);
        PowerMockito.when(cursor.getColumnIndex(DBConstantsUtils.KeyUtils.ANC_ID)).thenReturn(3);
        PowerMockito.when(cursor.getColumnIndex(DBConstantsUtils.KeyUtils.PHONE_NUMBER)).thenReturn(4);


        PowerMockito.when(cursor.getString(cursor.getColumnIndex(DBConstantsUtils.KeyUtils.FIRST_NAME)))
                .thenReturn("Mary");
        PowerMockito.when(cursor.getString(cursor.getColumnIndex(DBConstantsUtils.KeyUtils.LAST_NAME)))
                .thenReturn("Mary");
        PowerMockito.when(cursor.getString(cursor.getColumnIndex(DBConstantsUtils.KeyUtils.ANC_ID)))
                .thenReturn("234-234");
        PowerMockito.when(cursor.getString(cursor.getColumnIndex(DBConstantsUtils.KeyUtils.PHONE_NUMBER)))
                .thenReturn("020-234-234");

        Map<String, String> expectedMap = PatientRepositoryHelper.getWomanProfileDetails(DUMMY_BASE_ENTITY_ID);
        Assert.assertEquals("Mary", expectedMap.get(DBConstantsUtils.KeyUtils.FIRST_NAME));
        Assert.assertEquals("Mary", expectedMap.get(DBConstantsUtils.KeyUtils.LAST_NAME));
        Assert.assertEquals("234-234", expectedMap.get(DBConstantsUtils.KeyUtils.ANC_ID));
        Assert.assertEquals("020-234-234", expectedMap.get(DBConstantsUtils.KeyUtils.PHONE_NUMBER));
    }

//    @Test
//    public void testUpdateContactVisitDetails() throws Exception {
//
//        ContentValues contentValues = PowerMockito.mock(ContentValues.class);
//        PowerMockito.whenNew(ContentValues.class).withNoArguments().thenReturn(contentValues);
//        PatientRepositoryHelper.updateContactVisitDetails(null, true);
//    }
}
