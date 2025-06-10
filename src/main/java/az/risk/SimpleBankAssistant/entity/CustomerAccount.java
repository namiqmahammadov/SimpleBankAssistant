package az.risk.SimpleBankAssistant.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import az.risk.SimpleBankAssistant.enums.CurrencyType;
import jakarta.persistence.Column;
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
@Table(name = "costumer_accounts")
public class CustomerAccount {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long accountId;
	@Column(nullable = false, unique = true)
	private String iban;
	@Enumerated(EnumType.STRING)
	private CurrencyType currency; // AZN, USD, EUR
	@Column(precision = 19, scale = 2)
	private BigDecimal availableBalance;
	@Column(nullable = false)
	private Boolean isAccountActive = true;
	@Column(nullable = false, updatable = false)
	@CreationTimestamp
	private Timestamp openedDate;
	private String user;
	private Timestamp closedDate;
}

