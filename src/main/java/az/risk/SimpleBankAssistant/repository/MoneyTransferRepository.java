package az.risk.SimpleBankAssistant.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import az.risk.SimpleBankAssistant.entity.MoneyTransfer;

@Repository
public interface MoneyTransferRepository extends JpaRepository<MoneyTransfer, Long> {
    List<MoneyTransfer> findBySenderIban(String senderIban);
}