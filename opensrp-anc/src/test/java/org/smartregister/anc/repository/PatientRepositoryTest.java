package org.smartregister.anc.repository;

import junit.framework.Assert;

import org.junit.Test;
import org.smartregister.anc.activity.BaseUnitTest;

import java.util.Map;

/**
 * Created by ndegwamartin on 14/07/2018.
 */
public class PatientRepositoryTest extends BaseUnitTest {

    @Test
    public void testPatientRepositoryInstantiatesCorrectly() {
        PatientRepository patientRepository = new PatientRepository();
        Assert.assertNotNull(patientRepository);

        Map<String, String> womanProfileDetails = PatientRepository.getWomanProfileDetails(DUMMY_BASE_ENTITY_ID);
        Assert.assertNull(womanProfileDetails);

    }
}
