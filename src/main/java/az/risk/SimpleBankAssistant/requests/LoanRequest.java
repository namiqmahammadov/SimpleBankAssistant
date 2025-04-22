package az.risk.SimpleBankAssistant.requests;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class LoanRequest {
    private BigDecimal amount;
    private Integer loanTermInMonths; // Kredit müddəti (ayla)
    private String loanPurpose;  
    private String ibanSuffix; // məsələn: "2901"


}
