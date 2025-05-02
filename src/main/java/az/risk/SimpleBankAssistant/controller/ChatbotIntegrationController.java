//package az.risk.SimpleBankAssistant.controller;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestHeader;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import az.risk.SimpleBankAssistant.requests.ChatbotRequest;
//import az.risk.SimpleBankAssistant.service.ChatbotIntegrationService;
//
//@RestController
//@RequestMapping("/api/chatbot")
//public class ChatbotIntegrationController {
////    
////    private final ChatbotIntegrationService chatbotService;
//////    
//////    @Autowired
//////    public ChatbotIntegrationController(ChatbotIntegrationService chatbotService) {
//////        this.chatbotService = chatbotService;
//////    }
//////    
//////    @PostMapping("/process-query")
//////    public ResponseEntity<?> processChatbotQuery(@RequestBody ChatbotRequest request,
//////                                               @RequestHeader("Authorization") String authHeader) {
//////        return chatbotService.processQuery(request, authHeader);
//////    }
//////}