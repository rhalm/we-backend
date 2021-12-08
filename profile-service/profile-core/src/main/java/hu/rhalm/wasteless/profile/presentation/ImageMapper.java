package hu.rhalm.wasteless.profile.presentation;

import hu.rhalm.wasteless.common.exception.FailedAssetSignature;
import hu.rhalm.wasteless.common.filestore.FileStore;
import hu.rhalm.wasteless.common.filestore.Folder;
import hu.rhalm.wasteless.common.models.SignedUrl;
import hu.rhalm.wasteless.profile.data.ImageEntity;
import hu.rhalm.wasteless.profile.data.ImageRepository;
import lombok.AllArgsConstructor;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.time.Instant;
import java.util.Optional;

@Component
@AllArgsConstructor
public class ImageMapper {
    private final FileStore fileStore;
    private final ImageRepository imageRepository;

    @Named("signImageUrl")
    public URL signImageUrl(ImageEntity image) throws FailedAssetSignature {
        if (image == null) return null;

        Instant expiry = image.getSignedUrlExpiry();
        if (expiry == null || Instant.now().isAfter(expiry)) {
            Optional<SignedUrl> maybeSignedUrl = fileStore.signedUrl(image.getAssetId(), Folder.profiles);
            maybeSignedUrl.ifPresent(signedUrl -> {
                image.setSignedUrl(signedUrl.getUrl());
                image.setSignedUrlExpiry(signedUrl.getExpiry());
                imageRepository.save(image);
            });
            return maybeSignedUrl
                    .map(SignedUrl::getUrl)
                    .orElseThrow(() -> FailedAssetSignature.fromAssetId(image.getAssetId()));
        } else {
            return image.getSignedUrl();
        }
    }
}
