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
import java.util.stream.IntStream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    @Autowired
    EventRepository eventRepository;

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
                .andExpect(jsonPath("id").value(Matchers.not(100)))
                .andExpect(jsonPath("_links.sef").exists())
                .andExpect(jsonPath("_links.query-events").exists())
                .andExpect(jsonPath("_links.update").exists());
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
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].objectName").exists())
                .andExpect(jsonPath("$[0].defaultMessage").exists());
    }

    @Test
    @DisplayName("30개의 이벤트를 10개씩 두번쨰 페이지 조회하기")
    public void queryEvents() throws Exception {
        // Given
        IntStream.range(0,30).forEach(i ->{
            this.generateEvent(i);
        });

        // When
        this.mockMvc.perform(get("/api/events")
                    .param("page", "1") // 1번 페이지
                    .param("size", "10") //한 페이지당 개수
                    .param("sort", "name,DESC"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("_links.self").exists());
    }

    private void generateEvent(int index) {
        Event event = Event.builder()
                .name("event" + index)
                .description("test event")
                .build();

        this.eventRepository.save(event);
    }
}
