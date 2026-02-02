package com.ey.pft.goals;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GoalContributionRepository extends JpaRepository<GoalContribution, UUID> {
}