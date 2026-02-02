package com.ey.pft.transaction.dto;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.constraints.Size;

public class UpdateTransactionRequest {

	private UUID categoryId; // optional: must match transaction type if provided

	@Size(max = 250)
	private String description;

	@Size(max = 120)
	private String merchant;

	private LocalDate transactionDate;

	public UpdateTransactionRequest() {
	}

	public UUID getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(UUID categoryId) {
		this.categoryId = categoryId;
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
}