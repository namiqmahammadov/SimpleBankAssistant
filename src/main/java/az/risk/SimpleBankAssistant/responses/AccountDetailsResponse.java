package az.risk.SimpleBankAssistant.responses;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import az.risk.SimpleBankAssistant.enums.CurrencyType;

public class AccountDetailsResponse {
    private Long accountId;
    private String iban;
    private CurrencyType currency;
    private BigDecimal availableBalance;
    private Boolean isAccountActive;
    private LocalDateTime openedDate;
    private LocalDateTime closedDate;
}
