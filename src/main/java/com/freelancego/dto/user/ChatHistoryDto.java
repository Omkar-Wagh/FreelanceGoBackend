package com.freelancego.dto.user;

import java.time.OffsetDateTime;
import java.util.List;

public record ChatHistoryDto(int id, UserDto owner, UserDto opponent, OffsetDateTime createdAt,List<ChatDto> chats) {
}
