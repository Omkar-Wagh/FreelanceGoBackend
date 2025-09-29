package com.freelancego.service.ChatService;

import com.freelancego.dto.user.ChatDto;
import java.util.List;
import java.util.Map;

public interface ChatService {

    public ChatDto sendMessage(ChatDto message, String email);

    public List<ChatDto> getHistory(int senderId, int receiverId, String email);

    public Map<String, Object> authorizeChannel(String channelName, String socketId, String email);
}

