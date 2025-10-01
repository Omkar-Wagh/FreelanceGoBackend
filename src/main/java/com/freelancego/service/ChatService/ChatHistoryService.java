package com.freelancego.service.ChatService;

import com.freelancego.dto.user.ChatHistoryDto;

import java.util.List;

public interface ChatHistoryService {
    ChatHistoryDto createChatHistory(ChatHistoryDto chatHistory, String email);

    List<ChatHistoryDto> getConversationById(int id, String email);
}
