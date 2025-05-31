package az.risk.SimpleBankAssistant.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import az.risk.SimpleBankAssistant.entity.RefreshToken;
import az.risk.SimpleBankAssistant.entity.User;
import az.risk.SimpleBankAssistant.enums.Role;
import az.risk.SimpleBankAssistant.requests.LoginRequest;
import az.risk.SimpleBankAssistant.requests.OtpVerificationRequest;
import az.risk.SimpleBankAssistant.requests.RefreshRequest;
import az.risk.SimpleBankAssistant.requests.UserRequest;
import az.risk.SimpleBankAssistant.responses.AuthResponse;
import az.risk.SimpleBankAssistant.security.JwtTokenProvider;
import az.risk.SimpleBankAssistant.service.OtpService;
import az.risk.SimpleBankAssistant.service.RefreshTokenService;
import az.risk.SimpleBankAssistant.service.UserService;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
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
	public AuthResponse login(@RequestBody LoginRequest loginRequest) {
		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),
				loginRequest.getPassword());
		Authentication auth = authenticationManager.authenticate(authToken);
		SecurityContextHolder.getContext().setAuthentication(auth);
		String jwtToken = jwtTokenProvider.generateJwtToken(auth);
		User user = userService.getOneUserByEmail(loginRequest.getEmail());
		AuthResponse authResponse = new AuthResponse();
		authResponse.setAccessToken( jwtToken);
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
	    user.setRole(Role.USER);
	    userService.saveOneUser(user);

	  
	    otpService.sendOtpToEmail(user.getEmail());

	   
	    String refreshToken = refreshTokenService.createRefreshToken(user);

	    authResponse.setMessage("User successfully registered. OTP has been sent to email.");
	    authResponse.setAccessToken(null);
	    authResponse.setRefreshToken(refreshToken);
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
			response.setAccessToken( jwtToken);
			response.setUserId(user.getId());
			return new ResponseEntity<>(response, HttpStatus.OK);
		} else {
			response.setMessage("refresh token is not valid.");
			return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
		}

	}

	@PostMapping("/verify-otp")
	public ResponseEntity<AuthResponse> verifyOtp(@RequestBody OtpVerificationRequest request) {
	    String code = request.getCode();

	    // Məsələn, email-i təhlükəsizlik kontekstindən götürürük
	    String email = SecurityContextHolder.getContext().getAuthentication().getName();

	    AuthResponse response = new AuthResponse();

	    if (otpService.verifyOtp(email, code)) {
	        User user = userService.getOneUserByEmail(email);
	        if (user == null) {
	            response.setMessage("İstifadəçi tapılmadı.");
	            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	        }

	        user.setEnabled(true);
	        userService.saveOneUser(user);

	        response.setMessage("OTP təsdiqləndi, istifadəçi aktivləşdirildi.");
	        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
	    } else {
	        response.setMessage("OTP kod yalnış və ya vaxtı keçib.");
	        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
	    }
	}

	}



