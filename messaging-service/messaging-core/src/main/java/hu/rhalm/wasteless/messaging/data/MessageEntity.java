package hu.rhalm.wasteless.messaging.data;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
public class MessageEntity {
    @Id
    private UUID id = UUID.randomUUID();

    @ManyToOne
    @NotNull
    private ConversationEntity conversation;

    @NotNull
    private String senderId;

    @NotEmpty
    @Column(columnDefinition = "TEXT")
    private String content;

    @NotNull
    private Instant createdAt = Instant.now();
}
