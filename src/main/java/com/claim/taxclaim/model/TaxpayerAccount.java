package com.claim.taxclaim.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Data;

@Entity
@Data
@Table(name = "taxpayer_accounts")
public class TaxpayerAccount {

    @Id
    private String taxpayerId;

    private Double availableLimit;

    @Version
    private Long version;
}
