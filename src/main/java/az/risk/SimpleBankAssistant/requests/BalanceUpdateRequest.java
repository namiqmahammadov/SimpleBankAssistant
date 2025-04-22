package az.risk.SimpleBankAssistant.requests;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class BalanceUpdateRequest {
    private BigDecimal amount;
}
