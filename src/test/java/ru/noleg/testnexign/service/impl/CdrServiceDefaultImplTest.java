package ru.noleg.testnexign.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import ru.noleg.testnexign.dto.CreateCdrReportRequest;
import ru.noleg.testnexign.dto.CreateCdrReportResponse;
import ru.noleg.testnexign.entity.CdrRecord;
import ru.noleg.testnexign.repository.CdrRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CdrServiceDefaultImplTest {

    @Mock
    private CdrRepository cdrRepository;

    @InjectMocks
    private CdrServiceDefaultImpl cdrService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(cdrService, "reportDirectory", tempDir.toString());
    }

    @Test
    void testCreateCdrReport() throws IOException {
        String msisdn = "7777777777";
        LocalDateTime startDate = LocalDateTime.of(2025, 2, 1, 0, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 4, 1, 0, 0, 0);
        CreateCdrReportRequest request = new CreateCdrReportRequest(msisdn, startDate, endDate);

        List<CdrRecord> cdrRecords = List.of(
                new CdrRecord(1L, "02", "79694180707", msisdn,
                        LocalDateTime.of(2025, 3, 1, 10, 0),
                        LocalDateTime.of(2025, 3, 1, 10, 10))
        );
        when(this.cdrRepository.findAll()).thenReturn(cdrRecords);

        CreateCdrReportResponse response = cdrService.createCdrReport(request);

        assertThat(response).isNotNull();
        assertThat(response.id()).isNotNull();

        Path filePath = tempDir.resolve(msisdn + "_" + response.id() + ".csv");
        assertTrue(Files.exists(filePath));

        List<String> lines = Files.readAllLines(filePath);

        assertThat(lines).hasSize(2);
        assertThat(lines.get(0)).isEqualTo("TYPE_CALL,OUTGOING_NUMBER,INCOMING_NUMBER,START_CALL,END_CALL");
        assertThat(lines.get(1)).isEqualTo("02,79694180707,7777777777,2025-03-01T10:00,2025-03-01T10:10");
    }
}

