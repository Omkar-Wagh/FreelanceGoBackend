package com.freelancego.service.ChatService.Impl;

import com.freelancego.dto.user.ChatDto;
import com.freelancego.exception.BadRequestException;
import com.freelancego.exception.InternalServerErrorException;
import com.freelancego.exception.UnauthorizedAccessException;
import com.freelancego.exception.UserNotFoundException;
import com.freelancego.mapper.ChatMapper;
import com.freelancego.model.ChatMessage;
import com.freelancego.model.User;
import com.freelancego.repo.ChatMessageRepository;
import com.freelancego.repo.UserRepository;
import com.freelancego.service.ChatService.ChatService;
import io.ably.lib.realtime.AblyRealtime;
import static io.ably.lib.rest.Auth.TokenRequest;
import static io.ably.lib.rest.Auth.TokenParams;
import io.ably.lib.types.AblyException;
import io.ably.lib.types.ClientOptions;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ChatServiceImpl implements ChatService {

    private final ChatMessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChatMapper chatMapper;
    private final AblyRealtime ably;

    public ChatServiceImpl(ChatMessageRepository messageRepository, UserRepository userRepository, ChatMapper chatMapper) throws AblyException {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.chatMapper = chatMapper;
        ClientOptions options = new ClientOptions("OtSgGA.cAWR0g:rv1IQX4OtdQJLwINZeD4_v4JB3WpW26PZMlzjQ2UVLQ");
        this.ably = new AblyRealtime(options);
    }

    private String getConversationId(int senderId, int receiverId) {
        return senderId < receiverId ? senderId + "-" + receiverId : receiverId + "-" + senderId;
    }

    public void sendMessage(ChatDto dto, String email) throws AblyException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("user not found"));
        ChatMessage message = chatMapper.toEntity(dto);

        if (message.getSenderId() <= 0 || message.getReceiverId() <= 0) {
            throw new UnauthorizedAccessException("senderId and receiverId are required");
        }

        if (user.getId() != message.getSenderId()) {
            throw new UnauthorizedAccessException("Logged-in user does not match senderId");
        }

        if (!userRepository.existsById(message.getReceiverId())) {
            throw new UserNotFoundException("Receiver not found with Id " + dto.receiverId());
        }

        int id1 = Math.min(user.getId(), message.getReceiverId());
        int id2 = Math.max(user.getId(), message.getReceiverId());
        String channelName = "chat-" + id1 + "-" + id2;

        messageRepository.save(message);
        try {
            ably.channels.get(channelName).publish("message", message);
        }catch (Exception e){
            throw new InternalServerErrorException("Problem in Sending Message");
        }
    }

    public List<ChatDto> getHistory(int senderId, int receiverId, int page, int size, String email) {
        if (senderId <= 0 || receiverId <= 0) {
            throw new BadRequestException("senderId and receiverId are required");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (user.getId() != senderId) {
            throw new UnauthorizedAccessException("User with id: " + senderId + " does not have authorization for this action");
        }

        if (!userRepository.existsById(receiverId)) {
            throw new UserNotFoundException("Receiver not found with Id " + receiverId);
        }

        Pageable pageable = PageRequest.of(page, size);
        List<ChatMessage> messages = messageRepository.findConversation(senderId, receiverId, pageable).getContent();

        return messages.stream().map(message -> chatMapper.toDTO(message)).toList();
    }

    public TokenRequest getAblyToken(int otherUserId, String name) throws AblyException {
        User user = userRepository.findByEmail(name)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        String clientId = String.valueOf(user.getId());
        TokenParams params = new TokenParams();
        params.clientId = clientId;
        return ably.auth.createTokenRequest(params, null);
    }
}

