package hu.rhalm.wasteless.messaging.data;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserConversationRepository extends JpaRepository<UserConversationEntity, UUID> {
    Page<UserConversationEntity> findAllByUserId(String userId, Pageable pageable);

    Optional<UserConversationEntity> findByUserIdAndConversationId(String userId, UUID conversationId);
}
