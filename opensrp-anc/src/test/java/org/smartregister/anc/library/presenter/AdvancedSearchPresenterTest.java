package org.smartregister.anc.library.presenter;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.anc.library.AncLibrary;
import org.smartregister.anc.library.activity.BaseUnitTest;
import org.smartregister.anc.library.contract.AdvancedSearchContract;
import org.smartregister.anc.library.cursor.AdvancedMatrixCursor;
import org.smartregister.domain.Response;
import org.smartregister.domain.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by keyman on 30/06/2018.
 */
public class AdvancedSearchPresenterTest extends BaseUnitTest {

    @Mock
    private AdvancedSearchContract.View view;

    @Mock
    private AdvancedSearchContract.Interactor interactor;

    @Mock
    private AdvancedSearchContract.Model model;

    private AdvancedSearchContract.Presenter presenter;

    @Mock
    private AncLibrary ancLibrary;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        ReflectionHelpers.setStaticField(AncLibrary.class, "instance", ancLibrary);
        presenter = new AdvancedSearchPresenter(view, "advancedSearch");
    }

    @Test
    public void testLocalSearch() {
        AdvancedSearchPresenter advancedSearchPresenter = (AdvancedSearchPresenter) presenter;
        advancedSearchPresenter.setModel(model);

        String firstName = "first_name";
        String lastName = "last_name";
        String ancId = "anc_id";
        String edd = "edd";
        String dob = "dob";
        String phoneNumber = "phone_number";
        String alternateContact = "alternate_contact";
        boolean isLocal = true;

        String searchCriteria = "SEARCH CRITERIA";

        Map<String, String> editMap = new HashMap<>();
        editMap.put("1", "one");
        editMap.put("2", "two");
        editMap.put("3", "three");

        String mainCondition = "MAIN CONDITION";
        String countSelect = "Select count * from table";
        String mainSelect = "Select * from table";


        Mockito.doReturn(searchCriteria).when(model).createSearchString(firstName, lastName, ancId, edd, dob, phoneNumber, alternateContact);
        Mockito.doReturn(editMap).when(model).createEditMap(firstName, lastName, ancId, edd, dob, phoneNumber, alternateContact, isLocal);
        Mockito.doReturn(mainCondition).when(model).getMainConditionString(editMap);
        Mockito.doReturn(countSelect).when(model).countSelect(AdvancedSearchPresenter.TABLE_NAME, mainCondition);
        Mockito.doReturn(mainSelect).when(model).mainSelect(AdvancedSearchPresenter.TABLE_NAME, mainCondition);

        advancedSearchPresenter.search(firstName, lastName, ancId, edd, dob, phoneNumber, alternateContact, isLocal);

        Mockito.verify(model).createSearchString(firstName, lastName, ancId, edd, dob, phoneNumber, alternateContact);
        Mockito.verify(view).updateSearchCriteria(searchCriteria);
        Mockito.verify(model).createEditMap(firstName, lastName, ancId, edd, dob, phoneNumber, alternateContact, isLocal);
        Mockito.verify(view).showProgressView();
        Mockito.verify(view).switchViews(true);
        Mockito.verify(model).getMainConditionString(editMap);
        Mockito.verify(model).countSelect(AdvancedSearchPresenter.TABLE_NAME, mainCondition);
        Mockito.verify(model).mainSelect(AdvancedSearchPresenter.TABLE_NAME, mainCondition);
        Mockito.verify(view).initializeQueryParams(AdvancedSearchPresenter.TABLE_NAME, countSelect, mainSelect);
        Mockito.verify(view).initializeAdapter(Mockito.anySet());
        Mockito.verify(view).countExecute();
        Mockito.verify(view).filterandSortInInitializeQueries();
        Mockito.verify(view).hideProgressView();
    }

    @Test
    public void testGlobalSearch() {
        AdvancedSearchPresenter advancedSearchPresenter = (AdvancedSearchPresenter) presenter;
        advancedSearchPresenter.setModel(model);
        advancedSearchPresenter.setInteractor(interactor);

        String firstName = "first_name";
        String lastName = "last_name";
        String ancId = "anc_id";
        String edd = "edd";
        String dob = "dob";
        String phoneNumber = "phone_number";
        String alternateContact = "alternate_contact";
        boolean isLocal = false;

        String searchCriteria = "SEARCH CRITERIA";

        Map<String, String> editMap = new HashMap<>();
        editMap.put("1", "one");
        editMap.put("2", "two");
        editMap.put("3", "three");

        Mockito.doReturn(searchCriteria).when(model).createSearchString(firstName, lastName, ancId, edd, dob, phoneNumber, alternateContact);
        Mockito.doReturn(editMap).when(model).createEditMap(firstName, lastName, ancId, edd, dob, phoneNumber, alternateContact, isLocal);

        advancedSearchPresenter.search(firstName, lastName, ancId, edd, dob, phoneNumber, alternateContact, isLocal);

        Mockito.verify(model).createSearchString(firstName, lastName, ancId, edd, dob, phoneNumber, alternateContact);
        Mockito.verify(view).updateSearchCriteria(searchCriteria);
        Mockito.verify(model).createEditMap(firstName, lastName, ancId, edd, dob, phoneNumber, alternateContact, isLocal);
        Mockito.verify(view).showProgressView();
        Mockito.verify(view).switchViews(true);
        Mockito.verify(interactor).search(editMap, advancedSearchPresenter, ancId);
    }

    @Test
    public void testSearchWhenSearchCriteriaIsBlank() {
        AdvancedSearchPresenter advancedSearchPresenter = (AdvancedSearchPresenter) presenter;
        advancedSearchPresenter.setModel(model);
        advancedSearchPresenter.setInteractor(interactor);

        String firstName = "first_name";
        String lastName = "last_name";
        String ancId = "anc_id";
        String edd = "edd";
        String dob = "dob";
        String phoneNumber = "phone_number";
        String alternateContact = "alternate_contact";
        boolean isLocal = false;

        Mockito.doReturn("").when(model).createSearchString(firstName, lastName, ancId, edd, dob, phoneNumber, alternateContact);

        advancedSearchPresenter.search(firstName, lastName, ancId, edd, dob, phoneNumber, alternateContact, isLocal);

        Mockito.verify(model).createSearchString(firstName, lastName, ancId, edd, dob, phoneNumber, alternateContact);

        // Method should return after verifying search criteria is blank
        Mockito.verify(view, Mockito.times(0)).updateSearchCriteria(Mockito.anyString());
    }

    @Test
    public void testSearchWhenSearchCriteriaIsNull() {
        AdvancedSearchPresenter advancedSearchPresenter = (AdvancedSearchPresenter) presenter;
        advancedSearchPresenter.setModel(model);
        advancedSearchPresenter.setInteractor(interactor);

        String firstName = "first_name";
        String lastName = "last_name";
        String ancId = "anc_id";
        String edd = "edd";
        String dob = "dob";
        String phoneNumber = "phone_number";
        String alternateContact = "alternate_contact";
        boolean isLocal = false;

        Mockito.doReturn(null).when(model).createSearchString(firstName, lastName, ancId, edd, dob, phoneNumber, alternateContact);

        advancedSearchPresenter.search(firstName, lastName, ancId, edd, dob, phoneNumber, alternateContact, isLocal);

        Mockito.verify(model).createSearchString(firstName, lastName, ancId, edd, dob, phoneNumber, alternateContact);

        // Method should return after verifying search criteria is null
        Mockito.verify(view, Mockito.times(0)).updateSearchCriteria(Mockito.anyString());
    }

    @Test
    public void testOnGlobalResultsFound() {
        AdvancedSearchPresenter advancedSearchPresenter = (AdvancedSearchPresenter) presenter;
        advancedSearchPresenter.setModel(model);

        AdvancedMatrixCursor matrixCursor = Mockito.mock(AdvancedMatrixCursor.class);

        String payload = "PAYLOAD";
        Response<String> response = new Response<>(ResponseStatus.success, payload);

        Mockito.doReturn(matrixCursor).when(model).createMatrixCursor(response);

        advancedSearchPresenter.onResultsFound(response, BaseUnitTest.WHO_ANC_ID);
        Mockito.verify(model).createMatrixCursor(response);
        Mockito.verify(view).recalculatePagination(matrixCursor);
        Mockito.verify(view).filterandSortInInitializeQueries();
        Mockito.verify(view).hideProgressView();
    }

}
