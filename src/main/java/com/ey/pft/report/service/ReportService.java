package com.ey.pft.report.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ey.pft.account.Account;
import com.ey.pft.account.AccountRepository;
import com.ey.pft.common.util.SecurityUtils;
import com.ey.pft.report.dto.CategoryBreakdownResponse;
import com.ey.pft.report.dto.MonthlySummaryResponse;
import com.ey.pft.report.dto.TrendResponse;
import com.ey.pft.transaction.Transaction;
import com.ey.pft.transaction.TransactionRepository;
import com.ey.pft.transaction.TransactionType;

@Service
@Transactional(readOnly = true)
public class ReportService {

	private final TransactionRepository transactionRepository;
	private final AccountRepository accountRepository;

	public ReportService(TransactionRepository transactionRepository, AccountRepository accountRepository) {
		this.transactionRepository = transactionRepository;
		this.accountRepository = accountRepository;
	}

	public MonthlySummaryResponse monthlySummary(int year, int month) {
		UUID userId = SecurityUtils.getCurrentUserIdOrThrow();

		YearMonth ym = YearMonth.of(year, month);
		LocalDate from = ym.atDay(1);
		LocalDate to = ym.atEndOfMonth();

		// Income / expense totals
		BigDecimal income = sumFor(userId, TransactionType.INCOME, null, from, to);
		BigDecimal expense = sumFor(userId, TransactionType.EXPENSE, null, from, to);
		BigDecimal net = income.subtract(expense);

		// Expense by category breakdown
		List<MonthlySummaryResponse.CategoryTotal> expenseByCategory = groupByCategory(userId, TransactionType.EXPENSE,
				from, to).stream()
				.sorted(Comparator.comparing(MonthlySummaryResponse.CategoryTotal::getTotal).reversed())
				.collect(Collectors.toList());

		// Account balances snapshot (current balances)
		List<Account> accounts = accountRepository.findByUser_Id(userId);
		List<MonthlySummaryResponse.AccountBalance> accountBalances = accounts.stream()
				.map(a -> new MonthlySummaryResponse.AccountBalance(a.getId(), a.getName(), a.getType().name(),
						a.getCurrency(), a.getCurrentBalance()))
				.collect(Collectors.toList());

		MonthlySummaryResponse resp = new MonthlySummaryResponse();
		resp.setYear(year);
		resp.setMonth(month);
		resp.setFromDate(from);
		resp.setToDate(to);
		resp.setTotalIncome(income);
		resp.setTotalExpense(expense);
		resp.setNet(net);
		resp.setExpenseByCategory(expenseByCategory);
		resp.setAccountBalances(accountBalances);
		return resp;
	}

	public CategoryBreakdownResponse categoryBreakdown(LocalDate from, LocalDate to, TransactionType type) {
		UUID userId = SecurityUtils.getCurrentUserIdOrThrow();

		List<MonthlySummaryResponse.CategoryTotal> rows = groupByCategory(userId, type, from, to);
		BigDecimal grand = rows.stream().map(MonthlySummaryResponse.CategoryTotal::getTotal).reduce(BigDecimal.ZERO,
				BigDecimal::add);

		List<CategoryBreakdownResponse.Item> items = new ArrayList<>();
		for (MonthlySummaryResponse.CategoryTotal r : rows) {
			double pct = grand.compareTo(BigDecimal.ZERO) > 0 ? r.getTotal().multiply(BigDecimal.valueOf(100))
					.divide(grand, 2, BigDecimal.ROUND_HALF_UP).doubleValue() : 0.0;
			items.add(new CategoryBreakdownResponse.Item(r.getCategoryId(), r.getCategoryName(), r.getTotal(), pct));
		}

		CategoryBreakdownResponse resp = new CategoryBreakdownResponse();
		resp.setFromDate(from);
		resp.setToDate(to);
		resp.setType(type.name());
		resp.setGrandTotal(grand);
		resp.setItems(items.stream().sorted(Comparator.comparing(CategoryBreakdownResponse.Item::getTotal).reversed())
				.collect(Collectors.toList()));
		return resp;
	}

	public TrendResponse trends(int months) {
		if (months <= 0)
			months = 6;
		UUID userId = SecurityUtils.getCurrentUserIdOrThrow();

		YearMonth end = YearMonth.now();
		YearMonth start = end.minusMonths(months - 1);
		LocalDate from = start.atDay(1);
		LocalDate to = end.atEndOfMonth();

		// Fetch all transactions in range (both types) and group in-memory by YearMonth
		Page<Transaction> txPage = transactionRepository.search(userId, null, null, from, to, Pageable.unpaged());
		List<Transaction> txs = txPage.getContent();

		Map<YearMonth, List<Transaction>> byMonth = txs.stream()
				.collect(Collectors.groupingBy(t -> YearMonth.from(t.getTransactionDate())));

		// Build ordered points for each month in the window
		List<TrendResponse.MonthPoint> points = new ArrayList<>();
		YearMonth cursor = start;
		while (!cursor.isAfter(end)) {
			List<Transaction> mtx = byMonth.getOrDefault(cursor, Collections.emptyList());
			BigDecimal income = mtx.stream().filter(t -> t.getType() == TransactionType.INCOME)
					.map(Transaction::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
			BigDecimal expense = mtx.stream().filter(t -> t.getType() == TransactionType.EXPENSE)
					.map(Transaction::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
			BigDecimal net = income.subtract(expense);
			points.add(new TrendResponse.MonthPoint(cursor.getYear(), cursor.getMonthValue(), income, expense, net));
			cursor = cursor.plusMonths(1);
		}

		TrendResponse resp = new TrendResponse();
		resp.setMonths(months);
		resp.setPoints(points);
		return resp;
	}

	// ----- helpers -----

	private BigDecimal sumFor(UUID userId, TransactionType type, UUID categoryId, LocalDate from, LocalDate to) {
		Page<Transaction> page = transactionRepository.search(userId, type, categoryId, from, to, Pageable.unpaged());
		return page.stream().map(Transaction::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	private List<MonthlySummaryResponse.CategoryTotal> groupByCategory(UUID userId, TransactionType type,
			LocalDate from, LocalDate to) {
		Page<Transaction> page = transactionRepository.search(userId, type, null, from, to, Pageable.unpaged());
		Map<UUID, List<Transaction>> grouped = page.getContent().stream()
				.collect(Collectors.groupingBy(t -> t.getCategory() != null ? t.getCategory().getId() : null));

		List<MonthlySummaryResponse.CategoryTotal> rows = new ArrayList<>();
		for (Map.Entry<UUID, List<Transaction>> e : grouped.entrySet()) {
			UUID catId = e.getKey();
			String name;
			if (catId == null) {
				name = "(Uncategorized)";
			} else {
				// grab first non-null name from that category group
				name = e.getValue().stream().map(t -> t.getCategory() != null ? t.getCategory().getName() : null)
						.filter(Objects::nonNull).findFirst().orElse("(Uncategorized)");
			}
			BigDecimal total = e.getValue().stream().map(Transaction::getAmount).reduce(BigDecimal.ZERO,
					BigDecimal::add);
			rows.add(new MonthlySummaryResponse.CategoryTotal(catId, name, total));
		}
		return rows;
	}
}