package az.risk.SimpleBankAssistant.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import az.risk.SimpleBankAssistant.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

	User findByEmail(String email);

}
