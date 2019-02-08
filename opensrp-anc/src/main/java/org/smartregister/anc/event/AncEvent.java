package org.smartregister.anc.event;

import org.smartregister.clientandeventmodel.Event;

import java.util.List;

/**
 * Created by ndegwamartin on 07/02/2019.
 */
public class AncEvent extends Event {

    List<Event> events;

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }
}
