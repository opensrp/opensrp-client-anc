package org.smartregister.anc.library.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.smartregister.anc.library.activity.BaseUnitTest;
import org.smartregister.anc.library.contract.RegisterFragmentContract;
import org.smartregister.anc.library.util.Constants;
import org.smartregister.anc.library.util.DBConstants;
import org.smartregister.configurableviews.model.Field;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RegisterFragmentModelTest extends BaseUnitTest {

    private RegisterFragmentContract.Model model;

    @Before
    public void setUp() {
        model = new RegisterFragmentModel();
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
        String sql = "Select ec_woman.id as _id , ec_woman.relationalid , ec_woman.last_interacted_with , ec_woman.base_entity_id , ec_woman.first_name , ec_woman.last_name , ec_woman.anc_id , ec_woman.dob , ec_woman.phone_number , ec_woman.alt_name , ec_woman.date_removed , ec_woman.edd , ec_woman.red_flag_count , ec_woman.yellow_flag_count , ec_woman.contact_status , ec_woman.next_contact , ec_woman.next_contact_date , ec_woman.last_contact_record_date FROM " + DBConstants.WOMAN_TABLE_NAME + " WHERE " + mainCondition + " ";

        // With main condition
        String mainSelect = model.mainSelect(DBConstants.WOMAN_TABLE_NAME, mainCondition);
        Assert.assertEquals(mainSelect, sql);

        sql = "Select ec_woman.id as _id , ec_woman.relationalid , ec_woman.last_interacted_with , ec_woman.base_entity_id , ec_woman.first_name , ec_woman.last_name , ec_woman.anc_id , ec_woman.dob , ec_woman.phone_number , ec_woman.alt_name , ec_woman.date_removed , ec_woman.edd , ec_woman.red_flag_count , ec_woman.yellow_flag_count , ec_woman.contact_status , ec_woman.next_contact , ec_woman.next_contact_date , ec_woman.last_contact_record_date FROM " + DBConstants.WOMAN_TABLE_NAME;

        // Without main condition
        mainSelect = model.mainSelect(DBConstants.WOMAN_TABLE_NAME, "");
        Assert.assertEquals(mainSelect, sql);
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
        Assert.assertEquals(Constants.IDENTIFIER.ANC_ID + ":" + ancId, editMap.get(Constants.GLOBAL_IDENTIFIER));

    }
}
