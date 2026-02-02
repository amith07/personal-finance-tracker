package com.ey.pft.transaction.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ey.pft.account.Account;
import com.ey.pft.account.AccountRepository;
import com.ey.pft.category.Category;
import com.ey.pft.category.CategoryRepository;
import com.ey.pft.category.CategoryType;
import com.ey.pft.common.exception.BadRequestException;
import com.ey.pft.common.exception.ResourceNotFoundException;
import com.ey.pft.common.util.SecurityUtils;
import com.ey.pft.transaction.Transaction;
import com.ey.pft.transaction.TransactionRepository;
import com.ey.pft.transaction.TransactionStatus;
import com.ey.pft.transaction.TransactionType;
import com.ey.pft.transaction.dto.AddTagsRequest;
import com.ey.pft.transaction.dto.CreateTransactionRequest;
import com.ey.pft.transaction.dto.TagResponse;
import com.ey.pft.transaction.dto.TransactionResponse;
import com.ey.pft.transaction.dto.TransferRequest;
import com.ey.pft.transaction.dto.UpdateTransactionRequest;
import com.ey.pft.transaction.tag.Tag;
import com.ey.pft.transaction.tag.TagRepository;
import com.ey.pft.user.User;
import com.ey.pft.user.UserRepository;

@Service
@Transactional
public class TransactionService {

	private final TransactionRepository transactionRepository;
	private final AccountRepository accountRepository;
	private final CategoryRepository categoryRepository;
	private final UserRepository userRepository;
	private final TagRepository tagRepository;

	public TransactionService(TransactionRepository transactionRepository, AccountRepository accountRepository,
			CategoryRepository categoryRepository, UserRepository userRepository, TagRepository tagRepository) {
		this.transactionRepository = transactionRepository;
		this.accountRepository = accountRepository;
		this.categoryRepository = categoryRepository;
		this.userRepository = userRepository;
		this.tagRepository = tagRepository;
	}

	public TransactionResponse create(CreateTransactionRequest req) {
		UUID userId = SecurityUtils.getCurrentUserIdOrThrow();
		User user = getUser(userId);

		if (req.getType() == TransactionType.TRANSFER) {
			throw new BadRequestException("Use /transactions/transfer to create transfers");
		}

		Account account = accountRepository.findById(req.getAccountId())
				.orElseThrow(() -> new BadRequestException("Account not found"));
		if (!account.getUser().getId().equals(userId)) {
			throw new BadRequestException("Account not owned by current user");
		}

		Category category = categoryRepository.findById(req.getCategoryId())
				.orElseThrow(() -> new BadRequestException("Category not found"));

		// category must be system or owned by user
		if (category.getUser() != null && !category.getUser().getId().equals(userId)) {
			throw new BadRequestException("Category not owned by current user");
		}

		// type alignment
		if ((req.getType() == TransactionType.EXPENSE && category.getType() != CategoryType.EXPENSE)
				|| (req.getType() == TransactionType.INCOME && category.getType() != CategoryType.INCOME)) {
			throw new BadRequestException("Category type must match transaction type");
		}

		// currency alignment (v1)
		if (!account.getCurrency().equalsIgnoreCase(req.getCurrency())) {
			throw new BadRequestException("Transaction currency must match account currency");
		}

		Transaction tx = new Transaction();
		tx.setUser(user);
		tx.setAccount(account);
		tx.setCategory(category);
		tx.setType(req.getType());
		tx.setAmount(req.getAmount());
		tx.setCurrency(req.getCurrency());
		tx.setExchangeRate(req.getExchangeRate());
		tx.setDescription(req.getDescription());
		tx.setMerchant(req.getMerchant());
		tx.setTransactionDate(req.getTransactionDate());
		tx.setStatus(TransactionStatus.ACTIVE);

		// Tags (optional)
		if (req.getTags() != null && !req.getTags().isEmpty()) {
			Set<Tag> tags = new HashSet<>();
			for (String name : req.getTags()) {
				String n = name.trim();
				if (n.isEmpty())
					continue;
				Tag tag = tagRepository.findByUser_IdAndNameIgnoreCase(userId, n).orElseGet(() -> {
					Tag t = new Tag();
					t.setUser(user);
					t.setName(n);
					return tagRepository.save(t);
				});
				tags.add(tag);
			}
			tx.setTags(tags);
		}

		// Persist
		Transaction saved = transactionRepository.save(tx);

		// Update account balance
		applyBalanceDelta(account, req.getType(), req.getAmount(), false);
		accountRepository.save(account);

		return toResponse(saved);
	}

