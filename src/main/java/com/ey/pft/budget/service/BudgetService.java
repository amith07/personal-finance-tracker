package com.ey.pft.budget.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ey.pft.budget.Budget;
import com.ey.pft.budget.BudgetLine;
import com.ey.pft.budget.BudgetLineRepository;
import com.ey.pft.budget.BudgetRepository;
import com.ey.pft.budget.BudgetStatus;
import com.ey.pft.budget.dto.BudgetLineRequest;
import com.ey.pft.budget.dto.BudgetLineResponse;
import com.ey.pft.budget.dto.BudgetResponse;
import com.ey.pft.budget.dto.BudgetSummaryResponse;
import com.ey.pft.budget.dto.CreateBudgetRequest;
import com.ey.pft.budget.dto.UpdateBudgetRequest;
import com.ey.pft.category.Category;
import com.ey.pft.category.CategoryRepository;
import com.ey.pft.category.CategoryType;
import com.ey.pft.common.exception.BadRequestException;
import com.ey.pft.common.exception.ResourceNotFoundException;
import com.ey.pft.common.util.SecurityUtils;
import com.ey.pft.transaction.TransactionRepository;
import com.ey.pft.transaction.TransactionType;
import com.ey.pft.user.User;
import com.ey.pft.user.UserRepository;

@Service
@Transactional
public class BudgetService {

	private final BudgetRepository budgetRepository;
	private final BudgetLineRepository budgetLineRepository;
	private final UserRepository userRepository;
	private final CategoryRepository categoryRepository;
	private final TransactionRepository transactionRepository;

	public BudgetService(BudgetRepository budgetRepository, BudgetLineRepository budgetLineRepository,
			UserRepository userRepository, CategoryRepository categoryRepository,
			TransactionRepository transactionRepository) {
		this.budgetRepository = budgetRepository;
		this.budgetLineRepository = budgetLineRepository;
		this.userRepository = userRepository;
		this.categoryRepository = categoryRepository;
		this.transactionRepository = transactionRepository;
	}

	public BudgetResponse create(CreateBudgetRequest req) {
		UUID userId = SecurityUtils.getCurrentUserIdOrThrow();
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new IllegalStateException("Authenticated user not found"));

		if (budgetRepository.existsByUser_IdAndPeriodAndStartDate(userId, req.getPeriod(), req.getStartDate())) {
			throw new BadRequestException("Budget for this period and start date already exists");
		}

		Budget budget = new Budget();
		budget.setUser(user);
		budget.setName(req.getName());
		budget.setPeriod(req.getPeriod());
		budget.setStartDate(req.getStartDate());
		budget.setCurrency(req.getCurrency());
		budget.setTotalLimit(req.getTotalLimit());
		budget.setStatus(BudgetStatus.ACTIVE);

		if (req.getLines() != null && !req.getLines().isEmpty()) {
			// Validate duplicates within request
			ensureUniqueCategories(req.getLines());

			Set<BudgetLine> lines = new LinkedHashSet<>();
			for (BudgetLineRequest l : req.getLines()) {
				Category category = categoryRepository.findById(l.getCategoryId())
						.orElseThrow(() -> new BadRequestException("Category not found"));
				if (category.getType() != CategoryType.EXPENSE) {
					throw new BadRequestException("Budget lines must reference EXPENSE categories");
				}
				if (category.getUser() != null && !category.getUser().getId().equals(userId)) {
					throw new BadRequestException("Category not owned by current user");
				}
				BudgetLine bl = new BudgetLine();
				bl.setBudget(budget);
				bl.setCategory(category);
				bl.setThreshold(l.getThreshold());
				lines.add(bl);
			}
			budget.setLines(lines);
		}

