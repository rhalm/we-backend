package hu.rhalm.wasteless.messaging.data;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
public class ConversationEntity {
    @NotNull
    @Id
    private UUID id = UUID.randomUUID();

    @NotEmpty
    private String title;

    @NotNull
    private Instant createdAt = Instant.now();

    @OneToOne
    private MessageEntity lastMessage;

    @OneToMany
    @Transient
    private List<MessageEntity> messages = new ArrayList<>();

    @OneToMany(fetch = FetchType.EAGER)
    private Set<UserConversationEntity> UCEntities;
}
