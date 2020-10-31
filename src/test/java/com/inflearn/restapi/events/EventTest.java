package com.inflearn.restapi.events;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import static org.assertj.core.api.Assertions.assertThat;

@WebMvcTest
class EventTest {

    @DisplayName("@Builder가 존재하는지 테스트")
    @Test
    public void builder(){
        Event event = Event.builder().build();
        assertThat(event).isNotNull();
    }
}