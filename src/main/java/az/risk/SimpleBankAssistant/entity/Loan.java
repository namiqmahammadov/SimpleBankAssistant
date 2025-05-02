package az.risk.SimpleBankAssistant.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Data
@Table(name = "loans")
public class Loan {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private BigDecimal amount;
	private Integer loanTermInMonths;
	private Double interestRate;
	private String loanPurpose;
	private boolean isActive;
	@Column(nullable = false)
	private String iban;

}