	public TransactionResponse createTransfer(TransferRequest req) {
		UUID userId = SecurityUtils.getCurrentUserIdOrThrow();
		User user = getUser(userId);

		if (req.getFromAccountId().equals(req.getToAccountId())) {
			throw new BadRequestException("fromAccountId and toAccountId must be different");
		}

		Account from = accountRepository.findById(req.getFromAccountId())
				.orElseThrow(() -> new BadRequestException("From account not found"));
		Account to = accountRepository.findById(req.getToAccountId())
				.orElseThrow(() -> new BadRequestException("To account not found"));

		if (!from.getUser().getId().equals(userId) || !to.getUser().getId().equals(userId)) {
			throw new BadRequestException("Both accounts must belong to current user");
		}

		// Same currency for v1
		if (!from.getCurrency().equalsIgnoreCase(to.getCurrency())
				|| !from.getCurrency().equalsIgnoreCase(req.getCurrency())) {
			throw new BadRequestException("Cross-currency transfers are not supported yet");
		}

		// Find Transfer category (system)
		Category transferCategory = categoryRepository.findSystemByTypeAndName(CategoryType.TRANSFER, "Transfer")
				.orElseThrow(() -> new BadRequestException("System 'Transfer' category is missing"));

		UUID groupId = UUID.randomUUID();

		// From leg (outflow)
		Transaction fromTx = new Transaction();
		fromTx.setUser(user);
		fromTx.setAccount(from);
		fromTx.setCategory(transferCategory);
		fromTx.setType(TransactionType.TRANSFER);
		fromTx.setAmount(req.getAmount());
		fromTx.setCurrency(req.getCurrency());
		fromTx.setTransactionDate(req.getTransactionDate());
		fromTx.setDescription(req.getDescription());
		fromTx.setStatus(TransactionStatus.ACTIVE);
		fromTx.setTransferGroupId(groupId);
		transactionRepository.save(fromTx);

		// To leg (inflow)
		Transaction toTx = new Transaction();
		toTx.setUser(user);
		toTx.setAccount(to);
		toTx.setCategory(transferCategory);
		toTx.setType(TransactionType.TRANSFER);
		toTx.setAmount(req.getAmount());
		toTx.setCurrency(req.getCurrency());
		toTx.setTransactionDate(req.getTransactionDate());
		toTx.setDescription(req.getDescription());
		toTx.setStatus(TransactionStatus.ACTIVE);
		toTx.setTransferGroupId(groupId);
		transactionRepository.save(toTx);

		// Update balances
		applyBalanceDelta(from, TransactionType.EXPENSE, req.getAmount(), false); // outflow
		applyBalanceDelta(to, TransactionType.INCOME, req.getAmount(), false); // inflow
		accountRepository.save(from);
		accountRepository.save(to);

		// Return "from" leg as response
		return toResponse(fromTx);
	}

	@Transactional(readOnly = true)
	public Page<TransactionResponse> search(TransactionType type, UUID categoryId, LocalDate fromDate, LocalDate toDate,
			Pageable pageable) {
		UUID userId = SecurityUtils.getCurrentUserIdOrThrow();
		return transactionRepository.search(userId, type, categoryId, fromDate, toDate, pageable).map(this::toResponse);
	}

