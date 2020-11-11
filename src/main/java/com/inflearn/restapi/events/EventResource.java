package com.inflearn.restapi.events;


import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;

@Getter
public class EventResource extends RepresentationModel {

    private Event event;

    public EventResource(Event event) {
        this.event = event;
    }
}
