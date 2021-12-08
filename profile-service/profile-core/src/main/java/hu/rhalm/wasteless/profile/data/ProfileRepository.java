package hu.rhalm.wasteless.profile.data;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProfileRepository extends JpaRepository<ProfileEntity, UUID> {

    Page<ProfileEntity> findByUsernameContaining(String searchTerm, Pageable pageable);

    Optional<ProfileEntity> findByUserId(String userId);

    void deleteByUserId(String userId);
}
