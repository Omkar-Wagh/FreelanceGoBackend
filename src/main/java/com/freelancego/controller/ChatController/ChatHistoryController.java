package com.freelancego.controller.ChatController;

import com.freelancego.dto.user.ChatHistoryDto;
import com.freelancego.model.ChatHistory;
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

    @PostMapping("/create")
    public ResponseEntity<ChatHistoryDto> createChatHistory(@RequestBody ChatHistoryDto dto, Authentication auth) {
        ChatHistoryDto saved = chatHistoryService.createChatHistory(dto,auth.getName());
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{senderId}")
    public ResponseEntity<List<ChatHistoryDto>> getChatHistoryById(@PathVariable("senderId") int id, Authentication auth) {
        List<ChatHistoryDto> chatHistoryDto = chatHistoryService.getChatHistoryById(id,auth.getName());
        return ResponseEntity.ok(chatHistoryDto);
    }
}
