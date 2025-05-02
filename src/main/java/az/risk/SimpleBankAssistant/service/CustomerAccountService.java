package az.risk.SimpleBankAssistant.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
			CurrencyConverterUtil currencyConverterUtil, CustomerAccountHistoryRepository historyRepository) {
		this.customerAccountRepository = customerAccountRepository;
		this.currencyConverterUtil = currencyConverterUtil;
		this.historyRepository = historyRepository;
	}

	public CustomerAccount createAccount(CustomerAccount customerAccount) {

		customerAccount.setIban(IbanGenerator.generateRandomIban());
		customerAccount.setAvailableBalance(BigDecimal.ZERO);
		customerAccount.setUser(getUser());
		return customerAccountRepository.save(customerAccount);
	}

	public CustomerAccount updateBalance(Long accountId, BigDecimal amount) {
		Optional<CustomerAccount> accountOptional = customerAccountRepository.findById(accountId);

		if (accountOptional.isPresent()) {
			CustomerAccount account = accountOptional.get();
			BigDecimal currentBalance = account.getAvailableBalance() != null ? account.getAvailableBalance()
					: BigDecimal.ZERO;
			BigDecimal newBalance = currentBalance.add(amount);
			account.setAvailableBalance(newBalance);

			customerAccountRepository.save(account);

			// Tarixçəyə əlavə et
			CustomerAccountHistory history = new CustomerAccountHistory();
			history.setIban(account.getIban());
			history.setAmount(amount);
			history.setCurrency(account.getCurrency());
			history.setUser(account.getUser());
			history.setOperationType("BALANCE_UPDATE");
			history.setOperationDate(LocalDateTime.now());
			historyRepository.save(history);

			return account;
		}

		throw new RuntimeException("Hesab tapılmadı");
	}

	public BigDecimal getBalance(Long accountId) {
		Optional<CustomerAccount> accountOptional = customerAccountRepository.findById(accountId);
		if (accountOptional.isPresent()) {
			return accountOptional.get().getAvailableBalance();
		}
		throw new RuntimeException("Hesab tapılmadı");
	}

	public void closeAccount(Long accountId) {
		Optional<CustomerAccount> accountOptional = customerAccountRepository.findById(accountId);
		if (accountOptional.isPresent()) {
			CustomerAccount account = accountOptional.get();
			account.setIsAccountActive(false); // Hesab aktiv deyil olaraq qeyd edilir
			customerAccountRepository.save(account);
		} else {
			throw new RuntimeException("Hesab tapılmadı");
		}
	}

	public BigDecimal convertCurrency(Long accountId, String toCurrency) {
		Optional<CustomerAccount> accountOptional = customerAccountRepository.findById(accountId);

		if (accountOptional.isPresent()) {
			CustomerAccount account = accountOptional.get();

			String fromCurrency = account.getCurrency().name(); // Mövcud valyuta

			BigDecimal currentBalance = account.getAvailableBalance();
			BigDecimal convertedAmount = currencyConverterUtil.convert(currentBalance, fromCurrency, toCurrency);

			account.setAvailableBalance(convertedAmount);
			account.setCurrency(CurrencyType.valueOf(toCurrency.toUpperCase()));
			customerAccountRepository.save(account);

			// Tarixçəyə əlavə et
			CustomerAccountHistory history = new CustomerAccountHistory();
			history.setIban(account.getIban());
			history.setAmount(convertedAmount);
			history.setCurrency(CurrencyType.valueOf(toCurrency.toUpperCase()));
			history.setUser(account.getUser());
			history.setOperationType("CURRENCY_CONVERSION");
			history.setOperationDate(LocalDateTime.now());
			historyRepository.save(history);

			return convertedAmount;
		}

		throw new RuntimeException("Hesab tapılmadı");
	}

	private String getUser() {
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}

	public List<CustomerAccount> getUserAccounts(String user) {
		return customerAccountRepository.findByUserAndIsAccountActive(user, true);
	}

	public List<CustomerAccountHistory> getAccountHistory() {
		String username = getUser();
		return historyRepository.findByUser(username);
	}

}