package com.adopt.models.gametheory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity representing a competitor's bidding profile for game theory analysis
 */
@Entity
@Table(name = "competitor_profiles")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompetitorProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String competitorId;
    private String competitorName;
    private String adSlotId;
    
    private BigDecimal averageBidPrice;
    private BigDecimal minBidPrice;
    private BigDecimal maxBidPrice;
    private Integer bidCount;
    
    private Double estimatedQualityScore;
    private Double winRate;
    
    private String competitorStrategy;
    private String notes;
    
    private LocalDateTime lastUpdated;
    private LocalDateTime firstSeen;
    
    @PrePersist
    protected void onCreate() {
        if (firstSeen == null) {
            firstSeen = LocalDateTime.now();
        }
        lastUpdated = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        lastUpdated = LocalDateTime.now();
    }
    
    /**
     * Add a new bid observation to update the profile
     */
    public void addBidObservation(BigDecimal bidPrice, String adSlotId, boolean wonAuction) {
        // Update statistics based on new observation
        bidCount = (bidCount == null) ? 1 : bidCount + 1;
        
        // Update min/max
        if (minBidPrice == null || bidPrice.compareTo(minBidPrice) < 0) {
            minBidPrice = bidPrice;
        }
        
        if (maxBidPrice == null || bidPrice.compareTo(maxBidPrice) > 0) {
            maxBidPrice = bidPrice;
        }
        
        // Update average (simple rolling average)
        if (averageBidPrice == null) {
            averageBidPrice = bidPrice;
        } else {
            BigDecimal totalBefore = averageBidPrice.multiply(BigDecimal.valueOf(bidCount - 1));
            averageBidPrice = totalBefore.add(bidPrice).divide(BigDecimal.valueOf(bidCount), 
                                                            java.math.RoundingMode.HALF_UP);
        }
        
        // Update win rate
        if (winRate == null) {
            winRate = wonAuction ? 1.0 : 0.0;
        } else {
            double previousWins = winRate * (bidCount - 1);
            winRate = (previousWins + (wonAuction ? 1 : 0)) / bidCount;
        }
        
        // Update last updated timestamp
        lastUpdated = LocalDateTime.now();
    }
} 