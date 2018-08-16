package org.smartregister.anc.interactor;

import android.support.annotation.VisibleForTesting;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.DristhiConfiguration;
import org.smartregister.anc.application.AncApplication;
import org.smartregister.anc.contract.AdvancedSearchContract;
import org.smartregister.anc.util.AppExecutors;
import org.smartregister.anc.util.Constants;
import org.smartregister.domain.Response;
import org.smartregister.service.HTTPAgent;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

public class AdvancedSearchInteractor implements AdvancedSearchContract.Interactor {

    private AppExecutors appExecutors;

    private HTTPAgent httpAgent;

    private DristhiConfiguration dristhiConfiguration;
    private String[] whoAncId ;
    private String ancId = "";

    public static final String SEARCH_URL = "/rest/search/search";

    @VisibleForTesting
    AdvancedSearchInteractor(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
    }

    public AdvancedSearchInteractor() {
        this(new AppExecutors());
    }

    @Override
    public void search(final Map<String, String> editMap, final AdvancedSearchContract.InteractorCallBack callBack) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                final Response<String> response = globalSearch(editMap);
                if (!StringUtils.isEmpty(editMap.get(Constants.GLOBAL_IDENTIFIER))){
                    whoAncId = editMap.get(Constants.GLOBAL_IDENTIFIER).split(":",2);
                }
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                    	if (whoAncId != null) { ancId = whoAncId[1]; }
                        callBack.onResultsFound(response, ancId);
                    }
                });
            }
        };

        appExecutors.networkIO().execute(runnable);
    }

    private Response<String> globalSearch(Map<String, String> map) {
        String baseUrl = getDristhiConfiguration().dristhiBaseURL();
        String paramString = "";
        if (!map.isEmpty()) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value)) {
                    value = urlEncode(value);
                    String param = key.trim() + "=" + value.trim();
                    if (StringUtils.isBlank(paramString)) {
                        paramString = "?" + param;
                    } else {
                        paramString += "&" + param;
                    }
                }

            }
        }
        String uri = baseUrl + SEARCH_URL + paramString;

        return getHttpAgent().fetch(uri);
    }


    private String urlEncode(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return value;
        }
    }

    public HTTPAgent getHttpAgent() {
        if (this.httpAgent == null) {
            this.httpAgent = AncApplication.getInstance().getContext().getHttpAgent();
        }
        return this.httpAgent;

    }

    public void setHttpAgent(HTTPAgent httpAgent) {
        this.httpAgent = httpAgent;
    }

    public DristhiConfiguration getDristhiConfiguration() {
        if (this.dristhiConfiguration == null) {
            this.dristhiConfiguration = AncApplication.getInstance().getContext().configuration();
        }
        return this.dristhiConfiguration;
    }

    public void setDristhiConfiguration(DristhiConfiguration dristhiConfiguration) {
        this.dristhiConfiguration = dristhiConfiguration;
    }
}
