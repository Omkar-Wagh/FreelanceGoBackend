package com.freelancego.mapper;

import com.freelancego.dto.user.ChatDto;
import com.freelancego.model.ChatMessage;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ChatMapper {

    ChatDto toDTO(ChatMessage message);

    ChatMessage toEntity(ChatDto dto);

    List<ChatDto> toDtoList(List<ChatMessage> messages);
}
