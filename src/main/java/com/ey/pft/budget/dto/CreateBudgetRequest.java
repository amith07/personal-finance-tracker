package com.ey.pft.budget.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.ey.pft.budget.BudgetPeriod;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CreateBudgetRequest {

	@NotBlank
	@Size(min = 2, max = 120)
	private String name;

	@NotNull
	private BudgetPeriod period;

	@NotNull
	private LocalDate startDate;

	@NotBlank
	@Pattern(regexp = "^[A-Z]{3}$")
	private String currency;

	@Digits(integer = 17, fraction = 2)
	@DecimalMin(value = "0.00", inclusive = true)
	private BigDecimal totalLimit; // optional

	@Valid
	private List<@Valid BudgetLineRequest> lines; // optional list

	public CreateBudgetRequest() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BudgetPeriod getPeriod() {
		return period;
	}

	public void setPeriod(BudgetPeriod period) {
		this.period = period;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
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

	public List<BudgetLineRequest> getLines() {
		return lines;
	}

	public void setLines(List<BudgetLineRequest> lines) {
		this.lines = lines;
	}
}