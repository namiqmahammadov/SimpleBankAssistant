package az.risk.SimpleBankAssistant.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import az.risk.SimpleBankAssistant.entity.CustomerAccount;
import az.risk.SimpleBankAssistant.entity.CustomerAccountHistory;
import az.risk.SimpleBankAssistant.service.ChatBotService;

@RestController
@RequestMapping("/chatbot")
public class ChatBotController {

    private final ChatBotService chatBotService;

    public ChatBotController(ChatBotService chatBotService) {
        this.chatBotService = chatBotService;
    }

    @PostMapping("/ask")
    public ResponseEntity<String> askChatBot(@RequestBody String message, Authentication authentication) {
        // İstifadəçi adı (username) JWT-dən avtomatik gəlir (Spring Security)
        String username = authentication.getName();

        String response = chatBotService.processUserMessage(message, username);
        return ResponseEntity.ok(response);
    }
}
