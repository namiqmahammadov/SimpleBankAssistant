package az.risk.SimpleBankAssistant.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import az.risk.SimpleBankAssistant.requests.ChatbotRequest;
import az.risk.SimpleBankAssistant.service.ChatbotService;

@RestController
@RequestMapping("/api/chatbot")
public class ChatbotController {

    private final ChatbotService chatbotService;

    public ChatbotController(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    @PostMapping
    public ResponseEntity<String> chat(@RequestBody ChatbotRequest request) {
        String response = chatbotService.handleUserInput(request.getUserInput(), request.getLanguage());
        return ResponseEntity.ok(response);
    }
}
