package com.freelancego.dto.user;

import java.time.OffsetDateTime;

public record ChatHistoryDto(int id, UserDto owner, UserDto opponent, OffsetDateTime createdAt,ChatDto chats) {
}
