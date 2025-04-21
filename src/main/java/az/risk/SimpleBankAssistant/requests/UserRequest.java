package az.risk.SimpleBankAssistant.requests;


import lombok.Data;

@Data
public class UserRequest {
	String fullname;
	String email;
	String password;

}