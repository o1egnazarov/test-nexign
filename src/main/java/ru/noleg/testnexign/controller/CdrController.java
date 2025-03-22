package ru.noleg.testnexign.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.noleg.testnexign.dto.CreateCdrReportRequest;
import ru.noleg.testnexign.dto.CreateCdrReportResponse;
import ru.noleg.testnexign.service.CdrService;
import ru.noleg.testnexign.service.impl.CdrServiceDefaultImpl;

@RestController
@RequestMapping("/cdr-reports")
public class CdrController {
    private final CdrService cdrService;

    public CdrController(CdrService cdrService) {
        this.cdrService = cdrService;
    }

    @PostMapping
    public ResponseEntity<CreateCdrReportResponse> createCdrReport(@Valid @RequestBody CreateCdrReportRequest createDto) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(this.cdrService.createCdrReport(createDto));
    }
}
