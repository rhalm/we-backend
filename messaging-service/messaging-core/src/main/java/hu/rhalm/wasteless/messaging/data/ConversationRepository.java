package hu.rhalm.wasteless.messaging.data;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ConversationRepository extends JpaRepository<ConversationEntity, UUID> {
}
