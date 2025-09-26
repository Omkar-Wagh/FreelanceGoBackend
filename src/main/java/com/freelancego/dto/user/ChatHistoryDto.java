package com.freelancego.dto.user;

import com.freelancego.dto.client.JobDto;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

public record ChatHistoryDto(int id, UserDto owner, UserDto opponent, Set<JobDto> jobs, List<ChatDto> chats, OffsetDateTime createdAt) {
}
