package com.freelancego.controller.ChatController;

import com.freelancego.dto.user.ChatDto;
import com.freelancego.exception.InternalServerErrorException;
import com.freelancego.service.ChatService.ChatService;
import io.ably.lib.rest.Auth.TokenRequest;
import io.ably.lib.types.AblyException;
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

    @GetMapping("/token")
    public TokenRequest getAblyToken(@RequestParam int otherUserId, Authentication auth) throws AblyException {
        return chatService.getAblyToken(otherUserId,auth.getName());
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendMessage(@RequestBody ChatDto message, Authentication auth) throws AblyException {
        try{
            chatService.sendMessage(message, auth.getName());
        }catch (Exception e){
            throw new InternalServerErrorException("Problem in Sending Message");
        }
        return ResponseEntity.ok("Message Sent To User with Id " + message.receiverId());
    }

    @GetMapping("/history/{senderId}/{receiverId}")
    public ResponseEntity<List<ChatDto>> getHistory(@PathVariable int senderId, @PathVariable int receiverId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size, Authentication auth) {
        List<ChatDto> history = chatService.getHistory(senderId, receiverId, page, size, auth.getName());
        return ResponseEntity.ok(history);
    }

}

