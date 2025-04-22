package az.risk.SimpleBankAssistant.service;



import org.springframework.http.ResponseEntity;

import az.risk.SimpleBankAssistant.requests.LoanRequest;
import az.risk.SimpleBankAssistant.responses.LoanResponse;



public interface LoanService {
    LoanResponse applyForLoan(LoanRequest request);
    ResponseEntity<?> getLoanDebt(); // Yeni metod
}
