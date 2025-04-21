package az.risk.SimpleBankAssistant.controller;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import az.risk.SimpleBankAssistant.entity.CustomerAccount;
import az.risk.SimpleBankAssistant.service.CustomerAccountService;

@RestController
@RequestMapping("/accounts")
public class CustomerAccountController {

    private final CustomerAccountService customerAccountService;

    @Autowired
    public CustomerAccountController(CustomerAccountService customerAccountService) {
        this.customerAccountService = customerAccountService;
    }

    // Hesab yaratma
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerAccount createAccount(@RequestBody CustomerAccount customerAccount) {
        return customerAccountService.createAccount(customerAccount);
    }

    // Hesaba balans əlavə etmə
    @PutMapping("/{accountId}/balance")
    public CustomerAccount updateBalance(@PathVariable Long accountId, @RequestParam BigDecimal amount) {
        return customerAccountService.updateBalance(accountId, amount);
    }

    // Hesabın balansını göstərmə
    @GetMapping("/{accountId}/balance")
    public BigDecimal getBalance(@PathVariable Long accountId) {
        return customerAccountService.getBalance(accountId);
    }

    // Hesab bağlama
    @DeleteMapping("/{accountId}")
    public void closeAccount(@PathVariable Long accountId) {
        customerAccountService.closeAccount(accountId);
    }

    // Valyuta çevirmə
    @PutMapping("/{accountId}/convert")
    public BigDecimal convertCurrency(@PathVariable Long accountId,
                                      @RequestParam String fromCurrency,
                                      @RequestParam String toCurrency,
                                      @RequestParam BigDecimal amount) {
        return customerAccountService.convertCurrency(accountId, fromCurrency, toCurrency, amount);
    }
}
