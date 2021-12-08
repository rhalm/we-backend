package hu.rhalm.wasteless.profile.service;

import com.google.gson.Gson;
import hu.rhalm.wasteless.common.fileops.FileOps;
import hu.rhalm.wasteless.common.filestore.FileStore;
import hu.rhalm.wasteless.common.filestore.Folder;
import hu.rhalm.wasteless.common.models.SignedUrl;
import hu.rhalm.wasteless.profile.ProfileCreateDTO;
import hu.rhalm.wasteless.profile.ProfileUpdateDTO;
import hu.rhalm.wasteless.profile.data.ImageEntity;
import hu.rhalm.wasteless.profile.data.ImageRepository;
import hu.rhalm.wasteless.profile.data.ProfileEntity;
import hu.rhalm.wasteless.profile.data.ProfileRepository;
import hu.rhalm.wasteless.profile.presentation.ProfileMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class ProfileService {
    private final ProfileRepository profileRepository;
    private final ImageRepository imageRepository;
    private final ProfileMapper mapper;
    private final FileStore fileStore;

    private ImageEntity uploadImage(MultipartFile file) throws IOException {
        return uploadImage(file.getInputStream(), FileOps.fileName(file).orElse(null), FileOps.extension(file));
    }


    private ImageEntity mockImage(UUID assetId, String originalName, String fileType) {
        ImageEntity image = new ImageEntity();
        image.setAssetId(assetId);
        image.setOriginalAssetName(originalName);
        image.setFileType(fileType);
        imageRepository.save(image);
        return image;
    }

    private ImageEntity uploadImage(InputStream stream, String assetName, String fileType) throws IOException {
        UUID fileId = fileStore.upload(stream, Folder.profiles);
        ImageEntity image = new ImageEntity();
        image.setAssetId(fileId);
        image.setOriginalAssetName(assetName);
        image.setFileType(fileType);

        Optional<SignedUrl> maybeSignedUrl = fileStore.signedUrl(image.getAssetId(), Folder.ads);
        maybeSignedUrl.ifPresent(signedUrl -> {
                    image.setSignedUrl(signedUrl.getUrl());
                    image.setSignedUrlExpiry(signedUrl.getExpiry());
                });

        imageRepository.save(image);
        return image;
    }

    @PostConstruct
    public void mock() throws IOException {
        Gson gson = new Gson();

        InputStream profileInputStream = new ClassPathResource("/mocks/profiles.json").getInputStream();
        InputStreamReader profileReader = new InputStreamReader(profileInputStream, StandardCharsets.UTF_8);
        ProfileEntity[] profiles = gson.fromJson(profileReader, ProfileEntity[].class);

        Arrays.stream(profiles).forEach(profileRepository::save);
    }

    public Page<ProfileEntity> findByUsernameContaining(Optional<String> searchTerm, Pageable pageable) {
        return searchTerm
                .map(value -> profileRepository.findByUsernameContaining(value, pageable))
                .orElseGet(() -> profileRepository.findAll(pageable));
    }

    public Optional<ProfileEntity> findByUserId(String userId) {
        return profileRepository.findByUserId(userId);
    }

    public Optional<ProfileEntity> findById(UUID id) {
        return profileRepository.findById(id);
    }

    public ProfileEntity create(String userId, ProfileCreateDTO profile, Optional<MultipartFile> maybeFile) {
        ProfileEntity created = mapper.entityFromUserIdAndCreateDTO(userId, profile);

        maybeFile.ifPresent(file -> {
            try {
                created.setImage(uploadImage(file));
            } catch (IOException e) {
                throw new ProfileException("Couldn't upload maybeFile");
            }
        });

        return profileRepository.save(created);
    }

    public ProfileEntity update(String userId, ProfileUpdateDTO profile, Optional<MultipartFile> maybeFile) {
        ProfileEntity updated = profileRepository.findByUserId(userId)
                .orElseThrow(ProfileNotFoundException::new);

        updated.setIntroduction(profile.getIntroduction());

        maybeFile.ifPresent(file -> {
            try {
                updated.setImage(uploadImage(file));
            } catch (IOException e) {
                throw new ProfileException("Couldn't upload maybeFile");
            }
        });

        return profileRepository.save(updated);
    }

    public void delete(String userId) {
        profileRepository.deleteByUserId(userId);
    }
}
