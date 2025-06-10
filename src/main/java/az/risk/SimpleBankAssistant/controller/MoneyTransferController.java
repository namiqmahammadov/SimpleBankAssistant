package az.risk.SimpleBankAssistant.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import az.risk.SimpleBankAssistant.requests.OtpVerificationRequest;
import az.risk.SimpleBankAssistant.requests.TransferRequest;
import az.risk.SimpleBankAssistant.responses.TransferResponse;
import az.risk.SimpleBankAssistant.service.MoneyTransferService;
import az.risk.SimpleBankAssistant.service.OtpService;

@RestController
@RequestMapping("/transfers")
@CrossOrigin(origins = "*")
public class MoneyTransferController {

	@Autowired
	private MoneyTransferService transferService;

	@Autowired
	private OtpService otpService;

	private TransferRequest pendingTransferRequest;
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/initiate")
    public ResponseEntity<String> initiateTransfer(@RequestBody TransferRequest dto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        // İndi yoxlamalar + OTP göndərmə bu metodda olacaq
        String message = transferService.validateTransferAndSendOtp(email, dto);

        this.pendingTransferRequest = dto;
        return ResponseEntity.ok(message);
    }

	@PreAuthorize("hasRole('USER')")
	@PostMapping("/verify-otp")
	public ResponseEntity<TransferResponse> verifyOtp(@RequestBody OtpVerificationRequest request) {
	    String email = SecurityContextHolder.getContext().getAuthentication().getName();
	    String code = request.getCode(); // OtpVerificationRequest-dən kodu al

	    boolean isOtpValid = otpService.verifyOtp(email, code);
	    if (!isOtpValid) {
	        throw new RuntimeException("OTP kodu səhvdir və ya vaxtı bitib!");
	    }

	    if (pendingTransferRequest == null) {
	        throw new RuntimeException("Transfer məlumatı tapılmadı. Əvvəl transfer başlatmalısınız.");
	    }

	    TransferResponse response = transferService.transferMoney(email, pendingTransferRequest);
	    pendingTransferRequest = null;

	    return ResponseEntity.ok(response);
	}


}
