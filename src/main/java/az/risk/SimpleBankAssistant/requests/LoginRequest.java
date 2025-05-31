package az.risk.SimpleBankAssistant.requests;

import lombok.Data;

@Data
public class LoginRequest {
	String email;
	String password;
}
