package hu.rhalm.wasteless.feed.service;

import com.google.gson.Gson;
import hu.rhalm.wasteless.common.exception.UnauthorizedException;
import hu.rhalm.wasteless.common.fileops.FileOps;
import hu.rhalm.wasteless.common.filestore.FileStore;
import hu.rhalm.wasteless.common.filestore.Folder;
import hu.rhalm.wasteless.common.models.SignedUrl;
import hu.rhalm.wasteless.feed.data.ImageEntity;
import hu.rhalm.wasteless.feed.AdCreationDTO;
import hu.rhalm.wasteless.feed.AdUpdateDTO;
import hu.rhalm.wasteless.feed.data.*;
import hu.rhalm.wasteless.feed.presentation.FeedMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.persistence.criteria.Predicate;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

@AllArgsConstructor
@Service
@Slf4j
public class FeedService {

    private final FileStore fileStore;
    private final AdRepository adRepository;
    private final CategoryRepository categoryRepository;
    private final ImageRepository imageRepository;
    private final FeedMapper mapper;

    @PostConstruct
    public void mock() throws IOException {

        Gson gson = new Gson();

        InputStream imageInputStream = new ClassPathResource("/mocks/images.json").getInputStream();
        InputStreamReader imageReader = new InputStreamReader(imageInputStream, StandardCharsets.UTF_8);
        ImageEntity[] images = gson.fromJson(imageReader, ImageEntity[].class);

        InputStream categoryInputStream = new ClassPathResource("/mocks/categories.json").getInputStream();
        InputStreamReader categoryReader = new InputStreamReader(categoryInputStream, StandardCharsets.UTF_8);
        CategoryEntity[] categories = gson.fromJson(categoryReader, CategoryEntity[].class);
        Arrays.stream(categories).forEach(categoryRepository::save);

        InputStream adInputStream = new ClassPathResource("/mocks/ads.json").getInputStream();
        InputStreamReader adReader = new InputStreamReader(adInputStream, StandardCharsets.UTF_8);
        AdEntity[] ads = gson.fromJson(adReader, AdEntity[].class);

        for (int i = 0; i < ads.length; i++) {
            AdEntity ad = ads[i];
            // Uncomment if you have valid mock images set up in images.json
            //ImageEntity image = imageRepository.save(images[i]);
            //ad.setImages(Set.of(image));
            ad.setCategory(categories[i % categories.length]);
            adRepository.save(ad);
        }
    }

    public List<CategoryEntity> findAllCategories() {
        return categoryRepository.findAll();
    }

    public Page<AdEntity> findAds(SearchParams params, Pageable pageable) {
        return adRepository.findAll((root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // searchTerm
            String searchTerm = params.getSearchTerm();
            if (searchTerm != null && !searchTerm.isBlank())
                // for now only search in titles
                predicates.add(builder.like(
                        builder.lower(root.get("title")), builder.lower(builder.literal("%" + searchTerm + "%"))));

            // userId
            String userId = params.getUserId();
            if (userId != null)
                predicates.add(builder.equal(root.get("userId"), userId));

            // locations
            Set<String> locations = params.getLocations();
            if (locations != null && !locations.isEmpty())
                predicates.add(root.get("location").in(locations));

            // categories
            Set<String> categories = params.getCategories();
            if (categories != null && !categories.isEmpty())
                predicates.add(root.get("category").get("name").in(categories));

            // withImageOnly
            if (params.isWithImageOnly()) {
                predicates.add(builder.isNotEmpty(root.get("images")));
            }

            return builder.and(predicates.toArray(new Predicate[0]));
        }, pageable);
    }

    public AdEntity create(String userId, AdCreationDTO adCreationDTO, MultipartFile[] files) {

        AdEntity created = mapper.DTOtoAd(adCreationDTO);

        created.setUserId(userId);
        CategoryEntity category = categoryRepository.findByName(adCreationDTO.getCategory())
                .orElseThrow(CategoryNotFoundException::new);

        created.setCategory(category);

        created = uploadImages(created, files)
                .orElseThrow(FeedException::new);

        return adRepository.save(created);
    }

    public Optional<AdEntity> uploadImages(AdEntity ad, MultipartFile[] files) {
        Set<ImageEntity> images = new HashSet<>();
        if (files != null) {
            try {
                for (MultipartFile file : files) {
                    UUID newFileId = uploadFile(file);
                    ImageEntity image = new ImageEntity();
                    image.setAssetId(newFileId);
                    image.setOriginalAssetName(FileOps.fileName(file).orElse(null));
                    image.setFileType(FileOps.extension(file));

                    Optional<SignedUrl> maybeSignedUrl = fileStore.signedUrl(newFileId, Folder.ads);
                    maybeSignedUrl.ifPresent(signedUrl -> {
                        image.setSignedUrl(signedUrl.getUrl());
                        image.setSignedUrlExpiry(signedUrl.getExpiry());
                    });

                    imageRepository.save(image);
                    images.add(image);
                }
                ad.setImages(images);
                return Optional.of(ad);
            } catch (IOException e) {
                return Optional.empty();
            }
        } else {
            ad.setImages(images);
            return Optional.of(ad);
        }
    }

    public Optional<AdEntity> findAdById(UUID id) {
        return adRepository.findById(id);
    }

    public AdEntity tryToUpdateAd(UUID id, String userId, AdUpdateDTO adDto, MultipartFile[] files) {
        AdEntity updated = adRepository.findByIdAndUserId(id, userId)
                .orElseThrow(UnauthorizedException::new);

        if (adDto.getLocation() != null) updated.setLocation(adDto.getLocation());
        updated.setTitle(adDto.getTitle());
        updated.setDescription(adDto.getDescription());

        if (adDto.getCategory() != null) {
            CategoryEntity category = categoryRepository.findByName(adDto.getCategory())
                    .orElseThrow(CategoryNotFoundException::new);
            updated.setCategory(category);
        }

        if (files != null) {
            updated = uploadImages(updated, files)
                    .orElseThrow(FeedException::new);
        }

        return adRepository.save(updated);
    }

    public boolean tryToDeleteAd(UUID id, String userId) {
        return adRepository.deleteByIdAndUserId(id, userId) == 1;
    }

    public List<String> findAllLocations() {
        return adRepository.findDistinctLocation();
    }

    public UUID uploadFile(MultipartFile file) throws IOException {
        return fileStore.upload(file, Folder.ads);
    }
}
