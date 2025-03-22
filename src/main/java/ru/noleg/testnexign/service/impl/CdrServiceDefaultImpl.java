package ru.noleg.testnexign.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.noleg.testnexign.dto.CreateCdrReportRequest;
import ru.noleg.testnexign.dto.CreateCdrReportResponse;
import ru.noleg.testnexign.entity.CdrRecord;
import ru.noleg.testnexign.exception.ServiceException;
import ru.noleg.testnexign.repository.CdrRepository;
import ru.noleg.testnexign.service.CdrService;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
/**
 * Сервис CDR для генерации отчётов по звонкам.
 */
@Service
public class CdrServiceDefaultImpl implements CdrService {

    private static final String CSV_HEADER = "TYPE_CALL,OUTGOING_NUMBER,INCOMING_NUMBER,START_CALL,END_CALL\n";
    private static final String CSV_SEP = ",";

    @Value("${report.directory}")
    private String reportDirectory;
    private final CdrRepository cdrRepository;

    public CdrServiceDefaultImpl(CdrRepository cdrRepository) {
        this.cdrRepository = cdrRepository;
    }

    /**
     * Создаёт отчёт CDR и сохраняет его в виде CSV-файла.
     * @param createRequest Запрос на создание отчёта
     * @return Ответ с идентификатором отчёта
     */
    public CreateCdrReportResponse createCdrReport(CreateCdrReportRequest createRequest) {
        try {

            String msisdn = createRequest.msisdn();
            LocalDateTime startDate = createRequest.startDate();
            LocalDateTime endDate = createRequest.endDate();

            List<CdrRecord> cdrRecords = this.getCdrByMsisdn(msisdn, startDate, endDate);

            UUID id = UUID.randomUUID();
            Path filePath = this.getPath(msisdn, id);
            this.writeToCsv(filePath.toFile(), cdrRecords);

            return new CreateCdrReportResponse(id);
        } catch (IOException e) {
            throw new ServiceException("Service error on create cdr report.", e);
        }
    }
    /**
     * Создает путь для сохранения CDR-отчёта.
     * @param msisdn Номер телефона
     * @param id Уникальный идентификатор отчёта
     * @return Путь к файлу отчёта
     * @throws IOException В случае ошибки создания директории
     */
    private Path getPath(String msisdn, UUID id) throws IOException {
        Path reportDir = Paths.get(reportDirectory);
        if (!Files.exists(reportDir)) {
            Files.createDirectories(reportDir);
        }

        String fileName = msisdn + "_" + id + ".csv";
        return reportDir.resolve(fileName);
    }
    /**
     * Фильтрует CDR-записи по номеру телефона и временным границам.
     * @param msisdn Номер телефона
     * @param startDate Начальная дата
     * @param endDate Конечная дата
     * @return Список CDR-записей
     */
    private List<CdrRecord> getCdrByMsisdn(String msisdn, LocalDateTime startDate, LocalDateTime endDate) {
        return cdrRepository.findAll()
                .stream()
                .filter(cdrRecord -> cdrRecord.getStartCall().isAfter(startDate) &&
                        cdrRecord.getEndCall().isBefore(endDate))
                .filter(cdrRecord -> cdrRecord.getReceiver().equals(msisdn) ||
                        cdrRecord.getInitiator().equals(msisdn))
                .toList();
    }
    /**
     * Записывает CDR-записи в CSV-файл.
     * @param file Файл для записи
     * @param cdrRecords Список CDR-записей
     * @throws IOException В случае ошибки записи файла
     */
    private void writeToCsv(File file, List<CdrRecord> cdrRecords) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {

            writer.write(CSV_HEADER);
            cdrRecords.forEach(cdrRecord -> {
                try {

                    writer.write(this.convertCdrRecordToCsv(cdrRecord));
                } catch (IOException e) {

                    throw new RuntimeException(e);
                }
            });
        }
    }
    /**
     * Конвертирует CDR-запись в строку CSV-формата.
     * @param cdrRecord CDR-запись
     * @return Строка CSV
     */
    private String convertCdrRecordToCsv(CdrRecord cdrRecord) {
        return cdrRecord.getTypeCall() + CSV_SEP
                + cdrRecord.getInitiator() + CSV_SEP
                + cdrRecord.getReceiver() + CSV_SEP
                + cdrRecord.getStartCall() + CSV_SEP
                + cdrRecord.getEndCall() + "\n";
    }
}
