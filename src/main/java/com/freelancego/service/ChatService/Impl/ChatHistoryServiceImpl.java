package com.freelancego.service.ChatService.Impl;

import com.freelancego.dto.user.ChatHistoryDto;
import com.freelancego.exception.UnauthorizedAccessException;
import com.freelancego.exception.UserNotFoundException;
import com.freelancego.mapper.ChatHistoryMapper;
import com.freelancego.mapper.ChatMapper;
import com.freelancego.mapper.UserMapper;
import com.freelancego.model.ChatHistory;
import com.freelancego.model.ChatMessage;
import com.freelancego.model.User;
import com.freelancego.repo.ChatHistoryRepository;
import com.freelancego.repo.ChatMessageRepository;
import com.freelancego.repo.UserRepository;
import com.freelancego.service.ChatService.ChatHistoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChatHistoryServiceImpl implements ChatHistoryService {

    private final ChatHistoryRepository chatHistoryRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatHistoryMapper chatHistoryMapper;
    private final UserMapper userMapper;
    private final ChatMapper chatMapper;
    private final UserRepository userRepository;

    public ChatHistoryServiceImpl(ChatHistoryRepository chatHistoryRepository,ChatMessageRepository chatMessageRepository,UserRepository userRepository, ChatHistoryMapper chatHistoryMapper,UserMapper userMapper, ChatMapper chatMapper) {
        this.chatHistoryRepository = chatHistoryRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.userRepository= userRepository;
        this.chatHistoryMapper = chatHistoryMapper;
        this.userMapper = userMapper;
        this.chatMapper = chatMapper;
    }

    public ChatHistoryDto createChatHistory(int senderId,int receiverId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("user not found"));

        if (receiverId <= 0) {
            throw new UnauthorizedAccessException("senderId and receiverId are required");
        }
        if (user.getId() != senderId) {
            throw new UnauthorizedAccessException("Logged-in user does not match senderId");
        }

        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new UserNotFoundException("Receiver not found with Id " + receiverId));

        if (receiver.getId() <= 0) {
            throw new UserNotFoundException("Receiver not found with Id " + receiverId);
        }

        boolean status = chatHistoryRepository.existsByOwnerAndOpponent(user, receiver);
        ChatHistory newChatHistory1 = new ChatHistory();
        ChatHistory newChatHistory2 = new ChatHistory();

        if (!status) {

            newChatHistory1.setOwner(user);
            newChatHistory1.setOpponent(receiver);
            chatHistoryRepository.save(newChatHistory1);

            newChatHistory2.setOwner(user);
            newChatHistory2.setOpponent(receiver);
            chatHistoryRepository.save(newChatHistory2);

        }
        return chatHistoryMapper.toDTO(newChatHistory1);
    }

    public List<ChatHistoryDto> getConversationById(int id, int page, int size, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("user not found"));
        if(user.getId() != id){
            throw new UnauthorizedAccessException("Unauthorized request, user does not belong to chat history");
        }

        Pageable pageable = PageRequest.of(page, size);
        List<ChatHistory> histories = chatHistoryRepository.findByOwner(user, pageable);

        List<ChatHistoryDto> dto = new ArrayList<>();

        for(ChatHistory history : histories){
            Page<ChatMessage> lastMessagePage =
                    chatMessageRepository.findLatestMessageBetweenUsers(
                            history.getOwner().getId(),
                            history.getOpponent().getId(),
                            PageRequest.of(0, 1)
                    );

            ChatMessage lastMessage = lastMessagePage.hasContent() ? lastMessagePage.getContent().get(0) : null;

            ChatHistoryDto dto1 = new ChatHistoryDto(
                    history.getId(),
                    userMapper.toDTO(history.getOwner()),
                    userMapper.toDTO(history.getOpponent()),
                    history.getCreatedAt(),
                    lastMessage != null ? chatMapper.toDTO(lastMessage) : null
            );

            dto.add(dto1);
        }

        return dto;
    }


}