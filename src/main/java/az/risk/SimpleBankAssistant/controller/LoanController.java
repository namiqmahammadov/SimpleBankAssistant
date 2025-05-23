	package az.risk.SimpleBankAssistant.controller;
	
	import org.springframework.beans.factory.annotation.Autowired;
	import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
		@PreAuthorize("hasRole('USER')")
		@PostMapping("/apply")
		public LoanResponse applyForLoan(@RequestBody LoanRequest request) {
			return loanService.applyForLoan(request);
		}
		@PreAuthorize("hasRole('ADMIN')||hasRole('USER')")
		@GetMapping("/debt")
		public ResponseEntity<?> getLoanDebt() {
			return loanService.getLoanDebt();
		}
		 @PreAuthorize("hasRole('ADMIN')||hasRole('USER')")
		@GetMapping("/total-debt")
		public ResponseEntity<?> getTotalLoanDebt() {
			return loanService.getTotalLoanDebt();
		}
		 @PreAuthorize("hasRole('ADMIN')||hasRole('USER')")
		@GetMapping("/history")
		public ResponseEntity<?> getLoanHistory() {
			return loanService.getLoanHistory();
		}
	
	}
