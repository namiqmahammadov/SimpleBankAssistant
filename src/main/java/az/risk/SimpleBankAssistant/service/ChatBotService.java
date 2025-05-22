package az.risk.SimpleBankAssistant.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import az.risk.SimpleBankAssistant.entity.CustomerAccount;
import az.risk.SimpleBankAssistant.entity.CustomerAccountHistory;

@Service
public class ChatBotService {

    private final CustomerAccountService customerAccountService;
    private final RestTemplate restTemplate;
    private final String aiApiUrl = "https://6a98-37-114-160-237.ngrok-free.app/predict"; // AI modelin URL-i

    public ChatBotService(CustomerAccountService customerAccountService) {
        this.customerAccountService = customerAccountService;
        this.restTemplate = new RestTemplate(); // və ya WebClient
    }

    public String processUserMessage(String message, String username) {
        message = message.toLowerCase();

        if (message.contains("balans") || message.contains("hesabımda nə qədər pul var")) {
            List<CustomerAccount> accounts = customerAccountService.getUserAccounts(username);
            if (accounts.isEmpty()) {
                return "Sizin aktiv hesabınız tapılmadı.";
            }

            StringBuilder sb = new StringBuilder("Sizin aktiv hesablarınız və balansları:\n");
            for (CustomerAccount acc : accounts) {
                BigDecimal balance = acc.getAvailableBalance() != null ? acc.getAvailableBalance() : BigDecimal.ZERO;
                sb.append("IBAN: ").append(acc.getIban())
                  .append(", Balans: ").append(balance)
                  .append(" ").append(acc.getCurrency()).append("\n");
            }
            return sb.toString();
        }

        if (message.contains("əməliyyat tarixçəsi") || message.contains("hesabımın tarixçəsi")) {
            List<CustomerAccountHistory> historyList = customerAccountService.getAccountHistory();
            if (historyList.isEmpty()) {
                return "Əməliyyat tarixçəniz boşdur.";
            }

            StringBuilder sb = new StringBuilder("Əməliyyat tarixçəniz:\n");
            for (CustomerAccountHistory h : historyList) {
                sb.append(h.getOperationDate()).append(" - ")
                  .append(h.getOperationType()).append(" - ")
                  .append(h.getAmount()).append(" ")
                  .append(h.getCurrency()).append("\n");
            }
            return sb.toString();
        }

        // AI modelə POST sorğusu
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> request = new HashMap<>();
            request.put("user_input", message); // FastAPI modeldə `user_input` field-ı olmalıdır

            HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(aiApiUrl, entity, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                // Məsələn: {"response": "Sizin kartınız aktivdir."}
                return (String) response.getBody().get("response");
            } else {
                return "AI modeldən cavab alınmadı.";
            }
        } catch (Exception e) {
            return "AI modelə bağlanarkən xəta baş verdi: " + e.getMessage();
        }
    }
}
