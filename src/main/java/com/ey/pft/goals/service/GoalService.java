package com.ey.pft.goals.service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ey.pft.common.exception.BadRequestException;
import com.ey.pft.common.exception.ResourceNotFoundException;
import com.ey.pft.common.util.SecurityUtils;
import com.ey.pft.goals.Goal;
import com.ey.pft.goals.GoalContribution;
import com.ey.pft.goals.GoalContributionRepository;
import com.ey.pft.goals.GoalRepository;
import com.ey.pft.goals.GoalStatus;
import com.ey.pft.goals.dto.ContributionRequest;
import com.ey.pft.goals.dto.CreateGoalRequest;
import com.ey.pft.goals.dto.GoalContributionResponse;
import com.ey.pft.goals.dto.GoalResponse;
import com.ey.pft.goals.dto.UpdateGoalRequest;
import com.ey.pft.user.User;
import com.ey.pft.user.UserRepository;

@Service
@Transactional
public class GoalService {

	private final GoalRepository goalRepository;
	private final GoalContributionRepository contributionRepository;
	private final UserRepository userRepository;

	public GoalService(GoalRepository goalRepository, GoalContributionRepository contributionRepository,
			UserRepository userRepository) {
		this.goalRepository = goalRepository;
		this.contributionRepository = contributionRepository;
		this.userRepository = userRepository;
	}

	public GoalResponse create(CreateGoalRequest req) {
		UUID userId = SecurityUtils.getCurrentUserIdOrThrow();
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new IllegalStateException("Authenticated user not found"));

		Goal goal = new Goal();
		goal.setUser(user);
		goal.setName(req.getName());
		goal.setTargetAmount(req.getTargetAmount());
		goal.setCurrency(req.getCurrency());
		goal.setTargetDate(req.getTargetDate());
		goal.setStatus(GoalStatus.ACTIVE);

		Goal saved = goalRepository.save(goal);
		return toResponse(saved);
	}

	@Transactional(readOnly = true)
	public Page<GoalResponse> list(Pageable pageable) {
		UUID userId = SecurityUtils.getCurrentUserIdOrThrow();
		return goalRepository.findByUser_Id(userId, pageable).map(this::toResponse);
	}

	@Transactional(readOnly = true)
	public GoalResponse get(UUID id) {
		Goal goal = getGoalOwned(id);
		return toResponse(goal);
	}

	public GoalResponse update(UUID id, UpdateGoalRequest req) {
		Goal goal = getGoalOwned(id);

		if (req.getName() != null)
			goal.setName(req.getName());
		if (req.getTargetAmount() != null) {
			if (req.getTargetAmount().compareTo(BigDecimal.ZERO) <= 0) {
				throw new BadRequestException("Target amount must be greater than 0");
			}
			goal.setTargetAmount(req.getTargetAmount());
		}
		if (req.getTargetDate() != null)
			goal.setTargetDate(req.getTargetDate());
		if (req.getStatus() != null)
			goal.setStatus(req.getStatus());

		Goal saved = goalRepository.save(goal);
		return toResponse(saved);
	}

	public void archive(UUID id) {
		Goal goal = getGoalOwned(id);
		goal.setStatus(GoalStatus.ARCHIVED);
		goalRepository.save(goal);
	}

	public GoalResponse unarchive(UUID id) {
		Goal goal = getGoalOwned(id);
		goal.setStatus(GoalStatus.ACTIVE);
		goalRepository.save(goal);
		return toResponse(goal);
	}

	public GoalContributionResponse addContribution(UUID goalId, ContributionRequest req) {
		Goal goal = getGoalOwned(goalId);

		if (req.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
			throw new BadRequestException("Contribution amount must be greater than 0");
		}

		GoalContribution c = new GoalContribution();
		c.setGoal(goal);
		c.setAmount(req.getAmount());
		c.setContributionDate(req.getContributionDate());

		GoalContribution saved = contributionRepository.save(c);
		goal.getContributions().add(saved);

		return new GoalContributionResponse(saved.getId(), saved.getAmount(), saved.getContributionDate());
	}

	@Transactional(readOnly = true)
	public GoalResponse getWithContributions(UUID id) {
		Goal goal = getGoalOwned(id);
		return toResponse(goal);
	}

	// -------- Helpers --------

	private Goal getGoalOwned(UUID id) {
		UUID userId = SecurityUtils.getCurrentUserIdOrThrow();
		return goalRepository.findByIdAndUser_Id(id, userId)
				.orElseThrow(() -> new ResourceNotFoundException("Goal not found"));
	}

	private GoalResponse toResponse(Goal goal) {
		BigDecimal totalContrib = goal.getContributions().stream().map(GoalContribution::getAmount)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		BigDecimal remaining = goal.getTargetAmount().subtract(totalContrib);
		if (remaining.compareTo(BigDecimal.ZERO) < 0) {
			remaining = BigDecimal.ZERO;
		}

		double percent = goal.getTargetAmount().compareTo(BigDecimal.ZERO) > 0
				? totalContrib.multiply(BigDecimal.valueOf(100))
						.divide(goal.getTargetAmount(), 2, BigDecimal.ROUND_HALF_UP).doubleValue()
				: 0.0;

		List<GoalContributionResponse> contribDtos = goal.getContributions().stream()
				.sorted(Comparator.comparing(GoalContribution::getContributionDate))
				.map(c -> new GoalContributionResponse(c.getId(), c.getAmount(), c.getContributionDate()))
				.collect(Collectors.toList());

		GoalResponse resp = new GoalResponse();
		resp.setId(goal.getId());
		resp.setName(goal.getName());
		resp.setTargetAmount(goal.getTargetAmount());
		resp.setCurrency(goal.getCurrency());
		resp.setTargetDate(goal.getTargetDate());
		resp.setStatus(goal.getStatus());
		resp.setTotalContributed(totalContrib);
		resp.setRemaining(remaining);
		resp.setPercentComplete(percent);
		resp.setContributions(contribDtos);

		return resp;
	}
}
