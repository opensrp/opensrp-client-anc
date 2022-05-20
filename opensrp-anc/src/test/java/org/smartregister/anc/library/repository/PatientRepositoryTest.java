package org.smartregister.anc.library.repository;

import android.content.ContentValues;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.junit.After;
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
import org.robolectric.RobolectricTestRunner;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.anc.library.AncLibrary;
import org.smartregister.anc.library.domain.WomanDetail;
import org.smartregister.anc.library.util.DBConstantsUtils;
import org.smartregister.repository.Repository;
import org.smartregister.view.activity.DrishtiApplication;

import java.util.Map;

/**
 * Created by ndegwamartin on 14/07/2018.
 */

@RunWith(RobolectricTestRunner.class)
public class PatientRepositoryTest {

    protected static final String DUMMY_BASE_ENTITY_ID = "00ts-ime-hcla-0tib-0eht-ma0i";

    @Mock
    private Repository repository;

    @Mock
    private SQLiteDatabase sqLiteDatabase;

    @Mock
    private DrishtiApplication drishtiApplication;

    @Mock
    private AncLibrary ancLibrary;

    @Captor
    private ArgumentCaptor updateContactVisitDetailArgumentCaptor;

    @Captor
    private ArgumentCaptor updateWomanAlertStatusArgumentCaptor;

    @Captor
    private ArgumentCaptor updateContactVisitStartDate;

