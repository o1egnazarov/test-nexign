package ru.noleg.testnexign.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.noleg.testnexign.dto.CreateCdrReportRequest;
import ru.noleg.testnexign.dto.CreateCdrReportResponse;
import ru.noleg.testnexign.entity.CdrRecord;
import ru.noleg.testnexign.exception.ServiceException;
import ru.noleg.testnexign.repository.CdrRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CdrServiceDefaultImplTest {

    @Mock
    private CdrRepository cdrRepository;

    @InjectMocks
    private CdrServiceDefaultImpl cdrService;

    @Test
    void createCdrReport_returnValidCreateCdrReportResponse_andShouldCreateFileInDirectoryReport() {
        String msisdn = "7777777777";
        LocalDateTime startDate = LocalDateTime.now().minusDays(1);
        LocalDateTime endDate = LocalDateTime.now().plusDays(1);
        CreateCdrReportRequest request = new CreateCdrReportRequest(msisdn, startDate, endDate);

        List<CdrRecord> cdrRecords = List.of(
                new CdrRecord(1L, "02", "79694180707", msisdn,
                        LocalDateTime.now(), LocalDateTime.now().plusMinutes(5)),
                new CdrRecord(2L, "01", msisdn, "79543210360",
                        LocalDateTime.now(), LocalDateTime.now().plusMinutes(3))
        );
        when(this.cdrRepository.findAll()).thenReturn(cdrRecords);

        CreateCdrReportResponse response = this.cdrService.createCdrReport(request);

        assertNotNull(response.id());
        assertTrue(Files.exists(Path.of("report", msisdn + "_" + response.id() + ".csv")));

        verify(this.cdrRepository, times(1)).findAll();
    }
}
