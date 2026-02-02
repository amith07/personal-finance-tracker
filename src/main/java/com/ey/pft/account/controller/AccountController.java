package com.ey.pft.account.controller;

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

import com.ey.pft.account.dto.AccountResponse;
import com.ey.pft.account.dto.CreateAccountRequest;
import com.ey.pft.account.dto.UpdateAccountRequest;
import com.ey.pft.account.service.AccountService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

	private final AccountService accountService;

	public AccountController(AccountService accountService) {
		this.accountService = accountService;
	}

	@PostMapping
	public ResponseEntity<AccountResponse> create(@Valid @RequestBody CreateAccountRequest req) {
		return new ResponseEntity<>(accountService.create(req), HttpStatus.CREATED);
	}

	@GetMapping
	public ResponseEntity<Page<AccountResponse>> list(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size, @RequestParam(defaultValue = "createdAt,desc") String sort) {

		Sort sortObj = Sort.by(sort.split(",")[0]);
		if (sort.toLowerCase().endsWith(",desc"))
			sortObj = sortObj.descending();
		Pageable pageable = PageRequest.of(page, size, sortObj);
		return ResponseEntity.ok(accountService.list(pageable));
	}

	@GetMapping("/{id}")
	public ResponseEntity<AccountResponse> get(@PathVariable("id") UUID id) {
		return ResponseEntity.ok(accountService.get(id));
	}

	@PatchMapping("/{id}")
	public ResponseEntity<AccountResponse> update(@PathVariable("id") UUID id,
			@Valid @RequestBody UpdateAccountRequest req) {
		return ResponseEntity.ok(accountService.update(id, req));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable("id") UUID id) {
		accountService.archive(id);
		return ResponseEntity.noContent().build();
	}
}