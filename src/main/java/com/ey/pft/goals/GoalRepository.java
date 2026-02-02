package com.ey.pft.goals;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoalRepository extends JpaRepository<Goal, UUID> {

	Page<Goal> findByUser_Id(UUID userId, Pageable pageable);

	Optional<Goal> findByIdAndUser_Id(UUID id, UUID userId);
}
