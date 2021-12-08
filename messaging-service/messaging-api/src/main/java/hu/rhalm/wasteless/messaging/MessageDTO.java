package hu.rhalm.wasteless.messaging;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

@Data
public class MessageDTO {
    @NotNull
    private UUID id;

    @NotNull
    private UUID conversationId;

    @NotNull
    private String senderId;

    @NotEmpty
    private String content;

    @NotNull
    private Instant createdAt;
}
