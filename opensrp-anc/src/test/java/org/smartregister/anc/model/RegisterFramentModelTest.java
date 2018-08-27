package org.smartregister.anc.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.smartregister.anc.activity.BaseUnitTest;
import org.smartregister.anc.contract.RegisterFragmentContract;
import org.smartregister.anc.util.Constants;
import org.smartregister.anc.util.DBConstants;
import org.smartregister.configurableviews.model.Field;
import org.smartregister.repository.AllSharedPreferences;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RegisterFramentModelTest extends BaseUnitTest {

    private RegisterFragmentContract.Model model;

    @Before
    public void setUp() {
        model = new RegisterFramentModel();
    }

    @Test
    public void testCountSelect() {

        String mainCondition = "anc_id is not null";
        String sql = "SELECT COUNT(*) FROM " + DBConstants.WOMAN_TABLE_NAME + " WHERE " + mainCondition + " ";

        // With main condition
        String countSelect = model.countSelect(DBConstants.WOMAN_TABLE_NAME, mainCondition);
        Assert.assertEquals(countSelect, sql);

        // Without main condition
        sql = "SELECT COUNT(*) FROM " + DBConstants.WOMAN_TABLE_NAME;
        countSelect = model.countSelect(DBConstants.WOMAN_TABLE_NAME, "");
        Assert.assertEquals(countSelect, sql);

    }

    @Test
    public void testMainSelect() {

        String mainCondition = "anc_id is not null";
        String sql = "Select ec_woman.id as _id , ec_woman.relationalid , ec_woman.last_interacted_with , ec_woman.base_entity_id , ec_woman.first_name , ec_woman.last_name , ec_woman.anc_id , ec_woman.dob , ec_woman.phone_number , ec_woman.alt_name , ec_woman.date_removed FROM " + DBConstants.WOMAN_TABLE_NAME + " WHERE " + mainCondition + " ";

        // With main condition
        String mainSelect = model.mainSelect(DBConstants.WOMAN_TABLE_NAME, mainCondition);
        Assert.assertEquals(mainSelect, sql);

        sql = "Select ec_woman.id as _id , ec_woman.relationalid , ec_woman.last_interacted_with , ec_woman.base_entity_id , ec_woman.first_name , ec_woman.last_name , ec_woman.anc_id , ec_woman.dob , ec_woman.phone_number , ec_woman.alt_name , ec_woman.date_removed FROM " + DBConstants.WOMAN_TABLE_NAME;

        // Without main condition
        mainSelect = model.mainSelect(DBConstants.WOMAN_TABLE_NAME, "");
        Assert.assertEquals(mainSelect, sql);
    }

    @Test
    public void testGetInitials() {
        AllSharedPreferences allSharedPreferences = Mockito.mock(AllSharedPreferences.class);

        RegisterFramentModel registerFramentModel = (RegisterFramentModel) model;
        registerFramentModel.setAllSharedPreferences(allSharedPreferences);

        // Foo Bar ==> FB
        String username = "OpenSRP_USER_NAME";
        String preferredName = "Foo Bar";

        Mockito.doReturn(username).when(allSharedPreferences).fetchRegisteredANM();
        Mockito.doReturn(preferredName).when(allSharedPreferences).getANMPreferredName(ArgumentMatchers.anyString());

        String initials = registerFramentModel.getInitials();
        Assert.assertEquals("FB", initials);

        Mockito.verify(allSharedPreferences).fetchRegisteredANM();
        Mockito.verify(allSharedPreferences).getANMPreferredName(username);

    }

    @Test
    public void testGetInitialsFromThreeNames() {
        AllSharedPreferences allSharedPreferences = Mockito.mock(AllSharedPreferences.class);

        RegisterFramentModel registerFramentModel = (RegisterFramentModel) model;
        registerFramentModel.setAllSharedPreferences(allSharedPreferences);

        // Test Foo Bar ==> TF
        String username = "OpenSRP_USER_NAME";
        String preferredName = "Test Foo Bar";

        Mockito.doReturn(username).when(allSharedPreferences).fetchRegisteredANM();
        Mockito.doReturn(preferredName).when(allSharedPreferences).getANMPreferredName(ArgumentMatchers.anyString());

        String initials = registerFramentModel.getInitials();
        Assert.assertEquals("TF", initials);

        Mockito.verify(allSharedPreferences).fetchRegisteredANM();
        Mockito.verify(allSharedPreferences).getANMPreferredName(username);
    }

    @Test
    public void testGetInitialsFromOneNames() {
        AllSharedPreferences allSharedPreferences = Mockito.mock(AllSharedPreferences.class);

        RegisterFramentModel registerFramentModel = (RegisterFramentModel) model;
        registerFramentModel.setAllSharedPreferences(allSharedPreferences);

        // Test ==> T
        String username = "OpenSRP_USER_NAME";
        String preferredName = "Test";

        Mockito.doReturn(username).when(allSharedPreferences).fetchRegisteredANM();
        Mockito.doReturn(preferredName).when(allSharedPreferences).getANMPreferredName(ArgumentMatchers.anyString());

        String initials = registerFramentModel.getInitials();
        Assert.assertEquals("T", initials);

        Mockito.verify(allSharedPreferences).fetchRegisteredANM();
        Mockito.verify(allSharedPreferences).getANMPreferredName(username);
    }

    @Test
    public void testGetInitialsWhenPreferredNameIsEmpty() {
        AllSharedPreferences allSharedPreferences = Mockito.mock(AllSharedPreferences.class);

        RegisterFramentModel registerFramentModel = (RegisterFramentModel) model;
        registerFramentModel.setAllSharedPreferences(allSharedPreferences);

        // Test Foo Bar ==> TF
        String username = "OpenSRP_USER_NAME";

        Mockito.doReturn(username).when(allSharedPreferences).fetchRegisteredANM();
        Mockito.doReturn("").when(allSharedPreferences).getANMPreferredName(ArgumentMatchers.anyString());

        String initials = registerFramentModel.getInitials();
        Assert.assertNull(initials);

        Mockito.verify(allSharedPreferences).fetchRegisteredANM();
        Mockito.verify(allSharedPreferences).getANMPreferredName(username);
    }

    @Test
    public void testGetInitialsWhenAllSharedPreferencesIsNull() {
        RegisterFramentModel registerFramentModel = (RegisterFramentModel) model;
        registerFramentModel.setAllSharedPreferences(null);

        String initials = registerFramentModel.getInitials();
        Assert.assertNull(initials);
    }

    @Test
    public void testGetFilterText() {
        List<Field> list = new ArrayList<>();
        list.add(new Field("filter 1", "f1"));
        list.add(new Field("filter 2", "f2"));
        list.add(new Field("filter 3", "f3"));

        String expected = "<font color=#727272>FILTER</font> <font color=#f0ab41>(3)</font>";
        String filterText = model.getFilterText(list, "FILTER");
        Assert.assertEquals(expected, filterText);
    }

    @Test
    public void testGetFilterTextWithEmptyList() {
        String expected = "<font color=#727272>FILTER</font> <font color=#f0ab41>(0)</font>";
        String filterText = model.getFilterText(new ArrayList<Field>(), "FILTER");
        Assert.assertEquals(expected, filterText);
    }

    @Test
    public void testGetFilterTextWithNulls() {
        String expected = "<font color=#727272></font> <font color=#f0ab41>(0)</font>";
        String filterText = model.getFilterText(null, null);
        Assert.assertEquals(expected, filterText);
    }

    @Test
    public void testGetSortText() {
        Field field = new Field("Updated At", "updated_at asc");

        String expected = "(Sort: Updated At)";
        String sortText = model.getSortText(field);
        Assert.assertEquals(expected, sortText);
    }

    @Test
    public void testGetSortTextWithNull() {
        String expected = "";
        String sortText = model.getSortText(null);
        Assert.assertEquals(expected, sortText);
    }

    @Test
    public void testGetSortTextWithDisplayBlank() {
        Field field = new Field(null, "updated_at asc");

        String expected = "(Sort: updated_at asc)";
        String sortText = model.getSortText(field);
        Assert.assertEquals(expected, sortText);
    }

    @Test
    public void testGetSortTextWithBlankValues() {
        Field field = new Field("", null);

        String expected = "";
        String sortText = model.getSortText(field);
        Assert.assertEquals(expected, sortText);
    }


    @Test
    public void testCreateEditMap() {
        String ancId = "anc_id";

        Map<String, String> editMap = model.createEditMap(ancId);

        Assert.assertNotNull(editMap);
        Assert.assertEquals(1, editMap.size());

        //TODO Change to OpenSRP_ID
        Assert.assertEquals("OpenSRP_ID:" + ancId, editMap.get(Constants.GLOBAL_IDENTIFIER));

    }
}
