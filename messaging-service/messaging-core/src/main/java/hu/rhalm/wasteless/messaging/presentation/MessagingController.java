package hu.rhalm.wasteless.messaging.presentation;

import hu.rhalm.wasteless.messaging.ConversationCreateDTO;
import hu.rhalm.wasteless.messaging.ConversationDTO;
import hu.rhalm.wasteless.messaging.MessageCreateDTO;
import hu.rhalm.wasteless.messaging.data.MessageEntity;
import hu.rhalm.wasteless.messaging.data.UserConversationEntity;
import hu.rhalm.wasteless.messaging.service.ConversationNotFoundException;
import hu.rhalm.wasteless.messaging.service.MessagingException;
import hu.rhalm.wasteless.messaging.service.MessagingService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.security.Principal;
import java.util.UUID;

@AllArgsConstructor
@RestController
public class MessagingController {

    private final MessagingService service;
    private final MessagingMapper mapper;

    @GetMapping("/public/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok("OK");
    }

    @GetMapping()
    public ResponseEntity<Page<ConversationDTO>> getConversations(Pageable pageable, Principal principal) {
        return ResponseEntity.ok(service
                .findConversations(principal.getName(), pageable)
                .map(mapper::userConvToDTO));
    }

    @PostMapping()
    public ResponseEntity createConversation(
            @RequestBody @Valid ConversationCreateDTO conversation,
            UriComponentsBuilder builder,
            Principal principal) {
        try {
            String userId = principal.getName();
            UserConversationEntity created = service.createConversation(conversation, userId);
            UUID conversationId = created.getConversation().getId();
            UriComponents uriComponents = builder.path("/conversations/{id}").buildAndExpand(conversationId);
            return ResponseEntity.created(uriComponents.toUri()).body(mapper.userConvToDTO(created));
        } catch (MessagingException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{conversationId}")
    public ResponseEntity getConversation(
            @PathVariable UUID conversationId,
            Principal principal) {
        String userId = principal.getName();
        return service.findConversation(userId, conversationId)
                .map(entity -> ResponseEntity.ok(mapper.userConvToDTO(entity)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{conversationId}/messages")
    public ResponseEntity getMessages(
            @PathVariable UUID conversationId,
            Pageable pageable,
            Principal principal) {
        try {
            String userId = principal.getName();
            return ResponseEntity.ok(service
                    .findMessages(userId, conversationId, pageable)
                    .map(mapper::messageToDTO));
        } catch (ConversationNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Conversation not found");
        }
    }

    @PostMapping("/{conversationId}")
    public ResponseEntity createMessage(
            @PathVariable UUID conversationId,
            @RequestBody @Valid MessageCreateDTO message,
            Principal principal) {
        try {
            String userId = principal.getName();
            MessageEntity created = service.createMessage(message, conversationId, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(mapper.messageToDTO(created));
        } catch (ConversationNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Conversation not found");
        } catch (MessagingException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
