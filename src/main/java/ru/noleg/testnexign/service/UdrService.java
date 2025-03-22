package ru.noleg.testnexign.service;

import ru.noleg.testnexign.dto.UdrRecordDto;

import java.util.List;

public interface UdrService {
    UdrRecordDto getUdrBySubscriber(String msisdn, Integer year, Integer month);
    List<UdrRecordDto> getUdrRecords(Integer year, Integer month);
}
