package com.freelancego.service.ChatService;

import com.freelancego.dto.user.ChatDto;
import java.util.List;

public interface ChatService {

    public ChatDto sendMessage(ChatDto message, String email);

    public List<ChatDto> getHistory(int senderId, int receiverId, String email);

    public String authorizeChannel(String channelName, String socketId, String email);
}

