package az.risk.SimpleBankAssistant.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import az.risk.SimpleBankAssistant.entity.CustomerAccount;
import az.risk.SimpleBankAssistant.entity.MoneyTransfer;
import az.risk.SimpleBankAssistant.repository.CustomerAccountRepository;
import az.risk.SimpleBankAssistant.repository.MoneyTransferRepository;
import az.risk.SimpleBankAssistant.requests.TransferRequest;
import az.risk.SimpleBankAssistant.responses.TransferResponse;
@Service
public class MoneyTransferService {

    @Autowired
    private CustomerAccountRepository accountRepository;

    @Autowired
    private MoneyTransferRepository transferRepository;

    @Autowired
    private OtpService otpService;
    public TransferResponse transferMoney(String senderUsername, TransferRequest dto) {
        List<CustomerAccount> senderAccounts = accountRepository.findByUser(senderUsername);
        if (senderAccounts.isEmpty()) {
            throw new RuntimeException("Göndərən hesab tapılmadı");
        }

        CustomerAccount sender = senderAccounts.stream()
            .filter(acc -> acc.getIban().equals(dto.getSenderIban()))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Göndərən IBAN tapılmadı"));

    
        CustomerAccount receiver = accountRepository.findByIban(dto.getReceiverIban())
            .orElseThrow(() -> new RuntimeException("Qəbul edən IBAN tapılmadı"));

        if (sender.getAvailableBalance().compareTo(dto.getAmount()) < 0) {
            throw new RuntimeException("Balans kifayət etmir");
        }

        // Balansı dəyiş
        sender.setAvailableBalance(sender.getAvailableBalance().subtract(dto.getAmount()));
        receiver.setAvailableBalance(receiver.getAvailableBalance().add(dto.getAmount()));

        // yadda saxla
        accountRepository.save(sender);
        accountRepository.save(receiver);

        MoneyTransfer transfer = new MoneyTransfer();
        transfer.setSenderIban(sender.getIban());
        transfer.setReceiverIban(dto.getReceiverIban());
        transfer.setAmount(dto.getAmount());
        transfer.setTransferDate(LocalDateTime.now());
        transfer.setIsSuccessful(true);
        transferRepository.save(transfer);

        TransferResponse response = new TransferResponse();
        response.setMessage("Köçürmə uğurla tamamlandı");
        response.setDate(transfer.getTransferDate());
        return response;
    }

    public List<MoneyTransfer> getTransferHistory(String username) {
        List<CustomerAccount> accounts = accountRepository.findByUser(username);
        if (accounts.isEmpty()) {
            throw new RuntimeException("İstifadəçi hesabı tapılmadı");
        }

        return accounts.stream()
            .map(account -> transferRepository.findBySenderIban(account.getIban()))
            .flatMap(List::stream)
            .collect(Collectors.toList());
    }

}
