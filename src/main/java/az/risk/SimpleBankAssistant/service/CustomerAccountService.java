package az.risk.SimpleBankAssistant.service;


import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import az.risk.SimpleBankAssistant.entity.CustomerAccount;
import az.risk.SimpleBankAssistant.repository.CustomerAccountRepository;
import az.risk.SimpleBankAssistant.util.CurrencyConverterUtil;
import az.risk.SimpleBankAssistant.util.IbanGenerator;

@Service
public class CustomerAccountService {

    private final CustomerAccountRepository customerAccountRepository;
    private final CurrencyConverterUtil currencyConverterUtil;

    @Autowired
    public CustomerAccountService(CustomerAccountRepository customerAccountRepository, CurrencyConverterUtil currencyConverterUtil) {
        this.customerAccountRepository = customerAccountRepository;
        this.currencyConverterUtil = currencyConverterUtil;
    }

    // Hesab yaratma
    public CustomerAccount createAccount(CustomerAccount customerAccount) {
    	   customerAccount.setIban(IbanGenerator.generateRandomIban());
        return customerAccountRepository.save(customerAccount);
    }

    // Hesaba balans əlavə etmə
    public CustomerAccount updateBalance(Long accountId, BigDecimal amount) {
        Optional<CustomerAccount> accountOptional = customerAccountRepository.findById(accountId);
        if (accountOptional.isPresent()) {
            CustomerAccount account = accountOptional.get();
            account.setAvailableBalance(account.getAvailableBalance().add(amount)); // Balans artımı
            return customerAccountRepository.save(account);
        }
        throw new RuntimeException("Hesab tapılmadı");
    }

    // Hesabın balansını göstərmə
    public BigDecimal getBalance(Long accountId) {
        Optional<CustomerAccount> accountOptional = customerAccountRepository.findById(accountId);
        if (accountOptional.isPresent()) {
            return accountOptional.get().getAvailableBalance();
        }
        throw new RuntimeException("Hesab tapılmadı");
    }

    // Hesab bağlama
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

    // Valyuta çevirmə funksiyası
    public BigDecimal convertCurrency(Long accountId, String fromCurrency, String toCurrency, BigDecimal amount) {
        Optional<CustomerAccount> accountOptional = customerAccountRepository.findById(accountId);
        if (accountOptional.isPresent()) {
            BigDecimal convertedAmount = currencyConverterUtil.convert(amount, fromCurrency, toCurrency);
            return convertedAmount;
        }
        throw new RuntimeException("Hesab tapılmadı");
    }
}
