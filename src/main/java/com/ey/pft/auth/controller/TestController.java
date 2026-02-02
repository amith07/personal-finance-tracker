package com.ey.pft.auth.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/test")
public class TestController {

	@GetMapping
	public ResponseEntity<Map<String, String>> test() {
		return ResponseEntity.ok(Map.of("status", "ok", "message", "public test endpoint"));
	}
}