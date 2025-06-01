package az.risk.SimpleBankAssistant.requests;

import lombok.Data;

@Data
public class OtpVerificationRequest {
	private String email;
    private String code;

}