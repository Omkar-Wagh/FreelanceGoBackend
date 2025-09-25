package com.freelancego.controller.ChatController;

import com.freelancego.dto.user.ChatDto;
import com.freelancego.service.ChatService.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/send")
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
    public ResponseEntity<String> authorizeChannel(@RequestParam("channel_name") String channelName,
                                                   @RequestParam("socket_id") String socketId,
                                                   Authentication auth) {
        String auth1 = chatService.authorizeChannel(channelName, socketId, auth.getName());
        return ResponseEntity.ok(auth1);
    }
}

