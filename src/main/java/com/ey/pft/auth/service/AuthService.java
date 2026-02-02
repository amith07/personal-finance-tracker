package com.ey.pft.auth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ey.pft.auth.dto.AuthResponse;
import com.ey.pft.auth.dto.LoginRequest;
import com.ey.pft.auth.dto.RegisterRequest;
import com.ey.pft.auth.security.JwtUtil;
import com.ey.pft.common.exception.BadRequestException;
import com.ey.pft.user.Role;
import com.ey.pft.user.User;
import com.ey.pft.user.UserRepository;
import com.ey.pft.user.UserStatus;

@Service
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;
	private final JwtUtil jwtUtil;

	@Value("${security.jwt.access-ttl-minutes:30}")
	private long accessTtlMinutes;

	public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
			AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.authenticationManager = authenticationManager;
		this.jwtUtil = jwtUtil;
	}

	public void register(RegisterRequest req) {
		String email = req.getEmail().toLowerCase();
		if (userRepository.existsByEmail(email)) {
			throw new BadRequestException("Email already registered");
		}
		User user = new User();
		user.setFullName(req.getFullName());
		user.setEmail(email);
		user.setPassword(passwordEncoder.encode(req.getPassword()));
		user.setDefaultCurrency("INR");
		user.setRole(Role.USER);
		user.setStatus(UserStatus.ACTIVE);

		userRepository.save(user);
	}

	public AuthResponse login(LoginRequest req) {
		Authentication auth = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(req.getEmail().toLowerCase(), req.getPassword()));
		String token = jwtUtil.generateToken(auth);
		return new AuthResponse(token, "Bearer", accessTtlMinutes * 60);
	}
}