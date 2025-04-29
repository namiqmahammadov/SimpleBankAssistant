package az.risk.SimpleBankAssistant.service;



import java.math.BigDecimal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import az.risk.SimpleBankAssistant.requests.ChatbotRequest;
import az.risk.SimpleBankAssistant.requests.LoanRequest;
import az.risk.SimpleBankAssistant.responses.AiResponse;

@Service
public class ChatbotIntegrationService {
    
    private final CustomerAccountService accountService;
    private final LoanService loanService;
    private final MoneyTransferService transferService;
    private final RestTemplate restTemplate;
    
    @Autowired
    public ChatbotIntegrationService(CustomerAccountService accountService,
                                   LoanService loanService,
                                   MoneyTransferService transferService,
                                   RestTemplate restTemplate) {
        this.accountService = accountService;
        this.loanService = loanService;
        this.transferService = transferService;
        this.restTemplate = restTemplate;
    }
    
    public ResponseEntity<?> processQuery(ChatbotRequest request, String authHeader) {
        try {
            // Forward to AI for intent recognition
            String aiUrl = "https://deb0-37-26-14-232.ngrok-free.app/chat";
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", authHeader);
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<ChatbotRequest> aiRequest = new HttpEntity<>(request, headers);
            ResponseEntity<AiResponse> aiResponse = restTemplate.exchange(
                aiUrl, HttpMethod.POST, aiRequest, AiResponse.class);
            
            // Process based on AI's identified intent
            Object responseData = processIntent(aiResponse.getBody());
            return ResponseEntity.ok(responseData);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    private Object processIntent(AiResponse aiResponse) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        String intent = aiResponse.getIntent();
        Map<String, String> params = aiResponse.getParameters();
        
        switch(intent.toLowerCase()) {
            case "balance_inquiry":
                return handleBalanceInquiry(params);
            case "account_information":
                return accountService.getUserAccounts(username);
            case "currency_conversion":
                return handleCurrencyConversion(params);
            case "loan_debt":
                return loanService.getLoanDebt();
            case "total_debt":
                return loanService.getTotalLoanDebt();
            case "transfer_history":
                return transferService.getTransferHistory(username);
            case "apply_loan":
                return handleLoanApplication(params);
            default:
                throw new RuntimeException("Unrecognized intent: " + intent);
        }
    }
    
    private Object handleBalanceInquiry(Map<String, String> params) {
        Long accountId = Long.parseLong(params.get("accountId"));
        return accountService.getBalance(accountId);
    }
    
    private Object handleCurrencyConversion(Map<String, String> params) {
        return accountService.convertCurrency(
            Long.parseLong(params.get("accountId")),
            params.get("fromCurrency"),
            params.get("toCurrency"));
    }
    
    private Object handleLoanApplication(Map<String, String> params) {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.setAmount(new BigDecimal(params.get("amount")));
        loanRequest.setLoanTermInMonths(Integer.parseInt(params.get("term")));
        loanRequest.setLoanPurpose(params.get("purpose"));
        loanRequest.setIbanSuffix(params.get("ibanSuffix"));
        return loanService.applyForLoan(loanRequest);
    }
}