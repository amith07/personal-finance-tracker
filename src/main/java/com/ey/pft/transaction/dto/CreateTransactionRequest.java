package com.ey.pft.transaction.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.ey.pft.transaction.TransactionType;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CreateTransactionRequest {

	@NotNull
	private UUID accountId;

	@NotNull
	private UUID categoryId;

	@NotNull
	private TransactionType type; // EXPENSE or INCOME for this endpoint

	@NotNull
	@Digits(integer = 17, fraction = 2)
	@DecimalMin(value = "0.01", inclusive = true)
	private BigDecimal amount;

	@NotBlank
	@Pattern(regexp = "^[A-Z]{3}$", message = "must be a 3-letter currency code")
	private String currency;

	@Digits(integer = 13, fraction = 6)
	private BigDecimal exchangeRate;

	@Size(max = 250)
	private String description;

	@Size(max = 120)
	private String merchant;

	@NotNull
	private LocalDate transactionDate;

	private List<@Size(min = 1, max = 60) String> tags; // optional tag names

	public CreateTransactionRequest() {
	}

	public UUID getAccountId() {
		return accountId;
	}

	public void setAccountId(UUID accountId) {
		this.accountId = accountId;
	}

	public UUID getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(UUID categoryId) {
		this.categoryId = categoryId;
	}

	public TransactionType getType() {
		return type;
	}

	public void setType(TransactionType type) {
		this.type = type;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public BigDecimal getExchangeRate() {
		return exchangeRate;
	}

	public void setExchangeRate(BigDecimal exchangeRate) {
		this.exchangeRate = exchangeRate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getMerchant() {
		return merchant;
	}

	public void setMerchant(String merchant) {
		this.merchant = merchant;
	}

	public LocalDate getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(LocalDate transactionDate) {
		this.transactionDate = transactionDate;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}
}