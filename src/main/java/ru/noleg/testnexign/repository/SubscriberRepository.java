package ru.noleg.testnexign.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.noleg.testnexign.entity.Subscriber;

public interface SubscriberRepository extends JpaRepository<Subscriber, Integer> {
    boolean existsByMsisdn(String msisdn);
}
