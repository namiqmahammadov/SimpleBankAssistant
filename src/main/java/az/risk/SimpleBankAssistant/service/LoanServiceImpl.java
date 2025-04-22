package az.risk.SimpleBankAssistant.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import az.risk.SimpleBankAssistant.entity.CustomerAccount;
import az.risk.SimpleBankAssistant.entity.Loan;
import az.risk.SimpleBankAssistant.repository.CustomerAccountRepository;
import az.risk.SimpleBankAssistant.repository.LoanRepository;
import az.risk.SimpleBankAssistant.requests.LoanRequest;
import az.risk.SimpleBankAssistant.responses.LoanResponse;

@Service
public class LoanServiceImpl implements LoanService {

	@Autowired
	private LoanRepository loanRepository;
	@Autowired
	private CustomerAccountRepository customerAccountRepository; // IBAN-ı yoxlamaq üçün
	public LoanResponse applyForLoan(LoanRequest request) {
	    String currentUser = getUser();

	    // İstifadəçinin bütün hesablarını tap
	    CustomerAccount account = customerAccountRepository.findAll()
	        .stream()
	        .filter(acc -> acc.getUser().equals(currentUser) && acc.getIban().endsWith(request.getIbanSuffix()))
	        .findFirst()
	        .orElseThrow(() -> new RuntimeException("IBAN son 4 rəqəminə uyğun aktiv hesab tapılmadı."));

	    Loan loan = new Loan();
	    loan.setAmount(request.getAmount());
	    loan.setLoanTermInMonths(request.getLoanTermInMonths());
	    loan.setLoanPurpose(request.getLoanPurpose());
	    loan.setIban(account.getIban()); // Tam IBAN qeyd olunur

	    double interest = request.getLoanTermInMonths() <= 12 ? 8.5 : 10.0;
	    loan.setInterestRate(interest);

	    boolean hasActiveLoan = loanRepository.existsByIsActiveTrue();
	    loan.setActive(!hasActiveLoan);

	    Loan saved = loanRepository.save(loan);

	    LoanResponse response = new LoanResponse();
	    response.setId(saved.getId());
	    response.setAmount(saved.getAmount());
	    response.setLoanTermInMonths(saved.getLoanTermInMonths());
	    response.setInterestRate(saved.getInterestRate());
	    response.setLoanPurpose(saved.getLoanPurpose());
	    response.setActive(saved.isActive());
	    response.setIban(saved.getIban());

	    return response;
	}
	@Override
	public ResponseEntity<?> getLoanDebt() {
		// Aktiv krediti tap
		Loan activeLoan = loanRepository.findAll().stream().filter(Loan::isActive) // Aktiv kredit tapılır
				.findFirst().orElse(null);

		if (activeLoan == null) {
			return ResponseEntity.ok("Hazırda aktiv kredit yoxdur.");
		}

		// Borcun hesablanması: ana məbləğ + faiz
		BigDecimal principal = activeLoan.getAmount();
		double interestRate = activeLoan.getInterestRate() / 100.0;
		BigDecimal totalDebt = principal.add(principal.multiply(BigDecimal.valueOf(interestRate)));

		Map<String, Object> response = new HashMap<>();
		response.put("loanId", activeLoan.getId());
		response.put("principal", principal);
		response.put("interestRate", activeLoan.getInterestRate());
		response.put("loanTermInMonths", activeLoan.getLoanTermInMonths());
		response.put("totalDebt", totalDebt);
		response.put("loanPurpose", activeLoan.getLoanPurpose());

		return ResponseEntity.ok(response);
	}

	private String getUser() {
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}
}
