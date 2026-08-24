package tarasov.dev.outboxpatternspringbootstarter.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tarasov.dev.outboxpatternspringbootstarter.annotation.Outbox;
import tarasov.dev.outboxpatternspringbootstarter.exception.SerializationException;
import tarasov.dev.outboxpatternspringbootstarter.model.OutboxMessage;
import tarasov.dev.outboxpatternspringbootstarter.repository.OutboxRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OutboxService {

    private final OutboxRepository repository;
    private final ObjectMapper mapper;
    private final KafkaTemplate<String,String> template;

    @Transactional
    public String saveOutboxMessage(
            String eventType,
            Object payload
    ) {
        try {


            OutboxMessage message = new OutboxMessage();
            message.setType(eventType);
            message.setPayload(mapper.writeValueAsString(payload));
            message.setCreatedAt(LocalDateTime.now());
            message.setProcessed(false);

            repository.save(message);

            return mapper.writeValueAsString(message);

        } catch (JsonProcessingException e) {
            throw new SerializationException("Failed serialization");
        }
    }

    @Transactional
    @Scheduled(cron = "0 * * * * *")
    public void shedulingMessage()
    {
        List<OutboxMessage> messages = repository.findAll();
        messages.forEach(message -> template.send(message.getType(), message.toString()));
        messages.forEach(message -> repository.markAsProcessed(message.getId()));
    }
}
