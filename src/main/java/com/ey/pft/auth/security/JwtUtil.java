package com.ey.pft.auth.security;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

	private final SecretKey key;
	private final long ttlMinutes;
	private final String issuer;

	public JwtUtil(@Value("${security.jwt.secret}") String secret,
			@Value("${security.jwt.access-ttl-minutes:30}") long ttlMinutes,
			@Value("${security.jwt.issuer:pft-backend}") String issuer) {
		this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
		this.ttlMinutes = ttlMinutes;
		this.issuer = issuer;
	}

	public String generateToken(Authentication authentication) {
		AppUserDetails principal = (AppUserDetails) authentication.getPrincipal();
		Instant now = Instant.now();
		Instant expiry = now.plusSeconds(ttlMinutes * 60);
		return Jwts.builder().subject(principal.getUsername()).issuer(issuer).issuedAt(Date.from(now))
				.expiration(Date.from(expiry)).claim("uid", principal.getId().toString())
				.claim("role",
						principal.getAuthorities().stream().findFirst().map(Object::toString).orElse("ROLE_USER"))
				.signWith(key).compact();
	}

	public Claims parseClaims(String token) {
		return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
	}
}