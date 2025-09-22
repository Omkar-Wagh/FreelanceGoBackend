package com.freelancego.dto.user;

import java.time.OffsetDateTime;

public record ChatDto(int id, int senderId, int receiverId, String contend, OffsetDateTime timestamp) {
}
