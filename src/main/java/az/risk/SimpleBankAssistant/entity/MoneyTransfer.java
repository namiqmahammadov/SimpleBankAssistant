package az.risk.SimpleBankAssistant.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "money_transfers")
public class MoneyTransfer {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String senderIban;
	private String receiverIban;
	private BigDecimal amount;
	@CreationTimestamp
	private Timestamp transferDate;
	private Boolean isSuccessful;
}
