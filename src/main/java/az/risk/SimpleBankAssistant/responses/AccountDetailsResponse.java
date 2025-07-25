package az.risk.SimpleBankAssistant.responses;

import java.math.BigDecimal;
import java.sql.Timestamp;

import az.risk.SimpleBankAssistant.enums.CurrencyType;
import lombok.Data;

@Data
public class AccountDetailsResponse {
	private Long accountId;
	private String iban;
	private CurrencyType currency;
	private BigDecimal availableBalance;
	private Boolean isAccountActive;

	private Timestamp openedDate;

	private Timestamp closedDate;

}
