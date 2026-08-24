package tarasov.dev.outboxpatternspringbootstarter.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tarasov.dev.outboxpatternspringbootstarter.model.OutboxMessage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface OutboxRepository extends JpaRepository<OutboxMessage, UUID> {

    List<OutboxMessage> findProcessedFalseAndCreatedAtBeforeOrderByCreatedAtAsc (LocalDateTime createdBefore);

    @Modifying
    @Transactional
    @Query("UPDATE OutboxMessage m SET m.processed = true WHERE m.id = :id")
    void markAsProcessed(UUID id);

}
