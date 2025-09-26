package com.freelancego.service.ChatService.Impl;

import com.freelancego.dto.user.ChatHistoryDto;
import com.freelancego.exception.UnauthorizedAccessException;
import com.freelancego.exception.UserNotFoundException;
import com.freelancego.mapper.ChatHistoryMapper;
import com.freelancego.model.ChatHistory;
import com.freelancego.model.User;
import com.freelancego.repo.ChatHistoryRepository;
import com.freelancego.repo.UserRepository;
import com.freelancego.service.ChatService.ChatHistoryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatHistoryServiceImpl implements ChatHistoryService {

    private final ChatHistoryRepository chatHistoryRepository;
    private final ChatHistoryMapper chatHistoryMapper;
    private final UserRepository userRepository;

    public ChatHistoryServiceImpl(ChatHistoryRepository chatHistoryRepository,UserRepository userRepository, ChatHistoryMapper chatHistoryMapper) {
        this.chatHistoryRepository = chatHistoryRepository;
        this.userRepository= userRepository;
        this.chatHistoryMapper = chatHistoryMapper;
    }

    public ChatHistoryDto createChatHistory(ChatHistoryDto history) {

        ChatHistory chatHistory = chatHistoryMapper.toEntity(history);
        User owner = chatHistory.getOwner();
        User opponent = chatHistory.getOpponent();
        boolean status = chatHistoryRepository.existsByOwnerAndOpponent(owner,opponent);
        if(status == false) {
            chatHistoryRepository.save(chatHistory);
        }
        return chatHistoryMapper.toDTO(chatHistory);
    }

    public List<ChatHistoryDto> getChatHistoryById(int id, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("user not found"));
        if(user.getId() != id){
            throw  new UnauthorizedAccessException("Unauthorized request, user does not belongs to chat history");
        }
        List<ChatHistory> histories = chatHistoryRepository.findByOwner(user);
        return chatHistoryMapper.toDtoList(histories);
    }
}
