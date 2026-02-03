package com.ey.pft.report.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class CategoryBreakdownResponse {

	public static class Item {
		private UUID categoryId;
		private String categoryName;
		private BigDecimal total;
		private double percentage; // 0..100

		public Item() {
		}

		public Item(UUID categoryId, String categoryName, BigDecimal total, double percentage) {
			this.categoryId = categoryId;
			this.categoryName = categoryName;
			this.total = total;
			this.percentage = percentage;
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

		public BigDecimal getTotal() {
			return total;
		}

		public void setTotal(BigDecimal total) {
			this.total = total;
		}

		public double getPercentage() {
			return percentage;
		}

		public void setPercentage(double percentage) {
			this.percentage = percentage;
		}
	}

	private LocalDate fromDate;
	private LocalDate toDate;
	private String type; // "EXPENSE" or "INCOME"
	private BigDecimal grandTotal;
	private List<Item> items;

	public CategoryBreakdownResponse() {
	}

	public LocalDate getFromDate() {
		return fromDate;
	}

	public void setFromDate(LocalDate fromDate) {
		this.fromDate = fromDate;
	}

	public LocalDate getToDate() {
		return toDate;
	}

	public void setToDate(LocalDate toDate) {
		this.toDate = toDate;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public BigDecimal getGrandTotal() {
		return grandTotal;
	}

	public void setGrandTotal(BigDecimal grandTotal) {
		this.grandTotal = grandTotal;
	}

	public List<Item> getItems() {
		return items;
	}

	public void setItems(List<Item> items) {
		this.items = items;
	}
}