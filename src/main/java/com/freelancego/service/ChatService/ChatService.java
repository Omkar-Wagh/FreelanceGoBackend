package com.freelancego.service.ChatService;

import com.freelancego.dto.user.ChatDto;
import io.ably.lib.rest.Auth.TokenRequest;
import io.ably.lib.types.AblyException;
import java.util.List;

public interface ChatService {

    public void sendMessage(ChatDto message, String email) throws AblyException;

    public List<ChatDto> getHistory(int senderId, int receiverId, int page, int size, String email);

    TokenRequest getAblyToken(int otherUserId, String name) throws AblyException;

}

