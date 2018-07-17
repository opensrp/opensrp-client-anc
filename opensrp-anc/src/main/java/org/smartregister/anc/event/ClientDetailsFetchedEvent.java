package org.smartregister.anc.event;

import java.util.Map;

/**
 * Created by ndegwamartin on 17/07/2018.
 */
public class ClientDetailsFetchedEvent extends BaseEvent {
    Map<String, String> womanClient;

    public Map<String, String> getWomanClient() {
        return womanClient;
    }

    public ClientDetailsFetchedEvent(Map<String, String> client) {
        this.womanClient = client;
    }
}
