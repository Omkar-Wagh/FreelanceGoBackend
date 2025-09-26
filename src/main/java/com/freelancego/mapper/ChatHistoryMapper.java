package com.freelancego.mapper;

import com.freelancego.dto.user.ChatHistoryDto;
import com.freelancego.model.ChatHistory;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class,ChatMapper.class})
public interface ChatHistoryMapper {
    ChatHistoryDto toDTO(ChatHistory history);
    ChatHistory toEntity(ChatHistoryDto dto);
    List<ChatHistoryDto> toDtoList(List<ChatHistory> history);
}
