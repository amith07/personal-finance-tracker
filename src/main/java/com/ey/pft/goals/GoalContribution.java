package com.ey.pft.goals;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "goal_contributions")
public class GoalContribution {

	@Id
	@GeneratedValue
	@Column(columnDefinition = "BINARY(16)")
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "goal_id", columnDefinition = "BINARY(16)", nullable = false)
	private Goal goal;

	@Column(nullable = false, precision = 19, scale = 2)
	private BigDecimal amount;

	@Column(nullable = false)
	private LocalDate contributionDate;

	@CreationTimestamp
	private LocalDateTime createdAt;

	public GoalContribution() {
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public Goal getGoal() {
		return goal;
	}

	public void setGoal(Goal goal) {
		this.goal = goal;
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

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
}
