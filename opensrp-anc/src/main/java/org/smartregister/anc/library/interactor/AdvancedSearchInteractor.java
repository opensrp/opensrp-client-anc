package org.smartregister.anc.library.interactor;

import androidx.annotation.VisibleForTesting;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.DristhiConfiguration;
import org.smartregister.anc.library.AncLibrary;
import org.smartregister.anc.library.contract.AdvancedSearchContract;
import org.smartregister.anc.library.util.AppExecutors;
import org.smartregister.domain.Response;
import org.smartregister.service.HTTPAgent;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

public class AdvancedSearchInteractor implements AdvancedSearchContract.Interactor {

    public static final String SEARCH_URL = "/rest/search/search";
    private AppExecutors appExecutors;
    private HTTPAgent httpAgent;
    private DristhiConfiguration dristhiConfiguration;

    public AdvancedSearchInteractor() {
        this(new AppExecutors());
    }

    @VisibleForTesting
    AdvancedSearchInteractor(AppExecutors appExecutors) {
        this.appExecutors = appExecutors;
    }

    @Override
    public void search(final Map<String, String> editMap, final AdvancedSearchContract.InteractorCallBack callBack,
                       final String ancId) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                final Response<String> response = globalSearch(editMap);
                appExecutors.mainThread().execute(() -> callBack.onResultsFound(response, ancId));
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

    public DristhiConfiguration getDristhiConfiguration() {
        if (this.dristhiConfiguration == null) {
            this.dristhiConfiguration = AncLibrary.getInstance().getContext().configuration();
        }
        return this.dristhiConfiguration;
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
            this.httpAgent = AncLibrary.getInstance().getContext().getHttpAgent();
        }
        return this.httpAgent;

    }

    public void setHttpAgent(HTTPAgent httpAgent) {
        this.httpAgent = httpAgent;
    }

    public void setDristhiConfiguration(DristhiConfiguration dristhiConfiguration) {
        this.dristhiConfiguration = dristhiConfiguration;
    }
}
