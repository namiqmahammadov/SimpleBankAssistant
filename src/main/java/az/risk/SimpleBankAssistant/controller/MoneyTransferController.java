package az.risk.SimpleBankAssistant.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import az.risk.SimpleBankAssistant.entity.MoneyTransfer;
import az.risk.SimpleBankAssistant.requests.TransferRequest;
import az.risk.SimpleBankAssistant.responses.TransferResponse;
import az.risk.SimpleBankAssistant.service.MoneyTransferService;
import az.risk.SimpleBankAssistant.service.OtpService;

@RestController
@RequestMapping("/transfers")
public class MoneyTransferController {

    @Autowired
    private MoneyTransferService transferService;

    @Autowired
    private OtpService otpService;

    private TransferRequest pendingTransferRequest;

    @PostMapping
    public ResponseEntity<String> initiateTransfer(@RequestBody TransferRequest dto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        
        otpService.sendOtpToEmail(email);
        
        this.pendingTransferRequest = dto;

        return ResponseEntity.ok("OTP kodu göndərildi. OTP təsdiqindən sonra transfer həyata keçiriləcək.");
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<TransferResponse> verifyOtp(@RequestBody Map<String, String> request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        String code = request.get("code"); // Map-dən "code" açarı ilə OTP kodu al

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
