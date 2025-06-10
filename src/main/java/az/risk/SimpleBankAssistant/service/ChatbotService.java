package az.risk.SimpleBankAssistant.service;

import az.risk.SimpleBankAssistant.entity.CustomerAccount;
import az.risk.SimpleBankAssistant.entity.CustomerAccountHistory;
import az.risk.SimpleBankAssistant.entity.MoneyTransfer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class ChatbotService {
    private final CustomerAccountService customerAccountService;
    private final LoanService loanService;
    private final MoneyTransferService moneyTransferService;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final String CLASSIFIER_URL = "https://bankbot-2.onrender.com/classify";

    @Autowired
    public ChatbotService(CustomerAccountService customerAccountService,
                          LoanService loanService,
                          MoneyTransferService moneyTransferService) {
        this.customerAccountService = customerAccountService;
        this.loanService = loanService;
        this.moneyTransferService = moneyTransferService;
    }

    public String handleUserInput(String userInput, String language) {
        try {
            // AI cavabını al (JSON formatında)
            String aiRawResponse = getAiRawResponse(userInput, language);
            JsonNode aiJson = objectMapper.readTree(aiRawResponse);

            // JSON-dan lazım olan sahələri çıxart
            String category = aiJson.path("class name").asText("[other]");
            String staticText = aiJson.path("response").asText("");

            // DB-dən əlavə cavab
            String dynamicPart = getDynamicPart(category);

            return staticText + dynamicPart;
        } catch (Exception e) {
            e.printStackTrace();
            return "Cavab hazırlanarkən xəta baş verdi.";
        }
    }

    private String getAiRawResponse(String input, String language) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Question question = new Question(input, language);
            HttpEntity<Question> entity = new HttpEntity<>(question, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(CLASSIFIER_URL, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            } else {
                return "{\"class name\":\"[other]\",\"response\":\"AI cavabı alınmadı. \"}";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"class name\":\"[other]\",\"response\":\"AI cavabı alınmadı. \"}";
        }
    }

    private String getDynamicPart(String category) {
        String username = getAuthenticatedUsername();

        switch (category) {
            case "[account balans check]":
                List<CustomerAccount> accounts = customerAccountService.getUserAccounts(username);
                if (!accounts.isEmpty()) {
                    BigDecimal balance = accounts.get(0).getAvailableBalance();
                    return balance.toPlainString() + " AZN";
                }
                return "0 AZN";

            case "[iban code check]":
                List<CustomerAccount> ibanAccounts = customerAccountService.getUserAccounts(username);
                if (!ibanAccounts.isEmpty()) {
                    return ibanAccounts.get(0).getIban();
                }
                return "IBAN tapılmadı.";

            case "[number of accounts check]":
                List<CustomerAccount> accs = customerAccountService.getUserAccounts(username);
                return String.valueOf(accs.size());

            case "[account currency check]":
                List<CustomerAccount> currencyAccounts = customerAccountService.getUserAccounts(username);
                return currencyAccounts.stream()
                        .map(a -> a.getCurrency().name())
                        .distinct()
                        .reduce("", (a, b) -> a + " " + b);

            case "[loan debt check]":
                Object loanResponse = loanService.getLoanDebt().getBody();
                if (loanResponse instanceof String) {
                    return (String) loanResponse;
                } else if (loanResponse instanceof Map<?, ?> responseMap) {
                    Object totalDebt = responseMap.get("totalDebt");
                    return totalDebt != null ? totalDebt.toString() + " AZN" : "Borc tapılmadı.";
                }
                return "Kredit məlumatı alınmadı.";

            case "[account history]":
                List<CustomerAccountHistory> historyList = customerAccountService.getAccountHistory();
                if (historyList.isEmpty()) return "Tarixçə boşdur.";
                CustomerAccountHistory lastOp = historyList.get(historyList.size() - 1);
                return String.format("Son əməliyyat: %s, %s %s",
                        lastOp.getOperationType(),
                        lastOp.getAmount(),
                        lastOp.getCurrency());

            case "[loan history]":
                Object loanHistoryObj = loanService.getLoanHistory().getBody();
                if (!(loanHistoryObj instanceof List<?> loanHistoryList) || loanHistoryList.isEmpty())
                    return "Kredit tarixçəsi boşdur.";
                Object lastLoanObj = loanHistoryList.get(loanHistoryList.size() - 1);
                if (lastLoanObj instanceof Map<?, ?> map) {
                    return String.format("Son kredit: %s AZN, %s",
                            map.get("amount"), map.get("date"));
                }
                return "Son kredit məlumatı tapılmadı.";

            case "[transfer history]":
                List<MoneyTransfer> transfers = moneyTransferService.getTransferHistory();
                if (transfers.isEmpty()) return "Köçürmə tarixçəsi boşdur.";
                MoneyTransfer lastTransfer = transfers.get(transfers.size() - 1);
                return String.format("Son köçürmə: %s AZN -> %s",
                        lastTransfer.getAmount().toPlainString(),
                        lastTransfer.getReceiverIban());

            default:
                return "";
        }
    }

    private String getAuthenticatedUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    static class Question {
        public String question;
        public String language;

        public Question(String question, String language) {
            this.question = question;
            this.language = language;
        }

        public Question() {
        }
    }
}
