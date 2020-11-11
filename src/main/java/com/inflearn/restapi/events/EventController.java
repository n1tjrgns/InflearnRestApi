package com.inflearn.restapi.events;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

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
        WebMvcLinkBuilder selfLinkBuilder =linkTo(EventController.class).slash(newEvent.getId());
        URI createdUri = selfLinkBuilder.toUri();
        EventResource eventResource = new EventResource(event);
        eventResource.add(linkTo(EventController.class).withRel("query-events"));
        eventResource.add(selfLinkBuilder.withSelfRel());
        eventResource.add(selfLinkBuilder.withRel("update-event"));
        return ResponseEntity.created(createdUri).body(eventResource);
    }

    @GetMapping
    public ResponseEntity queryEvents(Pageable pageable, PagedResourcesAssembler<Event> assembler){
        Page<Event> page = this.eventRepository.findAll(pageable);
        PagedModel<EntityModel<Event>> pagedResources = assembler.toModel(page);
        return ResponseEntity.ok(pagedResources);
    }

    @GetMapping("/{id}")
    public ResponseEntity getEvent(@PathVariable Integer id){
        Optional<Event> optionalEvent = this.eventRepository.findById(id);
        if (optionalEvent.isPresent()){ //isEmpty가 왜 안되지
            return ResponseEntity.notFound().build();
        }

        Event event = optionalEvent.get();
        EventResource eventResource = new EventResource(event);

        return ResponseEntity.ok(eventResource);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateEvent(@PathVariable Integer id,
                                      @RequestBody @Valid EventDto eventDto,
                                      Errors errors){
        Optional<Event> byId = this.eventRepository.findById(id);
        if (byId.isEmpty()){
            return ResponseEntity.notFound().build();
        }

        if (errors.hasErrors()){
            return badRequest(errors);
        }

        Event existEvent = byId.get();
        this.modelMapper.map(eventDto, existEvent);
        Event savedEvent = this.eventRepository.save(existEvent);

        EventResource eventResource = new EventResource(savedEvent);
        ResponseEntity.ok(eventResource);
    }

    private ResponseEntity badRequest(Errors errors) {
        return ResponseEntity.badRequest().body(new EntityModel<>(errors));
    }
}
