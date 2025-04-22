package az.risk.SimpleBankAssistant.service;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import az.risk.SimpleBankAssistant.entity.CustomerAccount;
import az.risk.SimpleBankAssistant.enums.CurrencyType;
import az.risk.SimpleBankAssistant.repository.CustomerAccountRepository;
import az.risk.SimpleBankAssistant.util.CurrencyConverterUtil;
import az.risk.SimpleBankAssistant.util.IbanGenerator;

@Service
public class CustomerAccountService {

	private final CustomerAccountRepository customerAccountRepository;
	private final CurrencyConverterUtil currencyConverterUtil;

	@Autowired
	public CustomerAccountService(CustomerAccountRepository customerAccountRepository,
			CurrencyConverterUtil currencyConverterUtil) {
		this.customerAccountRepository = customerAccountRepository;
		this.currencyConverterUtil = currencyConverterUtil;
	}

	public CustomerAccount createAccount(CustomerAccount customerAccount) {

		customerAccount.setIban(IbanGenerator.generateRandomIban());

		customerAccount.setUser(getUser());
		return customerAccountRepository.save(customerAccount);
	}

	public CustomerAccount updateBalance(Long accountId, BigDecimal amount) {
		Optional<CustomerAccount> accountOptional = customerAccountRepository.findById(accountId);
		if (accountOptional.isPresent()) {
			CustomerAccount account = accountOptional.get();
			account.setAvailableBalance(account.getAvailableBalance().add(amount)); // Balans artımı
			return customerAccountRepository.save(account);
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

	public BigDecimal convertCurrency(Long accountId, String fromCurrency, String toCurrency) {
		Optional<CustomerAccount> accountOptional = customerAccountRepository.findById(accountId);

		if (accountOptional.isPresent()) {
			CustomerAccount account = accountOptional.get();

			BigDecimal currentBalance = account.getAvailableBalance();

			BigDecimal convertedAmount = currencyConverterUtil.convert(currentBalance, fromCurrency, toCurrency);

			account.setAvailableBalance(convertedAmount);
			account.setCurrency(CurrencyType.valueOf(toCurrency.toUpperCase()));

			customerAccountRepository.save(account);

			return convertedAmount;
		}

		throw new RuntimeException("Hesab tapılmadı");
	}

	private String getUser() {
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}
}