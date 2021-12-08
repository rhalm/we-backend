package hu.rhalm.wasteless.profile.presentation;

import hu.rhalm.wasteless.profile.ProfileCreateDTO;
import hu.rhalm.wasteless.profile.ProfileDTO;
import hu.rhalm.wasteless.profile.ProfileUpdateDTO;
import hu.rhalm.wasteless.profile.data.ProfileEntity;
import hu.rhalm.wasteless.profile.service.ProfileException;
import hu.rhalm.wasteless.profile.service.ProfileNotFoundException;
import hu.rhalm.wasteless.profile.service.ProfileService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.security.Principal;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@RestController
@Slf4j
public class ProfileController {

    private final ProfileService service;
    private final ProfileMapper mapper;

    @GetMapping("/public/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/public")
    public ResponseEntity<Page<ProfileDTO>> getProfiles(
            @RequestParam(required = false) String searchTerm,
            Pageable pageable,
            Principal principal
    ) {
        val currentUserId = (principal != null) ? principal.getName() : "";
        return ResponseEntity.ok(service
                .findByUsernameContaining(Optional.ofNullable(searchTerm), pageable)
                .map(profile -> mapper.entityToDTO(profile, profile.getUserId().equals(currentUserId))));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProfileDTO> createProfile(
            @RequestPart(value = "file", required = false) MultipartFile file,
            @RequestPart("model") @Valid ProfileCreateDTO profileCreationDTO,
            UriComponentsBuilder builder,
            Principal principal
    ) {
        try {
            val userId = principal.getName();
            val created = service.create(userId, profileCreationDTO, Optional.ofNullable(file));

            // build path of new entity and send it back in header
            val uriComponents = builder.path("/profiles/{id}").buildAndExpand(created.getId());
            return ResponseEntity.created(uriComponents.toUri()).body(mapper.entityToDTO(created, true));
        } catch (ProfileException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/public/user/{userId}")
    public ResponseEntity<ProfileDTO> getProfileByUserId(
            @PathVariable String userId,
            Principal principal
    ) {
        val currentUserId = (principal != null) ? principal.getName() : "";
        log.info(currentUserId);

        return service
                .findByUserId(userId)
                .map(entity -> ResponseEntity.ok(
                        mapper.entityToDTO(entity, entity.getUserId().equals(currentUserId))))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/public/{id}")
    public ResponseEntity<ProfileDTO> getProfileById(@PathVariable UUID id, Principal principal) {
        val currentUserId = (principal != null) ? principal.getName() : "";
        return service
                .findById(id)
                .map(entity -> ResponseEntity.ok(
                        mapper.entityToDTO(entity, entity.getUserId().equals(currentUserId))))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/me")
    public ResponseEntity<ProfileDTO> getProfile(Principal principal) {
        return service
                .findByUserId(principal.getName())
                .map(entity -> ResponseEntity.ok(mapper.entityToDTO(entity, true)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping(path = "/me", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProfileDTO> updateProfile(
            @RequestPart(value = "file", required = false) MultipartFile file,
            @RequestPart("model") @Valid ProfileUpdateDTO profileUpdateDTO,
            Principal principal
    ) {
        try {
            String userId = principal.getName();
            ProfileEntity updated = service.update(userId, profileUpdateDTO, Optional.ofNullable(file));
            return ResponseEntity.ok(mapper.entityToDTO(updated, true));
        } catch (ProfileNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (ProfileException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteProfile(Principal principal) {
        try {
            service.delete(principal.getName());
            return ResponseEntity.noContent().build();
        } catch (ProfileException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}