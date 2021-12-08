package hu.rhalm.wasteless.feed.presentation;

import hu.rhalm.wasteless.common.exception.UnauthorizedException;
import hu.rhalm.wasteless.feed.AdCreationDTO;
import hu.rhalm.wasteless.feed.AdDTO;
import hu.rhalm.wasteless.feed.AdUpdateDTO;
import hu.rhalm.wasteless.feed.data.AdEntity;
import hu.rhalm.wasteless.feed.data.CategoryEntity;
import hu.rhalm.wasteless.feed.service.SearchParams;
import hu.rhalm.wasteless.feed.service.CategoryNotFoundException;
import hu.rhalm.wasteless.feed.service.FeedException;
import hu.rhalm.wasteless.feed.service.FeedService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@RestController
public class FeedController {

    private final FeedMapper mapper;
    private final FeedService service;

    @GetMapping("/public/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/public")
    public ResponseEntity<Page<AdDTO>> getAds(
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) Set<String> locations,
            @RequestParam(required = false) Set<String> categories,
            @RequestParam(required = false, defaultValue = "false") boolean withImageOnly,
            @RequestParam(required = false) String userId,
            Pageable pageable
    ) {
        SearchParams params = new SearchParams(searchTerm, locations, categories, withImageOnly, userId);
        return ResponseEntity.ok(service
                .findAds(params, pageable)
                .map(mapper::adToDTO));
    }

    @GetMapping("/public/locations")
    public ResponseEntity<List<String>> getLocations() {
        return ResponseEntity.ok(service
                .findAllLocations());
    }

    @GetMapping("/public/categories")
    public ResponseEntity<List<String>> getCategories() {
        return ResponseEntity.ok(service
                .findAllCategories()
                .stream()
                .map(CategoryEntity::getName)
                .collect(Collectors.toList()));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AdDTO> createAd(
            @RequestPart(value = "file", required = false) MultipartFile[] files,
            @RequestPart(value = "model") @Valid AdCreationDTO adCreationDTO,
            UriComponentsBuilder builder,
            Principal principal
    ) {
        try {
            String userId = principal.getName();
            AdEntity created = service.create(userId, adCreationDTO, files);

            // build path of new entity and send it back in header
            UriComponents uriComponents = builder.path("/feed/{id}").buildAndExpand(created.getId());
            return ResponseEntity.created(uriComponents.toUri()).body(mapper.adToDTO(created));
        } catch (CategoryNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.info(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/public/{id}")
    public ResponseEntity<AdDTO> getAd(@PathVariable UUID id) {
        return service.findAdById(id)
                .map(entity -> ResponseEntity.ok(mapper.adToDTO(entity)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping(path = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AdDTO> updateAd(
            @PathVariable UUID id,
            @RequestPart(value = "model") @Valid AdUpdateDTO adUpdateDTO,
            @RequestPart(value = "file", required = false) MultipartFile[] files,
            Principal principal
    ) {
        try {
            String userId = principal.getName();
            AdEntity updated = service.tryToUpdateAd(id, userId, adUpdateDTO, files);
            return ResponseEntity.ok(mapper.adToDTO(updated));
        } catch (UnauthorizedException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } catch (CategoryNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (FeedException e) {
            log.info(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAd(@PathVariable UUID id, Principal principal) {
        String userId = principal.getName();
        boolean success = service.tryToDeleteAd(id, userId);

        return success
                ? ResponseEntity.noContent().build()
                : new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
}

