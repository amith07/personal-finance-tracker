package com.ey.pft.budget;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface BudgetLineRepository extends JpaRepository<BudgetLine, UUID> {
	List<BudgetLine> findByBudget_Id(UUID budgetId);

	@Transactional
	void deleteByBudget_Id(UUID budgetId);
}