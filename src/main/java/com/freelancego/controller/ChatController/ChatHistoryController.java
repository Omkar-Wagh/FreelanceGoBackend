package com.freelancego.controller.ChatController;

import com.freelancego.dto.user.ChatHistoryDto;
import com.freelancego.service.ChatService.ChatHistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat-history/")
public class ChatHistoryController {

    private final ChatHistoryService chatHistoryService;

    public ChatHistoryController(ChatHistoryService chatHistoryService) {
        this.chatHistoryService = chatHistoryService;
    }

    @PostMapping("/create/{senderId}/{receiverId}")
    public ResponseEntity<ChatHistoryDto> createChatHistory(@PathVariable("senderId") int senderId, @PathVariable("receiverId") int receiverId, Authentication auth) {
        ChatHistoryDto saved = chatHistoryService.createChatHistory(senderId,receiverId,auth.getName());
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{senderId}")
    public ResponseEntity<List<ChatHistoryDto>> getChatHistoryById(@PathVariable("senderId") int id,@RequestParam(defaultValue = "0") int page,
                                                                   @RequestParam(defaultValue = "5") int size, Authentication auth) {
        List<ChatHistoryDto> chatHistoryDto = chatHistoryService.getConversationById(id,page,size,auth.getName());
        return ResponseEntity.ok(chatHistoryDto);
    }
}