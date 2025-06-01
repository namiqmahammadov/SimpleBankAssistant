package az.risk.SimpleBankAssistant.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

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
