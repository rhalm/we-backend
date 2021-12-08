package hu.rhalm.wasteless.feed.data;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.validation.constraints.NotNull;
import java.net.URL;
import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
public class ImageEntity {
    @Id
    private UUID id = UUID.randomUUID();

    @NotNull
    private UUID assetId;

    private String originalAssetName;

    private String fileType;

    @Column(columnDefinition = "TEXT")
    private URL signedUrl;

    private Instant signedUrlExpiry;

    @NotNull
    private Instant createdAt = Instant.now();
}
