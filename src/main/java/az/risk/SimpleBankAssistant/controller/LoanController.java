package az.risk.SimpleBankAssistant.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import az.risk.SimpleBankAssistant.requests.LoanRequest;
import az.risk.SimpleBankAssistant.responses.LoanResponse;
import az.risk.SimpleBankAssistant.service.LoanService;

@RestController
@RequestMapping("/loans")
public class LoanController {

	@Autowired
	private LoanService loanService;

	@PostMapping("/apply")
	public LoanResponse applyForLoan(@RequestBody LoanRequest request) {
		return loanService.applyForLoan(request);
	}

	@GetMapping("/debt")
	public ResponseEntity<?> getLoanDebt() {
		return loanService.getLoanDebt();
	}

	@GetMapping("/total-debt")
	public ResponseEntity<?> getTotalLoanDebt() {
		return loanService.getTotalLoanDebt();
	}

	@GetMapping("/history")
	public ResponseEntity<?> getLoanHistory() {
		return loanService.getLoanHistory();
	}

}
