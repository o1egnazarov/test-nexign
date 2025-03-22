package ru.noleg.testnexign.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.noleg.testnexign.entity.CdrRecord;

import java.time.LocalDateTime;
import java.util.List;

public interface CdrRepository extends JpaRepository<CdrRecord, Long> {
    List<CdrRecord> findByInitiatorAndStartCallBetween(String callerNumber, LocalDateTime start, LocalDateTime end);
    List<CdrRecord> findByStartCallBetween(LocalDateTime start, LocalDateTime end);
}
