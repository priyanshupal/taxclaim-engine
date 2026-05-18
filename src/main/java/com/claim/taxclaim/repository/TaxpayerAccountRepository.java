package com.claim.taxclaim.repository;

import com.claim.taxclaim.model.TaxpayerAccount;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaxpayerAccountRepository extends JpaRepository<TaxpayerAccount, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM TaxpayerAccount t WHERE t.taxpayerId = :id")
    Optional<TaxpayerAccount> findByIdForUpdate(@Param("id") String id);
}
