package ru.noleg.testnexign.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.noleg.testnexign.entity.CdrRecord;
import ru.noleg.testnexign.entity.Subscriber;
import ru.noleg.testnexign.repository.CdrRepository;
import ru.noleg.testnexign.repository.SubscriberRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Component
public class CdrGenerator {

    @Value("${generation.count.cdr}")
    private int countOfCdr;

    private final CdrRepository cdrRepository;
    private final SubscriberRepository subscriberRepository;
    private final Random random;


    public CdrGenerator(CdrRepository cdrRepository, SubscriberRepository subscriberRepository, Random random) {
        this.cdrRepository = cdrRepository;
        this.subscriberRepository = subscriberRepository;
        this.random = random;
    }

    @PostConstruct
    public void generate() {

        List<String> numbers = subscriberRepository.findAll()
                .stream()
                .map(Subscriber::getMsisdn)
                .toList();
        LocalDateTime start = LocalDateTime.now().minusYears(1);

        for (int i = 0; i < countOfCdr; i++) {

            String caller = numbers.get(random.nextInt(numbers.size()));
            String callee;
            do {

                callee = numbers.get(random.nextInt(numbers.size()));
            } while (caller.equals(callee));

            LocalDateTime callStart = start
                    .plusDays(random.nextInt(365))
                    .plusHours(random.nextInt(24))
                    .plusMinutes(random.nextInt(60));

            int duration = random.nextInt(300);
            LocalDateTime callEnd = callStart.plusSeconds(duration);

            cdrRepository.save(
                    new CdrRecord(null, random.nextBoolean() ? "01" : "02", caller, callee, callStart, callEnd)
            );
        }
    }
}
