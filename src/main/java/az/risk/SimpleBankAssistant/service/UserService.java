package az.risk.SimpleBankAssistant.service;

import java.util.List;
import java.util.Optional;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import az.risk.SimpleBankAssistant.entity.User;
import az.risk.SimpleBankAssistant.repository.UserRepository;

@Service
public class UserService {

	private UserRepository userRepository;

	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;

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
			foundUser.setPassword(newUser.getPassword());

			userRepository.save(foundUser);
			return foundUser;
		} else
			return null;
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