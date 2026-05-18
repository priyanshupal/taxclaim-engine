package com.claim.taxclaim.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "claims")
public class Claim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String claimId;
    private String taxpayerId;
    private Double amount;
    private String status;
}
