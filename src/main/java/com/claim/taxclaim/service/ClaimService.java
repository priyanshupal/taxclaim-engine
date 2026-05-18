package com.claim.taxclaim.service;

import com.claim.taxclaim.model.Claim;
import com.claim.taxclaim.model.TaxpayerAccount;
import com.claim.taxclaim.repository.ClaimRepository;
import com.claim.taxclaim.repository.TaxpayerAccountRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClaimService {

    private final ClaimRepository claimRepository;

    private final TaxpayerAccountRepository taxpayerAccountRepository;

    private final AiFraundService aiFraundService;

    @Value("${spring.datasource.username}")
    private String abe;


    public ClaimService(ClaimRepository claimRepository, TaxpayerAccountRepository taxpayerAccountRepository, AiFraundService aiFraundService) {
        this.claimRepository = claimRepository;
        this.taxpayerAccountRepository = taxpayerAccountRepository;
        this.aiFraundService = aiFraundService;
    }

    @Async
    @Transactional
    @Retryable(
            retryFor = { ObjectOptimisticLockingFailureException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000)
    )
    public void processClaimRequestAsynchronously(Claim claim) {
        try {
            System.out.println("Processing claim for taxpayer: " + claim.getTaxpayerId() + " on thread " + Thread.currentThread().getName());
            Thread.sleep(2000);

            TaxpayerAccount account = taxpayerAccountRepository.findByIdForUpdate(claim.getTaxpayerId()).orElseThrow(() -> new RuntimeException("Taxpayer not found"));

            Thread.sleep(1000);

            if (account.getAvailableLimit() >= claim.getAmount()) {
                account.setAvailableLimit(account.getAvailableLimit() - claim.getAmount());
                taxpayerAccountRepository.save(account);
                claim.setStatus(aiFraundService.analyzeClaimText(claim.getTaxpayerId(), claim.getAmount()));
            }
            else {
                claim.setStatus("REJECTED_INSUFFICIENT_FUNDS");
            }

            claimRepository.save(claim);

            System.out.println("Workflow finished. Final status: " + claim.getStatus());
        }
        catch (ObjectOptimisticLockingFailureException ex){
            System.err.println("CONCURRENCY CONFLICT DETECTED! Thread " + Thread.currentThread().getName() + " failed to update due to optimistic lock.");
            claim.setStatus("FAILED_CONCURRENCY_RETRY");
            claimRepository.save(claim);
        }
        catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
}
