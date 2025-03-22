package ru.noleg.testnexign.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.noleg.testnexign.dto.CreateCdrReportRequest;
import ru.noleg.testnexign.dto.CreateCdrReportResponse;
import ru.noleg.testnexign.service.CdrService;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ExtendWith(MockitoExtension.class)
class CdrControllerTest {

    @Mock
    private CdrService cdrService;

    @InjectMocks
    private CdrController cdrController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(this.cdrController).build();
        this.objectMapper = new ObjectMapper().findAndRegisterModules();
    }

    @Test
    void createCdrReport() throws Exception {
        CreateCdrReportRequest request =
                new CreateCdrReportRequest("7777777777", LocalDateTime.now(), LocalDateTime.now().plusMinutes(5));
        UUID reportId = UUID.randomUUID();

        CreateCdrReportResponse response = new CreateCdrReportResponse(reportId);
        when(this.cdrService.createCdrReport(request)).thenReturn(response);

        this.mockMvc.perform(post("/cdr-reports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(reportId.toString()));

        verify(this.cdrService, times(1)).createCdrReport(request);
    }
}