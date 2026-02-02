package com.ey.pft.account.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ey.pft.account.Account;
import com.ey.pft.account.AccountRepository;
import com.ey.pft.account.AccountStatus;
import com.ey.pft.account.dto.AccountResponse;
import com.ey.pft.account.dto.CreateAccountRequest;
import com.ey.pft.account.dto.UpdateAccountRequest;
import com.ey.pft.common.exception.ResourceNotFoundException;
import com.ey.pft.common.util.SecurityUtils;
import com.ey.pft.user.User;
import com.ey.pft.user.UserRepository;

@Service
@Transactional
public class AccountService {

	private final AccountRepository accountRepository;
	private final UserRepository userRepository;

	public AccountService(AccountRepository accountRepository, UserRepository userRepository) {
		this.accountRepository = accountRepository;
		this.userRepository = userRepository;
	}

	public AccountResponse create(CreateAccountRequest req) {
		UUID userId = SecurityUtils.getCurrentUserIdOrThrow();
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new IllegalStateException("Authenticated user not found"));

		Account account = new Account();
		account.setUser(user);
		account.setName(req.getName());
		account.setType(req.getType());
		account.setCurrency(req.getCurrency());
		account.setCurrentBalance(req.getCurrentBalance());
		account.setStatus(AccountStatus.ACTIVE);

		Account saved = accountRepository.save(account);
		return toResponse(saved);
	}

	@Transactional(readOnly = true)
	public Page<AccountResponse> list(Pageable pageable) {
		UUID userId = SecurityUtils.getCurrentUserIdOrThrow();
		return accountRepository.findByUser_Id(userId, pageable).map(this::toResponse);
	}

	@Transactional(readOnly = true)
	public AccountResponse get(UUID id) {
		UUID userId = SecurityUtils.getCurrentUserIdOrThrow();
		Account account = accountRepository.findByIdAndUser_Id(id, userId)
				.orElseThrow(() -> new ResourceNotFoundException("Account not found"));
		return toResponse(account);
	}

	public AccountResponse update(UUID id, UpdateAccountRequest req) {
		UUID userId = SecurityUtils.getCurrentUserIdOrThrow();
		Account account = accountRepository.findByIdAndUser_Id(id, userId)
				.orElseThrow(() -> new ResourceNotFoundException("Account not found"));

		if (req.getName() != null)
			account.setName(req.getName());
		if (req.getType() != null)
			account.setType(req.getType());
		if (req.getCurrency() != null)
			account.setCurrency(req.getCurrency());
		if (req.getCurrentBalance() != null)
			account.setCurrentBalance(req.getCurrentBalance());
		if (req.getStatus() != null)
			account.setStatus(req.getStatus());

		Account saved = accountRepository.save(account);
		return toResponse(saved);
	}

	public void archive(UUID id) {
		UUID userId = SecurityUtils.getCurrentUserIdOrThrow();
		Account account = accountRepository.findByIdAndUser_Id(id, userId)
				.orElseThrow(() -> new ResourceNotFoundException("Account not found"));
		account.setStatus(AccountStatus.ARCHIVED);
		accountRepository.save(account);
	}

	private AccountResponse toResponse(Account a) {
		return new AccountResponse(a.getId(), a.getName(), a.getType(), a.getCurrency(), a.getCurrentBalance(),
				a.getStatus(), a.getCreatedAt(), a.getUpdatedAt());
	}
}