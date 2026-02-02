package com.ey.pft.account.dto;

import java.math.BigDecimal;

import com.ey.pft.account.AccountType;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CreateAccountRequest {

	@NotBlank
	@Size(min = 2, max = 120)
	private String name;

	@NotNull
	private AccountType type;

	@NotBlank
	@Pattern(regexp = "^[A-Z]{3}$", message = "must be a 3-letter currency code")
	private String currency;

	@NotNull
	@Digits(integer = 17, fraction = 2)
	@DecimalMin(value = "0.00", inclusive = true)
	private BigDecimal currentBalance;

	public CreateAccountRequest() {
	}

	public CreateAccountRequest(String name, AccountType type, String currency, BigDecimal currentBalance) {
		this.name = name;
		this.type = type;
		this.currency = currency;
		this.currentBalance = currentBalance;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public AccountType getType() {
		return type;
	}

	public void setType(AccountType type) {
		this.type = type;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public BigDecimal getCurrentBalance() {
		return currentBalance;
	}

	public void setCurrentBalance(BigDecimal currentBalance) {
		this.currentBalance = currentBalance;
	}
}