package hu.rhalm.wasteless.messaging.presentation;

import hu.rhalm.wasteless.messaging.ConversationDTO;
import hu.rhalm.wasteless.messaging.MessageCreateDTO;
import hu.rhalm.wasteless.messaging.MessageDTO;
import hu.rhalm.wasteless.messaging.data.MessageEntity;
import hu.rhalm.wasteless.messaging.data.UserConversationEntity;
import org.apache.commons.lang.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface MessagingMapper {
    MessageDTO messageToDTO(MessageEntity messageEntity);

    MessageEntity DTOToMessage(MessageCreateDTO messageCreateDTO);

    @Mapping(source = "conversation.id", target = "id")
    @Mapping(source = "conversation.lastMessage.content", target = "preview",
            qualifiedByName = "previewFromLastMessageContent")
    @Mapping(source = "conversation.lastMessage.createdAt", target = "modifiedAt")
    @Mapping(source = "conversation.createdAt", target = "createdAt")
    @Mapping(source = "conversation.title", target = "title")
    @Mapping(source = "conversation.UCEntities", target = "participants",
            qualifiedByName = "participantsFromUCEntities")
    ConversationDTO userConvToDTO(UserConversationEntity userConv);

    @Named("previewFromLastMessageContent")
    static String previewFromConversation(String content) {
        // TODO: move related constants to a configuration file
        int max_length = 50;
        String trimmed = content.replace('\n', ' ').trim();
        String preview = StringUtils.substring(trimmed, 0, max_length - 1);
        return (trimmed.length() > max_length) ?
                preview + "…" :
                preview;
    }

    @Named("participantsFromUCEntities")
    static List<String> participantsFromUCEntities(Set<UserConversationEntity> ucEntities) {
        return ucEntities.stream()
                .map(UserConversationEntity::getUserId)
                .collect(Collectors.toList());
    }
}
