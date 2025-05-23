package az.risk.SimpleBankAssistant.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import az.risk.SimpleBankAssistant.entity.CustomerAccountHistory;
import az.risk.SimpleBankAssistant.entity.MoneyTransfer;
import az.risk.SimpleBankAssistant.service.CustomerAccountService;
import az.risk.SimpleBankAssistant.service.LoanService;
import az.risk.SimpleBankAssistant.service.MoneyTransferService;

@RestController
@RequestMapping("/history")
public class HistoryController {

	@Autowired
	private CustomerAccountService customerAccountService;
	@Autowired
	private LoanService loanService;
	@Autowired
	private MoneyTransferService moneyTransferService;
	@PreAuthorize("hasRole('ADMIN')||hasRole('USER')")
	@GetMapping("/transfer")
	public List<MoneyTransfer> getTransferHistory() {

		return moneyTransferService.getTransferHistory();

	}
	@PreAuthorize("hasRole('ADMIN')||hasRole('USER')")
	@GetMapping("/customer")
	public List<CustomerAccountHistory> getAccountHistory() {
		return customerAccountService.getAccountHistory();
	}
	@PreAuthorize("hasRole('ADMIN')||hasRole('USER')")
	@GetMapping("/loan")
	public ResponseEntity<?> getLoanHistory() {
		return loanService.getLoanHistory();
	}

}