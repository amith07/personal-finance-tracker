package com.ey.pft.budget.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class BudgetSummaryResponse {

	public static class LineSummary {
		private UUID categoryId;
		private String categoryName;
		private BigDecimal threshold;
		private BigDecimal spent;
		private BigDecimal remaining;

		public LineSummary() {
		}

		public LineSummary(UUID categoryId, String categoryName, BigDecimal threshold, BigDecimal spent,
				BigDecimal remaining) {
			this.categoryId = categoryId;
			this.categoryName = categoryName;
			this.threshold = threshold;
			this.spent = spent;
			this.remaining = remaining;
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

		public BigDecimal getSpent() {
			return spent;
		}

		public void setSpent(BigDecimal spent) {
			this.spent = spent;
		}

		public BigDecimal getRemaining() {
			return remaining;
		}

		public void setRemaining(BigDecimal remaining) {
			this.remaining = remaining;
		}
	}

	private UUID budgetId;
	private String name;
	private String currency;
	private BigDecimal totalLimit;
	private BigDecimal totalSpent;
	private BigDecimal totalRemaining;
	private List<LineSummary> lines;

	public BudgetSummaryResponse() {
	}

	public UUID getBudgetId() {
		return budgetId;
	}

	public void setBudgetId(UUID budgetId) {
		this.budgetId = budgetId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public BigDecimal getTotalLimit() {
		return totalLimit;
	}

	public void setTotalLimit(BigDecimal totalLimit) {
		this.totalLimit = totalLimit;
	}

	public BigDecimal getTotalSpent() {
		return totalSpent;
	}

	public void setTotalSpent(BigDecimal totalSpent) {
		this.totalSpent = totalSpent;
	}

	public BigDecimal getTotalRemaining() {
		return totalRemaining;
	}

	public void setTotalRemaining(BigDecimal totalRemaining) {
		this.totalRemaining = totalRemaining;
	}

	public List<LineSummary> getLines() {
		return lines;
	}

	public void setLines(List<LineSummary> lines) {
		this.lines = lines;
	}
}