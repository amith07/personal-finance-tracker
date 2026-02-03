package com.ey.pft.report.controller;

import java.time.LocalDate;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ey.pft.report.dto.CategoryBreakdownResponse;
import com.ey.pft.report.dto.MonthlySummaryResponse;
import com.ey.pft.report.dto.TrendResponse;
import com.ey.pft.report.service.ReportService;
import com.ey.pft.transaction.TransactionType;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@RestController
@RequestMapping("/api/v1/reports")
public class ReportController {

	private final ReportService reportService;

	public ReportController(ReportService reportService) {
		this.reportService = reportService;
	}

	// GET /api/v1/reports/monthly-summary?year=2026&month=2
	@GetMapping("/monthly-summary")
	public ResponseEntity<MonthlySummaryResponse> monthlySummary(@RequestParam @Min(2000) @Max(2200) int year,
			@RequestParam @Min(1) @Max(12) int month) {
		return ResponseEntity.ok(reportService.monthlySummary(year, month));
	}

	// GET
	// /api/v1/reports/category-breakdown?from=2026-02-01&to=2026-02-28&type=EXPENSE
	@GetMapping("/category-breakdown")
	public ResponseEntity<CategoryBreakdownResponse> categoryBreakdown(@RequestParam LocalDate from,
			@RequestParam LocalDate to, @RequestParam TransactionType type) {
		return ResponseEntity.ok(reportService.categoryBreakdown(from, to, type));
	}

	// GET /api/v1/reports/trends?months=6
	@GetMapping("/trends")
	public ResponseEntity<TrendResponse> trends(@RequestParam(defaultValue = "6") int months) {
		return ResponseEntity.ok(reportService.trends(months));
	}
}