package com.ey.pft.report.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class MonthlySummaryResponse {

	public static class CategoryTotal {
		private UUID categoryId;
		private String categoryName;
		private BigDecimal total;

		public CategoryTotal() {
		}

		public CategoryTotal(UUID categoryId, String categoryName, BigDecimal total) {
			this.categoryId = categoryId;
			this.categoryName = categoryName;
			this.total = total;
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
	}

	public static class AccountBalance {
		private UUID accountId;
		private String accountName;
		private String accountType;
		private String currency;
		private BigDecimal currentBalance;

		public AccountBalance() {
		}

		public AccountBalance(UUID accountId, String accountName, String accountType, String currency,
				BigDecimal currentBalance) {
			this.accountId = accountId;
			this.accountName = accountName;
			this.accountType = accountType;
			this.currency = currency;
			this.currentBalance = currentBalance;
		}

		public UUID getAccountId() {
			return accountId;
		}

		public void setAccountId(UUID accountId) {
			this.accountId = accountId;
		}

		public String getAccountName() {
			return accountName;
		}

		public void setAccountName(String accountName) {
			this.accountName = accountName;
		}

		public String getAccountType() {
			return accountType;
		}

		public void setAccountType(String accountType) {
			this.accountType = accountType;
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

	private int year;
	private int month; // 1-12
	private LocalDate fromDate;
	private LocalDate toDate;
	private BigDecimal totalIncome;
	private BigDecimal totalExpense;
	private BigDecimal net; // income - expense
	private List<CategoryTotal> expenseByCategory;
	private List<AccountBalance> accountBalances;

	public MonthlySummaryResponse() {
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
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

	public BigDecimal getTotalIncome() {
		return totalIncome;
	}

	public void setTotalIncome(BigDecimal totalIncome) {
		this.totalIncome = totalIncome;
	}

	public BigDecimal getTotalExpense() {
		return totalExpense;
	}

	public void setTotalExpense(BigDecimal totalExpense) {
		this.totalExpense = totalExpense;
	}

	public BigDecimal getNet() {
		return net;
	}

	public void setNet(BigDecimal net) {
		this.net = net;
	}

	public List<CategoryTotal> getExpenseByCategory() {
		return expenseByCategory;
	}

	public void setExpenseByCategory(List<CategoryTotal> expenseByCategory) {
		this.expenseByCategory = expenseByCategory;
	}

	public List<AccountBalance> getAccountBalances() {
		return accountBalances;
	}

	public void setAccountBalances(List<AccountBalance> accountBalances) {
		this.accountBalances = accountBalances;
	}
}