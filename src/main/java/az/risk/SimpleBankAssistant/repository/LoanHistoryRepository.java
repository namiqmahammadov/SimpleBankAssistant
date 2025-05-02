package az.risk.SimpleBankAssistant.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import az.risk.SimpleBankAssistant.entity.LoanHistory;

public interface LoanHistoryRepository extends JpaRepository<LoanHistory, Long> {
	List<LoanHistory> findByUser(String user);
}
