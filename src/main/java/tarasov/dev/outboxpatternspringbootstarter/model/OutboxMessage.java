package tarasov.dev.outboxpatternspringbootstarter.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "outbox-messages")
@RequiredArgsConstructor
@Getter
@Setter
public class OutboxMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Column(nullable = false)
    private boolean processed = false;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}
