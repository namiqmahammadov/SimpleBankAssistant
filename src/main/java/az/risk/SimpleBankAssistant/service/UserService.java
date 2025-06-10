package az.risk.SimpleBankAssistant.service;

import java.util.List;
import java.util.Optional;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import az.risk.SimpleBankAssistant.entity.User;
import az.risk.SimpleBankAssistant.enums.Role;
import az.risk.SimpleBankAssistant.repository.UserRepository;

@Service
public class UserService {

	private UserRepository userRepository;
	private PasswordEncoder passwordEncoder;

	public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	public User saveOneUser(User newUser) {
		return userRepository.save(newUser);
	}

	public User getOneUserById(Long userId) {
		return userRepository.findById(userId).orElse(null);
	}

	public User updateOneUser(Long userId, User newUser) {
		Optional<User> user = userRepository.findById(userId);
		if (user.isPresent()) {
			User foundUser = user.get();
			foundUser.setFullname(newUser.getFullname());
			foundUser.setEmail(newUser.getEmail());
			foundUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
			userRepository.save(foundUser);
			return foundUser;
		} else
			return null;
	}

	public boolean updateUserRole(Long userId, Role newRole) {
		Optional<User> userOpt = userRepository.findById(userId);
		if (userOpt.isPresent()) {
			User user = userOpt.get();
			user.setRole(newRole);
			userRepository.save(user);
			return true;
		}
		return false;
	}

	public void deleteById(Long userId) {
		try {
			userRepository.deleteById(userId);
		} catch (EmptyResultDataAccessException e) {
			System.out.println("User " + userId + " doesn't exist");
		}
	}

	public User getOneUserByEmail(String email) {
		return userRepository.findByEmail(email);
	}

}