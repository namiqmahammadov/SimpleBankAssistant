package az.risk.SimpleBankAssistant.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import az.risk.SimpleBankAssistant.entity.OtpCode;

public interface OtpCodeRepository extends JpaRepository<OtpCode, Long> {
    OtpCode findTopByEmailAndCodeAndUsedFalseOrderByExpiryDateDesc(String email, String code);
}
