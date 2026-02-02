package com.ey.pft.transaction.controller;

import java.time.LocalDate;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ey.pft.transaction.TransactionType;
import com.ey.pft.transaction.dto.AddTagsRequest;
import com.ey.pft.transaction.dto.CreateTransactionRequest;
import com.ey.pft.transaction.dto.TagResponse;
import com.ey.pft.transaction.dto.TransactionResponse;
import com.ey.pft.transaction.dto.TransferRequest;
import com.ey.pft.transaction.dto.UpdateTransactionRequest;
import com.ey.pft.transaction.service.TransactionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

	private final TransactionService transactionService;

	public TransactionController(TransactionService transactionService) {
		this.transactionService = transactionService;
	}

	// Create expense/income
	@PostMapping
	public ResponseEntity<TransactionResponse> create(@Valid @RequestBody CreateTransactionRequest req) {
		return new ResponseEntity<>(transactionService.create(req), HttpStatus.CREATED);
	}

	// Create transfer (two legs)
	@PostMapping("/transfer")
	public ResponseEntity<TransactionResponse> createTransfer(@Valid @RequestBody TransferRequest req) {
		return new ResponseEntity<>(transactionService.createTransfer(req), HttpStatus.CREATED);
	}

	// Search/list (pageable)
	@GetMapping
	public ResponseEntity<Page<TransactionResponse>> search(@RequestParam(required = false) TransactionType type,
			@RequestParam(required = false) UUID categoryId, @RequestParam(required = false) LocalDate from,
			@RequestParam(required = false) LocalDate to, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size,
			@RequestParam(defaultValue = "transactionDate,desc") String sort) {
		Sort sortObj = Sort.by(sort.split(",")[0]);
		if (sort.toLowerCase().endsWith(",desc"))
			sortObj = sortObj.descending();
		Pageable pageable = PageRequest.of(page, size, sortObj);
		return ResponseEntity.ok(transactionService.search(type, categoryId, from, to, pageable));
	}

	// Get by id
	@GetMapping("/{id}")
	public ResponseEntity<TransactionResponse> get(@PathVariable UUID id) {
		return ResponseEntity.ok(transactionService.get(id));
	}

	// Patch basic fields
	@PatchMapping("/{id}")
	public ResponseEntity<TransactionResponse> update(@PathVariable UUID id,
			@Valid @RequestBody UpdateTransactionRequest req) {
		return ResponseEntity.ok(transactionService.update(id, req));
	}

	// Soft delete (reverse balances)
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable UUID id) {
		transactionService.delete(id);
		return ResponseEntity.noContent().build();
	}

	// Tags
	@PostMapping("/{id}/tags")
	public ResponseEntity<List<TagResponse>> addTags(@PathVariable UUID id, @Valid @RequestBody AddTagsRequest req) {
		return ResponseEntity.ok(transactionService.addTags(id, req));
	}

	@DeleteMapping("/{id}/tags/{tagId}")
	public ResponseEntity<Void> removeTag(@PathVariable UUID id, @PathVariable UUID tagId) {
		transactionService.removeTag(id, tagId);
		return ResponseEntity.noContent().build();
	}
}