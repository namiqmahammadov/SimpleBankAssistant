package az.risk.SimpleBankAssistant.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import az.risk.SimpleBankAssistant.enums.CurrencyType;
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

	private LocalDateTime openedDate;

	
	private LocalDateTime closedDate;

	private String user;
}
