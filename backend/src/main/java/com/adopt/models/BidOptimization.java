package com.adopt.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents a bid optimization recommendation
 */
@Entity
@Table(name = "bid_optimizations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BidOptimization {

    /**
     * Status of the optimization
     */
    public enum OptimizationStatus {
        PENDING,  // Not yet applied
        APPLIED,  // Applied to the campaign
        REJECTED  // Rejected by user
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "campaign_id", nullable = false)
    private AdCampaign campaign;
    
    @Column(nullable = false)
    private BigDecimal previousBid;
    
    @Column(nullable = false)
    private BigDecimal optimizedBid;
    
    @Column(nullable = false)
    private Double performanceChange;
    
    @Column(length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private OptimizationStatus status;
    
    @Column(nullable = false)
    private String optimizationReason;
    
    @Column(nullable = false)
    private String gameTheoryModel;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime appliedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) {
            status = OptimizationStatus.PENDING;
        }
    }
} 