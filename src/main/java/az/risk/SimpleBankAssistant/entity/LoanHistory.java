package az.risk.SimpleBankAssistant.entity;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "loan_history")
public class LoanHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long loanId;

    private BigDecimal amount;

    private Double interestRate;

    private Integer loanTermInMonths;

    private String loanPurpose;

    private String iban;

    private String operationType; // APPLY, CLOSE və s.

    private LocalDateTime operationDate;

    private String user; // Əməliyyatı edən istifadəçi
}
