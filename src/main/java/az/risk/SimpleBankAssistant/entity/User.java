package az.risk.SimpleBankAssistant.entity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import az.risk.SimpleBankAssistant.enums.Role;
import lombok.Data;

@Entity
@Table(name = "user")
@Data
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String fullname;
	private String email;
	private String password;
	private boolean enabled;
	@Enumerated(EnumType.STRING)
	private Role role;

}
