package az.risk.SimpleBankAssistant.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import az.risk.SimpleBankAssistant.enums.CurrencyType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "customer_account_history")
public class CustomerAccountHistory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String iban;

	private String operationType; // "BALANCE_UPDATE" or "CURRENCY_CONVERSION"

	private BigDecimal amount;

	@Enumerated(EnumType.STRING)
	private CurrencyType currency;

	private LocalDateTime operationDate;

	private String user;
}
