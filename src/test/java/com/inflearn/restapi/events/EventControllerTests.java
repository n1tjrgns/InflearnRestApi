package com.inflearn.restapi.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class EventControllerTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @DisplayName("이벤트 생성 테스트")
    @Test
    public void createEvent() throws Exception {
        EventDto event = EventDto.builder()
                .name("Spring")
                .description("REST API DEV With Spring")
                .beginEventDateTime(LocalDateTime.of(2020,10,31,18,00))
                .closeEnrollmentDateTime(LocalDateTime.of(2020,11,01,13,00))
                .beginEventDateTime(LocalDateTime.of(2020,10,30,18,00))
                .endEventDateTime(LocalDateTime.of(2020,11,01,13,00))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("스벅")
                .build();

        //repository에 save가 호출되면 event를 리턴해줘라
        //Mockito.when(eventRepository.save(event)).thenReturn(event);

        mockMvc.perform(post("/api/events")
            .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(event)))
        .andDo(print())
        .andExpect(status().isCreated())
        .andExpect(jsonPath("id").exists())
        .andExpect(header().exists(HttpHeaders.LOCATION))
        .andExpect(header().string(HttpHeaders.CONTENT_TYPE, String.valueOf(MediaType.APPLICATION_JSON)))
        .andExpect(jsonPath("id").value(Matchers.not(100)));
    }

    @DisplayName("이벤트 생성 배드 리퀘스트 테스트")
    @Test
    public void bad_createEvent() throws Exception {
        Event event = Event.builder()
                .id(100)
                .name("Spring")
                .description("REST API DEV With Spring")
                .beginEventDateTime(LocalDateTime.of(2020,10,31,18,00))
                .closeEnrollmentDateTime(LocalDateTime.of(2020,11,01,13,00))
                .beginEventDateTime(LocalDateTime.of(2020,10,30,18,00))
                .endEventDateTime(LocalDateTime.of(2020,11,01,13,00))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("스벅")
                .build();

        //repository에 save가 호출되면 event를 리턴해줘라
        //Mockito.when(eventRepository.save(event)).thenReturn(event);

        mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }


    @Test
    public void createEvent_Bad_Request_Empty_Input() throws Exception {
        EventDto eventDto = EventDto.builder().build(); //빈 껍데기로 보내기

        this.mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createEvent_Bad_Request_Wrong_Input() throws Exception {
        EventDto eventDto = EventDto.builder()
                .name("Spring")
                .description("REST API DEV With Spring")
                .beginEventDateTime(LocalDateTime.of(2020,10,31,18,00))
                .closeEnrollmentDateTime(LocalDateTime.of(2020,11,01,13,00))
                .beginEventDateTime(LocalDateTime.of(2020,10,30,18,00))
                .endEventDateTime(LocalDateTime.of(2020,11,01,13,00))
                .basePrice(10000)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("스벅")
                .build();

        this.mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andExpect(status().isBadRequest());
    }
}
