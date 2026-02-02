package com.ey.pft.budget;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BudgetRepository extends JpaRepository<Budget, UUID> {

	Page<Budget> findByUser_Id(UUID userId, Pageable pageable);

	Optional<Budget> findByIdAndUser_Id(UUID id, UUID userId);

	boolean existsByUser_IdAndPeriodAndStartDate(UUID userId, BudgetPeriod period, LocalDate startDate);
}
