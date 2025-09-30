package com.freelancego.controller.ChatController;

import com.freelancego.dto.user.ChatDto;
import com.freelancego.exception.UserNotFoundException;
import com.freelancego.mapper.ChatMapper;
import com.freelancego.model.ChatMessage;
import com.freelancego.model.User;
import com.freelancego.repo.ChatMessageRepository;
import com.freelancego.repo.UserRepository;
import com.freelancego.service.ChatService.ChatService;
import io.ably.lib.realtime.AblyRealtime;
import io.ably.lib.rest.Auth;
import io.ably.lib.rest.Auth.TokenRequest;
import io.ably.lib.types.AblyException;
import io.ably.lib.types.ClientOptions;
import io.ably.lib.types.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final AblyRealtime ably;
    private final ChatService chatService;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ChatMessageRepository chatMessageRepository;
    @Autowired
    private ChatMapper chatMapper;

    public ChatController(ChatService chatService) throws AblyException{
        this.chatService = chatService;
        ClientOptions options = new ClientOptions("OtSgGA.cAWR0g:rv1IQX4OtdQJLwINZeD4_v4JB3WpW26PZMlzjQ2UVLQ");
        this.ably = new AblyRealtime(options);
    }

    @GetMapping("/token")
    public TokenRequest getAblyToken(@RequestParam int otherUserId, Authentication auth) throws AblyException {
        User user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        String clientId = String.valueOf(user.getId());

        Auth.TokenParams params = new Auth.TokenParams();
        params.clientId = clientId;
        // optionally, you can restrict channel access:
        // params.capability = "{ \"chat-" + Math.min(user.getId(), otherUserId) + "-" + Math.max(user.getId(), otherUserId) + "\": [\"publish\",\"subscribe\"] }";

        return ably.auth.createTokenRequest(params, null);
    }



    @PostMapping("/send")
    public String sendAndSaveMessage(@RequestBody ChatDto chatDto, Authentication auth) throws Exception {

        // 1. Get logged-in user
        User user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new UserNotFoundException("user not found"));
        ChatMessage message = chatMapper.toEntity(chatDto);

        // other user validations

        int id1 = Math.min(user.getId(), message.getId());
        int id2 = Math.max(user.getId(), message.getId());
        String channelName = "chat-" + id1 + "-" + id2;

        chatMessageRepository.save(message);

        // 3. Publish to Ably
//        ably.channels.get(channelName)
//                .publish("message", new Message("message", message.getContent()));
        ably.channels.get(channelName).publish("message", message);
        return "Message sent!";
    }


    @PostMapping("/sending")
    public ResponseEntity<ChatDto> sendMessage(@RequestBody ChatDto message, Authentication auth) {
        ChatDto saved = chatService.sendMessage(message, auth.getName());
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/history/{senderId}/{receiverId}")
    public ResponseEntity<List<ChatDto>> getHistory(@PathVariable int senderId, @PathVariable int receiverId, Authentication auth) {
        List<ChatDto> history = chatService.getHistory(senderId, receiverId, auth.getName());
        return ResponseEntity.ok(history);
    }

    @PostMapping("/pusher/auth")
    public String authorizeChannel(@RequestParam("channel_name") String channelName,
                                                @RequestParam("socket_id") String socketId,
                                                Authentication auth) {
        return chatService.authorizeChannel(channelName, socketId, auth.getName());
    }
}

