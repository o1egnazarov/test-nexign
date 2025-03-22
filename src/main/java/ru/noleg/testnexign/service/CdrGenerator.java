package ru.noleg.testnexign.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.noleg.testnexign.entity.CdrRecord;
import ru.noleg.testnexign.entity.Subscriber;
import ru.noleg.testnexign.repository.CdrRepository;
import ru.noleg.testnexign.repository.SubscriberRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Component
public class CdrGenerator implements CommandLineRunner {

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

    @Override
    public void run(String... args) {
        this.addSubscribers();
        this.generateCdr();
    }

    public void generateCdr() {
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

    public void addSubscribers() {
        List<Subscriber> subscribers = Arrays.asList(
                new Subscriber("79937898253"),
                new Subscriber("79931125707"),
                new Subscriber("78022310055"),
                new Subscriber("77778889905"),
                new Subscriber("79215433129"),
                new Subscriber("79607843242"),
                new Subscriber("79301110085"),
                new Subscriber("76542317755"),
                new Subscriber("78125553113"),
                new Subscriber("79204188253")
        );
        subscriberRepository.saveAll(subscribers);
    }
}
