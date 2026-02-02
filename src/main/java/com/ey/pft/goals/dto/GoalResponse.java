package com.ey.pft.goals.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.ey.pft.goals.GoalStatus;

public class GoalResponse {

	private UUID id;
	private String name;
	private BigDecimal targetAmount;
	private String currency;
	private LocalDate targetDate;
	private GoalStatus status;

	private BigDecimal totalContributed;
	private BigDecimal remaining;
	private double percentComplete;

	private List<GoalContributionResponse> contributions;

	public GoalResponse() {
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
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

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
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

	public BigDecimal getTotalContributed() {
		return totalContributed;
	}

	public void setTotalContributed(BigDecimal totalContributed) {
		this.totalContributed = totalContributed;
	}

	public BigDecimal getRemaining() {
		return remaining;
	}

	public void setRemaining(BigDecimal remaining) {
		this.remaining = remaining;
	}

	public double getPercentComplete() {
		return percentComplete;
	}

	public void setPercentComplete(double percentComplete) {
		this.percentComplete = percentComplete;
	}

	public List<GoalContributionResponse> getContributions() {
		return contributions;
	}

	public void setContributions(List<GoalContributionResponse> contributions) {
		this.contributions = contributions;
	}

}
