package az.risk.SimpleBankAssistant.repository;

import az.risk.SimpleBankAssistant.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanRepository extends JpaRepository<Loan, Long> {
	boolean existsByIsActiveTrue();
}
