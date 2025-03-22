package ru.noleg.testnexign.service;

import ru.noleg.testnexign.dto.CreateCdrReportRequest;
import ru.noleg.testnexign.dto.CreateCdrReportResponse;

public interface CdrService {
    CreateCdrReportResponse createCdrReport(CreateCdrReportRequest request);
}
