package az.risk.SimpleBankAssistant.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.hibernate.annotations.CreationTimestamp;

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
	private String operationType; 
	private BigDecimal amount;
	@Enumerated(EnumType.STRING)
	private CurrencyType currency;
	private Timestamp operationDate;
	private String user;
}
