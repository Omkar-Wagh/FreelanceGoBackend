package com.freelancego.service.ChatService;

import com.freelancego.dto.user.ChatHistoryDto;

import java.util.List;

public interface ChatHistoryService {
    ChatHistoryDto createChatHistory(int senderId, int receiverId, String email);

    List<ChatHistoryDto> getConversationById(int id,int page, int size, String email);
}
