package ru.noleg.testnexign.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDateTime;

public record CreateCdrReportRequest(
        @Pattern(regexp = "^[0-9]{10,15}$", message = "Invalid MSISDN format") String msisdn,
        @NotNull LocalDateTime startDate,
        @NotNull LocalDateTime endDate) {
}
