package org.smartregister.anc.library.presenter;

import org.json.JSONArray;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.anc.library.AncLibrary;
import org.smartregister.anc.library.R;
import org.smartregister.anc.library.activity.BaseUnitTest;
import org.smartregister.anc.library.contract.AdvancedSearchContract;
import org.smartregister.anc.library.contract.RegisterFragmentContract;
import org.smartregister.anc.library.cursor.AdvancedMatrixCursor;
import org.smartregister.anc.library.repository.RegisterQueryProvider;
import org.smartregister.anc.library.util.DBConstantsUtils;
import org.smartregister.configurableviews.model.Field;
import org.smartregister.configurableviews.model.RegisterConfiguration;
import org.smartregister.configurableviews.model.View;
import org.smartregister.configurableviews.model.ViewConfiguration;
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

    @Mock
    private RegisterConfiguration configuration;

    @Mock
    private AncLibrary ancLibrary;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        ReflectionHelpers.setStaticField(AncLibrary.class, "instance", ancLibrary);
        presenter = new RegisterFragmentPresenter(view, "register");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testInitializeQueries() {
        ReflectionHelpers.setStaticField(AncLibrary.class, "instance", ancLibrary);
        Mockito.when(ancLibrary.getRegisterQueryProvider()).thenReturn(new RegisterQueryProvider());

        RegisterFragmentPresenter registerFragmentPresenter = (RegisterFragmentPresenter) presenter;
        registerFragmentPresenter.setModel(model);

        String mainCondition = "anc_id is not null";
        String countSelect = countSelect(DBConstantsUtils.DEMOGRAPHIC_TABLE_NAME, mainCondition);
        String mainSelect = mainSelect(DBConstantsUtils.DEMOGRAPHIC_TABLE_NAME, mainCondition);

        Mockito.doReturn(countSelect).when(model).countSelect(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        Mockito.doReturn(mainSelect).when(model).mainSelect(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        Mockito.doNothing().when(view).initializeQueryParams(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString());

        registerFragmentPresenter.initializeQueries("anc_id is not null");

        Mockito.verify(model).countSelect(DBConstantsUtils.DEMOGRAPHIC_TABLE_NAME, mainCondition);
        Mockito.verify(model).mainSelect(DBConstantsUtils.DEMOGRAPHIC_TABLE_NAME, mainCondition);

        Mockito.verify(view).initializeQueryParams(DBConstantsUtils.DEMOGRAPHIC_TABLE_NAME, countSelect, mainSelect);
        Mockito.verify(view).initializeAdapter(ArgumentMatchers.any((Class<Set<View>>) (Object) Set.class));
        Mockito.verify(view).countExecute();
        Mockito.verify(view).filterandSortInInitializeQueries();
        ReflectionHelpers.setStaticField(AncLibrary.class, "instance", null);

    }

    private String countSelect(String tableName, String mainCondition) {
        SmartRegisterQueryBuilder countQueryBuilder = new SmartRegisterQueryBuilder();
        countQueryBuilder.selectInitiateMainTableCounts(tableName);
        return countQueryBuilder.mainCondition(mainCondition);
    }

    private String mainSelect(String tableName, String mainCondition) {
        SmartRegisterQueryBuilder queryBUilder = new SmartRegisterQueryBuilder();
        String[] columns = new String[]{
                tableName + ".relationalid",
                tableName + "." + DBConstantsUtils.KeyUtils.LAST_INTERACTED_WITH,
                tableName + "." + DBConstantsUtils.KeyUtils.BASE_ENTITY_ID,
                tableName + "." + DBConstantsUtils.KeyUtils.FIRST_NAME,
                tableName + "." + DBConstantsUtils.KeyUtils.LAST_NAME,
                tableName + "." + DBConstantsUtils.KeyUtils.ANC_ID,
                tableName + "." + DBConstantsUtils.KeyUtils.DOB,
                tableName + "." + DBConstantsUtils.KeyUtils.DATE_REMOVED};
        queryBUilder.selectInitiateMainTable(tableName, columns);
        return queryBUilder.mainCondition(mainCondition);
    }

    @Test
    public void testViewConfiguration() {
        RegisterFragmentPresenter registerFragmentPresenter = (RegisterFragmentPresenter) presenter;
        registerFragmentPresenter.setModel(model);

        ViewConfiguration viewConfiguration = Mockito.mock(ViewConfiguration.class);
        Mockito.doReturn(viewConfiguration).when(model).getViewConfiguration("register");
        Assert.assertNotNull(viewConfiguration);

        Mockito.doReturn(configuration).when(viewConfiguration).getMetadata();
        Assert.assertNotNull(configuration);
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
        Mockito.verify(interactor).search(editMap, registerFragmentPresenter, ancId);

    }

    @Test
    public void testOnResultsFound() {
        RegisterFragmentPresenter registerFragmentPresenter = (RegisterFragmentPresenter) presenter;
        registerFragmentPresenter.setModel(model);

        AdvancedMatrixCursor matrixCursor = Mockito.mock(AdvancedMatrixCursor.class);
        Response<String> response = new Response<>(ResponseStatus.success, "payload");

        JSONArray jsonArray = new JSONArray().put("test");
        Mockito.doReturn(jsonArray).when(model).getJsonArray(response);
        Mockito.doReturn(matrixCursor).when(model).createMatrixCursor(response);

        registerFragmentPresenter.onResultsFound(response, BaseUnitTest.WHO_ANC_ID);

        Mockito.verify(model).getJsonArray(response);
        Mockito.verify(model).createMatrixCursor(response);
        Mockito.verify(view).recalculatePagination(matrixCursor);
        Mockito.verify(view).filterandSortInInitializeQueries();
        Mockito.verify(view).hideProgressView();

    }

    @Test
    public void showNotFoundPopup() {
        RegisterFragmentPresenter registerFragmentPresenter = (RegisterFragmentPresenter) presenter;
        registerFragmentPresenter.setModel(model);

        AdvancedMatrixCursor matrixCursor = Mockito.mock(AdvancedMatrixCursor.class);
        Response<String> response = new Response<>(ResponseStatus.success, "payload");

        JSONArray jsonArray = new JSONArray();
        Mockito.doReturn(jsonArray).when(model).getJsonArray(response);
        Mockito.doReturn(matrixCursor).when(model).createMatrixCursor(response);

        registerFragmentPresenter.onResultsFound(response, BaseUnitTest.WHO_ANC_ID);

        Mockito.verify(model).getJsonArray(response);
        Mockito.verify(view).showNotFoundPopup(BaseUnitTest.WHO_ANC_ID);
    }
}
