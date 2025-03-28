package ru.noleg.testnexign.service.impl;

import org.springframework.stereotype.Service;
import ru.noleg.testnexign.dto.Call;
import ru.noleg.testnexign.dto.UdrRecordDto;
import ru.noleg.testnexign.entity.CdrRecord;
import ru.noleg.testnexign.exception.NotFoundException;
import ru.noleg.testnexign.exception.ServiceException;
import ru.noleg.testnexign.repository.CdrRepository;
import ru.noleg.testnexign.repository.SubscriberRepository;
import ru.noleg.testnexign.service.UdrService;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Сервис UDR для получения информации о звонках абонентов.
 */
@Service
public class UdrServiceDefaultImpl implements UdrService {

    private final CdrRepository cdrRepository;
    private final SubscriberRepository subscriberRepository;

    public UdrServiceDefaultImpl(CdrRepository cdrRepository, SubscriberRepository subscriberRepository) {
        this.cdrRepository = cdrRepository;
        this.subscriberRepository = subscriberRepository;
    }

    /**
     * Получает UDR-запись для конкретного абонента за указанный месяц и год.
     *
     * @param msisdn Номер телефона
     * @param year   Год
     * @param month  Месяц
     * @return UDR-запись абонента
     */
    public UdrRecordDto getUdrBySubscriber(String msisdn, Integer year, Integer month) {
        try {
            if (!this.subscriberRepository.existsByMsisdn(msisdn)) {
                throw new NotFoundException(String.format("Subscriber with Msisdn: %s not found.", msisdn));
            }

            List<CdrRecord> cdrRecords = this.cdrRepository.findAll();

            if (year != null && month != null) {
                cdrRecords = this.filterCdrByDate(year, month, cdrRecords);
            }

            return this.createUdrRecord(msisdn, cdrRecords);
        } catch (Exception e) {
            throw new ServiceException("Error on get udr by subscriber.", e);
        }
    }

    /**
     * Получает список всех UDR-записей за указанный месяц и год.
     *
     * @param year  Год
     * @param month Месяц
     * @return Список UDR-записей
     */
    public List<UdrRecordDto> getUdrRecords(Integer year, Integer month) {
        try {
            List<CdrRecord> cdrRecords = this.cdrRepository.findAll();

            if (year != null && month != null) {
                cdrRecords = this.filterCdrByDate(year, month, cdrRecords);
            }

            Map<String, UdrRecordDto> udrMap = this.getMsisdnUdrRecordDtoMap(cdrRecords);
            return new ArrayList<>(udrMap.values());
        } catch (Exception e) {

            throw new ServiceException("Error on get udr records.", e);
        }
    }

    /**
     * Создаёт map UDR-записей для всех абонентов на основе CDR-записей.
     *
     * @param cdrRecords Список CDR-записей
     * @return Карта с UDR-записями (ключ — msisdn, значение — UDR-запись)
     */
    private Map<String, UdrRecordDto> getMsisdnUdrRecordDtoMap(List<CdrRecord> cdrRecords) {
        Map<String, UdrRecordDto> udrMap = new HashMap<>();

        for (CdrRecord cdr : cdrRecords) {

            String initiator = cdr.getInitiator();
            String receiver = cdr.getReceiver();
            Duration duration = Duration.between(cdr.getStartCall(), cdr.getEndCall());

            if ("01".equals(cdr.getTypeCall())) {
                udrMap.computeIfAbsent(
                        initiator, k -> new UdrRecordDto(k, new Call(0), new Call(0))
                ).outgoingCall().addSeconds(duration.getSeconds());

                udrMap.computeIfAbsent(
                        receiver, k -> new UdrRecordDto(k, new Call(0), new Call(0))
                ).incomingCall().addSeconds(duration.getSeconds());
            }

            if ("02".equals(cdr.getTypeCall())) {
                udrMap.computeIfAbsent(
                        initiator, k -> new UdrRecordDto(k, new Call(0), new Call(0))
                ).outgoingCall().addSeconds(duration.getSeconds());

                udrMap.computeIfAbsent(receiver,
                        k -> new UdrRecordDto(k, new Call(0), new Call(0))
                ).incomingCall().addSeconds(duration.getSeconds());
            }
        }
        return udrMap;
    }

    /**
     * Фильтрует CDR-записи по году и месяцу.
     *
     * @param year       Год
     * @param month      Месяц
     * @param cdrRecords Список CDR-записей
     * @return Отфильтрованный список CDR-записей
     */
    private List<CdrRecord> filterCdrByDate(Integer year, Integer month, List<CdrRecord> cdrRecords) {
        return cdrRecords.stream()
                .filter(cdrRecord -> cdrRecord.getStartCall().getYear() == year &&
                        cdrRecord.getStartCall().getMonthValue() == month)
                .toList();
    }

    /**
     * Создаёт UDR-запись на основе CDR-записей для конкретного абонента.
     *
     * @param msisdn     Номер телефона
     * @param cdrRecords Список CDR-записей
     * @return UDR-запись
     */
    private UdrRecordDto createUdrRecord(String msisdn, List<CdrRecord> cdrRecords) {
        long incomingTotal = cdrRecords.stream()
                .filter(cdrRecord -> cdrRecord.getTypeCall().equals("02") && cdrRecord.getReceiver().equals(msisdn))
                .mapToLong(cdrRecord -> Duration.between(cdrRecord.getStartCall(), cdrRecord.getEndCall()).getSeconds())
                .sum();

        long outgoingTotal = cdrRecords.stream()
                .filter(cdrRecord -> cdrRecord.getTypeCall().equals("01") && cdrRecord.getInitiator().equals(msisdn))
                .mapToLong(cdrRecord -> Duration.between(cdrRecord.getStartCall(), cdrRecord.getEndCall()).getSeconds())
                .sum();

        return new UdrRecordDto(
                msisdn,
                new Call(incomingTotal),
                new Call(outgoingTotal)
        );
    }
}
