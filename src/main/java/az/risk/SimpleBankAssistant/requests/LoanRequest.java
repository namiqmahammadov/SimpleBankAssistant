package az.risk.SimpleBankAssistant.requests;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class LoanRequest {
    private BigDecimal amount;
    private Integer loanTermInMonths; 
    private String loanPurpose;  
    private String ibanSuffix; 


}
