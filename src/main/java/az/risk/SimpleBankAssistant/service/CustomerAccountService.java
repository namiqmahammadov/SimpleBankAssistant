package az.risk.SimpleBankAssistant.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import az.risk.SimpleBankAssistant.entity.CustomerAccount;
import az.risk.SimpleBankAssistant.entity.CustomerAccountHistory;
import az.risk.SimpleBankAssistant.enums.CurrencyType;
import az.risk.SimpleBankAssistant.repository.CustomerAccountHistoryRepository;
import az.risk.SimpleBankAssistant.repository.CustomerAccountRepository;
import az.risk.SimpleBankAssistant.requests.AccountCreationRequest;
import az.risk.SimpleBankAssistant.util.CurrencyConverterUtil;
import az.risk.SimpleBankAssistant.util.IbanGenerator;

@Service
public class CustomerAccountService {

	private final CustomerAccountRepository customerAccountRepository;
	private final CurrencyConverterUtil currencyConverterUtil;
	private final CustomerAccountHistoryRepository historyRepository;

	@Autowired
	public CustomerAccountService(CustomerAccountRepository customerAccountRepository,
			CurrencyConverterUtil currencyConverterUtil, CustomerAccountHistoryRepository historyRepository) {
		this.customerAccountRepository = customerAccountRepository;
		this.currencyConverterUtil = currencyConverterUtil;
		this.historyRepository = historyRepository;
	}

	public CustomerAccount createAccount(AccountCreationRequest request) {
		CustomerAccount account = new CustomerAccount();
		account.setCurrency(request.getCurrency());

		account.setIban(IbanGenerator.generateRandomIban());
		account.setAvailableBalance(BigDecimal.ZERO);
		account.setUser(getAuthenticatedUsername());
		account.setIsAccountActive(true);
		

		return customerAccountRepository.save(account);
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
		account.setClosedDate(new Timestamp(System.currentTimeMillis()));

		customerAccountRepository.save(account);
	}

	public BigDecimal convertCurrency(Long accountId, String toCurrencyStr) {
		CustomerAccount account = customerAccountRepository.findById(accountId)
				.orElseThrow(() -> new RuntimeException("Hesab tapılmadı"));

		String fromCurrency = account.getCurrency().name();
		BigDecimal convertedAmount = currencyConverterUtil.convert(account.getAvailableBalance(), fromCurrency,
				toCurrencyStr);

		account.setAvailableBalance(convertedAmount);
		account.setCurrency(CurrencyType.valueOf(toCurrencyStr.toUpperCase()));
		customerAccountRepository.save(account);

		saveAccountHistory(account, convertedAmount, account.getCurrency(), "CURRENCY_CONVERSION");

		return convertedAmount;
	}

	private void saveAccountHistory(CustomerAccount account, BigDecimal amount, CurrencyType currency,
			String operationType) {
		CustomerAccountHistory history = new CustomerAccountHistory();
		history.setIban(account.getIban());
		history.setAmount(amount);
		history.setCurrency(currency);
		history.setUser(account.getUser());
		history.setOperationType(operationType);
		history.setOperationDate(new Timestamp(System.currentTimeMillis()));
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

	public record AccountInfoDTO(Long customerId, String iban) {}

	public List<AccountInfoDTO> getUserIbans() {
	    String username = getAuthenticatedUsername();
	    List<CustomerAccount> accounts = customerAccountRepository.findByUserAndIsAccountActive(username, true);
	    return accounts.stream()
	                   .map(account -> new AccountInfoDTO(account.getAccountId(), account.getIban()))
	                   .toList();
	}


}
