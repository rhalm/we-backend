package hu.rhalm.wasteless.messaging.data;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MessageRepository extends JpaRepository<MessageEntity, UUID> {
    Page<MessageEntity> findByConversationId(UUID conversationId, Pageable pageable);
}