package hu.rhalm.wasteless.messaging;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
public class ConversationCreateDTO {
    @NotNull
    private String receiverId;

    @NotEmpty
    private String title;

    @NotEmpty
    private String firstMessage;
}
