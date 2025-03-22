package ru.noleg.testnexign.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.noleg.testnexign.dto.Call;
import ru.noleg.testnexign.dto.UdrRecordDto;
import ru.noleg.testnexign.service.UdrService;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UdrControllerTest {

    @Mock
    private UdrService udrService;

    @InjectMocks
    private UdrController udrController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(this.udrController).build();
    }

    @Test
    void getUdrRecords_returnValidListUdrRecord() throws Exception {
        List<UdrRecordDto> expected = List.of(
                new UdrRecordDto("7777777777", new Call(1000), new Call(600)),
                new UdrRecordDto("7888888888", new Call(700), new Call(350))
        );

        when(this.udrService.getUdrRecords(2025, 3)).thenReturn(expected);

        this.mockMvc.perform(get("/udr-records?year=2025&month=3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(expected.size()))
                .andExpect(jsonPath("$[0].msisdn").value("7777777777"))
                .andExpect(jsonPath("$[1].msisdn").value("7888888888"));

        verify(this.udrService, times(1)).getUdrRecords(2025, 3);
    }


    @Test
    void getUdrBySubscriber_returnValidUdrRecord() throws Exception {
        String msisdn = "7777777777";
        UdrRecordDto expected =
                new UdrRecordDto(msisdn, new Call(600), new Call(300));

        when(udrService.getUdrBySubscriber(msisdn, 2025, 3)).thenReturn(expected);

        mockMvc.perform(get("/udr-records/{msisdn}?year=2025&month=3", msisdn))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msisdn").value(msisdn))
                .andExpect(jsonPath("$.incomingCall.totalTime").value("00:10:00"))
                .andExpect(jsonPath("$.outgoingCall.totalTime").value("00:05:00"));

        verify(this.udrService, times(1)).getUdrBySubscriber(msisdn, 2025, 3);
    }
}