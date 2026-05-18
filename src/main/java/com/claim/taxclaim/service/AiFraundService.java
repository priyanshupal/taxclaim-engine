package com.claim.taxclaim.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;

@Service
public class AiFraundService {

    private int requestCounter = 0;

    @CircuitBreaker(name = "aiFraudService", fallbackMethod = "fallbackAiReview")
    public String analyzeClaimText(String taxpayerId, Double amount) {
        requestCounter++;
        System.out.println("🤖 AI Service: Analyzing claim for " + taxpayerId + " (Attempt #" + requestCounter + ")");

        // Simulate a third-party dependency failing unexpectedly
        // Let's force it to throw errors on specific calls to trigger the breaker
        if (requestCounter >= 2 && requestCounter <= 5) {
            throw new RuntimeException("External AI Engine is unresponsive (HTTP 503 Internal Server Error)");
        }

        return "CLEAN_PASS";
    }

    public String fallbackAiReview(String taxpayerId, Double amount, Throwable t) {
        System.err.println("🛡️ CIRCUIT BREAKER TRIGGERED FALLBACK! Reason: " + t.getMessage());
        return "PENDING_MANUAL_REVIEW";
    }
}
