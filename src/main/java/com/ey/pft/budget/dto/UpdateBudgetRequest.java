package com.ey.pft.budget.dto;

import java.math.BigDecimal;

import com.ey.pft.budget.BudgetStatus;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Size;

public class UpdateBudgetRequest {

	@Size(min = 2, max = 120)
	private String name;

	private BudgetStatus status;

	@Digits(integer = 17, fraction = 2)
	@DecimalMin(value = "0.00", inclusive = true)
	private BigDecimal totalLimit;

	public UpdateBudgetRequest() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BudgetStatus getStatus() {
		return status;
	}

	public void setStatus(BudgetStatus status) {
		this.status = status;
	}

	public BigDecimal getTotalLimit() {
		return totalLimit;
	}

	public void setTotalLimit(BigDecimal totalLimit) {
		this.totalLimit = totalLimit;
	}
}