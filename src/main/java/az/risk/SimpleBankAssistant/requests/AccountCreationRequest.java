package az.risk.SimpleBankAssistant.requests;

import az.risk.SimpleBankAssistant.enums.CurrencyType;
import lombok.Data;

@Data
public class AccountCreationRequest {
	private Long customerId;
	private CurrencyType currency;
}