	@Transactional(readOnly = true)
	public TransactionResponse get(UUID id) {
		UUID userId = SecurityUtils.getCurrentUserIdOrThrow();
		Transaction tx = transactionRepository.findByIdAndUser_Id(id, userId)
				.orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));
		return toResponse(tx);
	}

	public TransactionResponse update(UUID id, UpdateTransactionRequest req) {
		UUID userId = SecurityUtils.getCurrentUserIdOrThrow();
		Transaction tx = transactionRepository.findByIdAndUser_Id(id, userId)
				.orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

		if (tx.getStatus() == TransactionStatus.DELETED) {
			throw new BadRequestException("Cannot update a deleted transaction");
		}

		if (req.getCategoryId() != null) {
			Category category = categoryRepository.findById(req.getCategoryId())
					.orElseThrow(() -> new BadRequestException("Category not found"));
			if (category.getUser() != null && !category.getUser().getId().equals(userId)) {
				throw new BadRequestException("Category not owned by current user");
			}
			// Ensure type compat
			if (tx.getType() == TransactionType.EXPENSE && category.getType() != CategoryType.EXPENSE) {
				throw new BadRequestException("Category type mismatch");
			}
			if (tx.getType() == TransactionType.INCOME && category.getType() != CategoryType.INCOME) {
				throw new BadRequestException("Category type mismatch");
			}
			if (tx.getType() == TransactionType.TRANSFER && category.getType() != CategoryType.TRANSFER) {
				throw new BadRequestException("Category type mismatch");
			}
			tx.setCategory(category);
		}
		if (req.getDescription() != null)
			tx.setDescription(req.getDescription());
		if (req.getMerchant() != null)
			tx.setMerchant(req.getMerchant());
		if (req.getTransactionDate() != null)
			tx.setTransactionDate(req.getTransactionDate());

		Transaction saved = transactionRepository.save(tx);
		return toResponse(saved);
	}

	public void delete(UUID id) {
		UUID userId = SecurityUtils.getCurrentUserIdOrThrow();
		Transaction tx = transactionRepository.findByIdAndUser_Id(id, userId)
				.orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

		if (tx.getStatus() == TransactionStatus.DELETED) {
			return; // idempotent
		}

		if (tx.getType() == TransactionType.TRANSFER && tx.getTransferGroupId() != null) {
			// Delete both legs
			List<Transaction> legs = transactionRepository.findByUser_IdAndTransferGroupId(userId,
					tx.getTransferGroupId());
			if (legs.isEmpty()) {
				// fallback: delete single leg
				softDeleteAndReverse(tx);
			} else {
				for (Transaction leg : legs) {
					if (leg.getStatus() == TransactionStatus.ACTIVE) {
						softDeleteAndReverse(leg, leg == tx ? "from" : "to");
					}
				}
			}
		} else {
			softDeleteAndReverse(tx);
		}
	}

	public List<TagResponse> addTags(UUID txId, AddTagsRequest req) {
		UUID userId = SecurityUtils.getCurrentUserIdOrThrow();
		User user = getUser(userId);
		Transaction tx = transactionRepository.findByIdAndUser_Id(txId, userId)
				.orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));
		if (tx.getStatus() == TransactionStatus.DELETED) {
			throw new BadRequestException("Cannot tag a deleted transaction");
		}
		Set<Tag> current = tx.getTags() == null ? new HashSet<>() : new HashSet<>(tx.getTags());
		for (String raw : req.getNames()) {
			String name = raw.trim();
			if (name.isEmpty())
				continue;
			Tag tag = tagRepository.findByUser_IdAndNameIgnoreCase(userId, name).orElseGet(() -> {
				Tag t = new Tag();
				t.setUser(user);
				t.setName(name);
				return tagRepository.save(t);
			});
			current.add(tag);
		}
		tx.setTags(current);
		transactionRepository.save(tx);
		return current.stream().map(t -> new TagResponse(t.getId(), t.getName()))
				.sorted(Comparator.comparing(TagResponse::getName)).collect(Collectors.toList());
	}

	public void removeTag(UUID txId, UUID tagId) {
		UUID userId = SecurityUtils.getCurrentUserIdOrThrow();
		Transaction tx = transactionRepository.findByIdAndUser_Id(txId, userId)
				.orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));
		Tag tag = tagRepository.findByIdAndUser_Id(tagId, userId)
				.orElseThrow(() -> new ResourceNotFoundException("Tag not found"));

		if (tx.getTags() != null && tx.getTags().removeIf(t -> t.getId().equals(tag.getId()))) {
			transactionRepository.save(tx);
		}
	}

	// ---- Helpers ----

	private User getUser(UUID userId) {
		return userRepository.findById(userId)
				.orElseThrow(() -> new IllegalStateException("Authenticated user not found"));
	}

	private void applyBalanceDelta(Account account, TransactionType type, BigDecimal amount, boolean reverse) {
		BigDecimal delta;
		// EXPENSE reduces balance, INCOME increases; TRANSFER handled via
		// expense/income semantics
		if (type == TransactionType.EXPENSE) {
			delta = amount.negate();
		} else if (type == TransactionType.INCOME) {
			delta = amount;
		} else {
			// TRANSFER: we won't call this directly; handled as EXPENSE/INCOME per leg
			return;
		}
		if (reverse)
			delta = delta.negate();
		account.setCurrentBalance(account.getCurrentBalance().add(delta));
	}

	private void softDeleteAndReverse(Transaction tx) {
		softDeleteAndReverse(tx, null);
	}

	private void softDeleteAndReverse(Transaction tx, String legHint) {
		Account account = tx.getAccount();

		if (tx.getType() == TransactionType.TRANSFER) {
			// Determine sign per leg: one leg should be outflow, the other inflow.
			// We don't store sign in amount; so decide by "legHint" or account role.
			// If legHint == "from" => reverse EXPENSE; else reverse INCOME.
			if ("from".equals(legHint)) {
				applyBalanceDelta(account, TransactionType.EXPENSE, tx.getAmount(), true);
			} else {
				applyBalanceDelta(account, TransactionType.INCOME, tx.getAmount(), true);
			}
		} else {
			// reverse effect
			applyBalanceDelta(account, tx.getType(), tx.getAmount(), true);
		}
		accountRepository.save(account);
		tx.setStatus(TransactionStatus.DELETED);
		transactionRepository.save(tx);
	}

	private TransactionResponse toResponse(Transaction t) {
		TransactionResponse r = new TransactionResponse();
		r.setId(t.getId());
		r.setAccountId(t.getAccount().getId());
		r.setAccountName(t.getAccount().getName());
		r.setCategoryId(t.getCategory() != null ? t.getCategory().getId() : null);
		r.setCategoryName(t.getCategory() != null ? t.getCategory().getName() : null);
		r.setType(t.getType());
		r.setAmount(t.getAmount());
		r.setCurrency(t.getCurrency());
		r.setExchangeRate(t.getExchangeRate());
		r.setAmountBaseCurrency(t.getAmountBaseCurrency());
		r.setDescription(t.getDescription());
		r.setMerchant(t.getMerchant());
		r.setTransactionDate(t.getTransactionDate());
		r.setStatus(t.getStatus());
		r.setTransferGroupId(t.getTransferGroupId());
		if (t.getTags() != null) {
			List<TagResponse> tags = t.getTags().stream().map(tag -> new TagResponse(tag.getId(), tag.getName()))
					.sorted(Comparator.comparing(TagResponse::getName)).collect(Collectors.toList());
			r.setTags(tags);
		}
		r.setCreatedAt(t.getCreatedAt());
		r.setUpdatedAt(t.getUpdatedAt());
		return r;
	}
}