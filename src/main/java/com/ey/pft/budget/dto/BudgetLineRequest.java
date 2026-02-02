package com.ey.pft.budget.dto;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

public class BudgetLineRequest {

	@NotNull
	private UUID categoryId;

	@NotNull
	@Digits(integer = 17, fraction = 2)
	@DecimalMin(value = "0.01", inclusive = true)
	private BigDecimal threshold;

	public BudgetLineRequest() {
	}

	public UUID getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(UUID categoryId) {
		this.categoryId = categoryId;
	}

	public BigDecimal getThreshold() {
		return threshold;
	}

	public void setThreshold(BigDecimal threshold) {
		this.threshold = threshold;
	}
}