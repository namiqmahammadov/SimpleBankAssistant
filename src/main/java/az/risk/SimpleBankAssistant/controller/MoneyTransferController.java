package az.risk.SimpleBankAssistant.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

  

    @PostMapping("/verify-otp")
    public ResponseEntity<Boolean> verifyOtp(@RequestParam String code) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean result = otpService.verifyOtp(email, code);
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<TransferResponse> transferMoney(@RequestBody TransferRequest dto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        otpService.sendOtpToEmail(email);
        return ResponseEntity.ok(transferService.transferMoney(email, dto));
    }

    @GetMapping("/history")
    public ResponseEntity<List<MoneyTransfer>> getHistory() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(transferService.getTransferHistory(username));
    }
}
