package ru.noleg.testnexign.controller;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.noleg.testnexign.dto.UdrRecordDto;
import ru.noleg.testnexign.service.UdrService;

import java.util.List;

@RestController
@Validated
@RequestMapping("/udr-records")
public class UdrController {

    private final UdrService udrService;

    public UdrController(UdrService udrService) {
        this.udrService = udrService;
    }

    @GetMapping()
    public ResponseEntity<List<UdrRecordDto>> getUdrRecords(@RequestParam("year") @Min(2000) @Max(2100) Integer year,
                                                            @RequestParam("month") @Min(1) @Max(12) Integer month) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(this.udrService.getUdrRecords(year, month));
    }

    @GetMapping("/{msisdn}")
    public ResponseEntity<UdrRecordDto> getUdrBySubscriber(
            @PathVariable("msisdn") @Pattern(regexp = "^[0-9]{10,15}$", message = "Invalid MSISDN format") String msisdn,
            @RequestParam(value = "year", required = false) @Min(2000) @Max(2100) Integer year,
            @RequestParam(value = "month", required = false) @Min(1) @Max(12) Integer month) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(this.udrService.getUdrBySubscriber(msisdn, year, month));
    }
}
