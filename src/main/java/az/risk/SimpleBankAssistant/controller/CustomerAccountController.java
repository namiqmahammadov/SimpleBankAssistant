package az.risk.SimpleBankAssistant.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
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
import az.risk.SimpleBankAssistant.requests.AccountCreationRequest;
import az.risk.SimpleBankAssistant.requests.BalanceUpdateRequest;
import az.risk.SimpleBankAssistant.requests.CurrencyConversionRequest;
import az.risk.SimpleBankAssistant.service.CustomerAccountService;
import az.risk.SimpleBankAssistant.service.CustomerAccountService.AccountInfoDTO;

@RestController
@RequestMapping("/accounts")
//@CrossOrigin(origins = "*")
public class CustomerAccountController {

	private final CustomerAccountService customerAccountService;

	@Autowired
	public CustomerAccountController(CustomerAccountService customerAccountService) {
		this.customerAccountService = customerAccountService;
	}

	@PreAuthorize("hasRole('USER')")
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public CustomerAccount createAccount(@RequestBody AccountCreationRequest customerAccount) {
		System.out.println("dbye melumat elave edildi");
		return customerAccountService.createAccount(customerAccount);
	}

	@PreAuthorize("hasRole('USER')")
	@PutMapping("/{accountId}/balance")
	public CustomerAccount updateBalance(@PathVariable Long accountId, @RequestBody BalanceUpdateRequest request) {
		return customerAccountService.updateBalance(accountId, request.getAmount());
	}

	@PreAuthorize("hasRole('USER')")
	@GetMapping("/{accountId}/balance")
	public BigDecimal getBalance(@PathVariable Long accountId) {
		return customerAccountService.getBalance(accountId);
	}

	@PreAuthorize("hasRole('USER')")
	@DeleteMapping("/{accountId}")
	public void closeAccount(@PathVariable Long accountId) {
		customerAccountService.closeAccount(accountId);
	}

	@PreAuthorize("hasRole('USER')")
	@PutMapping("/{accountId}/convert")
	public BigDecimal convertCurrency(@PathVariable Long accountId, @RequestBody CurrencyConversionRequest request) {
		return customerAccountService.convertCurrency(accountId, request.getToCurrency());
	}

	@PreAuthorize("hasRole('USER')")
	@GetMapping("/my-ibans")
	public List<AccountInfoDTO> getMyIbans() {
		return customerAccountService.getUserIbans();
	}

}
