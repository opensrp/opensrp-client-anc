package org.smartregister.anc.library.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.smartregister.anc.library.AncLibrary;
import org.smartregister.anc.library.contract.RegisterFragmentContract;
import org.smartregister.anc.library.repository.RegisterQueryProvider;
import org.smartregister.anc.library.util.ConstantsUtils;
import org.smartregister.anc.library.util.DBConstantsUtils;
import org.smartregister.configurableviews.model.Field;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@RunWith(PowerMockRunner.class)
@PrepareForTest(AncLibrary.class)
public class RegisterFragmentModelTest {

    private RegisterFragmentContract.Model model;

    @Mock
    private AncLibrary ancLibrary;

    @Before
    public void setUp() {
        model = new RegisterFragmentModel();
    }

    @Test
    public void testCountSelect() {

        String mainCondition = "register_id is not null";
        String sql = "SELECT COUNT(*) FROM " + DBConstantsUtils.DEMOGRAPHIC_TABLE_NAME + " WHERE " + mainCondition + " ";

        // With main condition
        String countSelect = model.countSelect(DBConstantsUtils.DEMOGRAPHIC_TABLE_NAME, mainCondition);
        Assert.assertEquals(countSelect, sql);

        // Without main condition
        sql = "SELECT COUNT(*) FROM " + DBConstantsUtils.DEMOGRAPHIC_TABLE_NAME;
        countSelect = model.countSelect(DBConstantsUtils.DEMOGRAPHIC_TABLE_NAME, "");
        Assert.assertEquals(countSelect, sql);

    }

    @Test
    public void testMainSelect() {

        PowerMockito.mockStatic(AncLibrary.class);
        PowerMockito.when(AncLibrary.getInstance()).thenReturn(ancLibrary);
        PowerMockito.when(ancLibrary.getRegisterQueryProvider()).thenReturn(new RegisterQueryProvider());

        String mainCondition = "register_id is not null";
        String sqlQueryWithMainCondition = "Select ec_client.id as _id , ec_client.relationalid , ec_client.last_interacted_with , ec_client.base_entity_id , ec_client.first_name , " +
                "ec_client.last_name , ec_client.register_id , ec_client.dob , ec_mother_details.phone_number , ec_mother_details.alt_name , ec_client.date_removed , " +
                "ec_mother_details.edd , ec_mother_details.red_flag_count , ec_mother_details.yellow_flag_count , ec_mother_details.contact_status , " +
                "ec_mother_details.next_contact , ec_mother_details.next_contact_date , ec_mother_details.last_contact_record_date FROM " + DBConstantsUtils.DEMOGRAPHIC_TABLE_NAME + "  " +
                "join ec_mother_details on ec_client.base_entity_id= ec_mother_details.base_entity_id WHERE " + mainCondition + " ";


        String mainSelect = model.mainSelect(DBConstantsUtils.DEMOGRAPHIC_TABLE_NAME, mainCondition);
        Assert.assertEquals(mainSelect, sqlQueryWithMainCondition);

        String sqlQueryWithOutMainCondition = "Select ec_client.id as _id , ec_client.relationalid , ec_client.last_interacted_with , ec_client.base_entity_id , ec_client.first_name , " +
                "ec_client.last_name , ec_client.register_id , ec_client.dob , ec_mother_details.phone_number , ec_mother_details.alt_name , ec_client.date_removed , " +
                "ec_mother_details.edd , ec_mother_details.red_flag_count , ec_mother_details.yellow_flag_count , ec_mother_details.contact_status , " +
                "ec_mother_details.next_contact , ec_mother_details.next_contact_date , ec_mother_details.last_contact_record_date FROM " + DBConstantsUtils.DEMOGRAPHIC_TABLE_NAME + "  " +
                "join ec_mother_details on ec_client.base_entity_id= ec_mother_details.base_entity_id";

        mainSelect = model.mainSelect(DBConstantsUtils.DEMOGRAPHIC_TABLE_NAME, "");
        Assert.assertEquals(mainSelect, sqlQueryWithOutMainCondition);
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
        Assert.assertEquals(ConstantsUtils.IdentifierUtils.ANC_ID + ":" + ancId, editMap.get(ConstantsUtils.GLOBAL_IDENTIFIER));

    }
}
