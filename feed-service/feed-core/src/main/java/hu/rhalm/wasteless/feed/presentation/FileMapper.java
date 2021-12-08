package hu.rhalm.wasteless.feed.presentation;

import hu.rhalm.wasteless.common.exception.FailedAssetSignature;
import hu.rhalm.wasteless.common.filestore.Folder;
import hu.rhalm.wasteless.common.models.SignedUrl;
import hu.rhalm.wasteless.feed.data.ImageEntity;
import hu.rhalm.wasteless.common.filestore.FileStore;
import hu.rhalm.wasteless.feed.data.ImageRepository;
import lombok.AllArgsConstructor;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class FileMapper {
    private final ImageRepository imageRepository;
    private final FileStore fileStore;

    @Named("imageToURL")
    public List<URL> imageToUrl(Set<ImageEntity> images) {
        return images
                .stream()
                .map(entity -> {
                    Instant expiry = entity.getSignedUrlExpiry();
                    if (expiry == null || Instant.now().isAfter(expiry)) {
                        Optional<SignedUrl> maybeSignedUrl = fileStore.signedUrl(entity.getAssetId(), Folder.ads);
                        maybeSignedUrl.ifPresent(signedUrl -> {
                            entity.setSignedUrl(signedUrl.getUrl());
                            entity.setSignedUrlExpiry(signedUrl.getExpiry());
                            imageRepository.save(entity);
                        });
                        return maybeSignedUrl
                                .map(SignedUrl::getUrl)
                                .orElseThrow(() -> FailedAssetSignature.fromAssetId(entity.getAssetId()));
                    } else {
                        return entity.getSignedUrl();
                    }
                })
                .collect(Collectors.toList());
    }
}
