package az.risk.SimpleBankAssistant.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import az.risk.SimpleBankAssistant.entity.CustomerAccount;
import az.risk.SimpleBankAssistant.requests.BalanceUpdateRequest;
import az.risk.SimpleBankAssistant.requests.CurrencyConversionRequest;
import az.risk.SimpleBankAssistant.service.CustomerAccountService;

@RestController
@RequestMapping("/accounts")
public class CustomerAccountController {

    private final CustomerAccountService customerAccountService;

    @Autowired
    public CustomerAccountController(CustomerAccountService customerAccountService) {
        this.customerAccountService = customerAccountService;
    }

   
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerAccount createAccount(@RequestBody CustomerAccount customerAccount) {
        return customerAccountService.createAccount(customerAccount);
    }

   
    @PutMapping("/{accountId}/balance")
    public CustomerAccount updateBalance(@PathVariable Long accountId,@RequestBody BalanceUpdateRequest request) {
        return customerAccountService.updateBalance(accountId, request.getAmount());
    }

  
    @GetMapping("/{accountId}/balance")
    public BigDecimal getBalance(@PathVariable Long accountId) {
        return customerAccountService.getBalance(accountId);
    }


    @DeleteMapping("/{accountId}")
    public void closeAccount(@PathVariable Long accountId) {
        customerAccountService.closeAccount(accountId);
    }

    @PutMapping("/{accountId}/convert")
    public BigDecimal convertCurrency(@PathVariable Long accountId,
                                      @RequestBody CurrencyConversionRequest request) {
        return customerAccountService.convertCurrency(accountId, request.getToCurrency());
    }


}
