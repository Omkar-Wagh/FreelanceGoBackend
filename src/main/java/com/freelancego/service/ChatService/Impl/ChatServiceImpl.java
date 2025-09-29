package com.freelancego.service.ChatService.Impl;

import com.freelancego.dto.user.ChatDto;
import com.freelancego.exception.BadRequestException;
import com.freelancego.exception.UnauthorizedAccessException;
import com.freelancego.exception.UserNotFoundException;
import com.freelancego.mapper.ChatMapper;
import com.freelancego.model.ChatMessage;
import com.freelancego.model.User;
import com.freelancego.repo.ChatMessageRepository;
import com.freelancego.repo.UserRepository;
import com.freelancego.service.ChatService.ChatService;
import com.pusher.rest.Pusher;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatServiceImpl implements ChatService {

    private final ChatMessageRepository messageRepository;
    private final UserRepository userRepository;
    private final Pusher pusher;
    private final ChatMapper chatMapper;

    public ChatServiceImpl(ChatMessageRepository messageRepository, UserRepository userRepository, ChatMapper chatMapper) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.chatMapper = chatMapper;

        this.pusher = new Pusher("APP_ID", "b0dddaee8d0f9b6e5184","SECRET");
        this.pusher.setCluster("ap2");
        this.pusher.setEncrypted(true);
    }

    private String getConversationId(int senderId, int receiverId) {
        return senderId < receiverId ? senderId + "-" + receiverId : receiverId + "-" + senderId;
    }

    public ChatDto sendMessage(ChatDto dto, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        ChatMessage message = chatMapper.toEntity(dto);
        if (message.getSenderId() <= 0 || message.getReceiverId() <= 0) {
            throw new UnauthorizedAccessException("senderId and receiverId are required");
        }

        if (user.getId() != message.getSenderId()) {
            throw new UnauthorizedAccessException("Logged-in user does not match senderId");
        }

        if (!userRepository.existsById(message.getSenderId())) {
            throw new UserNotFoundException("Sender not found");
        }
        if (!userRepository.existsById(message.getReceiverId())) {
            throw new UserNotFoundException("Receiver not found");
        }

        String channelName = "private-chat-" + getConversationId(message.getSenderId(), message.getReceiverId());
        authorizeChannelForOperation(channelName, user.getId());

        ChatMessage saved = messageRepository.save(message);

        try {
            pusher.trigger(channelName, "new-message", saved);
        } catch (Exception ex) {
            // Log or handle Pusher errors
            System.out.println("\n");
            System.out.println("Something went long during the message sending ");
            System.out.println("\n");
            ex.printStackTrace();
        }

        return chatMapper.toDTO(saved);
    }

    public List<ChatDto> getHistory(int senderId, int receiverId, String email) {
        if (senderId <= 0 || receiverId <= 0) {
            throw new BadRequestException("senderId and receiverId are required");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        String channelName = "private-chat-" + getConversationId(senderId, receiverId);
        authorizeChannelForOperation(channelName, user.getId());

        List<ChatMessage> messages = messageRepository.findConversation(senderId, receiverId);
        return chatMapper.toDtoList(messages);
    }

    private void authorizeChannelForOperation(String channelName, int currentUserId) {
        if (!channelName.startsWith("private-chat-")) {
            throw new BadRequestException("Invalid channel name");
        }

        String[] ids = channelName.replace("private-chat-", "").split("-");
        int user1 = Integer.parseInt(ids[0]);
        int user2 = Integer.parseInt(ids[1]);

        if (!(currentUserId == user1) && !(currentUserId == user2)) {
            throw new UnauthorizedAccessException("User not authorized for this channel");
        }

        int otherUserId = (currentUserId==user1) ? user2 : user1;
        if (!userRepository.existsById(otherUserId)) {
            throw new UserNotFoundException("Other participant not found");
        }
    }

    public String authorizeChannel(String channelName, String socketId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        authorizeChannelForOperation(channelName, user.getId());

        String auth =
                pusher.authenticate(socketId, channelName);
        System.out.println(auth);

        return auth;
    }
}

