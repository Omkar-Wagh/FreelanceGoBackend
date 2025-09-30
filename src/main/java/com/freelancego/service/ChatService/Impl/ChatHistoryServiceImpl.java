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
import org.springframework.data.domain.PageRequest;
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

    public ChatHistoryDto createChatHistory(ChatHistoryDto history, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("user not found"));

        ChatHistory chatHistory = chatHistoryMapper.toEntity(history);
        User owner = chatHistory.getOwner();
        User opponent = chatHistory.getOpponent();

        if(user.getId() != owner.getId()){
            throw  new UnauthorizedAccessException("Unauthorized request, user does not belongs to chat history");
        }

        boolean status = chatHistoryRepository.existsByOwnerAndOpponent(owner,opponent);
        if(!status) {
            ChatHistory newChatHistory = new ChatHistory();
            newChatHistory.setOwner(opponent);
            newChatHistory.setOpponent(owner);
            chatHistoryRepository.save(newChatHistory);
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

        List<ChatHistoryDto> dto = new ArrayList<>();

        for(ChatHistory history : histories){
            List<ChatMessage> last5 =
                    chatMessageRepository.findBySenderIdAndReceiverIdOrderByTimestampDesc(
                            history.getOwner().getId(),
                            history.getOpponent().getId(),
                            PageRequest.of(0, 5)
                    );
            ChatHistoryDto dto1 = new ChatHistoryDto(history.getId(),userMapper.toDTO(history.getOwner()), userMapper.toDTO(history.getOpponent()),history.getCreatedAt(),chatMapper.toDtoList(last5));
            dto.add(dto1);
        }
        return dto;
    }
}

