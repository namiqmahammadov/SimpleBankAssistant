package az.risk.SimpleBankAssistant.requests;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class TransferRequest {
	private String senderIban;
	private String receiverIban;
	private BigDecimal amount;

}
