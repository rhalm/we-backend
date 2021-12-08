package hu.rhalm.wasteless.feed.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AdRepository extends JpaRepository<AdEntity, UUID>, JpaSpecificationExecutor<AdEntity> {
    @Query("SELECT DISTINCT a.location FROM AdEntity a ORDER BY a.location")
    List<String> findDistinctLocation();

    @Transactional
    long deleteByIdAndUserId(UUID id, String userId);

    Optional<AdEntity> findByIdAndUserId(UUID id, String userId);
}
