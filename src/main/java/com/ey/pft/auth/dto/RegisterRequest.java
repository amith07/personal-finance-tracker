package com.ey.pft.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequest {

	@NotBlank
	@Size(min = 2, max = 120)
	private String fullName;

	@NotBlank
	@Email
	@Size(max = 320)
	private String email;

	@NotBlank
	@Size(min = 8, max = 100)
	private String password;

	public RegisterRequest() {
	}

	public RegisterRequest(String fullName, String email, String password) {
		this.fullName = fullName;
		this.email = email;
		this.password = password;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}