    @Captor
    private ArgumentCaptor updateEDDDateArgumentCaptor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        ReflectionHelpers.setStaticField(AncLibrary.class, "instance", ancLibrary);
        PowerMockito.when(ancLibrary.getRegisterQueryProvider()).thenReturn(new RegisterQueryProvider());
    }

    @Test
    public void testPatientRepositoryInstantiatesCorrectly() {
        PatientRepository patientRepositoryHelper = new PatientRepository();
        Assert.assertNotNull(patientRepositoryHelper);

        Map<String, String> womanProfileDetails = PatientRepository.getWomanProfileDetails(DUMMY_BASE_ENTITY_ID);
        Assert.assertNull(womanProfileDetails);

    }

    @Test
    public void testUpdateWomanDetailsInvokesUpdateMethodOfWritableDatabase() {
        ReflectionHelpers.setStaticField(DrishtiApplication.class, "mInstance", drishtiApplication);
        PowerMockito.when(drishtiApplication.getRepository()).thenReturn(repository);
        PowerMockito.when(repository.getReadableDatabase()).thenReturn(sqLiteDatabase);
        String sql = "SELECT first_name,last_name,dob,dob_unknown,ec_mother_details.phone_number,ec_mother_details.alt_name,ec_mother_details.alt_phone_number,ec_client.base_entity_id,ec_client.base_entity_id as _id,register_id,ec_mother_details.reminders,home_address,ec_mother_details.edd,ec_mother_details.contact_status,ec_mother_details.previous_contact_status,ec_mother_details.next_contact,ec_mother_details.next_contact_date,ec_mother_details.visit_start_date,ec_mother_details.red_flag_count,ec_mother_details.yellow_flag_count,ec_mother_details.last_contact_record_date,ec_mother_details.cohabitants,ec_client.relationalid,ec_mother_details.province,ec_mother_details.district,ec_mother_details.subdistrict,ec_mother_details.health_facility,ec_mother_details.village FROM ec_client join ec_mother_details on ec_client.base_entity_id = ec_mother_details.base_entity_id WHERE ec_client.base_entity_id = ?";
        Cursor cursor = Mockito.mock(Cursor.class);
        PowerMockito.when(sqLiteDatabase.rawQuery(sql, new String[]{DUMMY_BASE_ENTITY_ID})).thenReturn(cursor);
        PowerMockito.when(cursor.moveToFirst()).thenReturn(true);
        PowerMockito.when(cursor.getColumnName(0)).thenReturn(DBConstantsUtils.KeyUtils.FIRST_NAME);
        PowerMockito.when(cursor.getColumnName(1)).thenReturn(DBConstantsUtils.KeyUtils.LAST_NAME);
        PowerMockito.when(cursor.getColumnName(9)).thenReturn(DBConstantsUtils.KeyUtils.ANC_ID);
        PowerMockito.when(cursor.getColumnName(4)).thenReturn(DBConstantsUtils.KeyUtils.PHONE_NUMBER);


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

        Map<String, String> expectedMap = PatientRepository.getWomanProfileDetails(DUMMY_BASE_ENTITY_ID);
        Assert.assertEquals("Mary", expectedMap.get(DBConstantsUtils.KeyUtils.FIRST_NAME));
        Assert.assertEquals("Mary", expectedMap.get(DBConstantsUtils.KeyUtils.LAST_NAME));
        Assert.assertEquals("234-234", expectedMap.get(DBConstantsUtils.KeyUtils.ANC_ID));
        Assert.assertEquals("020-234-234", expectedMap.get(DBConstantsUtils.KeyUtils.PHONE_NUMBER));
    }

    @Test
    public void testUpdateContactVisitDetailsShouldPassCorrectArgsToUpdateDb() throws Exception {
        ReflectionHelpers.setStaticField(DrishtiApplication.class, "mInstance", drishtiApplication);
        PowerMockito.when(drishtiApplication.getRepository()).thenReturn(repository);
        PowerMockito.when(repository.getWritableDatabase()).thenReturn(sqLiteDatabase);

        WomanDetail womanDetail = new WomanDetail();
        womanDetail.setRedFlagCount(200);
        womanDetail.setRedFlags("234");
        womanDetail.setNextContact(2);
        womanDetail.setYellowFlagCount(2);
        PatientRepository.updateContactVisitDetails(womanDetail, true);
        Mockito.verify(sqLiteDatabase).update(Mockito.eq("ec_mother_details"), (ContentValues) updateContactVisitDetailArgumentCaptor.capture(),
                (String) updateContactVisitDetailArgumentCaptor.capture(), (String[]) updateContactVisitDetailArgumentCaptor.capture());
        ContentValues result = (ContentValues) updateContactVisitDetailArgumentCaptor.getAllValues().get(0);
        Assert.assertEquals(womanDetail.getRedFlagCount(), result.get(DBConstantsUtils.KeyUtils.RED_FLAG_COUNT));
        Assert.assertEquals(womanDetail.getYellowFlagCount(), result.get(DBConstantsUtils.KeyUtils.YELLOW_FLAG_COUNT));
        Assert.assertEquals(womanDetail.getNextContact(), result.get(DBConstantsUtils.KeyUtils.NEXT_CONTACT));
    }

    @Test
    public void testUpdateWomanAlertStatusShouldPassCorrectArgsToUpdateDb() {
        ReflectionHelpers.setStaticField(DrishtiApplication.class, "mInstance", drishtiApplication);
        PowerMockito.when(drishtiApplication.getRepository()).thenReturn(repository);
        PowerMockito.when(repository.getWritableDatabase()).thenReturn(sqLiteDatabase);
        PatientRepository.updateWomanAlertStatus("123-23", "red");
        Mockito.verify(sqLiteDatabase).update(Mockito.eq("ec_mother_details"), (ContentValues) updateWomanAlertStatusArgumentCaptor.capture(),
                (String) updateWomanAlertStatusArgumentCaptor.capture(), (String[]) updateWomanAlertStatusArgumentCaptor.capture());
        ContentValues result = (ContentValues) updateWomanAlertStatusArgumentCaptor.getAllValues().get(0);
        Assert.assertEquals("red", result.get(DBConstantsUtils.KeyUtils.CONTACT_STATUS));
    }

    @Test
    public void testUpdateEDDDateShouldPassCorrectArgsToUpdateDb() {
        ReflectionHelpers.setStaticField(DrishtiApplication.class, "mInstance", drishtiApplication);
        PowerMockito.when(drishtiApplication.getRepository()).thenReturn(repository);
        PowerMockito.when(repository.getWritableDatabase()).thenReturn(sqLiteDatabase);
        PatientRepository.updateEDDDate("123-23", null);
        Mockito.verify(sqLiteDatabase).update(Mockito.eq("ec_mother_details"), (ContentValues) updateEDDDateArgumentCaptor.capture(),
                (String) updateEDDDateArgumentCaptor.capture(), (String[]) updateEDDDateArgumentCaptor.capture());
        ContentValues result = (ContentValues) updateEDDDateArgumentCaptor.getAllValues().get(0);
        Assert.assertNull(result.get(DBConstantsUtils.KeyUtils.EDD));
    }

    @Test
    public void testUpdateContactVisitStartDateShouldPassCorrectArgsToUpdateDb() {
        ReflectionHelpers.setStaticField(DrishtiApplication.class, "mInstance", drishtiApplication);
        PowerMockito.when(drishtiApplication.getRepository()).thenReturn(repository);
        PowerMockito.when(repository.getWritableDatabase()).thenReturn(sqLiteDatabase);
        PatientRepository.updateContactVisitStartDate("123-23", null);
        Mockito.verify(sqLiteDatabase).update(Mockito.eq("ec_mother_details"), (ContentValues) updateContactVisitStartDate.capture(),
                (String) updateContactVisitStartDate.capture(), (String[]) updateEDDDateArgumentCaptor.capture());
        ContentValues result = (ContentValues) updateContactVisitStartDate.getAllValues().get(0);
        Assert.assertNull(result.get(DBConstantsUtils.KeyUtils.VISIT_START_DATE));
    }

    @After
    public void tearDown() {
        ReflectionHelpers.setStaticField(AncLibrary.class, "instance", null);
        ReflectionHelpers.setStaticField(DrishtiApplication.class, "mInstance", null);
    }

}
