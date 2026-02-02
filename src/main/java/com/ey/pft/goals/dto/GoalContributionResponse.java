package com.ey.pft.goals.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class GoalContributionResponse {

	private UUID id;
	private BigDecimal amount;
	private LocalDate contributionDate;

	public GoalContributionResponse() {
	}

	public GoalContributionResponse(UUID id, BigDecimal amount, LocalDate contributionDate) {
		this.id = id;
		this.amount = amount;
		this.contributionDate = contributionDate;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public LocalDate getContributionDate() {
		return contributionDate;
	}

	public void setContributionDate(LocalDate contributionDate) {
		this.contributionDate = contributionDate;
	}
}