package com.ey.pft.goals.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.ey.pft.goals.GoalStatus;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

public class UpdateGoalRequest {

	@Size(min = 2, max = 200)
	private String name;

	@DecimalMin("0.01")
	private BigDecimal targetAmount;

	private LocalDate targetDate;

	private GoalStatus status;

	public UpdateGoalRequest() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getTargetAmount() {
		return targetAmount;
	}

	public void setTargetAmount(BigDecimal targetAmount) {
		this.targetAmount = targetAmount;
	}

	public LocalDate getTargetDate() {
		return targetDate;
	}

	public void setTargetDate(LocalDate targetDate) {
		this.targetDate = targetDate;
	}

	public GoalStatus getStatus() {
		return status;
	}

	public void setStatus(GoalStatus status) {
		this.status = status;
	}

}