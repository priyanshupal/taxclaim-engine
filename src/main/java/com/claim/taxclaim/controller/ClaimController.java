package com.claim.taxclaim.controller;

import com.claim.taxclaim.model.Claim;
import com.claim.taxclaim.service.ClaimService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/claims")
public class ClaimController {

    private final ClaimService claimService;

    public ClaimController(ClaimService claimService) {
        this.claimService = claimService;
    }

    @PostMapping("/submit")
    public ResponseEntity<Map<String, String>> submitClaim(@RequestBody Claim claim) {
        String trackingId = UUID.randomUUID().toString();
        claim.setClaimId(trackingId);
        claim.setStatus("RECEIVED");

        claimService.processClaimRequestAsynchronously(claim);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(Map.of(
                "status", "Accepted",
                "trackingId", trackingId,
                "message", "Claim is being processed async"));
    }
}
