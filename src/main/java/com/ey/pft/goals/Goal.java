package com.ey.pft.goals;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.ey.pft.user.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "goals")
public class Goal {

	@Id
	@GeneratedValue
	@Column(columnDefinition = "BINARY(16)")
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", columnDefinition = "BINARY(16)", nullable = false)
	private User user;

	@Column(nullable = false, length = 200)
	private String name;

	@Column(nullable = false, precision = 19, scale = 2)
	private BigDecimal targetAmount;

	@Column(length = 3, nullable = false)
	private String currency;

	private LocalDate targetDate; // optional

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private GoalStatus status = GoalStatus.ACTIVE;

	@OneToMany(mappedBy = "goal", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<GoalContribution> contributions = new LinkedHashSet<>();

	@CreationTimestamp
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(nullable = false)
	private LocalDateTime updatedAt;

	public Goal() {
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
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

	public Set<GoalContribution> getContributions() {
		return contributions;
	}

	public void setContributions(Set<GoalContribution> contributions) {
		this.contributions = contributions;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}
}