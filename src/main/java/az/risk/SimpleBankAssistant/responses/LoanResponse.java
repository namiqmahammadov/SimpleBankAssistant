package az.risk.SimpleBankAssistant.responses;



import java.math.BigDecimal;

import lombok.Data;
@Data
public class LoanResponse {
    private Long id;
    private BigDecimal amount;
    private Integer loanTermInMonths;
    private Double interestRate;
    private String loanPurpose;
    private boolean isActive;
    private String iban;


}
