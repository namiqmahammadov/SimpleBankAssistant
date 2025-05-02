package az.risk.SimpleBankAssistant.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import az.risk.SimpleBankAssistant.entity.CustomerAccount;
import az.risk.SimpleBankAssistant.entity.Loan;
import az.risk.SimpleBankAssistant.entity.LoanHistory;
import az.risk.SimpleBankAssistant.repository.CustomerAccountRepository;
import az.risk.SimpleBankAssistant.repository.LoanHistoryRepository;
import az.risk.SimpleBankAssistant.repository.LoanRepository;
import az.risk.SimpleBankAssistant.requests.LoanRequest;
import az.risk.SimpleBankAssistant.responses.LoanResponse;

@Service
public class LoanServiceImpl implements LoanService {

	@Autowired
	private LoanRepository loanRepository;
	@Autowired
	private CustomerAccountRepository customerAccountRepository; 
	
	@Autowired
	private LoanHistoryRepository loanHistoryRepository;
	
	@Override
	public ResponseEntity<?> getLoanHistory() {
	    String currentUser = getUser();
	    var historyList = loanHistoryRepository.findByUser(currentUser);
	    return ResponseEntity.ok(historyList);
	}


	public LoanResponse applyForLoan(LoanRequest request) {
		String currentUser = getUser();

		// İstifadəçinin bütün hesablarını tap
		CustomerAccount account = customerAccountRepository.findAll().stream()
				.filter(acc -> acc.getUser().equals(currentUser) && acc.getIban().endsWith(request.getIbanSuffix()))
				.findFirst()
				.orElseThrow(() -> new RuntimeException("IBAN son 4 rəqəminə uyğun aktiv hesab tapılmadı."));

		// Mövcud istifadəçiyə aid bütün kreditlərin cəmini hesabla
		BigDecimal totalUserLoanAmount = loanRepository.findAll().stream()
				.filter(loan -> loan.getIban() != null
						&& loan.getIban().startsWith(account.getIban().substring(0, account.getIban().length() - 4))
						&& account.getUser().equals(currentUser))
				.map(Loan::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

		// Əgər yeni kreditlə birlikdə məbləğ 2000 AZN-dən çox olursa, istisna at
		BigDecimal totalWithNewLoan = totalUserLoanAmount.add(request.getAmount());
		if (totalWithNewLoan.compareTo(new BigDecimal("2000")) > 0) {
			throw new RuntimeException("Ümumi kredit məbləği 2000 AZN-i keçə bilməz.");
		}

		Loan loan = new Loan();
		loan.setAmount(request.getAmount());
		loan.setLoanTermInMonths(request.getLoanTermInMonths());
		loan.setLoanPurpose(request.getLoanPurpose());
		loan.setIban(account.getIban());

		double interest = request.getLoanTermInMonths() <= 12 ? 8.5 : 10.0;
		loan.setInterestRate(interest);

		// Əgər artıq aktiv kredit varsa, yenə də yeni kredit aktiv olacaq
		loan.setActive(true);

		Loan saved = loanRepository.save(loan);

		LoanResponse response = new LoanResponse();
		response.setId(saved.getId());
		response.setAmount(saved.getAmount());
		response.setLoanTermInMonths(saved.getLoanTermInMonths());
		response.setInterestRate(saved.getInterestRate());
		response.setLoanPurpose(saved.getLoanPurpose());
		response.setActive(saved.isActive());
		response.setIban(saved.getIban());
		
		LoanHistory history = new LoanHistory();
		history.setLoanId(saved.getId());
		history.setAmount(saved.getAmount());
		history.setInterestRate(saved.getInterestRate());
		history.setLoanTermInMonths(saved.getLoanTermInMonths());
		history.setLoanPurpose(saved.getLoanPurpose());
		history.setIban(saved.getIban());
		history.setOperationType("APPLY");
		history.setOperationDate(LocalDateTime.now());
		history.setUser(currentUser);
		loanHistoryRepository.save(history);

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

	@Override
	public ResponseEntity<?> getTotalLoanDebt() {
		String currentUser = getUser();

		// İstifadəçiyə aid bütün kreditlər
		var userIbans = customerAccountRepository.findAll().stream().filter(acc -> acc.getUser().equals(currentUser))
				.map(CustomerAccount::getIban).toList();

		var userLoans = loanRepository.findAll().stream().filter(loan -> userIbans.contains(loan.getIban())).toList();

		BigDecimal totalDebt = BigDecimal.ZERO;

		for (Loan loan : userLoans) {
			BigDecimal principal = loan.getAmount();
			BigDecimal interest = principal.multiply(BigDecimal.valueOf(loan.getInterestRate() / 100.0));
			totalDebt = totalDebt.add(principal.add(interest));
		}

		Map<String, Object> response = new HashMap<>();
		response.put("loanCount", userLoans.size());
		response.put("totalDebt", totalDebt);
		response.put("details", userLoans.stream().map(loan -> {
			Map<String, Object> map = new HashMap<>();
			BigDecimal interestAmount = loan.getAmount().multiply(BigDecimal.valueOf(loan.getInterestRate() / 100.0));
			map.put("loanId", loan.getId());
			map.put("amount", loan.getAmount());
			map.put("interestRate", loan.getInterestRate());
			map.put("interestAmount", interestAmount);
			map.put("totalWithInterest", loan.getAmount().add(interestAmount));
			map.put("loanTermInMonths", loan.getLoanTermInMonths());
			map.put("iban", loan.getIban());
			return map;
		}).toList());

		return ResponseEntity.ok(response);
	}

	private String getUser() {
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}
}
