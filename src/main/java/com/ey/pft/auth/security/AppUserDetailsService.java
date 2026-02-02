package com.ey.pft.auth.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ey.pft.user.User;
import com.ey.pft.user.UserRepository;

@Service
public class AppUserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

	private final UserRepository userRepository;

	public AppUserDetailsService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = userRepository.findByEmail(email.toLowerCase())
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));
		return AppUserDetails.from(user);
	}
}