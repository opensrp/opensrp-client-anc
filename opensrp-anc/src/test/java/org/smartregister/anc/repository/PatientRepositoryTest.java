package org.smartregister.anc.repository;

import android.content.ContentValues;

import junit.framework.Assert;

import net.sqlcipher.database.SQLiteDatabase;

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
import org.smartregister.Context;
import org.smartregister.anc.application.AncApplication;
import org.smartregister.repository.Repository;

import java.util.Map;

/**
 * Created by ndegwamartin on 14/07/2018.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({PatientRepository.class, AncApplication.class, SQLiteDatabase.class})
public class PatientRepositoryTest {

    @Mock
    private AncApplication ancApplication;

    @Mock
    private Context context;

    @Mock
    private Repository repository;

    @Mock
    private SQLiteDatabase sqLiteDatabase;

    protected static final String DUMMY_BASE_ENTITY_ID = "00ts-ime-hcla-0tib-0eht-ma0i";

    @Before
    public void setUp() {

        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testPatientRepositoryInstantiatesCorrectly() {

        PatientRepository patientRepository = new PatientRepository();
        Assert.assertNotNull(patientRepository);

        Map<String, String> womanProfileDetails = PatientRepository.getWomanProfileDetails(DUMMY_BASE_ENTITY_ID);
        Assert.assertNull(womanProfileDetails);

    }

    @Test
    public void testUpdateWomanDetailsInvokesUpdateMethodOfWritableDatabase() {

        PowerMockito.mockStatic(AncApplication.class);
        PowerMockito.when(AncApplication.getInstance()).thenReturn(ancApplication);
        PowerMockito.when(ancApplication.getContext()).thenReturn(context);


        PatientRepository spy = PowerMockito.spy(new PatientRepository());

        PowerMockito.when(spy.getMasterRepository()).thenReturn(repository);


        PowerMockito.when(spy.getMasterRepository().getWritableDatabase()).thenReturn(sqLiteDatabase);

        PowerMockito.when(spy.getMasterRepository().getWritableDatabase().update(ArgumentMatchers.anyString(), ArgumentMatchers.any(ContentValues.class), ArgumentMatchers.anyString(), ArgumentMatchers.eq(new String[]{DUMMY_BASE_ENTITY_ID}))).thenReturn(1);


        spy.updateWomanProfileDetails(DUMMY_BASE_ENTITY_ID);

        Mockito.verify(sqLiteDatabase, Mockito.times(1)).update(ArgumentMatchers.anyString(), ArgumentMatchers.any(ContentValues.class), ArgumentMatchers.anyString(), ArgumentMatchers.eq(new String[]{DUMMY_BASE_ENTITY_ID}));
    }
}
