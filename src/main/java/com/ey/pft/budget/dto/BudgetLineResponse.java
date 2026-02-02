package com.ey.pft.budget.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class BudgetLineResponse {
	private UUID id;
	private UUID categoryId;
	private String categoryName;
	private BigDecimal threshold;

	public BudgetLineResponse() {
	}

	public BudgetLineResponse(UUID id, UUID categoryId, String categoryName, BigDecimal threshold) {
		this.id = id;
		this.categoryId = categoryId;
		this.categoryName = categoryName;
		this.threshold = threshold;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public UUID getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(UUID categoryId) {
		this.categoryId = categoryId;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public BigDecimal getThreshold() {
		return threshold;
	}

	public void setThreshold(BigDecimal threshold) {
		this.threshold = threshold;
	}
}
