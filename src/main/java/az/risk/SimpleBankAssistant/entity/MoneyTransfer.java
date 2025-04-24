package az.risk.SimpleBankAssistant.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

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
    private LocalDateTime transferDate;

    private Boolean isSuccessful;
}