		Budget saved = budgetRepository.save(budget);
		return toResponse(saved);
	}

	@Transactional(readOnly = true)
	public Page<BudgetResponse> list(Pageable pageable) {
		UUID userId = SecurityUtils.getCurrentUserIdOrThrow();
		return budgetRepository.findByUser_Id(userId, pageable).map(this::toResponse);
	}

	@Transactional(readOnly = true)
	public BudgetResponse get(UUID id) {
		UUID userId = SecurityUtils.getCurrentUserIdOrThrow();
		Budget b = budgetRepository.findByIdAndUser_Id(id, userId)
				.orElseThrow(() -> new ResourceNotFoundException("Budget not found"));
		return toResponse(b);
	}

	public BudgetResponse update(UUID id, UpdateBudgetRequest req) {
		UUID userId = SecurityUtils.getCurrentUserIdOrThrow();
		Budget b = budgetRepository.findByIdAndUser_Id(id, userId)
				.orElseThrow(() -> new ResourceNotFoundException("Budget not found"));

		if (req.getName() != null)
			b.setName(req.getName());
		if (req.getTotalLimit() != null)
			b.setTotalLimit(req.getTotalLimit());
		if (req.getStatus() != null)
			b.setStatus(req.getStatus());

		Budget saved = budgetRepository.save(b);
		return toResponse(saved);
	}

	public BudgetResponse replaceLines(UUID id, List<BudgetLineRequest> linesReq) {
		UUID userId = SecurityUtils.getCurrentUserIdOrThrow();
		Budget b = budgetRepository.findByIdAndUser_Id(id, userId)
				.orElseThrow(() -> new ResourceNotFoundException("Budget not found"));

		// 1) Remove existing lines explicitly and flush to avoid unique constraint
		// collisions
		budgetLineRepository.deleteByBudget_Id(b.getId());
		budgetLineRepository.flush();

		// 2) If incoming list is null/empty -> keep no lines
		if (linesReq == null || linesReq.isEmpty()) {
			b.getLines().clear(); // keep the in-memory collection consistent
			Budget saved = budgetRepository.save(b);
			return toResponse(saved);
		}

		// 3) Validate duplicates within request
		ensureUniqueCategories(linesReq);

		// 4) Re-add new lines with validations
		Set<BudgetLine> newLines = new LinkedHashSet<>();
		for (BudgetLineRequest l : linesReq) {
			Category category = categoryRepository.findById(l.getCategoryId())
					.orElseThrow(() -> new BadRequestException("Category not found"));
			if (category.getType() != CategoryType.EXPENSE) {
				throw new BadRequestException("Budget lines must reference EXPENSE categories");
			}
			if (category.getUser() != null && !category.getUser().getId().equals(userId)) {
				throw new BadRequestException("Category not owned by current user");
			}
			BudgetLine bl = new BudgetLine();
			bl.setBudget(b);
			bl.setCategory(category);
			bl.setThreshold(l.getThreshold());
			newLines.add(bl);
		}

		// 5) Replace the in-memory lines collection
		b.getLines().clear();
		b.getLines().addAll(newLines);

		Budget saved = budgetRepository.save(b);
		return toResponse(saved);
	}

	public void archive(UUID id) {
		UUID userId = SecurityUtils.getCurrentUserIdOrThrow();
		Budget b = budgetRepository.findByIdAndUser_Id(id, userId)
				.orElseThrow(() -> new ResourceNotFoundException("Budget not found"));
		b.setStatus(BudgetStatus.ARCHIVED);
		budgetRepository.save(b);
	}

	public BudgetResponse unarchive(UUID id) {
		UUID userId = SecurityUtils.getCurrentUserIdOrThrow();
		Budget b = budgetRepository.findByIdAndUser_Id(id, userId)
				.orElseThrow(() -> new ResourceNotFoundException("Budget not found"));

		if (b.getStatus() == BudgetStatus.ACTIVE) {
			return toResponse(b); // already active, idempotent
		}

		b.setStatus(BudgetStatus.ACTIVE);
		Budget saved = budgetRepository.save(b);
		return toResponse(saved);
	}

	@Transactional(readOnly = true)
	public BudgetSummaryResponse summary(UUID id) {
		UUID userId = SecurityUtils.getCurrentUserIdOrThrow();
		Budget b = budgetRepository.findByIdAndUser_Id(id, userId)
				.orElseThrow(() -> new ResourceNotFoundException("Budget not found"));

		LocalDate from = b.getStartDate();
		LocalDate to = switch (b.getPeriod()) {
		case MONTHLY -> from.plusMonths(1).minusDays(1);
		case QUARTERLY -> from.plusMonths(3).minusDays(1);
		case YEARLY -> from.plusYears(1).minusDays(1);
		};

		// Total spent = all EXPENSE transactions in period
		BigDecimal totalSpent = sumExpenses(userId, null, from, to);

		// Line summaries
		List<BudgetSummaryResponse.LineSummary> lineSummaries = b.getLines().stream().map(line -> {
			UUID catId = line.getCategory().getId();
			String catName = line.getCategory().getName();
			BigDecimal spent = sumExpenses(userId, catId, from, to);
			BigDecimal remaining = line.getThreshold() != null
					? line.getThreshold().subtract(spent).max(BigDecimal.ZERO)
					: BigDecimal.ZERO;
			return new BudgetSummaryResponse.LineSummary(catId, catName, line.getThreshold(), spent, remaining);
		}).sorted(Comparator.comparing(BudgetSummaryResponse.LineSummary::getCategoryName))
				.collect(Collectors.toList());

		BigDecimal totalRemaining = b.getTotalLimit() != null
				? b.getTotalLimit().subtract(totalSpent).max(BigDecimal.ZERO)
				: BigDecimal.ZERO;

		BudgetSummaryResponse resp = new BudgetSummaryResponse();
		resp.setBudgetId(b.getId());
		resp.setName(b.getName());
		resp.setCurrency(b.getCurrency());
		resp.setTotalLimit(b.getTotalLimit());
		resp.setTotalSpent(totalSpent);
		resp.setTotalRemaining(totalRemaining);
		resp.setLines(lineSummaries);
		return resp;
	}

	// ---- helpers ----

	private void ensureUniqueCategories(List<BudgetLineRequest> lines) {
		Set<UUID> seen = new HashSet<>();
		for (BudgetLineRequest l : lines) {
			UUID id = l.getCategoryId();
			if (id == null)
				continue;
			if (!seen.add(id)) {
				throw new BadRequestException("Duplicate categoryId in lines: " + id);
			}
		}
	}

	private BigDecimal sumExpenses(UUID userId, UUID categoryId, LocalDate from, LocalDate to) {
		if (categoryId == null) {
			return transactionRepository.search(userId, TransactionType.EXPENSE, null, from, to, Pageable.unpaged())
					.stream().map(t -> t.getAmount()).reduce(BigDecimal.ZERO, BigDecimal::add);
		} else {
			return transactionRepository
					.search(userId, TransactionType.EXPENSE, categoryId, from, to, Pageable.unpaged()).stream()
					.map(t -> t.getAmount()).reduce(BigDecimal.ZERO, BigDecimal::add);
		}
	}

	private BudgetResponse toResponse(Budget b) {
		List<BudgetLineResponse> lineDtos = b.getLines().stream()
				.map(l -> new BudgetLineResponse(l.getId(), l.getCategory().getId(), l.getCategory().getName(),
						l.getThreshold()))
				.sorted(Comparator.comparing(BudgetLineResponse::getCategoryName)).collect(Collectors.toList());

		BudgetResponse resp = new BudgetResponse();
		resp.setId(b.getId());
		resp.setName(b.getName());
		resp.setPeriod(b.getPeriod());
		resp.setStartDate(b.getStartDate());
		resp.setCurrency(b.getCurrency());
		resp.setTotalLimit(b.getTotalLimit());
		resp.setStatus(b.getStatus());
		resp.setLines(lineDtos);
		resp.setCreatedAt(b.getCreatedAt());
		resp.setUpdatedAt(b.getUpdatedAt());
		return resp;
	}
}
