package az.risk.SimpleBankAssistant.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import az.risk.SimpleBankAssistant.entity.CustomerAccount;

@Repository
public interface CustomerAccountRepository extends JpaRepository<CustomerAccount, Long> {
	Optional<CustomerAccount> findByIban(String iban);

	List<CustomerAccount> findByUser(String user);

	List<CustomerAccount> findByUserAndIsAccountActive(String user, boolean isAccountActive);
	List<CustomerAccount> findByIsAccountActive(boolean isActive);


}