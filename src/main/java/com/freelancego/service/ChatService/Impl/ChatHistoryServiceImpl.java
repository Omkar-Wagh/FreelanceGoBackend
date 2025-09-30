package com.freelancego.service.ChatService.Impl;

import com.freelancego.dto.user.ChatHistoryDto;
import com.freelancego.exception.UnauthorizedAccessException;
import com.freelancego.exception.UserNotFoundException;
import com.freelancego.mapper.ChatHistoryMapper;
import com.freelancego.model.ChatHistory;
import com.freelancego.model.ChatMessage;
import com.freelancego.model.User;
import com.freelancego.repo.ChatHistoryRepository;
import com.freelancego.repo.ChatMessageRepository;
import com.freelancego.repo.UserRepository;
import com.freelancego.service.ChatService.ChatHistoryService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatHistoryServiceImpl implements ChatHistoryService {

    private final ChatHistoryRepository chatHistoryRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatHistoryMapper chatHistoryMapper;
    private final UserRepository userRepository;

    public ChatHistoryServiceImpl(ChatHistoryRepository chatHistoryRepository,ChatMessageRepository chatMessageRepository,UserRepository userRepository, ChatHistoryMapper chatHistoryMapper) {
        this.chatHistoryRepository = chatHistoryRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.userRepository= userRepository;
        this.chatHistoryMapper = chatHistoryMapper;
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
//        getHistoryWithLast5Chats();
        return chatHistoryMapper.toDtoList(histories);
    }

    public List<ChatMessage> getHistoryWithLast5Chats(int historyId) {
        ChatHistory history = chatHistoryRepository.findById(historyId)
                .orElseThrow(() -> new RuntimeException("History not found"));

        List<ChatMessage> last5 = chatMessageRepository.findByChatHistoryOrderByCreatedAtDesc(history, PageRequest.of(0, 5));
        return last5;
    }
}
