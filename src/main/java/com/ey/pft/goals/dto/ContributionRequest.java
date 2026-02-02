package com.ey.pft.goals.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public class ContributionRequest {

	@NotNull
	@DecimalMin("0.01")
	private BigDecimal amount;

	@NotNull
	private LocalDate contributionDate;

	public ContributionRequest() {
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