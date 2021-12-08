package hu.rhalm.wasteless.messaging;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
public class ConversationDTO {
    @NotNull
    private UUID id;

    @NotNull
    private List<String> participants;

    @NotNull
    private boolean isReadByUser;

    @NotEmpty
    private String title;

    @NotNull
    private Instant modifiedAt;

    @NotNull
    private Instant createdAt;

    @NotEmpty
    private String preview;
}
