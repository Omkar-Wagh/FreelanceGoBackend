package com.freelancego.service.ChatService;

import com.freelancego.dto.user.ChatHistoryDto;

import java.util.List;

public interface ChatHistoryService {
    ChatHistoryDto createChatHistory(ChatHistoryDto chatHistory);

    List<ChatHistoryDto> getChatHistoryById(int id, String email);

}
