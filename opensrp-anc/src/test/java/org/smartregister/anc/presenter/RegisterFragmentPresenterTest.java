package org.smartregister.anc.presenter;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.smartregister.anc.R;
import org.smartregister.anc.activity.BaseUnitTest;
import org.smartregister.anc.contract.AdvancedSearchContract;
import org.smartregister.anc.contract.RegisterFragmentContract;
import org.smartregister.anc.cursor.AdvancedMatrixCursor;
import org.smartregister.anc.util.DBConstants;
import org.smartregister.configurableviews.model.Field;
import org.smartregister.configurableviews.model.View;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.domain.Response;
import org.smartregister.domain.ResponseStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RegisterFragmentPresenterTest extends BaseUnitTest {

    @Mock
    private RegisterFragmentContract.View view;

    @Mock
    private RegisterFragmentContract.Model model;

    @Mock
    private AdvancedSearchContract.Interactor interactor;

    private RegisterFragmentContract.Presenter presenter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        presenter = new RegisterFragmentPresenter(view, "register");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testInitializeQueries() {
        RegisterFragmentPresenter registerFragmentPresenter = (RegisterFragmentPresenter) presenter;
        registerFragmentPresenter.setModel(model);

        String mainCondition = "anc_id is not null";
        String countSelect = countSelect(DBConstants.WOMAN_TABLE_NAME, mainCondition);
        String mainSelect = mainSelect(DBConstants.WOMAN_TABLE_NAME, mainCondition);

        Mockito.doReturn(countSelect).when(model).countSelect(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        Mockito.doReturn(mainSelect).when(model).mainSelect(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        Mockito.doNothing().when(view).initializeQueryParams(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString());

        registerFragmentPresenter.initializeQueries("anc_id is not null");

        Mockito.verify(model).countSelect(DBConstants.WOMAN_TABLE_NAME, mainCondition);
        Mockito.verify(model).mainSelect(DBConstants.WOMAN_TABLE_NAME, mainCondition);

        Mockito.verify(view).initializeQueryParams(DBConstants.WOMAN_TABLE_NAME, countSelect, mainSelect);
        Mockito.verify(view).initializeAdapter(ArgumentMatchers.any((Class<Set<View>>) (Object) Set.class));
        Mockito.verify(view).countExecute();
        Mockito.verify(view).filterandSortInInitializeQueries();
        Mockito.verify(view).refresh();
    }

    @Test
    public void testUpdateInitials() {
        RegisterFragmentPresenter registerFragmentPresenter = (RegisterFragmentPresenter) presenter;
        registerFragmentPresenter.setModel(model);

        String initials = "EK";

        // Null initials
        Mockito.doReturn(null).when(model).getInitials();

        registerFragmentPresenter.updateInitials();

        Mockito.verify(view, Mockito.times(0)).updateInitialsText(initials);

        // Not null initials
        Mockito.doReturn(initials).when(model).getInitials();

        registerFragmentPresenter.updateInitials();

        Mockito.verify(view).updateInitialsText(initials);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testUpdateSortAndFilter() {
        RegisterFragmentPresenter registerFragmentPresenter = (RegisterFragmentPresenter) presenter;
        registerFragmentPresenter.setModel(model);

        String filterText = "FILTER TEXT";
        String sortText = "SORT TEXT";

        List<Field> list = new ArrayList<>();
        list.add(new Field("Here", "hr"));

        Field sort = new Field("Where", "wh");

        String filter = "FILTER";

        Mockito.doReturn(filter).when(view).getString(ArgumentMatchers.anyInt());
        Mockito.doReturn(filterText).when(model).getFilterText(ArgumentMatchers.any((Class<List<Field>>) (Object) List.class), ArgumentMatchers.anyString());
        Mockito.doReturn(sortText).when(model).getSortText(ArgumentMatchers.any(Field.class));

        registerFragmentPresenter.updateSortAndFilter(list, sort);
        Mockito.verify(view).getString(R.string.filter);
        Mockito.verify(model).getFilterText(list, filter);
        Mockito.verify(model).getSortText(sort);

        Mockito.verify(view).updateFilterAndFilterStatus(filterText, sortText);

    }

    @Test
    public void testSearchGlobally() {
        RegisterFragmentPresenter registerFragmentPresenter = (RegisterFragmentPresenter) presenter;
        registerFragmentPresenter.setInteractor(interactor);
        registerFragmentPresenter.setModel(model);

        String ancId = "ANC ID";

        Map<String, String> editMap = new HashMap<>();
        editMap.put("abc", "1");

        Mockito.doReturn(editMap).when(model).createEditMap(ancId);

        registerFragmentPresenter.searchGlobally(ancId);

        Mockito.verify(view).showProgressView();
        Mockito.verify(interactor).search(editMap, registerFragmentPresenter);

    }

    @Test
    public void testOnResultsFound() {
        RegisterFragmentPresenter registerFragmentPresenter = (RegisterFragmentPresenter) presenter;
        registerFragmentPresenter.setModel(model);

        AdvancedMatrixCursor matrixCursor = Mockito.mock(AdvancedMatrixCursor.class);

        Response<String> response = new Response<>(ResponseStatus.success, "Payload");

        Mockito.doReturn(matrixCursor).when(model).createMatrixCursor(response);
	
	    registerFragmentPresenter.onResultsFound(response, BaseUnitTest.WHO_ANC_ID);

        Mockito.verify(model).createMatrixCursor(response);
        Mockito.verify(view).recalculatePagination(matrixCursor);
        Mockito.verify(view).filterandSortInInitializeQueries();
        Mockito.verify(view).refresh();
        Mockito.verify(view).hideProgressView();

    }

    private String countSelect(String tableName, String mainCondition) {
        SmartRegisterQueryBuilder countQueryBuilder = new SmartRegisterQueryBuilder();
        countQueryBuilder.SelectInitiateMainTableCounts(tableName);
        return countQueryBuilder.mainCondition(mainCondition);
    }

    private String mainSelect(String tableName, String mainCondition) {
        SmartRegisterQueryBuilder queryBUilder = new SmartRegisterQueryBuilder();
        String[] columns = new String[]{
                tableName + ".relationalid",
                tableName + "." + DBConstants.KEY.LAST_INTERACTED_WITH,
                tableName + "." + DBConstants.KEY.BASE_ENTITY_ID,
                tableName + "." + DBConstants.KEY.FIRST_NAME,
                tableName + "." + DBConstants.KEY.LAST_NAME,
                tableName + "." + DBConstants.KEY.ANC_ID,
                tableName + "." + DBConstants.KEY.DOB,
                tableName + "." + DBConstants.KEY.DATE_REMOVED};
        queryBUilder.SelectInitiateMainTable(tableName, columns);
        return queryBUilder.mainCondition(mainCondition);
    }
}
