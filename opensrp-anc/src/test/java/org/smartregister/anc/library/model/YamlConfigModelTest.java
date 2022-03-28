package org.smartregister.anc.library.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.smartregister.anc.library.util.DBConstantsUtils;

import java.util.HashMap;
import java.util.Map;

public class YamlConfigModelTest {

    private ContactSummaryModel model;

    @Before
    public void setUp() {
        model = new ContactSummaryModel();
    }

    @Test
    public void testNullDetails() {
        String patientName = model.extractPatientName(null);
        Assert.assertEquals("", patientName);
    }

    @Test
    public void testEmptyDetails() {
        String patientName = model.extractPatientName(new HashMap<String, String>());
        Assert.assertEquals("", patientName);
    }

    @Test
    public void testDetailsContainsFirstNameOnly() {

        String firstNameOnly = "Elly";

        Map<String, String> details = new HashMap<>();
        details.put(DBConstantsUtils.KeyUtils.FIRST_NAME, firstNameOnly);

        String patientName = model.extractPatientName(details);

        Assert.assertEquals(firstNameOnly.trim(), patientName);
    }

    @Test
    public void testDetailsContainsLastNameOnly() {

        String lastNameOnly = "Smith";

        Map<String, String> details = new HashMap<>();
        details.put(DBConstantsUtils.KeyUtils.LAST_NAME, lastNameOnly);

        String patientName = model.extractPatientName(details);

        Assert.assertEquals(lastNameOnly.trim(), patientName);
    }

    @Test
    public void testDetailsContainsFirstNameAndLastName() {

        String fistName = "Elly";
        String lastName = "Smith";

        Map<String, String> details = new HashMap<>();
        details.put(DBConstantsUtils.KeyUtils.FIRST_NAME, fistName);
        details.put(DBConstantsUtils.KeyUtils.LAST_NAME, lastName);


        String patientName = model.extractPatientName(details);

        Assert.assertEquals(fistName.trim() + " " + lastName.trim(), patientName);
    }

}
