package az.risk.SimpleBankAssistant.responses;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
public class TransactionHistoryResponse {

	private Long id;
	private BigDecimal amount;
	private String transactionType;
	private Timestamp transactionDate;

}
