package az.risk.SimpleBankAssistant.responses;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import az.risk.SimpleBankAssistant.enums.CurrencyType;
import lombok.Data;

@Data
public class AccountDetailsResponse {
	private Long accountId;
	private String iban;
	private CurrencyType currency;
	private BigDecimal availableBalance;
	private Boolean isAccountActive;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime openedDate;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime closedDate;

}
