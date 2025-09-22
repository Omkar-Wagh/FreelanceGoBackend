package com.freelancego.service.ChatService;

import com.freelancego.dto.user.ChatDto;
import com.freelancego.model.ChatMessage;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public interface ChatService {

    public ChatDto sendMessage(ChatMessage message, String email);

    public List<ChatDto> getHistory(int senderId, int receiverId, String email);

    public String authorizeChannel(String channelName, String socketId, String email);
}

