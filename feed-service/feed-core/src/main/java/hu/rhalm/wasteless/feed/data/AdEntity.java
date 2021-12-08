package hu.rhalm.wasteless.feed.data;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
public class AdEntity {
    @NotNull
    @Id
    private UUID id = UUID.randomUUID();

    private String userId;

    @NotEmpty
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @OneToMany
    private Set<ImageEntity> images;

    @NotEmpty
    private String location;

    @ManyToOne
    private CategoryEntity category;

    @NotNull
    private Instant createdAt = Instant.now();
}
