package az.risk.SimpleBankAssistant.responses;

import az.risk.SimpleBankAssistant.entity.User;
import az.risk.SimpleBankAssistant.enums.Role;
import lombok.Data;

@Data
public class UserResponse {
	private Long id;
	private String fullname;
	private String email;
	private boolean enabled;
	private Role role;

	public UserResponse(User entity) {
		this.id = entity.getId();
		this.fullname = entity.getFullname();
		this.email = entity.getEmail();
		this.role = entity.getRole();
	}
}
