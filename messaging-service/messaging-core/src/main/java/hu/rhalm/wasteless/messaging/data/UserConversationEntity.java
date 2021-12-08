package hu.rhalm.wasteless.messaging.data;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Entity
@Getter
@Setter
public class UserConversationEntity {
    @Id
    private UUID id = UUID.randomUUID();

    @NotNull
    private String userId;

    @NotNull
    @ManyToOne
    private ConversationEntity conversation;

    @NotNull
    private boolean isReadByUser;
}
