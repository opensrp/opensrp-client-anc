package org.smartregister.anc.interactor;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.smartregister.DristhiConfiguration;
import org.smartregister.anc.activity.BaseUnitTest;
import org.smartregister.anc.contract.AdvancedSearchContract;
import org.smartregister.anc.util.AppExecutors;
import org.smartregister.domain.Response;
import org.smartregister.domain.ResponseStatus;
import org.smartregister.service.HTTPAgent;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

/**
 * Created by keyman on 30/06/2018.
 */
public class AdvancedSearchInteractorTest extends BaseUnitTest {

    private AdvancedSearchContract.Interactor interactor;

    @Before
    public void setUp() {
        interactor = new AdvancedSearchInteractor(new AppExecutors(Executors.newSingleThreadExecutor(), Executors.newSingleThreadExecutor(), Executors.newSingleThreadExecutor()));
    }

    @Test
    public void testSearch() {

        DristhiConfiguration configuration = Mockito.mock(DristhiConfiguration.class);
        HTTPAgent httpAgent = Mockito.mock(HTTPAgent.class);

        AdvancedSearchInteractor advancedSearchInteractor = (AdvancedSearchInteractor) interactor;
        advancedSearchInteractor.setDristhiConfiguration(configuration);
        advancedSearchInteractor.setHttpAgent(httpAgent);

        AdvancedSearchContract.InteractorCallBack callBack = Mockito.mock(AdvancedSearchContract.InteractorCallBack.class);

        Map<String, String> editMap = new HashMap<>();
        editMap.put("one", "1");
        editMap.put("two", "2");
        editMap.put("three", "3");

        String baseUrl = "https://baseurl.com";
        String payload = "PAYLOAD";

        Response<String> response = new Response<>(ResponseStatus.success, payload);

        String fullURL = baseUrl + AdvancedSearchInteractor.SEARCH_URL + "?one=1&two=2&three=3";
        Mockito.doReturn(baseUrl).when(configuration).dristhiBaseURL();
        Mockito.doReturn(response).when(httpAgent).fetch(fullURL);

        interactor.search(editMap, callBack);

        Mockito.verify(configuration, Mockito.timeout(ASYNC_TIMEOUT)).dristhiBaseURL();
        Mockito.verify(httpAgent, Mockito.timeout(ASYNC_TIMEOUT)).fetch(fullURL);
        Mockito.verify(callBack, Mockito.timeout(ASYNC_TIMEOUT)).onResultsFound(response);
    }

    @Test
    public void testSearchWithOneParameter() {

        DristhiConfiguration configuration = Mockito.mock(DristhiConfiguration.class);
        HTTPAgent httpAgent = Mockito.mock(HTTPAgent.class);

        AdvancedSearchInteractor advancedSearchInteractor = (AdvancedSearchInteractor) interactor;
        advancedSearchInteractor.setDristhiConfiguration(configuration);
        advancedSearchInteractor.setHttpAgent(httpAgent);

        AdvancedSearchContract.InteractorCallBack callBack = Mockito.mock(AdvancedSearchContract.InteractorCallBack.class);

        Map<String, String> editMap = new HashMap<>();
        editMap.put("one", "1");

        String baseUrl = "https://baseurl.com";
        String payload = "PAYLOAD";

        Response<String> response = new Response<>(ResponseStatus.success, payload);

        String fullURL = baseUrl + AdvancedSearchInteractor.SEARCH_URL + "?one=1";
        Mockito.doReturn(baseUrl).when(configuration).dristhiBaseURL();
        Mockito.doReturn(response).when(httpAgent).fetch(fullURL);

        interactor.search(editMap, callBack);

        Mockito.verify(configuration, Mockito.timeout(ASYNC_TIMEOUT)).dristhiBaseURL();
        Mockito.verify(httpAgent, Mockito.timeout(ASYNC_TIMEOUT)).fetch(fullURL);
        Mockito.verify(callBack, Mockito.timeout(ASYNC_TIMEOUT)).onResultsFound(response);
    }

}

