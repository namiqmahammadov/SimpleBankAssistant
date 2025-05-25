package az.risk.SimpleBankAssistant.controller;

import az.risk.SimpleBankAssistant.service.ChatbotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chatbot")
public class ChatbotController {

    private final ChatbotService chatbotService;

    @Autowired
    public ChatbotController(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    // POST: http://localhost:8080/chatbot/ask
    @PostMapping("/ask")
    public String askBot(@RequestBody String userInput) {
        return chatbotService.handleUserInput(userInput);
    }
}
