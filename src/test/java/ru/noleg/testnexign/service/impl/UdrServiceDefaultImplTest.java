package ru.noleg.testnexign.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.noleg.testnexign.dto.UdrRecordDto;
import ru.noleg.testnexign.entity.CdrRecord;
import ru.noleg.testnexign.exception.ServiceException;
import ru.noleg.testnexign.repository.CdrRepository;
import ru.noleg.testnexign.repository.SubscriberRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UdrServiceDefaultImplTest {

    @Mock
    private CdrRepository cdrRepository;
    @Mock
    private SubscriberRepository subscriberRepository;

    @InjectMocks
    private UdrServiceDefaultImpl udrService;

    @Test
    void getUdrBySubscriber_withYearAndMonth_returnValidUdrRecord() {
        String msisdn = "79937898253";
        int year = 2025;
        int month = 3;
        List<CdrRecord> cdrRecords = List.of(
                new CdrRecord(1L, "02", "79694180707", msisdn,
                        LocalDateTime.of(year, month, 1, 10, 0),
                        LocalDateTime.of(year, month, 1, 10, 10)),
                new CdrRecord(2L, "01", msisdn, "79543210360",
                        LocalDateTime.of(2024, 3, 1, 11, 0),
                        LocalDateTime.of(2024, 3, 1, 11, 5))
        );
        when(this.cdrRepository.findAll()).thenReturn(cdrRecords);
        when(this.subscriberRepository.existsByMsisdn(msisdn)).thenReturn(true);

        UdrRecordDto result = this.udrService.getUdrBySubscriber(msisdn, year, month);

        assertThat(result.msisdn()).isEqualTo(msisdn);
        assertThat(result.incomingCall().getFormattedTime()).isEqualTo("00:10:00");
        assertThat(result.outgoingCall().getFormattedTime()).isEqualTo("00:00:00");
    }

    @Test
    void getUdrBySubscriber_withoutYearAndMonth_returnValidUdrRecord() {
        String msisdn = "79937898253";
        List<CdrRecord> cdrRecords = List.of(
                new CdrRecord(1L, "02", "79694180707", msisdn,
                        LocalDateTime.of(2025, 3, 1, 10, 0),
                        LocalDateTime.of(2025, 3, 1, 10, 10)),
                new CdrRecord(2L, "01", msisdn, "79543210360",
                        LocalDateTime.of(2024, 3, 1, 11, 0),
                        LocalDateTime.of(2024, 3, 1, 11, 5))
        );
        when(this.cdrRepository.findAll()).thenReturn(cdrRecords);
        when(this.subscriberRepository.existsByMsisdn(msisdn)).thenReturn(true);

        UdrRecordDto result = this.udrService.getUdrBySubscriber(msisdn, null, null);

        assertThat(result.msisdn()).isEqualTo(msisdn);
        assertThat(result.incomingCall().getFormattedTime()).isEqualTo("00:10:00");
        assertThat(result.outgoingCall().getFormattedTime()).isEqualTo("00:05:00");
    }

    @Test
    void getUdrBySubscriber_withNotExistMsisdn_returnException() {
        String msisdn = "77777777777";
        when(this.subscriberRepository.existsByMsisdn(msisdn)).thenReturn(false);

        assertThrows(ServiceException.class, () -> {
            this.udrService.getUdrBySubscriber(msisdn, null, null);
        });
    }

    @Test
    void getUdrRecords_returnValidListUdrRecord() {
        int year = 2025;
        int month = 3;
        List<CdrRecord> cdrRecords = List.of(
                new CdrRecord(1L, "01", "79694180707", "79937898253",
                        LocalDateTime.of(year, month, 1, 10, 0),
                        LocalDateTime.of(year, month, 1, 10, 10)),
                new CdrRecord(2L, "01", "79937898253", "79543210360",
                        LocalDateTime.of(year, month, 1, 11, 0),
                        LocalDateTime.of(year, month, 1, 11, 5)),
                new CdrRecord(3L, "01", "78005553535", "79543210360",
                        LocalDateTime.of(year, month, 1, 11, 0),
                        LocalDateTime.of(year, month, 1, 11, 5)),
                new CdrRecord(4L, "01", "79543210360", "78005553535",
                        LocalDateTime.of(year, month, 1, 11, 0),
                        LocalDateTime.of(year, month, 1, 11, 8)),
                new CdrRecord(5L, "02", "79543210360", "79694180707",
                        LocalDateTime.of(year, month, 1, 11, 0),
                        LocalDateTime.of(year, month, 1, 11, 9))
        );
        when(this.cdrRepository.findAll()).thenReturn(cdrRecords);

        List<UdrRecordDto> result = this.udrService.getUdrRecords(year, month);

        assertThat(result).hasSize(4);
        assertThat(result)
                .extracting(UdrRecordDto::msisdn)
                .containsExactlyInAnyOrder("79694180707", "79937898253", "79543210360", "78005553535");

        assertThat(result)
                .filteredOn(r -> r.msisdn().equals("79937898253"))
                .singleElement()
                .satisfies(r -> {
                    assertThat(r.incomingCall().getFormattedTime()).isEqualTo("00:10:00");
                    assertThat(r.outgoingCall().getFormattedTime()).isEqualTo("00:05:00");
                });
    }
}