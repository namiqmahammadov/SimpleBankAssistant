package az.risk.SimpleBankAssistant.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import az.risk.SimpleBankAssistant.entity.RefreshToken;
import az.risk.SimpleBankAssistant.entity.User;
import az.risk.SimpleBankAssistant.requests.RefreshRequest;
import az.risk.SimpleBankAssistant.requests.UserRequest;
import az.risk.SimpleBankAssistant.responses.AuthResponse;
import az.risk.SimpleBankAssistant.security.JwtTokenProvider;
import az.risk.SimpleBankAssistant.service.OtpService;
import az.risk.SimpleBankAssistant.service.RefreshTokenService;
import az.risk.SimpleBankAssistant.service.UserService;

@RestController
@RequestMapping("/auth")
public class AuthController {

	private AuthenticationManager authenticationManager;

	private JwtTokenProvider jwtTokenProvider;

	private UserService userService;

	private PasswordEncoder passwordEncoder;

	private RefreshTokenService refreshTokenService;

	private OtpService otpService;

	public AuthController(AuthenticationManager authenticationManager, UserService userService,
			PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider, RefreshTokenService refreshTokenService,
			OtpService otpService) {
		this.authenticationManager = authenticationManager;
		this.userService = userService;
		this.passwordEncoder = passwordEncoder;
		this.jwtTokenProvider = jwtTokenProvider;
		this.refreshTokenService = refreshTokenService;
		this.otpService = otpService;
	}

	@PostMapping("/login")
	public AuthResponse login(@RequestBody UserRequest loginRequest) {
		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),
				loginRequest.getPassword());
		Authentication auth = authenticationManager.authenticate(authToken);
		SecurityContextHolder.getContext().setAuthentication(auth);
		String jwtToken = jwtTokenProvider.generateJwtToken(auth);
		User user = userService.getOneUserByEmail(loginRequest.getEmail());
		AuthResponse authResponse = new AuthResponse();
		authResponse.setAccessToken("Bearer " + jwtToken);
		authResponse.setRefreshToken(refreshTokenService.createRefreshToken(user));
		authResponse.setUserId(user.getId());
		return authResponse;
	}

	@PostMapping("/register")
	public ResponseEntity<AuthResponse> register(@RequestBody UserRequest registerRequest) {
		AuthResponse authResponse = new AuthResponse();
		if (userService.getOneUserByEmail(registerRequest.getEmail()) != null) {
			authResponse.setMessage("Username already in use.");
			return new ResponseEntity<>(authResponse, HttpStatus.BAD_REQUEST);
		}

		User user = new User();
		user.setFullname(registerRequest.getFullname());
		user.setEmail(registerRequest.getEmail());
		user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
		user.setEnabled(false);
		userService.saveOneUser(user);

		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
				registerRequest.getEmail(), registerRequest.getPassword());
		Authentication auth = authenticationManager.authenticate(authToken);
		SecurityContextHolder.getContext().setAuthentication(auth);
		String jwtToken = jwtTokenProvider.generateJwtToken(auth);

		authResponse.setMessage("User successfully registered.");
		authResponse.setAccessToken("Bearer " + jwtToken);
		authResponse.setRefreshToken(refreshTokenService.createRefreshToken(user));
		authResponse.setUserId(user.getId());
		return new ResponseEntity<>(authResponse, HttpStatus.CREATED);
	}

	@PostMapping("/refresh")
	public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshRequest refreshRequest) {
		AuthResponse response = new AuthResponse();
		RefreshToken token = refreshTokenService.getByUser(refreshRequest.getUserId());
		if (token.getToken().equals(refreshRequest.getRefreshToken()) && !refreshTokenService.isRefreshExpired(token)) {

			User user = token.getUser();
			String jwtToken = jwtTokenProvider.generateJwtTokenByUserId(user.getId());
			response.setMessage("token successfully refreshed.");
			response.setAccessToken("Bearer " + jwtToken);
			response.setUserId(user.getId());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} else {
			response.setMessage("refresh token is not valid.");
			return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
		}

	}

	@PostMapping("/send-otp")
	public ResponseEntity<String> sendOtp(@RequestBody Map<String, String> request) {
		String email = request.get("email");
		User user = userService.getOneUserByEmail(email);
		if (user == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("İstifadəçi qeydiyyatdan keçməyib.");
		}
		if (user.isEnabled()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("İstifadəçi artıq aktivdir.");
		}
		otpService.sendOtpToEmail(email);
		return ResponseEntity.ok("OTP kod göndərildi.");
	}

	@PostMapping("/verify-otp")
	public ResponseEntity<AuthResponse> verifyOtp(@RequestBody Map<String, String> request) {
		String email = request.get("email");
		String code = request.get("code");

		AuthResponse response = new AuthResponse();
		if (otpService.verifyOtp(email, code)) {
			User user = userService.getOneUserByEmail(email);
			if (user == null) {
				response.setMessage("İstifadəçi tapılmadı.");
				return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
			}

			user.setEnabled(true); 
			userService.saveOneUser(user);

			UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(user.getEmail(),
					user.getPassword());
			Authentication auth = authenticationManager.authenticate(authToken);
			SecurityContextHolder.getContext().setAuthentication(auth);

			String jwtToken = jwtTokenProvider.generateJwtToken(auth);

			response.setMessage("OTP təsdiqləndi, giriş uğurludur.");
			response.setAccessToken("Bearer " + jwtToken);
			response.setRefreshToken(refreshTokenService.createRefreshToken(user));
			response.setUserId(user.getId());

			return ResponseEntity.ok(response);
		} else {
			response.setMessage("OTP kod yalnış və ya vaxtı keçib.");
			return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
		}
	}

}