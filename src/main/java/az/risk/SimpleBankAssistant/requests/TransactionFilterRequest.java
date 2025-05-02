package az.risk.SimpleBankAssistant.requests;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;

@Data
public class TransactionFilterRequest {

	private LocalDate startDate;
	private LocalDate endDate;
	private BigDecimal minAmount;
	private BigDecimal maxAmount;
	private String transactionType; // Gəlir, xərc

}
