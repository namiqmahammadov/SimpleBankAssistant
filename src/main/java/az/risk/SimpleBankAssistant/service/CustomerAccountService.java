package az.risk.SimpleBankAssistant.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import az.risk.SimpleBankAssistant.entity.CustomerAccount;
import az.risk.SimpleBankAssistant.entity.CustomerAccountHistory;
import az.risk.SimpleBankAssistant.enums.CurrencyType;
import az.risk.SimpleBankAssistant.repository.CustomerAccountHistoryRepository;
import az.risk.SimpleBankAssistant.repository.CustomerAccountRepository;
import az.risk.SimpleBankAssistant.util.CurrencyConverterUtil;
import az.risk.SimpleBankAssistant.util.IbanGenerator;

@Service
public class CustomerAccountService {

	private final CustomerAccountRepository customerAccountRepository;
	private final CurrencyConverterUtil currencyConverterUtil;
	private final CustomerAccountHistoryRepository historyRepository;

	@Autowired
	public CustomerAccountService(CustomerAccountRepository customerAccountRepository,
								   CurrencyConverterUtil currencyConverterUtil,
								   CustomerAccountHistoryRepository historyRepository) {
		this.customerAccountRepository = customerAccountRepository;
		this.currencyConverterUtil = currencyConverterUtil;
		this.historyRepository = historyRepository;
	}

	public CustomerAccount createAccount(CustomerAccount customerAccount) {
		customerAccount.setIban(IbanGenerator.generateRandomIban());
		customerAccount.setAvailableBalance(BigDecimal.ZERO);
		customerAccount.setUser(getAuthenticatedUsername());
		return customerAccountRepository.save(customerAccount);
	}

	public CustomerAccount updateBalance(Long accountId, BigDecimal amount) {
		CustomerAccount account = customerAccountRepository.findById(accountId)
				.orElseThrow(() -> new RuntimeException("Hesab tapılmadı"));

		BigDecimal newBalance = account.getAvailableBalance().add(amount);
		account.setAvailableBalance(newBalance);
		customerAccountRepository.save(account);

		saveAccountHistory(account, amount, account.getCurrency(), "BALANCE_UPDATE");

		return account;
	}

	public BigDecimal getBalance(Long accountId) {
		CustomerAccount account = customerAccountRepository.findById(accountId)
				.orElseThrow(() -> new RuntimeException("Hesab tapılmadı"));
		return account.getAvailableBalance();
	}

	public void closeAccount(Long accountId) {
		CustomerAccount account = customerAccountRepository.findById(accountId)
				.orElseThrow(() -> new RuntimeException("Hesab tapılmadı"));
		account.setIsAccountActive(false);
		account.setClosedDate(LocalDateTime.now());
		customerAccountRepository.save(account);
	}

	public BigDecimal convertCurrency(Long accountId, String toCurrencyStr) {
		CustomerAccount account = customerAccountRepository.findById(accountId)
				.orElseThrow(() -> new RuntimeException("Hesab tapılmadı"));

		String fromCurrency = account.getCurrency().name();
		BigDecimal convertedAmount = currencyConverterUtil.convert(account.getAvailableBalance(), fromCurrency, toCurrencyStr);

		account.setAvailableBalance(convertedAmount);
		account.setCurrency(CurrencyType.valueOf(toCurrencyStr.toUpperCase()));
		customerAccountRepository.save(account);

		saveAccountHistory(account, convertedAmount, account.getCurrency(), "CURRENCY_CONVERSION");

		return convertedAmount;
	}

	private void saveAccountHistory(CustomerAccount account, BigDecimal amount, CurrencyType currency, String operationType) {
		CustomerAccountHistory history = new CustomerAccountHistory();
		history.setIban(account.getIban());
		history.setAmount(amount);
		history.setCurrency(currency);
		history.setUser(account.getUser());
		history.setOperationType(operationType);
		history.setOperationDate(LocalDateTime.now());
		historyRepository.save(history);
	}

	private String getAuthenticatedUsername() {
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}

	public List<CustomerAccount> getUserAccounts(String user) {
		return customerAccountRepository.findByUserAndIsAccountActive(user, true);
	}

	public List<CustomerAccountHistory> getAccountHistory() {
		String username = getAuthenticatedUsername();
		return historyRepository.findByUser(username);
	}
}
