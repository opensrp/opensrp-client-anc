package org.smartregister.anc.event;

import org.smartregister.domain.FetchStatus;

/**
 * Created by ndegwamartin on 14/03/2018.
 */

public class SyncEvent extends BaseEvent {

    private FetchStatus fetchStatus;

    public SyncEvent(FetchStatus fetchStatus) {
        this.fetchStatus = fetchStatus;
    }

    public FetchStatus getFetchStatus() {
        return fetchStatus;
    }
}


