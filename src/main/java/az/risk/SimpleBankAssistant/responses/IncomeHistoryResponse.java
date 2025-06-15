package az.risk.SimpleBankAssistant.responses;

import java.util.List;

import az.risk.SimpleBankAssistant.entity.CustomerAccountHistory;
import az.risk.SimpleBankAssistant.entity.MoneyTransfer;

public record IncomeHistoryResponse(
	        List<MoneyTransfer> incomingTransfers,
	        List<CustomerAccountHistory> balanceIncomes
	) {}


