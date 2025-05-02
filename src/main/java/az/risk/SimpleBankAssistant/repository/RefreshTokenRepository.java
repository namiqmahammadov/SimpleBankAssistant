package az.risk.SimpleBankAssistant.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import az.risk.SimpleBankAssistant.entity.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

	RefreshToken findByUserId(Long userId);

}
