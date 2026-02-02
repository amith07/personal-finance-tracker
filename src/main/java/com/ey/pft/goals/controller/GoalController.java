package com.ey.pft.goals.controller;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ey.pft.goals.dto.ContributionRequest;
import com.ey.pft.goals.dto.CreateGoalRequest;
import com.ey.pft.goals.dto.GoalContributionResponse;
import com.ey.pft.goals.dto.GoalResponse;
import com.ey.pft.goals.dto.UpdateGoalRequest;
import com.ey.pft.goals.service.GoalService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/goals")
public class GoalController {

	private final GoalService goalService;

	public GoalController(GoalService goalService) {
		this.goalService = goalService;
	}

	@PostMapping
	public ResponseEntity<GoalResponse> create(@Valid @RequestBody CreateGoalRequest req) {
		return new ResponseEntity<>(goalService.create(req), HttpStatus.CREATED);
	}

	@GetMapping
	public ResponseEntity<Page<GoalResponse>> list(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size, @RequestParam(defaultValue = "createdAt,desc") String sort) {

		Sort sortObj = Sort.by(sort.split(",")[0]);
		if (sort.toLowerCase().endsWith(",desc"))
			sortObj = sortObj.descending();

		Pageable pageable = PageRequest.of(page, size, sortObj);

		return ResponseEntity.ok(goalService.list(pageable));
	}

	@GetMapping("/{id}")
	public ResponseEntity<GoalResponse> get(@PathVariable UUID id) {
		return ResponseEntity.ok(goalService.getWithContributions(id));
	}

	@PatchMapping("/{id}")
	public ResponseEntity<GoalResponse> update(@PathVariable UUID id, @Valid @RequestBody UpdateGoalRequest req) {
		return ResponseEntity.ok(goalService.update(id, req));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> archive(@PathVariable UUID id) {
		goalService.archive(id);
		return ResponseEntity.noContent().build();
	}

	@PatchMapping("/{id}/unarchive")
	public ResponseEntity<GoalResponse> unarchive(@PathVariable UUID id) {
		return ResponseEntity.ok(goalService.unarchive(id));
	}

	@PostMapping("/{id}/contributions")
	public ResponseEntity<GoalContributionResponse> addContribution(@PathVariable UUID id,
			@Valid @RequestBody ContributionRequest req) {
		return new ResponseEntity<>(goalService.addContribution(id, req), HttpStatus.CREATED);
	}
}