package org.smartregister.anc.library.repository;

import android.content.ContentValues;

import net.sqlcipher.database.SQLiteDatabase;

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
import org.smartregister.Context;
import org.smartregister.anc.library.AncLibrary;
import org.smartregister.anc.library.util.ConstantsUtils;
import org.smartregister.repository.Repository;
import org.smartregister.view.activity.DrishtiApplication;

import java.util.Map;

/**
 * Created by ndegwamartin on 14/07/2018.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({PatientRepositoryHelper.class, AncLibrary.class, SQLiteDatabase.class})
public class PatientRepositoryHelperTest {

    protected static final String DUMMY_BASE_ENTITY_ID = "00ts-ime-hcla-0tib-0eht-ma0i";
    @Mock
    private Context context;
    @Mock
    private Repository repository;
    @Mock
    private SQLiteDatabase sqLiteDatabase;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        AncLibrary.init(context, 1);
    }

    @Test
    public void testPatientRepositoryInstantiatesCorrectly() {
        PatientRepositoryHelper patientRepositoryHelper = new PatientRepositoryHelper();
        Assert.assertNotNull(patientRepositoryHelper);

        Map<String, String> womanProfileDetails = PatientRepositoryHelper.getWomanProfileDetails(DUMMY_BASE_ENTITY_ID);
        Assert.assertNull(womanProfileDetails);

    }

    @PrepareForTest({ContentValues.class})
    @Test
    public void testUpdateWomanDetailsInvokesUpdateMethodOfWritableDatabase() {
        PatientRepositoryHelper spy = PowerMockito.spy(new PatientRepositoryHelper());
        PowerMockito.mockStatic(ContentValues.class);

        DrishtiApplication drishtiApplication = Mockito.mock(DrishtiApplication.class);
        ReflectionHelpers.setStaticField(DrishtiApplication.class, "mInstance", drishtiApplication);

        Mockito.doReturn(repository).when(drishtiApplication).getRepository();
        PowerMockito.when(PatientRepositoryHelper.getMasterRepository().getWritableDatabase()).thenReturn(sqLiteDatabase);
        PowerMockito.when(PatientRepositoryHelper.getMasterRepository().getWritableDatabase().update(ArgumentMatchers.anyString(), ArgumentMatchers.any(ContentValues.class), ArgumentMatchers.anyString(), ArgumentMatchers.eq(new String[]{DUMMY_BASE_ENTITY_ID}))).thenReturn(1);
        PatientRepositoryHelper.updateWomanAlertStatus(DUMMY_BASE_ENTITY_ID, ConstantsUtils.AlertStatusUtils.IN_PROGRESS);
        Mockito.verify(sqLiteDatabase, Mockito.times(1)).update(ArgumentMatchers.anyString(), ArgumentMatchers.any(ContentValues.class), ArgumentMatchers.anyString(), ArgumentMatchers.eq(new String[]{DUMMY_BASE_ENTITY_ID}));
    }
}
