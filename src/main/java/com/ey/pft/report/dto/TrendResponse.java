package com.ey.pft.report.dto;

import java.math.BigDecimal;
import java.util.List;

public class TrendResponse {

	public static class MonthPoint {
		private int year;
		private int month; // 1..12
		private BigDecimal income;
		private BigDecimal expense;
		private BigDecimal net;

		public MonthPoint() {
		}

		public MonthPoint(int year, int month, BigDecimal income, BigDecimal expense, BigDecimal net) {
			this.year = year;
			this.month = month;
			this.income = income;
			this.expense = expense;
			this.net = net;
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

		public BigDecimal getIncome() {
			return income;
		}

		public void setIncome(BigDecimal income) {
			this.income = income;
		}

		public BigDecimal getExpense() {
			return expense;
		}

		public void setExpense(BigDecimal expense) {
			this.expense = expense;
		}

		public BigDecimal getNet() {
			return net;
		}

		public void setNet(BigDecimal net) {
			this.net = net;
		}
	}

	private int months; // how many recent months are included
	private List<MonthPoint> points;

	public TrendResponse() {
	}

	public int getMonths() {
		return months;
	}

	public void setMonths(int months) {
		this.months = months;
	}

	public List<MonthPoint> getPoints() {
		return points;
	}

	public void setPoints(List<MonthPoint> points) {
		this.points = points;
	}
}