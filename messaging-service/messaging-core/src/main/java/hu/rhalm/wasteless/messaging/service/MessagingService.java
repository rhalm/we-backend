package hu.rhalm.wasteless.messaging.service;

import com.google.gson.Gson;
import hu.rhalm.wasteless.messaging.ConversationCreateDTO;
import hu.rhalm.wasteless.messaging.MessageCreateDTO;
import hu.rhalm.wasteless.messaging.data.*;
import hu.rhalm.wasteless.messaging.presentation.MessagingMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
@Slf4j
public class MessagingService {
    private final MessagingMapper mapper;
    private final MessageRepository msgRepository;
    private final ConversationRepository convRepository;
    private final UserConversationRepository userConvRepository;

    // Uncomment if you have valid mock files set up
    @PostConstruct
    public void mock() throws IOException {
        Gson gson = new Gson();

        InputStream convInputStream = new ClassPathResource("/mocks/conversations.json").getInputStream();
        InputStreamReader adReader = new InputStreamReader(convInputStream, StandardCharsets.UTF_8);
        ConversationEntity[] conversations = gson.fromJson(adReader, ConversationEntity[].class);

        for (ConversationEntity conv : conversations) {
            List<MessageEntity> messages = conv.getMessages();
            Set<UserConversationEntity> ucs = conv.getUCEntities();
            MessageEntity lastMessage = conv.getLastMessage();
            conv.setLastMessage(null);
            conv.setUCEntities(Set.of());

            convRepository.save(conv);
            messages.forEach(msg -> {
                msg.setConversation(conv);
                msgRepository.save(msg);
            });
            ucs.forEach(uc -> {
                uc.setConversation(conv);
                userConvRepository.save(uc);
            });

            conv.setLastMessage(lastMessage);
            conv.setUCEntities(ucs);
            convRepository.save(conv);
        }
    }

    public Page<UserConversationEntity> findConversations(String userId, Pageable pageable) {
        return userConvRepository.findAllByUserId(userId, pageable);
    }

    public UserConversationEntity createConversation(ConversationCreateDTO conversation, String senderId) {
        return saveConversation(senderId, conversation.getReceiverId(), conversation.getTitle(), conversation.getFirstMessage());
    }

    private UserConversationEntity saveConversation(String senderId, String receiverId, String title, String firstMessage) {
        ConversationEntity conversation = new ConversationEntity();
        UUID conversationId = UUID.randomUUID();
        conversation.setId(conversationId);
        conversation.setTitle(title);
        convRepository.save(conversation);

        UserConversationEntity ucSender = saveUserConversation(senderId, true, conversation);
        UserConversationEntity ucReceiver = saveUserConversation(receiverId, false, conversation);
        conversation.setUCEntities(Stream.of(ucSender, ucReceiver).collect(Collectors.toSet()));
        convRepository.save(conversation);

        saveMessage(conversation, senderId, firstMessage);

        return ucSender;
    }

    private UserConversationEntity saveUserConversation(String userId, boolean isSender, ConversationEntity conversation) {
        UserConversationEntity entity = new UserConversationEntity();
        entity.setConversation(conversation);
        entity.setReadByUser(isSender);
        entity.setUserId(userId);

        return userConvRepository.save(entity);
    }

    public Page<MessageEntity> findMessages(String userId, UUID conversationId, Pageable pageable) {
        UserConversationEntity uc = userConvRepository.findByUserIdAndConversationId(userId, conversationId)
                .orElseThrow(ConversationNotFoundException::new);

        // if a successful request was sent for the messages then it is considered read
        uc.setReadByUser(true);
        userConvRepository.save(uc);
        return msgRepository.findByConversationId(conversationId, pageable);
    }

    public MessageEntity createMessage(MessageCreateDTO message, UUID conversationId, String senderId) {
        MessageEntity msg = mapper.DTOToMessage(message);
        ConversationEntity conversation = convRepository.findById(conversationId)
                .orElseThrow(ConversationNotFoundException::new);

        return saveMessage(conversation, senderId, msg.getContent());
    }

    private MessageEntity saveMessage(ConversationEntity conversation, String senderId, String content) {
        // TODO: move related constants to a configuration file
        int max_content_length = 1000;

        if (content.length() > max_content_length)
            throw new MessagingException("Message content length should be under " + max_content_length + " characters");

        MessageEntity message = new MessageEntity();
        message.setConversation(conversation);
        message.setSenderId(senderId);
        message.setContent(content);

        Set<UserConversationEntity> ucs = conversation.getUCEntities();
        for (UserConversationEntity uc : ucs)
            if (!uc.getUserId().equals(senderId)) {
                uc.setReadByUser(false);
                userConvRepository.save(uc);
            }

        msgRepository.save(message);
        conversation.setLastMessage(message);
        val messages = conversation.getMessages();
        messages.add(message);
        conversation.setMessages(messages);
        convRepository.save(conversation);

        return message;
    }

    public Optional<UserConversationEntity> findConversation(String userId, UUID conversationId) {
        return userConvRepository.findByUserIdAndConversationId(userId, conversationId);
    }
}
