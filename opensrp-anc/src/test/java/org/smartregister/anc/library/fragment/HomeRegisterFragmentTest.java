package org.smartregister.anc.library.fragment;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.internal.WhiteboxImpl;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.anc.library.AncLibrary;
import org.smartregister.anc.library.activity.BaseUnitTest;
import org.smartregister.anc.library.repository.RegisterQueryProvider;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter;

import java.util.Arrays;

public class HomeRegisterFragmentTest extends BaseUnitTest {

    @Mock
    private AncLibrary ancLibrary;

    private HomeRegisterFragment homeRegisterFragment;

    @Mock
    private CommonRepository commonRepository;

    @Mock
    private RecyclerViewPaginatedAdapter clientAdapter;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        homeRegisterFragment = Mockito.spy(HomeRegisterFragment.class);
    }

    @Test
    public void testCountExecuteShouldPopulateClientAdapterWithCorrectValues() {
        ReflectionHelpers.setStaticField(AncLibrary.class, "instance", ancLibrary);
        WhiteboxImpl.setInternalState(homeRegisterFragment, "clientAdapter", clientAdapter);
        Mockito.doReturn(commonRepository).when(homeRegisterFragment).commonRepository();
        Mockito.when(commonRepository.countSearchIds(Mockito.anyString())).thenReturn(10);
        Mockito.when(ancLibrary.getRegisterQueryProvider()).thenReturn(new RegisterQueryProvider());
        homeRegisterFragment.countExecute();
        Mockito.verify(clientAdapter, Mockito.times(1)).setTotalcount(Mockito.anyInt());
        Mockito.verify(clientAdapter, Mockito.times(1)).setCurrentlimit(Mockito.anyInt());
        Mockito.verify(clientAdapter, Mockito.times(1)).setCurrentoffset(Mockito.anyInt());
    }

    @Test
    public void testFilterAndSortQuery() throws Exception {
        ReflectionHelpers.setStaticField(AncLibrary.class, "instance", ancLibrary);
        Mockito.when(ancLibrary.getRegisterQueryProvider()).thenReturn(new RegisterQueryProvider());
        Mockito.doReturn(commonRepository).when(homeRegisterFragment).commonRepository();
        Mockito.when(commonRepository.isFts()).thenReturn(true);
        Mockito.when(commonRepository.findSearchIds(Mockito.anyString())).thenReturn(Arrays.asList("2323-23", "546456-234"));
        WhiteboxImpl.setInternalState(homeRegisterFragment, "mainSelect", "");
        WhiteboxImpl.setInternalState(homeRegisterFragment, "clientAdapter", clientAdapter);
        Mockito.when(clientAdapter.getCurrentlimit()).thenReturn(10);
        Mockito.when(clientAdapter.getCurrentoffset()).thenReturn(1);
        String result = WhiteboxImpl.invokeMethod(homeRegisterFragment, "filterAndSortQuery");
        String expected = "Select ec_client.id as _id , first_name , last_name , dob , dob_unknown , ec_mother_details.phone_number , ec_mother_details.alt_name , ec_mother_details.alt_phone_number , ec_client.base_entity_id , ec_client.base_entity_id as _id , register_id , ec_mother_details.reminders , home_address , ec_mother_details.edd , ec_mother_details.contact_status , ec_mother_details.previous_contact_status , ec_mother_details.next_contact , ec_mother_details.next_contact_date , ec_mother_details.visit_start_date , ec_mother_details.red_flag_count , ec_mother_details.yellow_flag_count , ec_mother_details.last_contact_record_date , ec_mother_details.cohabitants , ec_client.relationalid FROM ec_client  join ec_mother_details on ec_client.base_entity_id= ec_mother_details.base_entity_id  where _id IN ('2323-23','546456-234')  order by last_interacted_with DESC";
        Assert.assertEquals(expected, result);
    }
}