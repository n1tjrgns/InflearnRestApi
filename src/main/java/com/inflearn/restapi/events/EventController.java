package com.inflearn.restapi.events;

import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;


@Controller
@RequestMapping(value = "/api/events", produces = MediaType.APPLICATION_JSON_VALUE)
public class EventController {

    private final EventRepository eventRepository;

    private final ModelMapper modelMapper;

    private final EventValidator eventValidator;

    public EventController(EventRepository eventRepository, ModelMapper modelMapper, EventValidator eventValidator) {
        this.eventRepository = eventRepository;
        this.modelMapper = modelMapper;
        this.eventValidator = eventValidator;
    }

    @PostMapping
    public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto, Errors errors) {
        if (errors.hasErrors()){
            return ResponseEntity.badRequest().build();
        }

        //validator 검증 코드
        //errors 객체는 자바 빈 객체가 아니기 때문에 직렬화가 불가능하다.
        // ErrorSerializer 라는 JsonComponent를 작성하여 직렬화 커스텀 설정한다.
        eventValidator.validate(eventDto, errors);
        if (errors.hasErrors()){
            return ResponseEntity.badRequest().build();
        }

        Event event = modelMapper.map(eventDto, Event.class);
        Event newEvent = this.eventRepository.save(event);
        //HateOas 메소드
        //URI createdUri = linkTo(methodOn(EventController.class).createEvent()).slash("{id}").toUri();
        URI createdUri = linkTo(EventController.class).slash(newEvent.getId()).toUri();
        return ResponseEntity.created(createdUri).body(newEvent);
    }
}
