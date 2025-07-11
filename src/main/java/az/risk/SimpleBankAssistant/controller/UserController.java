package az.risk.SimpleBankAssistant.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import az.risk.SimpleBankAssistant.entity.User;
import az.risk.SimpleBankAssistant.enums.Role;
import az.risk.SimpleBankAssistant.exception.UserNotFoundException;
import az.risk.SimpleBankAssistant.responses.UserResponse;
import az.risk.SimpleBankAssistant.service.UserService;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class UserController {

	private UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping
	public List<UserResponse> getAllUsers() {
		return userService.getAllUsers().stream().map(u -> new UserResponse(u)).toList();
	}
	@PreAuthorize("hasRole('USER')")
	@PostMapping
	public ResponseEntity<Void> createUser(@RequestBody User newUser) {
		User user = userService.saveOneUser(newUser);
		if (user != null)
			
			return new ResponseEntity<>(HttpStatus.CREATED);
		return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	}
   @PreAuthorize("hasRole('USER')")
	@GetMapping("/{userId}")	
	public UserResponse getOneUser(@PathVariable Long userId) {
		User user = userService.getOneUserById(userId);
		if (user == null) {
			throw new UserNotFoundException();
		}
		return new UserResponse(user);
	}
   @PreAuthorize("hasRole('ADMIN')||hasRole('USER')")
	public ResponseEntity<Void> updateOneUser(@PathVariable Long userId, @RequestBody User newUser) {
		User user = userService.updateOneUser(userId, newUser);
		if (user != null)
			return new ResponseEntity<>(HttpStatus.OK);
		return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

	}
   @PreAuthorize("hasRole('ADMIN')")
   @PutMapping("/{userId}/role")
   public ResponseEntity<Void> updateUserRole(@PathVariable Long userId, @RequestBody Role newRole) {
       boolean isUpdated = userService.updateUserRole(userId, newRole);
       if (isUpdated) {
           return ResponseEntity.ok().build();
       }
       return ResponseEntity.notFound().build();
   }

	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/{userId}")
	public void deleteOneUser(@PathVariable Long userId) {
		userService.deleteById(userId);
	}

	@ExceptionHandler(UserNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	private void handleUserNotFound() {

	}
}