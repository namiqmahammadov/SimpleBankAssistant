package az.risk.SimpleBankAssistant.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import az.risk.SimpleBankAssistant.entity.CustomerAccountHistory;

public interface CustomerAccountHistoryRepository extends JpaRepository<CustomerAccountHistory, Long> {
	List<CustomerAccountHistory> findByUser(String user);
}
