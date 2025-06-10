package az.risk.SimpleBankAssistant.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import az.risk.SimpleBankAssistant.entity.CustomerAccount;
import az.risk.SimpleBankAssistant.entity.MoneyTransfer;
import az.risk.SimpleBankAssistant.exception.CheckTransferException;
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
	
	  public String validateTransferAndSendOtp(String senderUsername, TransferRequest dto) {
	        // 1. Göndərən hesabı tap
	        List<CustomerAccount> senderAccounts = accountRepository.findByUser(senderUsername);
	        if (senderAccounts.isEmpty()) {
	            throw new RuntimeException("Göndərən hesab tapılmadı");
	        }

	        CustomerAccount sender = senderAccounts.stream()
	                .filter(acc -> acc.getIban().equals(dto.getSenderIban()))
	                .findFirst()
	                .orElseThrow(() -> new CheckTransferException("Göndərən IBAN tapılmadı"));

	        // 2. Qəbul edən hesabı tap
	        CustomerAccount receiver = accountRepository.findByIban(dto.getReceiverIban())
	                .orElseThrow(() -> new CheckTransferException("Qəbul edən IBAN tapılmadı"));

	        // 3. Balans yoxla
	        if (sender.getAvailableBalance().compareTo(dto.getAmount()) < 0) {
	            throw new CheckTransferException("Balans kifayət etmir");
	        }

	        // Əgər buraya gəldisə, yoxlamalar uğurludur
	        otpService.sendOtpToEmail(senderUsername);

	        return "OTP kodu göndərildi. OTP təsdiqindən sonra transfer həyata keçiriləcək.";
	    }

	public TransferResponse transferMoney(String senderUsername, TransferRequest dto) {
		List<CustomerAccount> senderAccounts = accountRepository.findByUser(senderUsername);
		if (senderAccounts.isEmpty()) {
			throw new CheckTransferException("Göndərən hesab tapılmadı");
		}

		CustomerAccount sender = senderAccounts.stream().filter(acc -> acc.getIban().equals(dto.getSenderIban()))
				.findFirst().orElseThrow(() -> new CheckTransferException("Göndərən IBAN tapılmadı"));

		CustomerAccount receiver = accountRepository.findByIban(dto.getReceiverIban())
				.orElseThrow(() -> new CheckTransferException("Qəbul edən IBAN tapılmadı"));

		if (sender.getAvailableBalance().compareTo(dto.getAmount()) < 0) {
			throw new CheckTransferException("Balans kifayət etmir");
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
		transfer.setTransferDate(new Timestamp(System.currentTimeMillis()));
		transfer.setIsSuccessful(true);
		transferRepository.save(transfer);

		TransferResponse response = new TransferResponse();
		response.setMessage("Köçürmə uğurla tamamlandı");
		response.setDate(transfer.getTransferDate());
		return response;
	}

	private String getUser() {
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}

	public List<MoneyTransfer> getTransferHistory() {
		String username = getUser();
		List<CustomerAccount> accounts = accountRepository.findByUser(username);
		if (accounts.isEmpty()) {
			throw new CheckTransferException("İstifadəçi hesabı tapılmadı");
		}

		return accounts.stream().map(account -> transferRepository.findBySenderIban(account.getIban()))
				.flatMap(List::stream).collect(Collectors.toList());
	}

}
