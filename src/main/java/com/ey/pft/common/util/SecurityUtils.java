package com.ey.pft.common.util;

import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.ey.pft.auth.security.AppUserDetails;

public final class SecurityUtils {

	private SecurityUtils() {
	}

	public static UUID getCurrentUserIdOrThrow() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !(auth.getPrincipal() instanceof AppUserDetails details)) {
			throw new IllegalStateException("No authenticated user found");
		}
		return details.getId();
	}

	public static String getCurrentUserEmailOrThrow() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !(auth.getPrincipal() instanceof AppUserDetails details)) {
			throw new IllegalStateException("No authenticated user found");
		}
		return details.getUsername();
	}
}