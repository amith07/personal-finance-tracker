package com.ey.pft.budget;

import java.math.BigDecimal;
import java.util.UUID;

import com.ey.pft.category.Category;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "budget_lines", uniqueConstraints = @UniqueConstraint(name = "uk_budgetline_budget_category", columnNames = {
		"budget_id", "category_id" }))
public class BudgetLine {

	@Id
	@GeneratedValue
	@Column(columnDefinition = "BINARY(16)")
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "budget_id", columnDefinition = "BINARY(16)", nullable = false)
	private Budget budget;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "category_id", columnDefinition = "BINARY(16)", nullable = false)
	private Category category;

	@Column(nullable = false, precision = 19, scale = 2)
	private BigDecimal threshold;

	public BudgetLine() {
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public Budget getBudget() {
		return budget;
	}

	public void setBudget(Budget budget) {
		this.budget = budget;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public BigDecimal getThreshold() {
		return threshold;
	}

	public void setThreshold(BigDecimal threshold) {
		this.threshold = threshold;
	}
}