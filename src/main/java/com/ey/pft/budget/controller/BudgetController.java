package com.ey.pft.budget.controller;

import java.util.List;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ey.pft.budget.dto.BudgetLineRequest;
import com.ey.pft.budget.dto.BudgetResponse;
import com.ey.pft.budget.dto.BudgetSummaryResponse;
import com.ey.pft.budget.dto.CreateBudgetRequest;
import com.ey.pft.budget.dto.UpdateBudgetRequest;
import com.ey.pft.budget.service.BudgetService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/budgets")
public class BudgetController {

	private final BudgetService budgetService;

	public BudgetController(BudgetService budgetService) {
		this.budgetService = budgetService;
	}

	@PostMapping
	public ResponseEntity<BudgetResponse> create(@Valid @RequestBody CreateBudgetRequest req) {
		BudgetResponse created = budgetService.create(req);
		return new ResponseEntity<>(created, HttpStatus.CREATED);
	}

	@GetMapping
	public ResponseEntity<Page<BudgetResponse>> list(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size, @RequestParam(defaultValue = "startDate,desc") String sort) {
		Sort sortObj = Sort.by(sort.split(",")[0]);
		if (sort.toLowerCase().endsWith(",desc"))
			sortObj = sortObj.descending();
		Pageable pageable = PageRequest.of(page, size, sortObj);
		return ResponseEntity.ok(budgetService.list(pageable));
	}

	@GetMapping("/{id}")
	public ResponseEntity<BudgetResponse> get(@PathVariable UUID id) {
		return ResponseEntity.ok(budgetService.get(id));
	}

	@PatchMapping("/{id}")
	public ResponseEntity<BudgetResponse> update(@PathVariable UUID id, @Valid @RequestBody UpdateBudgetRequest req) {
		return ResponseEntity.ok(budgetService.update(id, req));
	}

	@PutMapping("/{id}/lines")
	public ResponseEntity<BudgetResponse> replaceLines(@PathVariable UUID id,
			@Valid @RequestBody List<@Valid BudgetLineRequest> lines) {
		return ResponseEntity.ok(budgetService.replaceLines(id, lines));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable UUID id) {
		budgetService.archive(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/{id}/summary")
	public ResponseEntity<BudgetSummaryResponse> summary(@PathVariable UUID id) {
		return ResponseEntity.ok(budgetService.summary(id));
	}

	@PatchMapping("/{id}/unarchive")
	public ResponseEntity<BudgetResponse> unarchive(@PathVariable UUID id) {
		return ResponseEntity.ok(budgetService.unarchive(id));
	}

}